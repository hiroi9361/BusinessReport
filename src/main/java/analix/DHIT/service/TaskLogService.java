package analix.DHIT.service;

import analix.DHIT.input.TaskDetailInput;
import analix.DHIT.input.TaskSearchInput;
import analix.DHIT.model.TaskLog;
import analix.DHIT.repository.TaskLogRepository;
import org.springframework.scheduling.config.Task;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskLogService {
    private final TaskLogRepository taskLogRepository;

    public TaskLogService(
            TaskLogRepository taskLogRepository
    ) {
        this.taskLogRepository = taskLogRepository;
    }
    public List<TaskLog> getTaskLogsByReportId(int reportId)
    {
        List<TaskLog> taskLogs = this.taskLogRepository.selectByReportId(reportId);
        if (taskLogs == null) {
            return new ArrayList<>();
        }
        return taskLogs;
    }

    public List<TaskLog> getIncompleteTaskLogsByReportId(int reportId)
    {
        List<TaskLog> taskLogs = this.taskLogRepository.selectIncompleteByReportId(reportId);
        if (taskLogs == null) {
            return new ArrayList<>();
        }
        return taskLogs;
    }

    public void create(TaskLog taskLog){
        this.taskLogRepository.save(taskLog);
    }

    public boolean countName(String name){
        int count = this.taskLogRepository.countByName(name);
        return count > 0;
    }

    public void setCounter(TaskLog taskLog){
        this.taskLogRepository.setCounter(taskLog);
    }

    public List<TaskLog>taskListByName(String name){
        return this.taskLogRepository.taskListByName(name);
    }

    //これ task_log 全消しメソッド
    public void deleteByReportId(int reportId){
        this.taskLogRepository.deleteByReportId(reportId);
    }

    public int maxTask() {
        return this.taskLogRepository.maxTask();
    }

    public List<TaskLog> taskList(int employeeCode)
    {
        List<TaskLog> taskLogs = this.taskLogRepository.taskList(employeeCode);
        return taskLogs;
    }

    public List<TaskDetailInput> taskDetail(int sorting, int employeeCode) {
        List<TaskDetailInput> taskDetailInput = this.taskLogRepository.taskDetail(sorting, employeeCode);
        return taskDetailInput;
    }

    public List<TaskLog>taskFilter(TaskSearchInput taskSearchInput) {
        List<TaskLog>taskLogsFilter = this.taskLogRepository.taskFilter(taskSearchInput);
        return taskLogsFilter;
    }

    public List<TaskLog> selectByEmployeeCode(int employeeCode){
        return this.taskLogRepository.selectByEmployeeCode(employeeCode);
    }

    public List<TaskLog> selectBySorting(int sorting){
        return this.taskLogRepository.selectBySorting(sorting);
    }
}
