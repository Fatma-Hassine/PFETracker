package com.pfetracker.service.module3;

import com.pfetracker.dto.module3.CreateMeetingRequest;
import com.pfetracker.dto.module3.MeetingDTO;
import com.pfetracker.dto.module3.MeetingResponseRequest;
import com.pfetracker.dto.module3.PageResponse;
import com.pfetracker.dto.module3.UpdateMeetingRequest;
import com.pfetracker.entity.module3.Meeting;
import com.pfetracker.entity.module3.PFE;
import com.pfetracker.exception.module3.BadRequestException;
import com.pfetracker.exception.module3.ResourceNotFoundException;
import com.pfetracker.exception.module3.UnauthorizedException;
import com.pfetracker.mapper.module3.MeetingMapper;
import com.pfetracker.repository.module3.MeetingRepository;
import com.pfetracker.repository.module3.PFERepository;
import com.pfetracker.repository.module3.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final MeetingMapper meetingMapper;
    private final PFERepository pfeRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final GoogleCalendarService googleCalendarService;

    public MeetingDTO createMeeting(Long createdBy, CreateMeetingRequest request) {
        PFE pfe = pfeRepository.findById(request.getPfeId())
                .orElseThrow(() -> new ResourceNotFoundException("PFE non trouvÃ©"));

        if (!isParticipantInPFE(createdBy, pfe)) {
            throw new UnauthorizedException("Vous n'Ãªtes pas autorisÃ© Ã  crÃ©er une rÃ©union pour ce PFE");
        }

        if (!request.getParticipantId().equals(pfe.getStudentId()) && 
            !request.getParticipantId().equals(pfe.getSupervisorId())) {
            throw new BadRequestException("Le participant doit Ãªtre l'Ã©tudiant ou l'encadrant du PFE");
        }

        String meetingLink = request.getMeetingLink();
        String provider = request.getMeetProvider();

        if (meetingLink == null || meetingLink.isEmpty()) {
            // Try to generate Google Meet link; fall back to mock if integration not ready
            try {
                meetingLink = googleCalendarService.generateGoogleMeetLink(
                    request.getTitle(),
                    request.getMeetingDate(),
                    request.getDuration(),
                    userRepository.findById(createdBy)
                        .map(u -> u.getEmail())
                        .orElse("unknown@example.com")
                );
                provider = "GOOGLE_MEET";
            } catch (Exception e) {
                log.warn("Failed to generate Google Meet link, using mock link: {}", e.getMessage());
                meetingLink = generateMockMeetLink(request.getTitle());
                provider = "MOCK";
            }
        }

        Meeting meeting = Meeting.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .meetingDate(request.getMeetingDate())
                .duration(request.getDuration())
                .createdBy(createdBy)
                .participantId(request.getParticipantId())
                .pfeId(request.getPfeId())
                .status(Meeting.MeetingStatus.PENDING)
                .meetingLink(meetingLink)
                .meetProvider(provider)
                .isOnline(request.getIsOnline() != null ? request.getIsOnline() : true)
                .location(request.getLocation())
                .build();

        Meeting saved = meetingRepository.save(meeting);
        log.info("Meeting created: id={}, pfe={}, by={}", saved.getId(), request.getPfeId(), createdBy);

        notificationService.createMeetingNotification(request.getParticipantId(), saved, "CREATED");
        notificationService.sendEmailNotification(request.getParticipantId(), 
                "Nouvelle rÃ©union planifiÃ©e - " + saved.getTitle(),
                buildMeetingEmailBody(saved, "Nouvelle rÃ©union"));

        return enrichMeeting(meetingMapper.toDTO(saved));
    }

    public MeetingDTO respondToMeeting(Long meetingId, Long userId, MeetingResponseRequest request) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new ResourceNotFoundException("RÃ©union non trouvÃ©e"));

        if (!meeting.getParticipantId().equals(userId)) {
            throw new UnauthorizedException("Vous n'Ãªtes pas le participant invitÃ© Ã  cette rÃ©union");
        }

        if (meeting.getStatus() != Meeting.MeetingStatus.PENDING) {
            throw new BadRequestException("Cette rÃ©union a dÃ©jÃ  Ã©tÃ© traitÃ©e");
        }

        meeting.setStatus(request.getStatus());
        if (request.getStatus() == Meeting.MeetingStatus.REFUSED) {
            meeting.setRejectionReason(request.getRejectionReason());
        }

        Meeting saved = meetingRepository.save(meeting);
        log.info("Meeting {} responded by {}: {}", meetingId, userId, request.getStatus());

        notificationService.createMeetingNotification(meeting.getCreatedBy(), saved,
                request.getStatus() == Meeting.MeetingStatus.ACCEPTED ? "ACCEPTED" : "REFUSED");

        return enrichMeeting(meetingMapper.toDTO(saved));
    }

    public MeetingDTO updateMeeting(Long meetingId, Long userId, UpdateMeetingRequest request) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new ResourceNotFoundException("RÃ©union non trouvÃ©e"));

        if (!meeting.getCreatedBy().equals(userId)) {
            throw new UnauthorizedException("Seul le crÃ©ateur peut modifier cette rÃ©union");
        }

        if (request.getTitle() != null) meeting.setTitle(request.getTitle());
        if (request.getDescription() != null) meeting.setDescription(request.getDescription());
        if (request.getMeetingDate() != null) meeting.setMeetingDate(request.getMeetingDate());
        if (request.getDuration() != null) meeting.setDuration(request.getDuration());
        if (request.getMeetingLink() != null) meeting.setMeetingLink(request.getMeetingLink());
        if (request.getMeetProvider() != null) meeting.setMeetProvider(request.getMeetProvider());
        if (request.getIsOnline() != null) meeting.setIsOnline(request.getIsOnline());
        if (request.getLocation() != null) meeting.setLocation(request.getLocation());

        meeting.setReminderSent24h(false);
        meeting.setReminderSent15min(false);

        Meeting saved = meetingRepository.save(meeting);
        log.info("Meeting updated: id={}", meetingId);

        Long otherParty = meeting.getCreatedBy().equals(userId) ? meeting.getParticipantId() : meeting.getCreatedBy();
        notificationService.createMeetingNotification(otherParty, saved, "UPDATED");

        return enrichMeeting(meetingMapper.toDTO(saved));
    }

    public void deleteMeeting(Long meetingId, Long userId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new ResourceNotFoundException("RÃ©union non trouvÃ©e"));

        if (!meeting.getCreatedBy().equals(userId)) {
            throw new UnauthorizedException("Seul le crÃ©ateur peut supprimer cette rÃ©union");
        }

        meeting.setStatus(Meeting.MeetingStatus.CANCELLED);
        meetingRepository.save(meeting);

        Long otherParty = meeting.getCreatedBy().equals(userId) ? meeting.getParticipantId() : meeting.getCreatedBy();
        notificationService.createMeetingNotification(otherParty, meeting, "CANCELLED");

        log.info("Meeting cancelled: id={}", meetingId);
    }

    @Transactional(readOnly = true)
    public MeetingDTO getMeetingById(Long meetingId, Long userId) {
        Meeting meeting = meetingRepository.findByIdAndParticipant(meetingId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("RÃ©union non trouvÃ©e ou accÃ¨s non autorisÃ©"));
        return enrichMeeting(meetingMapper.toDTO(meeting));
    }

    @Transactional(readOnly = true)
    public PageResponse<MeetingDTO> getMeetingsByPfe(Long pfeId, Long userId, int page, int size) {
        PFE pfe = pfeRepository.findById(pfeId)
                .orElseThrow(() -> new ResourceNotFoundException("PFE non trouvÃ©"));

        if (!isParticipantInPFE(userId, pfe)) {
            throw new UnauthorizedException("AccÃ¨s non autorisÃ©");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("meetingDate").descending());
        Page<Meeting> meetings = meetingRepository.findByPfeId(pfeId, pageable);

        List<MeetingDTO> enriched = meetings.getContent().stream()
                .map(m -> enrichMeeting(meetingMapper.toDTO(m)))
                .collect(Collectors.toList());

        return PageResponse.<MeetingDTO>builder()
                .content(enriched)
                .pageNumber(meetings.getNumber())
                .pageSize(meetings.getSize())
                .totalElements(meetings.getTotalElements())
                .totalPages(meetings.getTotalPages())
                .last(meetings.isLast())
                .first(meetings.isFirst())
                .build();
    }

    @Transactional(readOnly = true)
    public List<MeetingDTO> getUpcomingMeetings(Long userId) {
        return meetingRepository.findUpcomingByParticipant(userId, LocalDateTime.now()).stream()
                .map(m -> enrichMeeting(meetingMapper.toDTO(m)))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MeetingDTO> getUpcomingMeetingsByPfeIds(List<Long> pfeIds) {
        return meetingRepository.findUpcomingByPfeIds(pfeIds, LocalDateTime.now()).stream()
                .map(m -> enrichMeeting(meetingMapper.toDTO(m)))
                .collect(Collectors.toList());
    }

    public MeetingDTO addReport(Long meetingId, Long userId, String report) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new ResourceNotFoundException("RÃ©union non trouvÃ©e"));

        if (!meeting.getCreatedBy().equals(userId) && !meeting.getParticipantId().equals(userId)) {
            throw new UnauthorizedException("AccÃ¨s non autorisÃ©");
        }

        meeting.setReport(report);
        meeting.setStatus(Meeting.MeetingStatus.COMPLETED);
        Meeting saved = meetingRepository.save(meeting);

        log.info("Report added to meeting: id={}", meetingId);
        return enrichMeeting(meetingMapper.toDTO(saved));
    }

    private String generateMockMeetLink(String title) {
        String code = UUID.randomUUID().toString().substring(0, 12).replace("-", "");
        return "https://meet.google.com/" + code;
    }

    private boolean isParticipantInPFE(Long userId, PFE pfe) {
        return pfe.getStudentId().equals(userId) ||
               (pfe.getSupervisorId() != null && pfe.getSupervisorId().equals(userId));
    }

    private MeetingDTO enrichMeeting(MeetingDTO dto) {
        userRepository.findById(dto.getCreatedBy())
                .ifPresent(user -> dto.setCreatedByName(user.getFullName()));
        userRepository.findById(dto.getParticipantId())
                .ifPresent(user -> dto.setParticipantName(user.getFullName()));
        return dto;
    }

    private String buildMeetingEmailBody(Meeting meeting, String action) {
        return String.format(
            "Bonjour,%n%n" +
            "%s%n%n" +
            "Titre: %s%n" +
            "Date: %s%n" +
            "DurÃ©e: %d minutes%n" +
            "Lien de rÃ©union: %s%n%n" +
            "Cordialement,%nPFETracker",
            action, meeting.getTitle(), meeting.getMeetingDate(), 
            meeting.getDuration(), meeting.getMeetingLink()
        );
    }
}

