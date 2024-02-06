package analix.DHIT.repository;

import analix.DHIT.input.TaskDetailInput;
import analix.DHIT.input.TaskHandoverCreateInput;
import analix.DHIT.input.TaskSearchInput;
import analix.DHIT.model.TaskLog;

import java.util.List;

public interface HandoverRepository {

    public void save(TaskHandoverCreateInput taskHandoverCreateInput);
    void updateByDeleteKey(boolean deleteKey, int reportId);
    List<TaskLog> selectTaskByAfter(int employeeCode);
    List<TaskLog> selectTaskByBefore(int employeeCode);
    int countByAfter(int employeeCode);
}

