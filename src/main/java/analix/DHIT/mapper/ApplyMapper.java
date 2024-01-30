package analix.DHIT.mapper;

import analix.DHIT.model.Apply;
import analix.DHIT.model.Report;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ApplyMapper {

    // 勤怠申請登録
    @Insert("INSERT INTO apply(employee_code, application_type, attendance_type, start_date, end_date, start_time, end_time, reason, approval, created_date) " +
            "VALUES(#{employeeCode}, #{applicationType}, #{attendanceType}, #{startDate}, #{endDate}, #{startTime}, #{endTime}, #{reason}, #{approval},#{createdDate})")
    @Options(useGeneratedKeys = true, keyProperty = "apply_id")
    void insertApply(Apply apply);

    // エラー回避
    @Select("SELECT * FROM apply WHERE apply_id = #{applyId}")
    Apply SelectById(int applyId);

    // 一覧表示
    @Select("SELECT * FROM apply WHERE employee_code=#{employeeCode}")
    List<Report> selectAll(int employeeCode);

}
