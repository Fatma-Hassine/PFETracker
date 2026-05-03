package com.pfetracker.mapper.module3;

import com.pfetracker.dto.module3.MessageDTO;
import com.pfetracker.entity.module3.Message;
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
public class MessageMapperImpl implements MessageMapper {

    @Override
    public MessageDTO toDTO(Message message) {
        if ( message == null ) {
            return null;
        }

        MessageDTO.MessageDTOBuilder messageDTO = MessageDTO.builder();

        messageDTO.attachmentName( message.getAttachmentName() );
        messageDTO.attachmentUrl( message.getAttachmentUrl() );
        messageDTO.content( message.getContent() );
        messageDTO.createdAt( message.getCreatedAt() );
        messageDTO.id( message.getId() );
        messageDTO.pfeId( message.getPfeId() );
        messageDTO.readAt( message.getReadAt() );
        messageDTO.receiverId( message.getReceiverId() );
        messageDTO.senderId( message.getSenderId() );
        messageDTO.status( message.getStatus() );

        return messageDTO.build();
    }

    @Override
    public List<MessageDTO> toDTOList(List<Message> messages) {
        if ( messages == null ) {
            return null;
        }

        List<MessageDTO> list = new ArrayList<MessageDTO>( messages.size() );
        for ( Message message : messages ) {
            list.add( toDTO( message ) );
        }

        return list;
    }

    @Override
    public Message toEntity(MessageDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Message.MessageBuilder message = Message.builder();

        message.attachmentName( dto.getAttachmentName() );
        message.attachmentUrl( dto.getAttachmentUrl() );
        message.content( dto.getContent() );
        message.createdAt( dto.getCreatedAt() );
        message.id( dto.getId() );
        message.pfeId( dto.getPfeId() );
        message.readAt( dto.getReadAt() );
        message.receiverId( dto.getReceiverId() );
        message.senderId( dto.getSenderId() );
        message.status( dto.getStatus() );

        return message.build();
    }
}
