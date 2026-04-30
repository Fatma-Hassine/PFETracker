package com.pfetracker.mapper.module3;

import com.pfetracker.dto.module3.NotificationDTO;
import com.pfetracker.entity.module3.Notification;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-01T00:36:04+0200",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class NotificationMapperImpl implements NotificationMapper {

    @Override
    public NotificationDTO toDTO(Notification notification) {
        if ( notification == null ) {
            return null;
        }

        NotificationDTO.NotificationDTOBuilder notificationDTO = NotificationDTO.builder();

        notificationDTO.actionUrl( notification.getActionUrl() );
        notificationDTO.createdAt( notification.getCreatedAt() );
        notificationDTO.id( notification.getId() );
        notificationDTO.isRead( notification.getIsRead() );
        notificationDTO.message( notification.getMessage() );
        notificationDTO.readAt( notification.getReadAt() );
        notificationDTO.relatedId( notification.getRelatedId() );
        notificationDTO.relatedType( notification.getRelatedType() );
        notificationDTO.sentByEmail( notification.getSentByEmail() );
        notificationDTO.type( notification.getType() );
        notificationDTO.userId( notification.getUserId() );

        return notificationDTO.build();
    }

    @Override
    public List<NotificationDTO> toDTOList(List<Notification> notifications) {
        if ( notifications == null ) {
            return null;
        }

        List<NotificationDTO> list = new ArrayList<NotificationDTO>( notifications.size() );
        for ( Notification notification : notifications ) {
            list.add( toDTO( notification ) );
        }

        return list;
    }

    @Override
    public Notification toEntity(NotificationDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Notification.NotificationBuilder notification = Notification.builder();

        notification.actionUrl( dto.getActionUrl() );
        notification.createdAt( dto.getCreatedAt() );
        notification.id( dto.getId() );
        notification.isRead( dto.getIsRead() );
        notification.message( dto.getMessage() );
        notification.readAt( dto.getReadAt() );
        notification.relatedId( dto.getRelatedId() );
        notification.relatedType( dto.getRelatedType() );
        notification.sentByEmail( dto.getSentByEmail() );
        notification.type( dto.getType() );
        notification.userId( dto.getUserId() );

        return notification.build();
    }
}
