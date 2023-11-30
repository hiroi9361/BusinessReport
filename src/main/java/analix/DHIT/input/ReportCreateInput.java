package analix.DHIT.input;

import analix.DHIT.model.TaskLog;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ReportCreateInput {

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean isLateness;
    private String latenessReason;
    private boolean isLeftEarly;
    private String condition;
    private String tomorrowSchedule;
    private String impressions;
    private List<TaskLog> taskLogs = new ArrayList<>();

    public List<TaskLog> getTaskLogs() {
        return taskLogs;
    }

    public void setTaskLogs(List<TaskLog> taskLogs) {
        this.taskLogs = taskLogs;
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

    public boolean getIsLeftEarly() {
        return isLeftEarly;
    }

    public void setIsLeftEarly(Boolean leftEarly) {
        isLeftEarly = leftEarly;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getTomorrowSchedule() {
        return tomorrowSchedule;
    }

    public void setTomorrowSchedule(String tomorrowSchedule) {
        this.tomorrowSchedule = tomorrowSchedule;
    }

    public String getImpressions() {
        return impressions;
    }

    public void setImpressions(String impressions) {
        this.impressions = impressions;
    }

    public String getLatenessReason() {
        return latenessReason;
    }

    public void setLatenessReason(String latenessReason) {
        this.latenessReason = latenessReason;
    }
}

