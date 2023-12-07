package analix.DHIT.input;

//個人詳細ページに検索するための入力値

import java.time.LocalDate;

//ソート用Input
public class ReportSortInput {
    private int employeeCode;
    private Boolean feedback;
    private LocalDate date;
    private  boolean sort;

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

    public Boolean getFeedback() {
        return feedback;
    }

    public void setFeedback(Boolean feedback) {
        this.feedback = feedback;
    }

    public  boolean getSort() {
        return sort;
    }

    public  void  setSort(boolean sort) {
        this.sort=sort;
    }
}
