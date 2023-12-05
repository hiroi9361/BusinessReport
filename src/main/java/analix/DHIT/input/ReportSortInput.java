package analix.DHIT.input;

//個人詳細ページに検索するための入力値

import java.time.LocalDate;

//ソート用Input
public class ReportSortInput {
    private int employeeCode;
    private boolean feedback;
    private LocalDate date;
    private boolean isLateness;
    private  boolean isLeftEarly;

    public int getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(int employeeCode) {
        this.employeeCode = employeeCode;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public boolean getisLateness() {
        return isLateness;
    }

    public void setisLateness(boolean isLateness) {
        this.isLateness = isLateness;
    }

    public boolean getisLeftEarly() {
        return isLeftEarly;
    }

    public void setisLeftEarly(boolean isLeftEarly) {
        this.isLeftEarly = isLeftEarly;
    }
}
