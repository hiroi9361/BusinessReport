package analix.DHIT.service;

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

    //これ task_log 全消しメソッド
    public void deleteByReportId(int reportId){
        this.taskLogRepository.deleteByReportId(reportId);
    }
}
