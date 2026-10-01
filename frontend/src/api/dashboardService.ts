import axiosInstance from './axiosConfig';
import { ApiResponse } from './messageService';
import { MeetingDTO } from './meetingService';
import { NotificationDTO } from './notificationService';

// MODIF : ces interfaces ne correspondaient pas aux vrais DTO Java
// (DashboardStudentDTO/DashboardSupervisorDTO) — noms de champs différents
// (ex. "progressionGlobale" au lieu de "globalProgress"), ce qui aurait
// renvoyé `undefined` partout côté frontend. Alignées sur le backend.

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
  lastActivity?: string;
  isInactive: boolean;
}

export interface DashboardStudentDTO {
  pfeId: number;
  pfeTitle: string;
  globalProgress: number;
  currentMilestone?: string;
  nextMilestone?: string;
  ongoingTasks: TaskSummaryDTO[];
  upcomingDeadlines: TaskSummaryDTO[];
  nextMeeting?: MeetingDTO;
  recentNotifications: NotificationDTO[];
  recentActivity?: ActivityLogDTO[];
  activeAlerts: AlertDTO[];
}

export interface DashboardSupervisorDTO {
  supervisorId: number;
  totalStudents: number;
  activePFEs: number;
  averageProgress: number;
  students: StudentSummaryDTO[];
  pendingValidations: TaskSummaryDTO[];
  upcomingMeetings: MeetingDTO[];
  priorityAlerts: AlertDTO[];
  recentNotifications: NotificationDTO[];
}

export interface DashboardDeptManagerDTO {
  managerId: number;
  departmentName: string;
  totalStudents: number;
  activePFEs: number;
  completedPFEs: number;
  delayedPFEs: number;
  averageProgress: number;
  totalMeetings: number;
  completedMeetings: number;
  overallInactiveStudents: number;
}

export interface DashboardDirectorDTO {
  directorId: number;
  totalDepartments: number;
  totalStudents: number;
  totalPFEs: number;
  completedPFEs: number;
  delayedPFEs: number;
  globalAverageProgress: number;
  totalMeetings: number;
  completedMeetings: number;
  criticalAlertCount: number;
  pfeStatusDistribution: Record<string, number>;
  departmentProgressComparison: Record<string, number>;
}

const dashboardService = {
  getStudentDashboard: async (): Promise<DashboardStudentDTO> => {
    const res = await axiosInstance.get<ApiResponse<DashboardStudentDTO>>('/dashboard/student');
    return res.data.data;
  },

  getSupervisorDashboard: async (): Promise<DashboardSupervisorDTO> => {
    const res = await axiosInstance.get<ApiResponse<DashboardSupervisorDTO>>('/dashboard/supervisor');
    return res.data.data;
  },

  getDeptManagerDashboard: async (): Promise<DashboardDeptManagerDTO> => {
    const res = await axiosInstance.get<ApiResponse<DashboardDeptManagerDTO>>('/dashboard/dept-manager');
    return res.data.data;
  },

  getDirectorDashboard: async (): Promise<DashboardDirectorDTO> => {
    const res = await axiosInstance.get<ApiResponse<DashboardDirectorDTO>>('/dashboard/director');
    return res.data.data;
  },
};

export default dashboardService;
