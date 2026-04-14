package com.smartarchive.workflow.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartarchive.archivemanage.domain.TransferApplication;
import com.smartarchive.archivemanage.domain.TransferApplicationDetail;
import com.smartarchive.archivemanage.mapper.TransferApplicationDetailMapper;
import com.smartarchive.archivemanage.mapper.TransferApplicationExtMapper;
import com.smartarchive.archivemanage.mapper.TransferApplicationMapper;
import com.smartarchive.workflow.domain.WorkflowInstance;
import com.smartarchive.workflow.domain.WorkflowTask;
import com.smartarchive.workflow.dto.CompleteTaskCommand;
import com.smartarchive.workflow.dto.DelegateTaskCommand;
import com.smartarchive.workflow.dto.MergeProcessesCommand;
import com.smartarchive.workflow.dto.RejectTaskCommand;
import com.smartarchive.workflow.dto.SplitProcessCommand;
import com.smartarchive.workflow.dto.StartProcessCommand;
import com.smartarchive.workflow.dto.WorkflowTransferDetailResponse;
import com.smartarchive.workflow.mapper.WorkflowInstanceMapper;
import com.smartarchive.workflow.mapper.WorkflowTaskMapper;
import com.smartarchive.archivemanage.support.TransferApplicationWorkflowHook;
import com.smartarchive.workflow.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkflowServiceImpl implements WorkflowService {

    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final JdbcTemplate jdbcTemplate;
    private final WorkflowInstanceMapper workflowInstanceMapper;
    private final WorkflowTaskMapper workflowTaskMapper;
    private final ObjectMapper objectMapper;
    private final TransferApplicationWorkflowHook transferApplicationWorkflowHook;
    private final TransferApplicationMapper transferApplicationMapper;
    private final TransferApplicationDetailMapper transferApplicationDetailMapper;
    private final TransferApplicationExtMapper transferApplicationExtMapper;

    @Override
    @Transactional
    public WorkflowInstance startProcess(StartProcessCommand command) {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                command.getProcessDefinitionKey(),
                command.getBusinessKey(),
                command.getVariables()
        );

        WorkflowInstance workflowInstance = new WorkflowInstance();
        workflowInstance.setProcessInstanceId(processInstance.getId());
        workflowInstance.setBusinessKey(processInstance.getBusinessKey());
        workflowInstance.setProcessDefinitionId(processInstance.getProcessDefinitionId());
        workflowInstance.setProcessDefinitionKey(processInstance.getProcessDefinitionKey());
        workflowInstance.setStatus("RUNNING");
        workflowInstance.setBusinessType(command.getBusinessType());
        workflowInstance.setBusinessId(command.getBusinessId());
        workflowInstance.setInitiatorId(command.getInitiatorId());
        workflowInstance.setInitiatorName(command.getInitiatorName());
        workflowInstance.setStartTime(LocalDateTime.now());
        try {
            workflowInstance.setVariables(objectMapper.writeValueAsString(command.getVariables()));
        } catch (Exception e) {
            workflowInstance.setVariables("{}");
        }
        workflowInstance.setDeleteFlag("N");
        workflowInstance.setCreatedBy(1L);
        workflowInstance.setCreationDate(LocalDateTime.now());
        workflowInstance.setLastUpdatedBy(1L);
        workflowInstance.setLastUpdateDate(LocalDateTime.now());

        workflowInstanceMapper.insert(workflowInstance);
        return workflowInstance;
    }

    @Override
    @Transactional
    public void completeTask(CompleteTaskCommand command) {
        Task currentTask = taskService.createTaskQuery()
                .taskId(command.getTaskId())
                .singleResult();
        String processInstanceId = currentTask == null ? null : currentTask.getProcessInstanceId();

        taskService.complete(command.getTaskId(), command.getVariables());
        finalizeProcessInstance(processInstanceId, "APPROVED");
    }

    @Override
    @Transactional
    public void rejectTask(RejectTaskCommand command) {
        Task currentTask = taskService.createTaskQuery()
                .taskId(command.getTaskId())
                .singleResult();
        String processInstanceId = currentTask == null ? null : currentTask.getProcessInstanceId();

        taskService.complete(command.getTaskId(), Map.of(
                "rejected", true,
                "rejectReason", command.getReason() == null ? "" : command.getReason()
        ));
        finalizeProcessInstance(processInstanceId, "REJECTED");
    }

    @Override
    @Transactional
    public void delegateTask(DelegateTaskCommand command) {
        taskService.delegateTask(command.getTaskId(), command.getAssignee());
    }

    @Override
    public List<WorkflowTask> getMyTasks(String userId) {
        List<Task> tasks = taskService.createTaskQuery()
                .taskAssignee(userId)
                .active()
                .list();

        if (tasks.isEmpty()) {
            return loadRuntimeTasksByAssignee(userId);
        }

        return tasks.stream().map(task -> {
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId())
                    .singleResult();
            WorkflowTask workflowTask = new WorkflowTask();
            workflowTask.setTaskId(task.getId());
            workflowTask.setProcessInstanceId(task.getProcessInstanceId());
            workflowTask.setTaskDefinitionKey(task.getTaskDefinitionKey());
            workflowTask.setTaskName(task.getName());
            workflowTask.setAssignee(task.getAssignee());
            workflowTask.setStatus("ACTIVE");
            workflowTask.setBusinessKey(processInstance == null ? null : processInstance.getBusinessKey());
            workflowTask.setCreateTime(task.getCreateTime() == null
                    ? null
                    : LocalDateTime.ofInstant(task.getCreateTime().toInstant(), ZoneId.systemDefault()));
            return workflowTask;
        }).collect(Collectors.toList());
    }

    @Override
    public List<WorkflowInstance> getMyProcesses(String userId) {
        return workflowInstanceMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<WorkflowInstance>()
                        .eq(WorkflowInstance::getInitiatorId, userId)
                        .orderByDesc(WorkflowInstance::getLastUpdateDate)
                        .eq(WorkflowInstance::getDeleteFlag, "N")
        );
    }

    @Override
    public List<WorkflowInstance> listProcesses(String processDefinitionKey) {
        var wrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<WorkflowInstance>()
                .orderByDesc(WorkflowInstance::getLastUpdateDate)
                .eq(WorkflowInstance::getDeleteFlag, "N");
        if (processDefinitionKey != null && !processDefinitionKey.isBlank()) {
            wrapper.eq(WorkflowInstance::getProcessDefinitionKey, processDefinitionKey);
        }
        return workflowInstanceMapper.selectList(wrapper);
    }

    @Override
    public List<WorkflowInstance> getMyParticipatedProcesses(String userId) {
        List<String> processInstanceIds = jdbcTemplate.queryForList("""
                select distinct proc_inst_id_
                  from (
                        select proc_inst_id_, assignee_
                          from act_ru_task
                        union all
                        select proc_inst_id_, assignee_
                          from act_hi_taskinst
                    ) task_union
                 where assignee_ = ?
                """, String.class, userId);

        if (processInstanceIds.isEmpty()) {
            return List.of();
        }

        return workflowInstanceMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<WorkflowInstance>()
                        .in(WorkflowInstance::getProcessInstanceId, processInstanceIds)
                        .orderByDesc(WorkflowInstance::getLastUpdateDate)
                        .eq(WorkflowInstance::getDeleteFlag, "N")
        );
    }

    @Override
    @Transactional
    public List<WorkflowInstance> splitProcess(SplitProcessCommand command) {
        return command.getChildBusinessKeys().stream().map(childBusinessKey -> {
            StartProcessCommand startCommand = new StartProcessCommand();
            startCommand.setProcessDefinitionKey(command.getChildProcessDefinitionKey());
            startCommand.setBusinessKey(childBusinessKey);
            startCommand.setBusinessType(command.getBusinessType());
            startCommand.setBusinessId(command.getBusinessId());
            startCommand.setInitiatorId(command.getInitiatorId());
            startCommand.setInitiatorName(command.getInitiatorName());
            startCommand.setVariables(command.getVariables());
            return startProcess(startCommand);
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public WorkflowInstance mergeProcesses(MergeProcessesCommand command) {
        StartProcessCommand startCommand = new StartProcessCommand();
        startCommand.setProcessDefinitionKey(command.getParentProcessDefinitionKey());
        startCommand.setBusinessKey(command.getParentBusinessKey());
        startCommand.setBusinessType(command.getBusinessType());
        startCommand.setBusinessId(command.getBusinessId());
        startCommand.setInitiatorId(command.getInitiatorId());
        startCommand.setInitiatorName(command.getInitiatorName());
        startCommand.setVariables(command.getVariables());
        return startProcess(startCommand);
    }

    @Override
    public WorkflowInstance getProcessInstance(String processInstanceId) {
        return workflowInstanceMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<WorkflowInstance>()
                        .eq(WorkflowInstance::getProcessInstanceId, processInstanceId)
                        .eq(WorkflowInstance::getDeleteFlag, "N")
        );
    }

    @Override
    public WorkflowTask getTask(String taskId) {
        List<WorkflowTask> runtimeTasks = jdbcTemplate.query("""
                select t.id_,
                       t.proc_inst_id_,
                       t.task_def_key_,
                       t.name_,
                       t.assignee_,
                       t.create_time_,
                       p.business_key_
                  from act_ru_task t
                  left join act_ru_execution p on p.proc_inst_id_ = t.proc_inst_id_ and p.parent_id_ is null
                 where t.id_ = ?
                """,
                (rs, rowNum) -> mapRuntimeTask(
                        rs.getString("id_"),
                        rs.getString("proc_inst_id_"),
                        rs.getString("task_def_key_"),
                        rs.getString("name_"),
                        rs.getString("assignee_"),
                        rs.getString("business_key_"),
                        rs.getTimestamp("create_time_")
                ),
                taskId
        );
        if (!runtimeTasks.isEmpty()) {
            return runtimeTasks.get(0);
        }

        List<WorkflowTask> historyTasks = jdbcTemplate.query("""
                select t.id_,
                       t.proc_inst_id_,
                       t.task_def_key_,
                       t.name_,
                       t.assignee_,
                       t.start_time_,
                       t.end_time_,
                       p.business_key_
                  from act_hi_taskinst t
                  left join act_hi_procinst p on p.proc_inst_id_ = t.proc_inst_id_
                 where t.id_ = ?
                """,
                (rs, rowNum) -> mapHistoricTask(
                        rs.getString("id_"),
                        rs.getString("proc_inst_id_"),
                        rs.getString("task_def_key_"),
                        rs.getString("name_"),
                        rs.getString("assignee_"),
                        rs.getString("business_key_"),
                        rs.getTimestamp("start_time_"),
                        rs.getTimestamp("end_time_")
                ),
                taskId
        );

        return historyTasks.isEmpty() ? null : historyTasks.get(0);
    }

    @Override
    public List<WorkflowTask> getProcessTasks(String processInstanceId) {
        return jdbcTemplate.query("""
                select id_,
                       proc_inst_id_,
                       task_def_key_,
                       name_,
                       assignee_,
                       business_key_,
                       create_time_,
                       complete_time_,
                       status
                  from (
                        select t.id_,
                               t.proc_inst_id_,
                               t.task_def_key_,
                               t.name_,
                               t.assignee_,
                               p.business_key_,
                               t.create_time_,
                               cast(null as timestamp) as complete_time_,
                               'ACTIVE' as status,
                               0 as sort_order
                          from act_ru_task t
                          left join act_ru_execution p on p.proc_inst_id_ = t.proc_inst_id_ and p.parent_id_ is null
                         where t.proc_inst_id_ = ?
                        union all
                        select t.id_,
                               t.proc_inst_id_,
                               t.task_def_key_,
                               t.name_,
                               t.assignee_,
                               p.business_key_,
                               t.start_time_ as create_time_,
                               t.end_time_ as complete_time_,
                               case when t.end_time_ is null then 'ACTIVE' else 'COMPLETED' end as status,
                               1 as sort_order
                          from act_hi_taskinst t
                          left join act_hi_procinst p on p.proc_inst_id_ = t.proc_inst_id_
                         where t.proc_inst_id_ = ?
                    ) task_list
                 order by sort_order asc, create_time_ desc
                """,
                (rs, rowNum) -> {
                    WorkflowTask task = new WorkflowTask();
                    task.setTaskId(rs.getString("id_"));
                    task.setProcessInstanceId(rs.getString("proc_inst_id_"));
                    task.setTaskDefinitionKey(rs.getString("task_def_key_"));
                    task.setTaskName(rs.getString("name_"));
                    task.setAssignee(rs.getString("assignee_"));
                    task.setStatus(rs.getString("status"));
                    task.setBusinessKey(rs.getString("business_key_"));
                    Timestamp createTime = rs.getTimestamp("create_time_");
                    if (createTime != null) {
                        task.setCreateTime(createTime.toLocalDateTime());
                    }
                    Timestamp completeTime = rs.getTimestamp("complete_time_");
                    if (completeTime != null) {
                        task.setCompleteTime(completeTime.toLocalDateTime());
                    }
                    return task;
                },
                processInstanceId,
                processInstanceId
        );
    }

    @Override
    public WorkflowTransferDetailResponse getTransferDetail(String processInstanceId) {
        WorkflowTransferDetailResponse response = new WorkflowTransferDetailResponse();
        WorkflowInstance instance = workflowInstanceMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<WorkflowInstance>()
                        .eq(WorkflowInstance::getProcessInstanceId, processInstanceId)
                        .eq(WorkflowInstance::getDeleteFlag, "N")
                        .last("limit 1")
        );
        if (instance == null || !"TRANSFER_APPLICATION".equals(instance.getBusinessType())) {
            return response;
        }
        Long applicationId = parseTransferApplicationId(instance.getBusinessKey());
        if (applicationId == null) {
            return response;
        }
        TransferApplication application = transferApplicationMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<TransferApplication>()
                        .eq(TransferApplication::getApplicationId, applicationId)
                        .eq(TransferApplication::getDeleteFlag, "N")
                        .last("limit 1")
        );
        if (application == null) {
            return response;
        }

        String initiatorName = instance.getInitiatorName();
        if (initiatorName == null || initiatorName.isBlank()) {
            initiatorName = application.getApplicant() == null ? "" : "用户-" + application.getApplicant();
        }
        response.setTransferorName(initiatorName);
        response.setAssigneeId(application.getDocumentRecipient() == null ? "" : String.valueOf(application.getDocumentRecipient()));
        response.setTransferMethod(application.getApplyMethod());
        response.setLogisticsCompany(application.getExpressType());
        response.setTrackingNumber(application.getExpressNumber());
        response.setRemark(application.getApplicationDescription());
        response.setApplicationNumber(application.getApplicationNumber());

        List<TransferApplicationDetail> details = transferApplicationDetailMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<TransferApplicationDetail>()
                        .eq(TransferApplicationDetail::getApplicationId, applicationId)
                        .eq(TransferApplicationDetail::getDeleteFlag, "N")
                        .orderByAsc(TransferApplicationDetail::getApplicationDetailId)
        );

        List<Map<String, Object>> extRows = transferApplicationExtMapper.selectByMasterId(applicationId, application.getTenantid());
        Map<Long, Map<String, String>> extByDetailId = new HashMap<>();
        for (Map<String, Object> row : extRows) {
            Object detailIdObj = row.get("object_id");
            if (!(detailIdObj instanceof Number detailIdNumber)) {
                continue;
            }
            Long detailId = detailIdNumber.longValue();
            Map<String, String> extFields = extByDetailId.computeIfAbsent(detailId, key -> new HashMap<>());
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                String key = entry.getKey();
                if (key == null || !key.startsWith("attr")) {
                    continue;
                }
                Object value = entry.getValue();
                if (value != null) {
                    extFields.put(key, String.valueOf(value));
                }
            }
        }

        List<WorkflowTransferDetailResponse.TransferDocumentItem> documents = details.stream().map(detail -> {
            WorkflowTransferDetailResponse.TransferDocumentItem item =
                    new WorkflowTransferDetailResponse.TransferDocumentItem();
            item.setBusiModuleCode(application.getBusiModuleCode());
            item.setBusinessCode(detail.getDocBusiNo());
            item.setDocumentOrganizationCode(detail.getCompanyProjectCode());
            item.setExtFields(extByDetailId.getOrDefault(detail.getApplicationDetailId(), Map.of()));
            return item;
        }).toList();
        response.setDocuments(documents);
        return response;
    }

    private List<WorkflowTask> loadRuntimeTasksByAssignee(String userId) {
        return jdbcTemplate.query("""
                select t.id_,
                       t.proc_inst_id_,
                       t.task_def_key_,
                       t.name_,
                       t.assignee_,
                       t.create_time_,
                       p.business_key_
                  from act_ru_task t
                  left join act_ru_execution p on p.proc_inst_id_ = t.proc_inst_id_ and p.parent_id_ is null
                 where t.assignee_ = ?
                 order by t.create_time_ desc
                """,
                (rs, rowNum) -> mapRuntimeTask(
                        rs.getString("id_"),
                        rs.getString("proc_inst_id_"),
                        rs.getString("task_def_key_"),
                        rs.getString("name_"),
                        rs.getString("assignee_"),
                        rs.getString("business_key_"),
                        rs.getTimestamp("create_time_")
                ),
                userId
        );
    }

    private WorkflowTask mapRuntimeTask(String taskId, String processInstanceId, String taskDefinitionKey,
                                        String taskName, String assignee, String businessKey, Timestamp createTime) {
        WorkflowTask workflowTask = new WorkflowTask();
        workflowTask.setTaskId(taskId);
        workflowTask.setProcessInstanceId(processInstanceId);
        workflowTask.setTaskDefinitionKey(taskDefinitionKey);
        workflowTask.setTaskName(taskName);
        workflowTask.setAssignee(assignee);
        workflowTask.setStatus("ACTIVE");
        workflowTask.setBusinessKey(businessKey);
        if (createTime != null) {
            workflowTask.setCreateTime(createTime.toLocalDateTime());
        }
        return workflowTask;
    }

    private WorkflowTask mapHistoricTask(String taskId, String processInstanceId, String taskDefinitionKey,
                                         String taskName, String assignee, String businessKey,
                                         Timestamp startTime, Timestamp endTime) {
        WorkflowTask workflowTask = new WorkflowTask();
        workflowTask.setTaskId(taskId);
        workflowTask.setProcessInstanceId(processInstanceId);
        workflowTask.setTaskDefinitionKey(taskDefinitionKey);
        workflowTask.setTaskName(taskName);
        workflowTask.setAssignee(assignee);
        workflowTask.setStatus(endTime == null ? "ACTIVE" : "COMPLETED");
        workflowTask.setBusinessKey(businessKey);
        if (startTime != null) {
            workflowTask.setCreateTime(startTime.toLocalDateTime());
        }
        if (endTime != null) {
            workflowTask.setCompleteTime(endTime.toLocalDateTime());
        }
        return workflowTask;
    }

    private Long parseTransferApplicationId(String businessKey) {
        final String prefix = "TRN-APP-";
        if (businessKey == null || !businessKey.startsWith(prefix)) {
            return null;
        }
        try {
            return Long.valueOf(businessKey.substring(prefix.length()));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private void finalizeProcessInstance(String processInstanceId, String finalStatus) {
        if (processInstanceId == null) {
            return;
        }

        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        if (processInstance != null) {
            return;
        }

        WorkflowInstance workflowInstance = workflowInstanceMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<WorkflowInstance>()
                        .eq(WorkflowInstance::getProcessInstanceId, processInstanceId)
                        .eq(WorkflowInstance::getDeleteFlag, "N")
        );
        if (workflowInstance == null) {
            return;
        }

        transferApplicationWorkflowHook.onProcessFinished(workflowInstance, finalStatus);

        workflowInstance.setStatus(finalStatus);
        workflowInstance.setEndTime(LocalDateTime.now());
        workflowInstance.setLastUpdatedBy(1L);
        workflowInstance.setLastUpdateDate(LocalDateTime.now());
        workflowInstanceMapper.updateById(workflowInstance);
    }
}
