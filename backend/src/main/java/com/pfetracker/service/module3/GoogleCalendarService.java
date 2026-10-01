package com.pfetracker.service.module3;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.ConferenceData;
import com.google.api.services.calendar.model.ConferenceSolutionKey;
import com.google.api.services.calendar.model.CreateConferenceRequest;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Event;

import com.pfetracker.entity.module3.GoogleOAuthToken;
import com.pfetracker.repository.module3.GoogleOAuthTokenRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Intégration réelle Google Calendar / Google Meet (cahier des charges §6.3.1).
 *
 * Nécessite un projet Google Cloud avec l'API Google Calendar activée et des
 * identifiants OAuth2 "Web application" (client ID + client secret), à
 * renseigner via google.oauth.client-id / google.oauth.client-secret /
 * google.oauth.redirect-uri dans application.properties — ce sont des
 * identifiants propres à chaque déploiement, ils ne peuvent pas être
 * générés automatiquement par le code.
 *
 * Tant qu'un utilisateur (encadrant ou étudiant créant la réunion) n'a pas
 * autorisé l'accès à son Google Calendar via /v3/meetings/google/authorize,
 * generateGoogleMeetLink lève une exception — MeetingService bascule alors
 * automatiquement sur un lien de secours (voir §6.3.1 "option alternative :
 * saisie manuelle d'un lien Zoom/Teams" — ou le lien mock existant).
 */
// MODIF : PAS de @Transactional ici — cette classe participait à la
// transaction de l'appelant (ex. MeetingService.createMeeting), et quand
// generateGoogleMeetLink() lève une exception "non configuré" (cas normal
// tant qu'aucun OAuth2 n'est configuré), Spring marquait TOUTE la
// transaction ambiante comme rollback-only avant même que l'appelant
// puisse l'attraper — ce qui faisait échouer la création de réunion avec
// un 500 (UnexpectedRollbackException) malgré le try/catch en place.
@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleCalendarService {

    private final GoogleOAuthTokenRepository tokenRepository;

    @Value("${google.oauth.client-id:}")
    private String clientId;

    @Value("${google.oauth.client-secret:}")
    private String clientSecret;

    @Value("${google.oauth.redirect-uri:http://localhost:8083/api/v3/public/google/callback}")
    private String redirectUri;

    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR_EVENTS);
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private HttpTransport httpTransport() throws Exception {
        return GoogleNetHttpTransport.newTrustedTransport();
    }

    private boolean estConfigure() {
        return clientId != null && !clientId.isBlank()
                && clientSecret != null && !clientSecret.isBlank();
    }

    private GoogleAuthorizationCodeFlow construireFlow() throws Exception {
        GoogleClientSecrets.Details details = new GoogleClientSecrets.Details()
                .setClientId(clientId)
                .setClientSecret(clientSecret);
        GoogleClientSecrets secrets = new GoogleClientSecrets().setWeb(details);

        return new GoogleAuthorizationCodeFlow.Builder(
                httpTransport(), JSON_FACTORY, secrets, SCOPES)
                .setDataStoreFactory(MemoryDataStoreFactory.getDefaultInstance())
                .setAccessType("offline")
                .setApprovalPrompt("force")
                .build();
    }

    /**
     * URL vers laquelle rediriger l'utilisateur pour qu'il autorise l'accès
     * à son Google Calendar. Le paramètre state permet de retrouver son
     * email au retour du callback (pas de session serveur, l'app est stateless).
     */
    public String getOAuth2AuthorizationUrl(String userEmail) {
        if (!estConfigure()) {
            throw new IllegalStateException(
                    "Intégration Google Calendar non configurée (google.oauth.client-id/client-secret manquants)");
        }
        try {
            return construireFlow().newAuthorizationUrl()
                    .setRedirectUri(redirectUri)
                    .setState(userEmail)
                    .build();
        } catch (Exception e) {
            throw new IllegalStateException("Erreur lors de la génération de l'URL d'autorisation Google", e);
        }
    }

    /** Échange le code d'autorisation contre des jetons et les stocke pour cet utilisateur. */
    public boolean handleOAuth2Callback(String userEmail, String authorizationCode) {
        if (!estConfigure()) {
            log.warn("Callback Google OAuth2 reçu mais l'intégration n'est pas configurée");
            return false;
        }
        try {
            GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                    httpTransport(), JSON_FACTORY, clientId, clientSecret,
                    authorizationCode, redirectUri)
                    .execute();

            GoogleOAuthToken token = tokenRepository.findByUserEmail(userEmail)
                    .orElse(GoogleOAuthToken.builder().userEmail(userEmail).build());

            token.setAccessToken(tokenResponse.getAccessToken());
            if (tokenResponse.getRefreshToken() != null) {
                token.setRefreshToken(tokenResponse.getRefreshToken());
            }
            token.setAccessTokenExpiration(
                    LocalDateTime.now().plusSeconds(tokenResponse.getExpiresInSeconds() != null
                            ? tokenResponse.getExpiresInSeconds() : 3600));

            tokenRepository.save(token);
            log.info("Autorisation Google Calendar enregistrée pour {}", userEmail);
            return true;
        } catch (Exception e) {
            log.error("Échec de l'échange du code d'autorisation Google pour {} : {}", userEmail, e.getMessage());
            return false;
        }
    }

    public boolean hasGoogleCalendarAuthorization(String userEmail) {
        return tokenRepository.findByUserEmail(userEmail).isPresent();
    }

    public boolean revokeGoogleCalendarAccess(String userEmail) {
        tokenRepository.deleteByUserEmail(userEmail);
        return true;
    }

    /**
     * Crée un événement dans le calendrier de l'utilisateur avec une visio
     * Google Meet et renvoie le lien généré.
     *
     * @throws IllegalStateException si l'intégration n'est pas configurée ou
     *         si l'utilisateur n'a pas autorisé l'accès à son calendrier —
     *         l'appelant (MeetingService) bascule alors sur un lien de secours.
     */
    public String generateGoogleMeetLink(String meetingTitle, LocalDateTime meetingDateTime,
                                          Integer durationMinutes, String userEmail) {
        if (!estConfigure()) {
            throw new IllegalStateException("Intégration Google Calendar non configurée");
        }

        GoogleOAuthToken token = tokenRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException(
                        "L'utilisateur " + userEmail + " n'a pas autorisé l'accès à Google Calendar"));

        try {
            GoogleCredential credential = new GoogleCredential.Builder()
                    .setTransport(httpTransport())
                    .setJsonFactory(JSON_FACTORY)
                    .setClientSecrets(clientId, clientSecret)
                    .build()
                    .setRefreshToken(token.getRefreshToken());
            credential.refreshToken();

            Calendar calendar = new Calendar.Builder(httpTransport(), JSON_FACTORY, credential)
                    .setApplicationName("PFETracker")
                    .build();

            ZoneId zone = ZoneId.systemDefault();
            EventDateTime debut = new EventDateTime().setDateTime(
                    new com.google.api.client.util.DateTime(
                            meetingDateTime.atZone(zone).toInstant().toEpochMilli()));
            EventDateTime fin = new EventDateTime().setDateTime(
                    new com.google.api.client.util.DateTime(
                            meetingDateTime.plusMinutes(durationMinutes != null ? durationMinutes : 30)
                                    .atZone(zone).toInstant().toEpochMilli()));

            Event event = new Event()
                    .setSummary(meetingTitle)
                    .setStart(debut)
                    .setEnd(fin)
                    .setConferenceData(new ConferenceData()
                            .setCreateRequest(new CreateConferenceRequest()
                                    .setRequestId(UUID.randomUUID().toString())
                                    .setConferenceSolutionKey(new ConferenceSolutionKey().setType("hangoutsMeet"))));

            Event cree = calendar.events().insert("primary", event)
                    .setConferenceDataVersion(1)
                    .execute();

            log.info("Événement Google Calendar créé pour {} : {}", userEmail, cree.getId());
            return cree.getHangoutLink();
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Erreur lors de la création de l'événement Google Calendar : " + e.getMessage(), e);
        }
    }
}
