package analix.DHIT.mapper;

import analix.DHIT.model.Report;
import analix.DHIT.model.Setting;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SettingMapper {

    @Select("SELECT * FROM setting WHERE employee_code=#{employeeCode}")
    Setting selectSettingTime(int employeeCode);

    @Update("UPDATE setting SET " +
            "start_time = #{startTime}, " +
            "end_time = #{endTime} " +
            "WHERE employee_code = #{employeeCode}")
    void updateSetting(Setting setting);

    @Insert("INSERT INTO setting(start_time, end_time, employee_code) " +
            "VALUES(#{startTime}, #{endTime}, #{employeeCode})")
    @Options(useGeneratedKeys = true, keyProperty = "setting_id")
    void insertSetting(Setting setting);

    @Delete("DELETE FROM setting WHERE employee_code = #{employeeCode}")
    void deleteById(int employeeCode);
}
