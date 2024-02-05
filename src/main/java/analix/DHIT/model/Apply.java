package analix.DHIT.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name="apply")
public class Apply {

    @Id
    @Column(name = "apply_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int apply_id;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "apply_id")
    private int applyId;

    @ManyToOne
    @JoinColumn(name = "employee_code", referencedColumnName = "employee_code")
    private User user;

    @Column(name = "employee_code")
    private int employeeCode;

    @Column(name = "application_type")
    private int applicationType;

    @Column(name = "attendance_type")
    private int attendanceType;

    @Column(name = "reason")
    private String reason;

    @Column(name = "approval")
    private int approval;

    @Temporal(TemporalType.DATE)
    private LocalDate startDate;

    @Temporal(TemporalType.DATE)
    private LocalDate endDate;

    @Temporal(TemporalType.TIME)
    private LocalTime startTime;
    @Temporal(TemporalType.TIME)
    private LocalTime endTime;

    @Temporal(TemporalType.DATE)
    private LocalDateTime createdDate;
    private String formattedCreatedDate;

    public int getId() {
        return id;
    }
    public void setApplyId(int applyId) {
        this.id = applyId;
    }

    public void setApply_id(int apply_id) {
        this.id = apply_id;
    }

    public int getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(int employeeCode) {
        this.employeeCode = employeeCode;
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


    public int getApplicationType() {
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
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setApproval(int approval) {
        this.approval = approval;
    }

    public int getApproval() {
        return approval;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getFormattedCreatedDate() {
        return formattedCreatedDate;
    }

    public void setFormattedCreatedDate(String formattedCreatedDate) {
        this.formattedCreatedDate = formattedCreatedDate;
    }

    public Apply() {
    }
}
