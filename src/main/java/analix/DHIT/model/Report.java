package analix.DHIT.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name="report", uniqueConstraints = {@UniqueConstraint(name = "report_feedback", columnNames = {"feedback_id"})})
public class Report {

    @Id
    //test追記
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    //test追記
    @Column(name = "employee_code")
    private int employeeCode;

    //test追記
    @Column(name = "condition_rate")
    private int conditionRate;
    private String condition;
    private String impressions;
    private String tomorrowSchedule;

    @Temporal(TemporalType.DATE)
    private LocalDate date;

    @Temporal(TemporalType.TIME)
    private LocalTime endTime;

    @Temporal(TemporalType.TIME)
    private LocalTime startTime;
    private boolean isLateness;
    private String latenessReason;
    private boolean isLeftEarly;

    @ManyToOne
    @JoinColumn(name = "employee_code")
    private User user;

    @OneToOne(mappedBy = "report")
    private Feedback feedback;

    public Report() {
    }

    public int getId() {
        return id;
    }

    public void setReportId(int reportId) {
        this.id = reportId;
    }
    public void setReport_id(int report_id) {
        this.id = report_id;
    }

    public int getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(int employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getImpressions() {
        return impressions;
    }

    public void setImpressions(String impressions) {
        this.impressions = impressions;
    }

    public String getTomorrowSchedule() {
        return tomorrowSchedule;
    }

    public void setTomorrowSchedule(String tomorrowSchedule) {
        this.tomorrowSchedule = tomorrowSchedule;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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

    public boolean getIsLateness() {
        return isLateness;
    }

    public void setIsLateness(Boolean lateness) {
        isLateness = lateness;
    }

    public String getLatenessReason() {
        return latenessReason;
    }

    public void setLatenessReason(String latenessReason) {
        this.latenessReason = latenessReason;
    }

    public boolean getIsLeftEarly() {
        return isLeftEarly;
    }

    public void setIsLeftEarly(Boolean leftEarly) {
        isLeftEarly = leftEarly;
    }

    public int getConditionRate() {
        return conditionRate;
    }

    public void setConditionRate(int conditionRate) {
        this.conditionRate=conditionRate;
    }
}
