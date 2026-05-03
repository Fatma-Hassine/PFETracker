import axiosInstance from './axiosConfig';
import { ApiResponse } from './messageService';
import { MeetingDTO } from './meetingService';
import { NotificationDTO } from './notificationService';

export interface TaskSummaryDTO {
  id: number;
  title: string;
  status: string;
  deadline?: string;
  isOverdue?: boolean;
  completionPercentage?: number;
  pfeId?: number;
  pfeTitle?: string;
  studentId?: number;
  studentName?: string;
}

export interface ActivityLogDTO {
  id: number;
  type: string;
  description: string;
  timestamp: string;
  actionUrl?: string;
  relatedId?: number;
}

export interface AlertDTO {
  id: number;
  type: string;
  severity: string;
  message: string;
  relatedId?: number;
  relatedType?: string;
  actionUrl?: string;
}

export interface StudentSummaryDTO {
  studentId: number;
  studentName: string;
  studentEmail: string;
  pfeId: number;
  pfeTitle: string;
  pfeProgress: number;
  pfeStatus: string;
  pendingTasks: number;
  overdueTasks: number;
  lastActivity: string;
  isInactive: boolean;
}

export interface StudentDashboardDTO {
  progressionGlobale: number;
  tachesEnCours: number;
  tachesSoumises: number;
  tachesValidees: number;
  tachesTotal: number;
  tachesRecentes: TaskSummaryDTO[];
  prochainesReunions: MeetingDTO[];
  notificationsNonLues: NotificationDTO[];
  jalonActuel?: string;
  prochainJalon?: string;
  journalActivite: ActivityLogDTO[];
  alertesActives: AlertDTO[];
}

export interface SupervisorDashboardDTO {
  etudiants: StudentSummaryDTO[];
  totalEtudiants: number;
  etudiantsEnRetard: number;
  etudiantsInactifs: number;
  tachesAValider: TaskSummaryDTO[];
  reunionsAVenir: MeetingDTO[];
  alertesPrioritaires: AlertDTO[];
  tauxMoyenProgression: number;
  tempsMoyenReponse?: number;
}

const dashboardService = {
  getStudentDashboard: async (): Promise<StudentDashboardDTO> => {
    const res = await axiosInstance.get<ApiResponse<StudentDashboardDTO>>('/dashboard/student');
    return res.data.data;
  },

  getSupervisorDashboard: async (): Promise<SupervisorDashboardDTO> => {
    const res = await axiosInstance.get<ApiResponse<SupervisorDashboardDTO>>('/dashboard/supervisor');
    return res.data.data;
  },

  getDeptManagerDashboard: async () => {
    const res = await axiosInstance.get<ApiResponse<any>>('/dashboard/dept-manager');
    return res.data.data;
  },

  getDirectorDashboard: async () => {
    const res = await axiosInstance.get<ApiResponse<any>>('/dashboard/director');
    return res.data.data;
  },
};

export default dashboardService;
