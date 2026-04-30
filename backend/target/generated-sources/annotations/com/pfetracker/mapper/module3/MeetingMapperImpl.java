package com.pfetracker.mapper.module3;

import com.pfetracker.dto.module3.MeetingDTO;
import com.pfetracker.entity.module3.Meeting;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-01T00:36:05+0200",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class MeetingMapperImpl implements MeetingMapper {

    @Override
    public MeetingDTO toDTO(Meeting meeting) {
        if ( meeting == null ) {
            return null;
        }

        MeetingDTO.MeetingDTOBuilder meetingDTO = MeetingDTO.builder();

        meetingDTO.createdAt( meeting.getCreatedAt() );
        meetingDTO.createdBy( meeting.getCreatedBy() );
        meetingDTO.description( meeting.getDescription() );
        meetingDTO.duration( meeting.getDuration() );
        meetingDTO.id( meeting.getId() );
        meetingDTO.isOnline( meeting.getIsOnline() );
        meetingDTO.location( meeting.getLocation() );
        meetingDTO.meetProvider( meeting.getMeetProvider() );
        meetingDTO.meetingDate( meeting.getMeetingDate() );
        meetingDTO.meetingLink( meeting.getMeetingLink() );
        meetingDTO.participantId( meeting.getParticipantId() );
        meetingDTO.pfeId( meeting.getPfeId() );
        meetingDTO.rejectionReason( meeting.getRejectionReason() );
        meetingDTO.reminderSent15min( meeting.getReminderSent15min() );
        meetingDTO.reminderSent24h( meeting.getReminderSent24h() );
        meetingDTO.report( meeting.getReport() );
        meetingDTO.status( meeting.getStatus() );
        meetingDTO.title( meeting.getTitle() );
        meetingDTO.updatedAt( meeting.getUpdatedAt() );

        return meetingDTO.build();
    }

    @Override
    public List<MeetingDTO> toDTOList(List<Meeting> meetings) {
        if ( meetings == null ) {
            return null;
        }

        List<MeetingDTO> list = new ArrayList<MeetingDTO>( meetings.size() );
        for ( Meeting meeting : meetings ) {
            list.add( toDTO( meeting ) );
        }

        return list;
    }

    @Override
    public Meeting toEntity(MeetingDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Meeting.MeetingBuilder meeting = Meeting.builder();

        meeting.createdAt( dto.getCreatedAt() );
        meeting.createdBy( dto.getCreatedBy() );
        meeting.description( dto.getDescription() );
        meeting.duration( dto.getDuration() );
        meeting.id( dto.getId() );
        meeting.isOnline( dto.getIsOnline() );
        meeting.location( dto.getLocation() );
        meeting.meetProvider( dto.getMeetProvider() );
        meeting.meetingDate( dto.getMeetingDate() );
        meeting.meetingLink( dto.getMeetingLink() );
        meeting.participantId( dto.getParticipantId() );
        meeting.pfeId( dto.getPfeId() );
        meeting.rejectionReason( dto.getRejectionReason() );
        meeting.reminderSent15min( dto.getReminderSent15min() );
        meeting.reminderSent24h( dto.getReminderSent24h() );
        meeting.report( dto.getReport() );
        meeting.status( dto.getStatus() );
        meeting.title( dto.getTitle() );
        meeting.updatedAt( dto.getUpdatedAt() );

        return meeting.build();
    }
}
