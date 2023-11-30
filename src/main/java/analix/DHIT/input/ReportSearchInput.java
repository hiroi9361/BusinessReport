package analix.DHIT.input;

//個人詳細ページに検索するための入力値

import java.time.LocalDate;

public class ReportSearchInput {
    private int employeeCode;
    private LocalDate date;

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
}
