package analix.DHIT.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name="setting")
public class Setting implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="setting_id")
    private int settingId;
    @Column(name="start_time")
    private LocalTime startTime;
    @Column(name="end_time")
    private LocalTime endTime;

    public int getSettingId(){
        return settingId;
    }

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


}

