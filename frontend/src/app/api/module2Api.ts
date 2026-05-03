import { api } from './axios';
import {
  AiAlert,
  AiGenerationResponse,
  AiGenerationType,
  Milestone,
  Pfe,
  PfeImportResult,
  Priority,
  Sprint,
  Task,
  TaskStatus,
} from '../types/module2.types';

export const module2Api = {
  getMyPfes: async (): Promise<Pfe[]> => {
    const response = await api.get('/pfes');
    return response.data;
  },

  getPfeById: async (pfeId: number): Promise<Pfe> => {
    const response = await api.get(`/pfes/${pfeId}`);
    return response.data;
  },

  createPfe: async (payload: {
    studentId: number;
    studentName?: string;
    studentEmail?: string;
    supervisorId: number;
    supervisorName?: string;
    supervisorEmail?: string;
    department?: string;
    title: string;
    description?: string;
    problemStatement?: string;
    objectives?: string;
    technologies?: string;
    startDate?: string;
    defenseDate?: string;
  }): Promise<Pfe> => {
    const response = await api.post('/pfes', payload);
    return response.data;
  },

  importAffectationsExcel: async (file: File): Promise<PfeImportResult> => {
    const formData = new FormData();
    formData.append('file', file);

    const response = await api.post('/import/affectations', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });

    return response.data;
  },

  getMilestones: async (pfeId: number): Promise<Milestone[]> => {
    const response = await api.get(`/pfes/${pfeId}/milestones`);
    return response.data;
  },

  createMilestone: async (
    pfeId: number,
    payload: {
      title: string;
      description?: string;
      expectedDeliverable?: string;
      plannedStartDate?: string;
      plannedEndDate?: string;
      weight?: number;
    }
  ): Promise<Milestone> => {
    const response = await api.post(`/pfes/${pfeId}/milestones`, payload);
    return response.data;
  },

  createMilestonesBulk: async (
    pfeId: number,
    milestones: Array<{
      title: string;
      description?: string;
      expectedDeliverable?: string;
      plannedStartDate?: string;
      plannedEndDate?: string;
      weight?: number;
    }>
  ): Promise<Milestone[]> => {
    const response = await api.post(`/pfes/${pfeId}/milestones/bulk`, {
      milestones,
    });

    return response.data;
  },

  getTasksByMilestone: async (milestoneId: number): Promise<Task[]> => {
    const response = await api.get(`/milestones/${milestoneId}/tasks`);
    return response.data;
  },

  getTasksByPfe: async (pfeId: number): Promise<Task[]> => {
    const response = await api.get(`/pfes/${pfeId}/tasks`);
    return response.data;
  },

  updateTaskStatus: async (
    taskId: number,
    status: TaskStatus,
    comment?: string
  ): Promise<Task> => {
    const response = await api.patch(`/tasks/${taskId}/status`, {
      status,
      comment,
    });

    return response.data;
  },

  getSprints: async (pfeId: number): Promise<Sprint[]> => {
    const response = await api.get(`/pfes/${pfeId}/sprints`);
    return response.data;
  },

  getAlerts: async (pfeId: number): Promise<AiAlert[]> => {
    const response = await api.get(`/pfes/${pfeId}/alerts`);
    return response.data;
  },

  runAiDetection: async (): Promise<string> => {
    const response = await api.post('/ai/run-detection');
    return response.data;
  },

  generateAiItems: async (payload: {
    type: AiGenerationType;
    prompt: string;
    pfeId?: number;
    milestoneId?: number;
  }): Promise<AiGenerationResponse> => {
    const response = await api.post('/ai/generate', payload);
    return response.data;
  },

  addAiItem: async (payload: {
    milestoneId: number;
    title: string;
    description?: string;
    priority?: Priority;
    estimatedHours?: number;
    itemType?: string;
  }): Promise<Task> => {
    const response = await api.post('/ai/add-item', payload);
    return response.data;
  },

  addAllAiItems: async (
    items: Array<{
      milestoneId: number;
      title: string;
      description?: string;
      priority?: Priority;
      estimatedHours?: number;
      itemType?: string;
    }>
  ): Promise<Task[]> => {
    const response = await api.post('/ai/add-all', {
      items,
    });

    return response.data;
  },

  uploadTaskDeliverable: async (taskId: number, file: File) => {
    const formData = new FormData();
    formData.append('file', file);

    const response = await api.post(`/tasks/${taskId}/deliverables`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });

    return response.data;
  },
};