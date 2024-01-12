package analix.DHIT.input;

import java.time.LocalDate;

public class TaskSearchInput {

    private int progressRateAbove;
    private int progressRateBelow;
    private LocalDate date;
    private String state;
    private int employeeCode;

    public int getProgressRateAbove() {
        return progressRateAbove;
    }
    public void setProgressRateAbove(int progressRateAbove) {
        this.progressRateAbove = progressRateAbove;
    }

    public int getProgressRateBelow() {
        return progressRateBelow;
    }
    public void setProgressRateBelow(int progressRateBelow) {
        this.progressRateBelow = progressRateBelow;
    }

    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }

    public int getEmployeeCode() {
        return employeeCode;
    }
    public void setEmployeeCode(int employeeCode) {
        this.employeeCode = employeeCode;
    }
}
