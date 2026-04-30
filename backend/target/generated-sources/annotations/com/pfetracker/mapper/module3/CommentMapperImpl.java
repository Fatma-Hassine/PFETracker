package com.pfetracker.mapper.module3;

import com.pfetracker.dto.module3.CommentDTO;
import com.pfetracker.entity.module3.Comment;
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
public class CommentMapperImpl implements CommentMapper {

    @Override
    public CommentDTO toDTO(Comment comment) {
        if ( comment == null ) {
            return null;
        }

        CommentDTO.CommentDTOBuilder commentDTO = CommentDTO.builder();

        commentDTO.attachmentUrl( comment.getAttachmentUrl() );
        commentDTO.content( comment.getContent() );
        commentDTO.createdAt( comment.getCreatedAt() );
        commentDTO.id( comment.getId() );
        List<Long> list = comment.getMentionedUserIds();
        if ( list != null ) {
            commentDTO.mentionedUserIds( new ArrayList<Long>( list ) );
        }
        commentDTO.parentId( comment.getParentId() );
        commentDTO.taskId( comment.getTaskId() );
        commentDTO.updatedAt( comment.getUpdatedAt() );
        commentDTO.userId( comment.getUserId() );

        return commentDTO.build();
    }

    @Override
    public List<CommentDTO> toDTOList(List<Comment> comments) {
        if ( comments == null ) {
            return null;
        }

        List<CommentDTO> list = new ArrayList<CommentDTO>( comments.size() );
        for ( Comment comment : comments ) {
            list.add( toDTO( comment ) );
        }

        return list;
    }

    @Override
    public Comment toEntity(CommentDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Comment.CommentBuilder comment = Comment.builder();

        comment.attachmentUrl( dto.getAttachmentUrl() );
        comment.content( dto.getContent() );
        comment.createdAt( dto.getCreatedAt() );
        comment.id( dto.getId() );
        List<Long> list = dto.getMentionedUserIds();
        if ( list != null ) {
            comment.mentionedUserIds( new ArrayList<Long>( list ) );
        }
        comment.parentId( dto.getParentId() );
        comment.taskId( dto.getTaskId() );
        comment.updatedAt( dto.getUpdatedAt() );
        comment.userId( dto.getUserId() );

        return comment.build();
    }
}
