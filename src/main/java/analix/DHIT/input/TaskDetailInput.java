package analix.DHIT.input;

import analix.DHIT.model.TaskLog;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class TaskDetailInput {

    private List<TaskLog> taskLogs = new ArrayList<>();
    private List<LocalDate> dates = new ArrayList<>();

    public List<TaskLog> getTaskLogs() {
        return taskLogs;
    }
    public void setTaskLogs(List<TaskLog> taskLogs) {
        this.taskLogs = taskLogs;
    }

    public List<LocalDate> getDates() {
        return dates;
    }
    public void setDates(List<LocalDate> dates) {
        this.dates = dates;
    }

}
