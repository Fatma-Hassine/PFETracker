package com.pfetracker.mapper.module1;
import com.pfetracker.dto.module1.NotificationResponse;
import com.pfetracker.entity.module1.NotificationM1;
import org.springframework.stereotype.Component;

@Component("notificationMapperM1")

public class NotificationMapper {
	 public NotificationResponse toResponse(NotificationM1 n) {
	        return NotificationResponse.builder()
	                .id(n.getId())
	                .type(n.getType())
	                .message(n.getMessage())
	                .lu(n.getLu())
	                .dateCreation(n.getDateCreation())
	                .build();
	    }
}
