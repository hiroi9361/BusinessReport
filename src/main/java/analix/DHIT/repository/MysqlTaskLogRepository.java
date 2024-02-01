package analix.DHIT.repository;

import analix.DHIT.input.TaskDetailInput;
import analix.DHIT.input.TaskSearchInput;
import analix.DHIT.mapper.TaskLogMapper;
import analix.DHIT.model.Report;
import analix.DHIT.model.TaskLog;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MysqlTaskLogRepository implements TaskLogRepository {
    private final TaskLogMapper taskLogMapper;

    public MysqlTaskLogRepository(TaskLogMapper taskLogMapper) {
        this.taskLogMapper = taskLogMapper;
    }

    @Override
    public List<TaskLog> selectByReportId(int reportId) {
        return this.taskLogMapper.selectByReportId(reportId);
    }

    @Override
    public List<TaskLog> selectIncompleteByReportId(int reportId) {
        return this.taskLogMapper.selectIncompleteByReportId(reportId);
    }

    @Override
    public void save(TaskLog taskLog) {
        this.taskLogMapper.insertTaskLog(taskLog);
    }

    @Override
    public int countByName(String name){
        return this.taskLogMapper.countByName(name);
    }

    @Override
    public void setCounter(TaskLog taskLog){
        this.taskLogMapper.setCounter(taskLog);
    }

    @Override
    public List<TaskLog>taskListByName(String name){
        return this.taskLogMapper.taskListByName(name);
    }

    @Override
    public void deleteByReportId(int reportId) {
        this.taskLogMapper.deleteByReportId(reportId);
    }

    @Override
    public int maxTask(){
        return this.taskLogMapper.maxTask();
    }

    @Override
    public List<TaskLog> taskList(int employeeCode) {
        return this.taskLogMapper.tasklogList(employeeCode);
    }

    @Override
    public List<TaskDetailInput> taskDetail(int sorting, int employeeCode) {
        return this.taskLogMapper.taskDetail(sorting, employeeCode);
    }

    @Override
    public List<TaskLog>taskFilter(TaskSearchInput taskSearchInput) {
        return this.taskLogMapper.taskLogFilter(taskSearchInput);
    }

    @Override
    public List<TaskLog> selectByEmployeeCode(int employeeCode){
        return this.taskLogMapper.selectByEmployeeCode(employeeCode);
    }

}
