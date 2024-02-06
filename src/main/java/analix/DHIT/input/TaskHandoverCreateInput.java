package analix.DHIT.input;

import analix.DHIT.model.TaskLog;
import jakarta.persistence.Column;

import java.util.List;

public class TaskHandoverCreateInput {
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

    public int getHandoverId() {
        return handover_id;
    }

    public void setHandoverId(int handoverId) {
        this.handover_id = handoverId;
    }

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public int getSorting() {
        return sorting;
    }

    public void setSorting(int sorting) {
        this.sorting = sorting;
    }

    private int reportId;
    private int handover_id;
    private int taskLogId;
    private int sorting;
    private int taskBefore;
    private int taskAfter;
    private boolean deleteKey;
}
