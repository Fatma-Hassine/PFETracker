package com.pfetracker.dto.module3;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateMeetingRequest {
    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 200, message = "Le titre ne doit pas dépasser 200 caractères")
    private String title;

    @Size(max = 2000, message = "La description ne doit pas dépasser 2000 caractères")
    private String description;

    @NotNull(message = "La date de réunion est obligatoire")
    @Future(message = "La date doit être dans le futur")
    private LocalDateTime meetingDate;

    @NotNull(message = "La durée est obligatoire")
    @Min(value = 15, message = "La durée minimum est de 15 minutes")
    @Max(value = 240, message = "La durée maximum est de 4 heures")
    private Integer duration;

    @NotNull(message = "L'ID du participant est obligatoire")
    private Long participantId;

    @NotNull(message = "L'ID du PFE est obligatoire")
    private Long pfeId;

    private String meetingLink;
    private String meetProvider;
    private Boolean isOnline;
    private String location;
}

