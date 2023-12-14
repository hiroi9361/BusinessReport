package analix.DHIT.input;

import analix.DHIT.model.Assignment;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class AssignmentCreateInput {
    private int employeeCode;
    private int teamId;
    private boolean isManager;
    private List<Assignment> assignments = new ArrayList<>();

    public void setEmployeeCode(int employeeCode) {
        this.employeeCode = employeeCode;
    }
    public int getEmployeeCode(){
        return employeeCode;
    }
    public int getTeamId() {
        return teamId;
    }
    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }
    public boolean getIsManager() {
        return isManager;
    }
    public void setIsManager(boolean manager) {
        isManager = manager;
    }
    public List<Assignment> getAssignments(){
        return assignments;
    }
    public void setAssignments(List<Assignment> assignments) {
        this.assignments = assignments;
    }
}
