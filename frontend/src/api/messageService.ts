import axiosInstance from './axiosConfig';

export interface MessageDTO {
  id: number;
  pfeId: number;
  senderId: number;
  senderName: string;
  receiverId: number;
  receiverName: string;
  content: string;
  status: 'READ' | 'UNREAD';
  createdAt: string;
  readAt?: string;
  attachmentUrl?: string;
  attachmentName?: string;
}

export interface SendMessageRequest {
  pfeId: number;
  receiverId: number;
  content: string;
  attachmentUrl?: string;
  attachmentName?: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

const messageService = {
  getConversation: async (pfeId: number, otherUserId: number, page = 0, size = 50) => {
    const res = await axiosInstance.get<ApiResponse<PageResponse<MessageDTO>>>(
      `/messages/conversation/${pfeId}/${otherUserId}?page=${page}&size=${size}`
    );
    return res.data.data;
  },

  sendMessage: async (request: SendMessageRequest) => {
    const res = await axiosInstance.post<ApiResponse<MessageDTO>>('/messages', request);
    return res.data.data;
  },

  markAsRead: async (messageId: number) => {
    await axiosInstance.put(`/messages/read/${messageId}`);
  },

  getUnreadCount: async () => {
    const res = await axiosInstance.get<ApiResponse<number>>('/messages/unread/count');
    return res.data.data;
  },

  getMessagesForPfe: async (pfeId: number, page = 0, size = 50) => {
    const res = await axiosInstance.get<ApiResponse<PageResponse<MessageDTO>>>(
      `/messages/pfe/${pfeId}?page=${page}&size=${size}`
    );
    return res.data.data;
  },

  searchMessages: async (pfeId: number, otherUserId: number, keyword: string) => {
    const res = await axiosInstance.get<ApiResponse<PageResponse<MessageDTO>>>(
      `/messages/conversation/${pfeId}/${otherUserId}/search?keyword=${encodeURIComponent(keyword)}&page=0&size=50`
    );
    return res.data.data;
  },
};

export default messageService;
