package com.pfetracker.dto.module3;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendMessageRequest {
    @NotNull(message = "L'ID du PFE est obligatoire")
    private Long pfeId;

    @NotNull(message = "L'ID du destinataire est obligatoire")
    private Long receiverId;

    @NotBlank(message = "Le contenu est obligatoire")
    @Size(max = 4000, message = "Le message ne doit pas dépasser 4000 caractères")
    private String content;

    private String attachmentUrl;
    private String attachmentName;
}

