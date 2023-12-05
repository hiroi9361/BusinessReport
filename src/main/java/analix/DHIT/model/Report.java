package analix.DHIT.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name="report", uniqueConstraints = {@UniqueConstraint(name = "report_feedback", columnNames = {"feedback_id"})})
public class Report {

    @Id
    private int id;
    private int employeeCode;

    private int Rating;
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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "feedback_id")
    private Feedback feedback;
    public Report() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}
