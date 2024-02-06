package analix.DHIT.repository;

import analix.DHIT.input.TaskDetailInput;
import analix.DHIT.input.TaskHandoverCreateInput;
import analix.DHIT.input.TaskSearchInput;
import analix.DHIT.mapper.HandoverMapper;
import analix.DHIT.mapper.TaskLogMapper;
import analix.DHIT.model.TaskLog;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MysqlHandoverRepository implements HandoverRepository {
    private final HandoverMapper handoverMapper;

    public MysqlHandoverRepository(HandoverMapper handoverMapper){ this.handoverMapper = handoverMapper; }

    @Override
    public void save(TaskHandoverCreateInput taskHandoverCreateInput){
        this.handoverMapper.save(taskHandoverCreateInput);
    }
    @Override
    public void updateByDeleteKey(boolean deleteKey, int reportId){
        this.handoverMapper.updateByDeleteKey(deleteKey, reportId);
    }
    @Override
    public List<TaskLog> selectTaskByAfter(int employeeCode){
        return this.handoverMapper.selectTaskByAfter(employeeCode);
    }
    @Override
    public List<TaskLog> selectTaskByBefore(int employeeCode){
        return this.handoverMapper.selectTaskByBefore(employeeCode);
    }
    @Override
    public int countByAfter(int employeeCode){
        return this.handoverMapper.countByAfter(employeeCode);
    }
}
