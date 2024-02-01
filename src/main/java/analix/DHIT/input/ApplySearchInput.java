package analix.DHIT.input;

//個人詳細ページに検索するための入力値

import java.time.LocalDateTime;

public class ApplySearchInput {
    private int employeeCode;
    private LocalDateTime createdDate;


    public int getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(int employeeCode) {
        this.employeeCode = employeeCode;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
}
