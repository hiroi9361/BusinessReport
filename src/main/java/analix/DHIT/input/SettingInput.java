package analix.DHIT.input;

import java.time.LocalTime;


public class SettingInput {
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean employment;

    public LocalTime getStartTime(){
        return startTime;
    }
    public void setStartTime(LocalTime startTime){
        this.startTime=startTime;
    }

    public LocalTime getEndTime(){
        return endTime;
    }
    public void setEndTime(LocalTime endTime){
        this.endTime=endTime;
    }

    public boolean getEmployment(){
        return employment;
    }
    public void setEmployment(boolean employment){
        this.employment=employment;
    }
}
