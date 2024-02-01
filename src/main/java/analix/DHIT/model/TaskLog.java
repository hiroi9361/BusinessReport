package analix.DHIT.model;

import jakarta.persistence.*;

@Entity
@Table(name="task_log")
public class TaskLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int taskId;
    private int reportId;
    private String name;
    private int progressRate;
    private int counter;
    private int sorting;

    public TaskLog() {
    }

    public int getId() {
        return taskId;
    }
    public void setId(int taskId) {
        this.taskId = taskId;
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

    public int getCounter() {
        return counter;
    }
    public void setCounter(int counter) {
        this.counter = counter;
    }

    public int getSorting() {
        return sorting;
    }
    public void setSorting(int sorting) {
        this.sorting = sorting;
    }
}
