package com.pfetracker.dto.module1;
import lombok.*;
import java.time.LocalDateTime;

import com.pfetracker.entity.module1.NotificationM1;
import com.pfetracker.entity.module1.enums.TypeNotification;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
	private Long id;
    private TypeNotification type;
    private String message;
    private Boolean lu;
    private LocalDateTime dateCreation;

    public static NotificationResponse fromEntity(NotificationM1 n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .type(n.getType())
                .message(n.getMessage())
                .lu(n.getLu())
                .dateCreation(n.getDateCreation())
                .build();
    }
}
