package analix.DHIT.input;

import java.time.LocalDate;

public class TaskSearchInput {

    private int progressRate;
    private LocalDate date;
    private String state;

    public int getProgress() {
        return progressRate;
    }
    public void setProgress(int progressRate) {
        this.progressRate = progressRate;
    }

    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
}
