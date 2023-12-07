package analix.DHIT.repository;

import analix.DHIT.model.Assignment;
import analix.DHIT.model.Report;

import java.util.List;

public interface AssignmentRepository {

    List<Assignment> selectByUser(int employeeCode);

    List<Assignment> selectByTeam(int teamId);

    void save(Assignment assignment);

    void update(Assignment assignment);

    void deleteById(int assignmentId);



}
