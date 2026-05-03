package com.pfetracker.mapper.module3;

import com.pfetracker.dto.module3.NotificationDTO;
import com.pfetracker.entity.module3.NotificationM3;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationDTO toDTO(NotificationM3 notification);
    List<NotificationDTO> toDTOList(List<NotificationM3> notifications);
    NotificationM3 toEntity(NotificationDTO dto);
}

