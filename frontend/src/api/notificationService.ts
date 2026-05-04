import axiosInstance from './axiosConfig';
import { ApiResponse, PageResponse } from './messageService';

export interface NotificationDTO {
  id: number;
  userId: number;
  message: string;
  type: string;
  isRead: boolean;
  createdAt: string;
  readAt?: string;
  relatedId?: number;
  relatedType?: string;
  actionUrl?: string;
  sentByEmail?: boolean;
}

export interface NotificationPreferenceDTO {
  inAppEnabled: boolean;
  emailEnabled: boolean;
  taskNotifications: boolean;
  meetingNotifications: boolean;
  messageNotifications: boolean;
  alertNotifications: boolean;
}

const notificationService = {
  getAll: async (page = 0, size = 30) => {
    const res = await axiosInstance.get<ApiResponse<PageResponse<NotificationDTO>>>(
      `/notifications?page=${page}&size=${size}`
    );
    return res.data.data;
  },

  getCenter: async () => {
    const res = await axiosInstance.get<ApiResponse<NotificationDTO[]>>('/notifications/center');
    return res.data.data;
  },

  getUnread: async () => {
    const res = await axiosInstance.get<ApiResponse<NotificationDTO[]>>('/notifications/unread');
    return res.data.data;
  },

  getUnreadCount: async () => {
    const res = await axiosInstance.get<ApiResponse<number>>('/notifications/unread/count');
    return res.data.data;
  },

  markAsRead: async (ids: number[]) => {
    await axiosInstance.put('/notifications/read', { notificationIds: ids });
  },

  markAllAsRead: async () => {
    await axiosInstance.put('/notifications/read/all');
  },

  markReadByAction: async (notificationId: number) => {
    await axiosInstance.put(`/notifications/${notificationId}/read-by-action`);
  },

  delete: async (notificationId: number) => {
    await axiosInstance.delete(`/notifications/${notificationId}`);
  },

  getPreferences: async () => {
    const res = await axiosInstance.get<ApiResponse<NotificationPreferenceDTO>>('/notifications/preferences');
    return res.data.data;
  },

  updatePreferences: async (prefs: Partial<NotificationPreferenceDTO>) => {
    const res = await axiosInstance.put<ApiResponse<NotificationPreferenceDTO>>('/notifications/preferences', prefs);
    return res.data.data;
  },
};

export default notificationService;
