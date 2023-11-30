package analix.DHIT.repository;

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
    public void deleteByReportId(int reportId) {
        this.taskLogMapper.deleteByReportId(reportId);
    }


}
