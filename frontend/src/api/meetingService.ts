import axiosInstance from './axiosConfig';
import { ApiResponse, PageResponse } from './messageService';

export interface MeetingDTO {
  id: number;
  title: string;
  description?: string;
  meetingDate: string;
  duration?: number;
  createdBy: number;
  createdByName: string;
  participantId: number;
  participantName: string;
  pfeId: number;
  status: 'PENDING' | 'ACCEPTED' | 'REFUSED' | 'CANCELLED' | 'COMPLETED';
  meetingLink?: string;
  meetProvider?: string;
  isOnline?: boolean;
  location?: string;
  createdAt?: string;
  report?: string;
  rejectionReason?: string;
}

export interface CreateMeetingRequest {
  pfeId: number;
  participantId: number;
  title: string;
  description?: string;
  meetingDate: string;
  duration?: number;
  meetingLink?: string;
  meetProvider?: string;
  isOnline?: boolean;
  location?: string;
}

export interface UpdateMeetingRequest {
  title?: string;
  description?: string;
  meetingDate?: string;
  duration?: number;
  meetingLink?: string;
  location?: string;
}

const meetingService = {
  createMeeting: async (request: CreateMeetingRequest) => {
    const res = await axiosInstance.post<ApiResponse<MeetingDTO>>('/meetings', request);
    return res.data.data;
  },

  respondToMeeting: async (meetingId: number, accepted: boolean, reason?: string) => {
    const res = await axiosInstance.put<ApiResponse<MeetingDTO>>(
      `/meetings/${meetingId}/respond`,
      { status: accepted ? 'ACCEPTED' : 'REFUSED', rejectionReason: reason }
    );
    return res.data.data;
  },

  updateMeeting: async (meetingId: number, request: UpdateMeetingRequest) => {
    const res = await axiosInstance.put<ApiResponse<MeetingDTO>>(`/meetings/${meetingId}`, request);
    return res.data.data;
  },

  cancelMeeting: async (meetingId: number) => {
    await axiosInstance.delete(`/meetings/${meetingId}`);
  },

  getMeeting: async (meetingId: number) => {
    const res = await axiosInstance.get<ApiResponse<MeetingDTO>>(`/meetings/${meetingId}`);
    return res.data.data;
  },

  getMeetingsForPfe: async (pfeId: number, page = 0, size = 20) => {
    const res = await axiosInstance.get<ApiResponse<PageResponse<MeetingDTO>>>(
      `/meetings/pfe/${pfeId}?page=${page}&size=${size}`
    );
    return res.data.data;
  },

  getUpcomingMeetings: async () => {
    // MODIF : le backend renvoie une liste simple (MeetingController#getUpcomingMeetings
    // -> List<MeetingDTO>), pas une page — pas de pagination sur cet endpoint.
    const res = await axiosInstance.get<ApiResponse<MeetingDTO[]>>('/meetings/upcoming');
    return res.data.data;
  },

  addReport: async (meetingId: number, report: string) => {
    const res = await axiosInstance.post<ApiResponse<MeetingDTO>>(
      `/meetings/${meetingId}/report`,
      { report }
    );
    return res.data.data;
  },
};

export default meetingService;
