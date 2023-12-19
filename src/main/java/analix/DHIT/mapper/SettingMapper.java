package analix.DHIT.mapper;

import analix.DHIT.model.Setting;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SettingMapper {

    @Select("SELECT * FROM setting")
    Setting selectSettingTime();

    @Update("UPDATE setting SET " +
            "start_time = #{startTime}, " +
            "end_time = #{endTime}")
    void updateSetting(Setting setting);
}
