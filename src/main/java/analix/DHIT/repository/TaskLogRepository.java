package analix.DHIT.repository;

import analix.DHIT.input.TaskDetailInput;
import analix.DHIT.input.TaskSearchInput;
import analix.DHIT.model.TaskLog;

import java.util.List;

public interface TaskLogRepository {
    List<TaskLog> selectByReportId(int reportId);

    List<TaskLog> selectIncompleteByReportId(int reportId);

    void save(TaskLog taskLog);

    void deleteByReportId(int reportId);

    int maxTask();

    int countByName(String name);

    void setCounter(TaskLog taskLog);

    List<TaskLog> selectByEmployeeCode(int employeeCode);

    List<TaskLog>taskListByName(String name);

    List<TaskLog> taskList(int employeeCode);

    List<TaskDetailInput> taskDetail(int sorting, int employeeCode);

    List<TaskLog>taskFilter(TaskSearchInput taskSearchInput);
}

