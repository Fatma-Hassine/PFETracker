package com.pfetracker.service.module3;

import com.pfetracker.dto.module3.AlertDTO;
import com.pfetracker.dto.module3.DashboardStudentDTO;
import com.pfetracker.dto.module3.DashboardSupervisorDTO;
import com.pfetracker.dto.module3.MeetingDTO;
import com.pfetracker.dto.module3.StudentSummaryDTO;
import com.pfetracker.dto.module3.TaskSummaryDTO;
import com.pfetracker.entity.module3.Meeting;
import com.pfetracker.entity.module3.NotificationM3;
import com.pfetracker.entity.module3.PFE;
import com.pfetracker.entity.module3.Task;
import com.pfetracker.entity.module3.User;
import com.pfetracker.exception.module3.ResourceNotFoundException;
import com.pfetracker.mapper.module3.MeetingMapper;
import com.pfetracker.mapper.module3.NotificationMapper;
import com.pfetracker.repository.module3.MeetingRepository;
import com.pfetracker.repository.module3.NotificationRepositoryM3;
import com.pfetracker.repository.module3.PFERepository;
import com.pfetracker.repository.module3.TaskRepository;
import com.pfetracker.repository.module3.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final PFERepository pfeRepository;
    private final TaskRepository taskRepository;
    private final MeetingRepository meetingRepository;
    private final NotificationRepositoryM3 notificationRepository;
    private final UserRepository userRepository;
    private final MeetingMapper meetingMapper;
    private final NotificationMapper notificationMapper;

    public DashboardStudentDTO getStudentDashboard(Long studentId) {
        PFE pfe = pfeRepository.findByStudentId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun PFE trouvÃ© pour cet Ã©tudiant"));

        List<Task> allTasks = taskRepository.findByPfeId(pfe.getId());
        List<Task> ongoingTasks = allTasks.stream()
                .filter(t -> !t.getStatus().equals(Task.TaskStatus.VALIDATED) && 
                             !t.getStatus().equals(Task.TaskStatus.CANCELLED))
                .sorted(Comparator.comparing(Task::getDeadline, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());

        List<Task> upcomingDeadlines = ongoingTasks.stream()
                .filter(t -> t.getDeadline() != null && 
                            t.getDeadline().isAfter(LocalDateTime.now()) &&
                            t.getDeadline().isBefore(LocalDateTime.now().plusDays(7)))
                .collect(Collectors.toList());

        List<Meeting> upcomingMeetings = meetingRepository.findUpcomingByParticipant(studentId, LocalDateTime.now());
        MeetingDTO nextMeeting = upcomingMeetings.isEmpty() ? null : 
                meetingMapper.toDTO(upcomingMeetings.get(0));

        List<NotificationM3> recentNotifs = notificationRepository.findByUserId(studentId, 
                org.springframework.data.domain.PageRequest.of(0, 5, org.springframework.data.domain.Sort.by("createdAt").descending()))
                .getContent();

        List<AlertDTO> alerts = generateStudentAlerts(pfe, ongoingTasks);

        return DashboardStudentDTO.builder()
                .pfeId(pfe.getId())
                .pfeTitle(pfe.getTitle())
                .globalProgress(pfe.getGlobalProgress())
                .currentMilestone(getCurrentMilestone(allTasks))
                .nextMilestone(getNextMilestone(allTasks))
                .ongoingTasks(mapTasksToSummary(ongoingTasks))
                .upcomingDeadlines(mapTasksToSummary(upcomingDeadlines))
                .nextMeeting(nextMeeting)
                .recentNotifications(notificationMapper.toDTOList(recentNotifs))
                .activeAlerts(alerts)
                .build();
    }

    public DashboardSupervisorDTO getSupervisorDashboard(Long supervisorId) {
        List<PFE> activePFEs = pfeRepository.findActiveBySupervisorId(supervisorId);
        List<Long> pfeIds = activePFEs.stream().map(PFE::getId).collect(Collectors.toList());
        List<Long> studentIds = activePFEs.stream().map(PFE::getStudentId).collect(Collectors.toList());

        List<User> students = userRepository.findByIds(studentIds);
        List<Task> pendingValidations = taskRepository.findPendingValidations(pfeIds);
        List<Meeting> upcomingMeetings = meetingRepository.findUpcomingByPfeIds(pfeIds, LocalDateTime.now());

        List<StudentSummaryDTO> studentSummaries = activePFEs.stream()
                .map(pfe -> {
                    User student = students.stream()
                            .filter(u -> u.getId().equals(pfe.getStudentId()))
                            .findFirst().orElse(null);
                    List<Task> studentTasks = taskRepository.findByPfeId(pfe.getId());
                    long overdue = studentTasks.stream()
                            .filter(t -> t.getDeadline() != null && 
                                         t.getDeadline().isBefore(LocalDateTime.now()) &&
                                         !t.getStatus().equals(Task.TaskStatus.VALIDATED) &&
                                         !t.getStatus().equals(Task.TaskStatus.CANCELLED))
                            .count();

                    return StudentSummaryDTO.builder()
                            .studentId(pfe.getStudentId())
                            .studentName(student != null ? student.getFullName() : "Inconnu")
                            .studentEmail(student != null ? student.getEmail() : "")
                            .pfeId(pfe.getId())
                            .pfeTitle(pfe.getTitle())
                            .pfeProgress(pfe.getGlobalProgress())
                            .pfeStatus(pfe.getStatus().toString())
                            .pendingTasks((int) studentTasks.stream()
                                    .filter(t -> !t.getStatus().equals(Task.TaskStatus.VALIDATED) &&
                                               !t.getStatus().equals(Task.TaskStatus.CANCELLED))
                                    .count())
                            .overdueTasks((int) overdue)
                            .isInactive(isStudentInactive(pfe.getId()))
                            .build();
                })
                .collect(Collectors.toList());

        double avgProgress = activePFEs.isEmpty() ? 0.0 :
                activePFEs.stream().mapToDouble(PFE::getGlobalProgress).average().orElse(0.0);

        List<AlertDTO> alerts = generateSupervisorAlerts(activePFEs, studentSummaries);

        List<NotificationM3> recentNotifs = notificationRepository.findByUserId(supervisorId,
                org.springframework.data.domain.PageRequest.of(0, 5, org.springframework.data.domain.Sort.by("createdAt").descending()))
                .getContent();

        return DashboardSupervisorDTO.builder()
                .supervisorId(supervisorId)
                .totalStudents(activePFEs.size())
                .activePFEs(activePFEs.size())
                .averageProgress(Math.round(avgProgress * 100.0) / 100.0)
                .students(studentSummaries)
                .pendingValidations(mapTasksToSummary(pendingValidations))
                .upcomingMeetings(upcomingMeetings.stream()
                        .map((Meeting m) -> meetingMapper.toDTO(m))
                        .collect(Collectors.<MeetingDTO>toList()))
                .priorityAlerts(alerts)
                .recentNotifications(notificationMapper.toDTOList(recentNotifs))
                .build();
    }

    private List<AlertDTO> generateStudentAlerts(PFE pfe, List<Task> tasks) {
        List<AlertDTO> alerts = new ArrayList<>();

        long overdueCount = tasks.stream()
                .filter(t -> t.getDeadline() != null && t.getDeadline().isBefore(LocalDateTime.now()) &&
                            !t.getStatus().equals(Task.TaskStatus.VALIDATED))
                .count();

        if (overdueCount > 0) {
            alerts.add(AlertDTO.builder()
                    .type("OVERDUE_TASKS")
                    .severity("HIGH")
                    .message(overdueCount + " tÃ¢che(s) en retard")
                    .relatedId(pfe.getId())
                    .relatedType("PFE")
                    .actionUrl("/tasks")
                    .build());
        }

        if (pfe.getGlobalProgress() < 30.0 && pfe.getStatus().equals(PFE.PFEStatus.IN_PROGRESS)) {
            alerts.add(AlertDTO.builder()
                    .type("LOW_PROGRESS")
                    .severity("MEDIUM")
                    .message("Progression faible: " + pfe.getGlobalProgress() + "%")
                    .relatedId(pfe.getId())
                    .relatedType("PFE")
                    .actionUrl("/dashboard")
                    .build());
        }

        return alerts;
    }

    private List<AlertDTO> generateSupervisorAlerts(List<PFE> pfes, List<StudentSummaryDTO> students) {
        List<AlertDTO> alerts = new ArrayList<>();

        long inactiveCount = students.stream().filter(StudentSummaryDTO::getIsInactive).count();
        if (inactiveCount > 0) {
            alerts.add(AlertDTO.builder()
                    .type("INACTIVE_STUDENTS")
                    .severity("HIGH")
                    .message(inactiveCount + " Ã©tudiant(s) sans activitÃ© depuis 7 jours")
                    .actionUrl("/dashboard")
                    .build());
        }

        long delayedCount = pfes.stream()
                .filter(p -> p.getStatus().equals(PFE.PFEStatus.DELAYED))
                .count();
        if (delayedCount > 0) {
            alerts.add(AlertDTO.builder()
                    .type("DELAYED_PFES")
                    .severity("HIGH")
                    .message(delayedCount + " PFE en retard")
                    .actionUrl("/dashboard")
                    .build());
        }

        return alerts;
    }

    private boolean isStudentInactive(Long pfeId) {
        List<Task> tasks = taskRepository.findByPfeId(pfeId);
        return tasks.stream()
                .allMatch(t -> t.getStatus().equals(Task.TaskStatus.NOT_STARTED) ||
                              (t.getStatus().equals(Task.TaskStatus.VALIDATED)));
    }

    private String getCurrentMilestone(List<Task> tasks) {
        return tasks.stream()
                .filter(t -> !t.getStatus().equals(Task.TaskStatus.VALIDATED) &&
                           !t.getStatus().equals(Task.TaskStatus.CANCELLED))
                .findFirst()
                .map(Task::getTitle)
                .orElse("Aucune tÃ¢che en cours");
    }

    private String getNextMilestone(List<Task> tasks) {
        List<Task> pending = tasks.stream()
                .filter(t -> t.getStatus().equals(Task.TaskStatus.NOT_STARTED))
                .collect(Collectors.toList());
        return pending.isEmpty() ? "Toutes les tÃ¢ches sont terminÃ©es" : pending.get(0).getTitle();
    }

    private List<TaskSummaryDTO> mapTasksToSummary(List<Task> tasks) {
        return tasks.stream()
                .map(t -> TaskSummaryDTO.builder()
                        .id(t.getId())
                        .title(t.getTitle())
                        .status(t.getStatus().toString())
                        .deadline(t.getDeadline())
                        .isOverdue(t.getDeadline() != null && t.getDeadline().isBefore(LocalDateTime.now()) &&
                                  !t.getStatus().equals(Task.TaskStatus.VALIDATED))
                        .completionPercentage(t.getCompletionPercentage())
                        .pfeId(t.getPfeId())
                        .build())
                .collect(Collectors.toList());
    }

    public com.pfetracker.dto.module3.DashboardDeptManagerDTO getDeptManagerDashboard(Long managerId) {
        // Implement department manager dashboard
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found with ID: " + managerId));
        
        Long departmentId = manager.getDepartmentId();
        
        // Get all users and filter by department and student role
        List<User> allUsers = userRepository.findAll();
        List<User> departmentStudents = allUsers.stream()
                .filter(u -> departmentId != null && departmentId.equals(u.getDepartmentId()))
                .filter(u -> "STUDENT".equals(u.getRole().toString()))
                .collect(Collectors.toList());
        
        int totalStudents = departmentStudents.size();
        
        // Get all PFEs and filter by department students
        List<PFE> allPFEs = pfeRepository.findAll();
        java.util.Set<Long> deptStudentIds = departmentStudents.stream()
                .map(User::getId)
                .collect(Collectors.toSet());
        
        List<PFE> departmentPFEs = allPFEs.stream()
                .filter(p -> deptStudentIds.contains(p.getStudentId()))
                .collect(Collectors.toList());
        
        // Calculate statistics
        int activePFEs = (int) departmentPFEs.stream()
                .filter(p -> p.getStatus() == PFE.PFEStatus.IN_PROGRESS)
                .count();
        
        int completedPFEs = (int) departmentPFEs.stream()
                .filter(p -> p.getStatus() == PFE.PFEStatus.COMPLETED || p.getStatus() == PFE.PFEStatus.DEFENDED)
                .count();
        
        int delayedPFEs = (int) departmentPFEs.stream()
                .filter(p -> p.getStatus() == PFE.PFEStatus.DELAYED)
                .count();
        
        double averageProgress = departmentPFEs.isEmpty() ? 0.0 :
                departmentPFEs.stream()
                        .mapToDouble(PFE::getGlobalProgress)
                        .average()
                        .orElse(0.0);
        
        // Get meeting statistics
        List<Meeting> allMeetings = meetingRepository.findAll();
        
        // Create mapping of PFE ID to Student ID for department
        java.util.Set<Long> deptPfeIds = departmentPFEs.stream()
                .map(PFE::getId)
                .collect(Collectors.toSet());
        
        List<Meeting> departmentMeetings = allMeetings.stream()
                .filter(m -> deptPfeIds.contains(m.getPfeId()))
                .collect(Collectors.toList());
        
        long totalMeetings = departmentMeetings.size();
        long completedMeetings = departmentMeetings.stream()
                .filter(m -> m.getStatus() == Meeting.MeetingStatus.COMPLETED)
                .count();
        
        return com.pfetracker.dto.module3.DashboardDeptManagerDTO.builder()
                .managerId(managerId)
                .departmentName(departmentId != null ? "Department-" + departmentId : "Unknown")
                .totalStudents(totalStudents)
                .activePFEs(activePFEs)
                .completedPFEs(completedPFEs)
                .delayedPFEs(delayedPFEs)
                .averageProgress(Math.round(averageProgress * 100.0) / 100.0)
                .totalMeetings(totalMeetings)
                .completedMeetings(completedMeetings)
                .overallInactiveStudents(0) // Simplified: User entity doesn't have lastLogin
                .build();
    }

    public com.pfetracker.dto.module3.DashboardDirectorDTO getDirectorDashboard(Long directorId) {
        // Implement director dashboard with system-wide statistics
        
        // Get all users
        List<User> allUsers = userRepository.findAll();
        
        // Get all departments (unique departmentIds)
        java.util.Set<Long> uniqueDepartmentsSet = allUsers.stream()
                .map(User::getDepartmentId)
                .filter(deptId -> deptId != null)
                .collect(Collectors.toSet());
        
        int totalDepartments = uniqueDepartmentsSet.size();
        
        // Get all students
        List<User> allStudents = allUsers.stream()
                .filter(u -> "STUDENT".equals(u.getRole().toString()))
                .collect(Collectors.toList());
        
        int totalStudents = allStudents.size();
        
        // Get all PFEs
        List<PFE> allPFEs = pfeRepository.findAll();
        int totalPFEs = allPFEs.size();
        
        int completedPFEs = (int) allPFEs.stream()
                .filter(p -> p.getStatus() == PFE.PFEStatus.COMPLETED || p.getStatus() == PFE.PFEStatus.DEFENDED)
                .count();
        
        int delayedPFEs = (int) allPFEs.stream()
                .filter(p -> p.getStatus() == PFE.PFEStatus.DELAYED)
                .count();
        
        double globalAverageProgress = allPFEs.isEmpty() ? 0.0 :
                allPFEs.stream()
                        .mapToDouble(PFE::getGlobalProgress)
                        .average()
                        .orElse(0.0);
        
        // Get all meetings
        List<Meeting> allMeetings = meetingRepository.findAll();
        long totalMeetings = allMeetings.size();
        long completedMeetings = allMeetings.stream()
                .filter(m -> m.getStatus() == Meeting.MeetingStatus.COMPLETED)
                .count();
        
        // Calculate PFE status distribution
        Map<String, Long> pfeStatusDistribution = allPFEs.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getStatus().toString(),
                        Collectors.counting()
                ));
        
        // Calculate department progress comparison
        Map<String, Double> departmentProgressComparison = new HashMap<>();
        java.util.Set<Long> studentIds = allStudents.stream()
                .map(User::getId)
                .collect(Collectors.toSet());
        
        for (Long deptId : uniqueDepartmentsSet) {
            java.util.Set<Long> deptStudentIds = allStudents.stream()
                    .filter(s -> deptId.equals(s.getDepartmentId()))
                    .map(User::getId)
                    .collect(Collectors.toSet());
            
            double avgProgress = allPFEs.stream()
                    .filter(p -> deptStudentIds.contains(p.getStudentId()))
                    .mapToDouble(PFE::getGlobalProgress)
                    .average()
                    .orElse(0.0);
            
            departmentProgressComparison.put("Dept-" + deptId, Math.round(avgProgress * 100.0) / 100.0);
        }
        
        return com.pfetracker.dto.module3.DashboardDirectorDTO.builder()
                .directorId(directorId)
                .totalDepartments(totalDepartments)
                .totalStudents(totalStudents)
                .totalPFEs(totalPFEs)
                .completedPFEs(completedPFEs)
                .delayedPFEs(delayedPFEs)
                .globalAverageProgress(Math.round(globalAverageProgress * 100.0) / 100.0)
                .totalMeetings(totalMeetings)
                .completedMeetings(completedMeetings)
                .criticalAlertCount((int) allPFEs.stream()
                        .filter(p -> p.getStatus() == PFE.PFEStatus.DELAYED || 
                               p.getStatus() == PFE.PFEStatus.SUSPENDED)
                        .count())
                .pfeStatusDistribution(pfeStatusDistribution)
                .departmentProgressComparison(departmentProgressComparison)
                .build();
    }
}

