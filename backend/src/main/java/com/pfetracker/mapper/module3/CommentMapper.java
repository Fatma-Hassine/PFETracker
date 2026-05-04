package com.pfetracker.mapper.module3;

import com.pfetracker.dto.module3.CommentDTO;
import com.pfetracker.entity.module3.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mappings({
        @Mapping(target = "userName", ignore = true),
        @Mapping(target = "userAvatar", ignore = true),
        @Mapping(target = "mentionedUserNames", ignore = true)
    })
    CommentDTO toDTO(Comment comment);

    List<CommentDTO> toDTOList(List<Comment> comments);

    Comment toEntity(CommentDTO dto);
}