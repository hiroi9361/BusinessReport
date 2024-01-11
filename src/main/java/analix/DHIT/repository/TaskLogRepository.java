package analix.DHIT.repository;

import analix.DHIT.input.TaskDetailInput;
import analix.DHIT.model.TaskLog;

import java.util.List;

public interface TaskLogRepository {
    List<TaskLog> selectByReportId(int reportId);

    List<TaskLog> selectIncompleteByReportId(int reportId);

    void save(TaskLog taskLog);

    void deleteByReportId(int reportId);

    int maxTask();

    List<TaskLog> taskList(int employeeCode);

    List<TaskDetailInput> taskDetail(int sorting);
}

