package analix.DHIT.input;

//個人詳細ページに検索するための入力値

import java.time.LocalDate;
import java.time.LocalDateTime;

//ソート用Input
public class ApplySortInput {
    private int employeeCode;
//    private Boolean feedback;
    private LocalDateTime createdDate;
    private  boolean sort;

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

//    public int getApproval() {
//        return approval;
//    }

//    public void setApproval(int approval) {
//        this.approval = approval;
//    }

    public  boolean getSort() {
        return sort;
    }

    public  void  setSort(boolean sort) {
        this.sort=sort;
    }
}
