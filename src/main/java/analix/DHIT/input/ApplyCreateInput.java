package analix.DHIT.input;

//import analix.DHIT.model.TaskLog;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ApplyCreateInput {

    private int applyId;

    private int applicationType;
    private int attendanceType;
    private LocalDate startDate;
    private LocalDate endDate;
//    private boolean allDay;
    private LocalTime startTime;
    private LocalTime endTime;
    private String reason;

    private int approval;
    private LocalDateTime createdDate;

    public int getApplyId() {
        return applyId;
    }
    public void setApplyId(int applyId) {
        this.applyId = applyId;
    }

    public int getApplicationType(){
        return applicationType;
    }

    public void setApplicationType(int applicationType) {
        this.applicationType = applicationType;
    }

    public int getAttendanceType() {
        return attendanceType;
    }

    public void setAttendanceType(int attendanceType) {
        this.attendanceType = attendanceType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

//    public boolean isAllDay() {
//        return allDay;
//    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getApproval() {
        return approval;
    }

    public void setApproval(int approval) {
        this.approval = approval;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

}