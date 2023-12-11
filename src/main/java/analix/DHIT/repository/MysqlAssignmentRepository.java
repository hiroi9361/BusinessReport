package analix.DHIT.repository;

import analix.DHIT.mapper.TeamMapper;
import analix.DHIT.model.Assignment;
import analix.DHIT.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MysqlAssignmentRepository implements AssignmentRepository{
    public MysqlAssignmentRepository(TeamMapper teamMapper) {
        this.teamMapper = teamMapper;
    }

    private final TeamMapper teamMapper;

    @Override
    public List<Assignment> selectByUser(int employeeCode){return this.teamMapper.selectByEmployeeCode(employeeCode);}

    @Override
    public List<Assignment> selectByTeam(int teamId){return this.teamMapper.selectByTeamId(teamId);}

    @Override
    public void save(Assignment assignment) {
        this.teamMapper.insertAssignment(assignment);
    }

    @Override
    public void update(Assignment assignment) {
        this.teamMapper.updateAssignment(assignment);
    }

    @Override
    public void deleteById(int assignmentId){ this.teamMapper.deleteAssignmentById(assignmentId);}

    @Override
    public void deleteByUser(int employeeCode){ this.teamMapper.deleteAssignmentByUser(employeeCode);}

    @Override
    public List<Assignment> getAllAssignment(){return this.teamMapper.allAssignments();}

}
