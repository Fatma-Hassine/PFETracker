package com.pfetracker.mapper.module3;

import com.pfetracker.dto.module3.NotificationDTO;
import com.pfetracker.entity.module3.NotificationM3;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-03T12:19:56+0100",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260224-0835, environment: Java 25.0.2 (Eclipse Adoptium)"
)
@Component
public class NotificationMapperImpl implements NotificationMapper {

    @Override
    public NotificationDTO toDTO(NotificationM3 notification) {
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
    public List<NotificationDTO> toDTOList(List<NotificationM3> notifications) {
        if ( notifications == null ) {
            return null;
        }

        List<NotificationDTO> list = new ArrayList<NotificationDTO>( notifications.size() );
        for ( NotificationM3 notificationM3 : notifications ) {
            list.add( toDTO( notificationM3 ) );
        }

        return list;
    }

    @Override
    public NotificationM3 toEntity(NotificationDTO dto) {
        if ( dto == null ) {
            return null;
        }

        NotificationM3.NotificationM3Builder notificationM3 = NotificationM3.builder();

        notificationM3.actionUrl( dto.getActionUrl() );
        notificationM3.createdAt( dto.getCreatedAt() );
        notificationM3.id( dto.getId() );
        notificationM3.isRead( dto.getIsRead() );
        notificationM3.message( dto.getMessage() );
        notificationM3.readAt( dto.getReadAt() );
        notificationM3.relatedId( dto.getRelatedId() );
        notificationM3.relatedType( dto.getRelatedType() );
        notificationM3.sentByEmail( dto.getSentByEmail() );
        notificationM3.type( dto.getType() );
        notificationM3.userId( dto.getUserId() );

        return notificationM3.build();
    }
}
