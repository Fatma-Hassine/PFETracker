export type PfeStatus =
  | 'INITIALIZED'
  | 'IN_PROGRESS'
  | 'LATE'
  | 'SUSPENDED'
  | 'FINISHED'
  | 'DEFENDED';

export type MilestoneStatus = 'NOT_STARTED' | 'IN_PROGRESS' | 'COMPLETED';

export type TaskStatus =
  | 'NOT_STARTED'
  | 'IN_PROGRESS'
  | 'SUBMITTED'
  | 'VALIDATED'
  | 'TO_CORRECT'
  | 'CANCELLED';

export type Priority = 'LOW' | 'NORMAL' | 'HIGH' | 'CRITICAL';

export type SprintStatus =
  | 'PLANNED'
  | 'IN_PROGRESS'
  | 'REVIEW_SUBMITTED'
  | 'VALIDATED'
  | 'CLOSED';

export type AiGenerationType =
  | 'TASKS'
  | 'USER_STORIES'
  | 'SPECIFICATIONS'
  | 'MILESTONES';

export interface Pfe {
  id: number;
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

  status: PfeStatus;
  progress: number;

  ficheValidationComment?: string;
}

export interface Milestone {
  id: number;
  orderIndex: number;

  title: string;
  description?: string;
  expectedDeliverable?: string;

  plannedStartDate?: string;
  plannedEndDate?: string;

  actualStartDate?: string;
  actualEndDate?: string;

  status: MilestoneStatus;
  progress: number;
  weight?: number;

  supervisorComment?: string;
}

export interface Task {
  id: number;
  title: string;
  description?: string;
  deadline?: string;

  priority: Priority;
  status: TaskStatus;

  estimatedHours?: number;
  progress?: number;
  assignedStudentId?: number;

  correctionNote?: string;
}

export interface Sprint {
  id: number;
  objective: string;
  startDate?: string;
  endDate?: string;
  studentReport?: string;
  supervisorComment?: string;
  status: SprintStatus;
}

export interface AiAlert {
  id: number;
  pfeId: number;
  studentId?: number;
  supervisorId?: number;
  type: string;
  severity: 'INFO' | 'WARNING' | 'CRITICAL';
  message: string;
  referenceType?: string;
  referenceId?: number;
  resolved: boolean;
}

export interface AiGeneratedItem {
  title: string;
  description?: string;

  priority: Priority;
  estimatedHours?: number;
  itemType: string;

  expectedDeliverable?: string;
  plannedStartDate?: string;
  plannedEndDate?: string;
  weight?: number;
}

export interface AiGenerationResponse {
  message: string;
  items: AiGeneratedItem[];
}

export interface PfeImportResult {
  totalRows: number;
  createdCount: number;
  skippedCount: number;
  errorCount: number;
  messages: string[];
}