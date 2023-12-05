package analix.DHIT.model;

import jakarta.persistence.*;

@Entity
@Table(name="task_log")
public class TaskLog {

    private int reportId;
    private String name;
    private int progressRate;

    public TaskLog() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProgressRate() {
        return progressRate;
    }

    public void setProgressRate(int progressRate) {
        this.progressRate = progressRate;
    }

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }
}
