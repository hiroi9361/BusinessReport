package analix.DHIT.model;

import jakarta.persistence.*;

@Entity
@Table(name="handover")
public class Handover {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int handoverId;
    private int taskLogId;
    private int taskBefore;
    private int taskAfter;
    private boolean deleteKey;
    private int reportId;

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public int getHandoverId() {
        return handoverId;
    }

    public void setHandoverId(int handoverId) {
        this.handoverId = handoverId;
    }

    public int getTaskLogId() {
        return taskLogId;
    }

    public void setTaskLogId(int taskLogId) {
        this.taskLogId = taskLogId;
    }

    public int getTaskBefore() {
        return taskBefore;
    }

    public void setTaskBefore(int taskBefore) {
        this.taskBefore = taskBefore;
    }

    public int getTaskAfter() {
        return taskAfter;
    }

    public void setTaskAfter(int taskAfter) {
        this.taskAfter = taskAfter;
    }

    public boolean isDeleteKey() {
        return deleteKey;
    }

    public void setDeleteKey(boolean deleteKey) {
        this.deleteKey = deleteKey;
    }

    public Handover() {}


}
