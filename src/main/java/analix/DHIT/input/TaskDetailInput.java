package analix.DHIT.input;

import jakarta.persistence.Column;

import java.time.LocalDate;

public class TaskDetailInput {

    private int progressRate;
    private String name;
    private LocalDate date;

    public int getProgress() {
        return progressRate;
    }
    public void setProgress(int progressRate) {
        this.progressRate = progressRate;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }

}
