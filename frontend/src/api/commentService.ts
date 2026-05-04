import axiosInstance from './axiosConfig';
import { ApiResponse, PageResponse } from './messageService';

export interface CommentDTO {
  id: number;
  taskId: number;
  userId: number;
  userName: string;
  userRole: string;
  content: string;
  mentions: number[]; // IDs des utilisateurs mentionnés
  createdAt: string;
}

export interface CreateCommentRequest {
  taskId: number;
  content: string;
  mentions?: number[];
}

const commentService = {
  addComment: async (request: CreateCommentRequest) => {
    const res = await axiosInstance.post<ApiResponse<CommentDTO>>('/comments', request);
    return res.data.data;
  },

  getCommentsByTask: async (taskId: number, page = 0, size = 20) => {
    const res = await axiosInstance.get<ApiResponse<PageResponse<CommentDTO>>>(
      `/comments/task/${taskId}?page=${page}&size=${size}`
    );
    return res.data.data;
  },

  getAllCommentsByTask: async (taskId: number) => {
    const res = await axiosInstance.get<ApiResponse<CommentDTO[]>>(`/comments/task/${taskId}/all`);
    return res.data.data;
  },

  getCommentCount: async (taskId: number) => {
    const res = await axiosInstance.get<ApiResponse<number>>(`/comments/task/${taskId}/count`);
    return res.data.data;
  },
};

export default commentService;
