package analix.DHIT.input;

import analix.DHIT.model.Assignment;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class AssignmentCreateInput {

    @Getter
    private int employeeCode;

    @Getter
    private int teamId;

    private boolean isManager;

    @Getter
    private static List<Assignment> assignments = new ArrayList<>();

    public void setAssignments(List<Assignment> assignments) {
        AssignmentCreateInput.assignments = assignments;
    }

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
}
