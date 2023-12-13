package analix.DHIT.service;

import analix.DHIT.mapper.TeamMapper;
import analix.DHIT.model.Assignment;
import analix.DHIT.model.Report;
import analix.DHIT.model.Team;
import analix.DHIT.model.User;
import analix.DHIT.repository.AssignmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AssignmentService {

    private final TeamMapper teamMapper;
    private final AssignmentRepository assignmentRepository;
    public AssignmentService(TeamMapper teamMapper, AssignmentRepository assignmentRepository) {
        this.teamMapper = teamMapper;
        this.assignmentRepository = assignmentRepository;
    }

    public List<Assignment> getAllAssignment(){
        List<Assignment> assignments = this.assignmentRepository.getAllAssignment();
        if (assignments == null){
            return new ArrayList<>();
        }
        return assignments;
    }

    public List<Assignment> getAsManager(int employeeCode){
        List<Assignment> assignments = this.teamMapper.selectAsManagerByEmployeeCode(employeeCode);
        if (assignments == null){
            return new ArrayList<>();
        }
        return assignments;
    }

    public List<Assignment> getAssignmentByEmployeeCode(int employeeCode)
    {
        List<Assignment> assignments = this.assignmentRepository.selectByUser(employeeCode);
        if (assignments == null) {
            return new ArrayList<>();
        }
        return assignments;
    }

    public List<Assignment> getAssignmentByTeam(int teamId)
    {
        List<Assignment> assignments = this.assignmentRepository.selectByTeam(teamId);
        if (assignments == null) {
            return new ArrayList<>();
        }
        return assignments;
    }

//    public List<Integer> ManagersByTeam(int teamId){
//        List<Integer> managers = this.assignmentRepository.managersByTeam(teamId);
//        if (managers == null){
//            return new ArrayList<>();
//        }
//        return managers;
//    }
//
//    public List<Integer> MembersByTeam(int teamId){
//        List<Integer> members = this.assignmentRepository.membersByTeam(teamId);
//        if (members == null){
//            return new ArrayList<>();
//        }
//        return members;
//    }


    public void deleteByTeam(int teamId){this.teamMapper.deleteAllAssignmentByTeamId(teamId);}
    public void deleteById(int assignmentId) {
        this.assignmentRepository.deleteById(assignmentId);
    }

    public void deleteByUser(int emoloyeeCode){
        this.assignmentRepository.deleteByUser(emoloyeeCode);
    }

    public int create(
            int employeeCode,
            boolean isManager,
            int teamId
    ) {

        Assignment newAssignment = new Assignment();
        newAssignment.setEmployeeCode(employeeCode);
        newAssignment.setTeamId(teamId);
        newAssignment.setIsManager(isManager);

        this.assignmentRepository.save(newAssignment);

        return newAssignment.getAssignmentId();

    }


    public boolean existsAssignment(int employeeCode, int teamId) {
        int count = teamMapper.countAssignmentByEmployeeCodeAndTeamId(employeeCode, teamId);
        return count > 0;
    }


    public boolean getIsManager(int employeeCode, int teamId){
        return this.assignmentRepository.getIsManager(employeeCode, teamId);
    }

    public boolean getCountIsManager(int employeeCode, int reportId) {
        int count = this.assignmentRepository.countIsManager(employeeCode, reportId);

        return count > 0;
    }

}
