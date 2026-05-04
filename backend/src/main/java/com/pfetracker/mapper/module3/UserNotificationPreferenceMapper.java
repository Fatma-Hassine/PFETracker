package com.pfetracker.mapper.module3;

import com.pfetracker.dto.module3.UserNotificationPreferenceDTO;
import com.pfetracker.entity.module3.UserNotificationPreference;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserNotificationPreferenceMapper {

    UserNotificationPreferenceDTO toDTO(UserNotificationPreference entity);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserNotificationPreference toEntity(UserNotificationPreferenceDTO dto);
}
