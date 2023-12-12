package analix.DHIT.repository;

import analix.DHIT.model.Assignment;
import analix.DHIT.model.User;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;

public interface AssignmentRepository {

    List<Assignment> getAllAssignment();

    List<Assignment> selectByUser(int employeeCode);

    List<Assignment> selectByTeam(int teamId);

    void save(Assignment assignment);

    void update(Assignment assignment);

    void deleteById(int assignmentId);

    void deleteByUser(int employeeCode);

    boolean getIsManager(int employeeCode, int teamId);

    int countIsManager(int employeeCode, int reportId);
}
