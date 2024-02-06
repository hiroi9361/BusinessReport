package analix.DHIT.input;

import analix.DHIT.model.TaskLog;

import java.time.LocalDate;
import java.util.List;

public class TaskHandoverInput {
    private int teamId;
    private int sorting;
    private int employeeCode;
    private int employeeCodePartner;
    private List<TaskLog>taskLogs;

    public List<TaskLog> getTaskLogs() {
        return taskLogs;
    }
    public void setTaskLogs(List<TaskLog> taskLogs) {
        this.taskLogs = taskLogs;
    }
    public int getEmployeeCode() {
        return employeeCode;
    }
    public void setEmployeeCode(int employeeCode) {
        this.employeeCode = employeeCode;
    }
    public int getTeamId() {
        return teamId;
    }
    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }
    public int getSorting() {
        return sorting;
    }
    public void setSorting(int sorting) {
        this.sorting = sorting;
    }
    public int getEmployeeCodePartner() {
        return employeeCodePartner;
    }
    public void setEmployeeCodePartner(int employeeCodePartner) {
        this.employeeCodePartner = employeeCodePartner;
    }
}
