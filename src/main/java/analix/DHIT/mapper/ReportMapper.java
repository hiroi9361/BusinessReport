package analix.DHIT.mapper;

import analix.DHIT.input.ReportSortInput;
import analix.DHIT.model.Report;
import analix.DHIT.model.User;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ReportMapper {
    @Select("SELECT report_id FROM report WHERE employee_code = #{employeeCode} and date = #{date}")
    String selectIdByEmployeeCodeAndDate(int employeeCode, LocalDate date);
    @Select("SELECT * FROM report WHERE report_id = #{reportId}")
    Report SelectById(int reportId);

    @Select("SELECT report_id FROM report WHERE employee_code = (SELECT employee_code FROM report WHERE report_id = #{reportId}) AND date < (SELECT date FROM report WHERE report_id = #{reportId}) ORDER BY date DESC LIMIT 1")
    String selectBeforeIdById(int reportId);

    @Select("SELECT report_id FROM report WHERE employee_code = (SELECT employee_code FROM report WHERE report_id = #{reportId}) AND date > (SELECT date FROM report WHERE report_id = #{reportId}) ORDER BY date LIMIT 1")
    String selectAfterIdById(int reportId);

    @Select("SELECT report_id FROM report WHERE employee_code = #{employeeCode} ORDER BY date DESC LIMIT 1")
    String selectLatestIdByEmployeeCode(int employeeCode);

    @Select("SELECT * FROM report WHERE employee_code = #{employeeCode} ORDER BY date DESC LIMIT 2")
    List<Report> selectLastTwoReportByEmployeeCode(int employeeCode);

    @Select("SELECT * FROM report WHERE employee_code = #{employeeCode} ORDER BY date DESC LIMIT 1")
    Report selectLastOneReportByEmployeeCode(int employeeCode);

    @Select("SELECT * FROM report WHERE employee_code = #{employeeCode} and date = #{date}")
    Report selectByEmployeeCodeAndDate(int employeeCode, LocalDate date);

    //↓@Optionsは自動生成された"id"を返す処理をする
    @Insert("INSERT INTO report(employee_code, `condition`, condition_rate, impressions, tomorrow_schedule, date, start_time, end_time, is_lateness, lateness_reason, is_left_early) " +
            "VALUES(#{employeeCode}, #{condition}, #{conditionRate}, #{impressions}, #{tomorrowSchedule}, #{date}, #{startTime}, #{endTime}, #{isLateness}, #{latenessReason}, #{isLeftEarly})")
    @Options(useGeneratedKeys = true, keyProperty = "report_id")
    void insertReport(Report report);

    //↓COUNT(*)は集計関数していされた行の数を返す
    @Select("SELECT COUNT(*) FROM report WHERE employee_code = #{employeeCode} AND date = #{date}")
    int countReportByEmployeeCodeAndDate(int employeeCode, LocalDate date);

    @Delete("DELETE FROM report WHERE report_id = #{reportId}")
    void deleteById(int reportId);

    @Delete("DELETE FROM report WHERE employee_code = #{employeeCode}")
    void deleteByEmployeeCode(int employeeCode);

    @Update("UPDATE report SET " +
            "`condition` = #{condition}, " +
            "condition_rate = #{conditionRate}, " +
            "impressions = #{impressions}, " +
            "tomorrow_schedule = #{tomorrowSchedule}, " +
            "start_time = #{startTime}, " +
            "end_time = #{endTime}, " +
            "is_lateness = #{isLateness}, " +
            "lateness_reason = #{latenessReason}, " +
            "is_left_early = #{isLeftEarly} " +
            "WHERE report_id = #{id}")
    void updateReport(Report report);

    //reportテーブルのemployeeCodeに紐づいているIdを全取得
    @Select("SELECT report_id FROM report WHERE employee_code=#{employeeCode}")
    List<Integer> selectIdsByEmployeeCode(int employeeCode);


    //追記*****************************************************
    //報告一覧表示----------------------------------
    //引数として受け取ったemployeeCodeと一致するデータを全件取得するSELECT文
    @Select("SELECT * FROM report WHERE employee_code=#{employeeCode}")
    List<Report> selectAll(int employeeCode);
    //検索条件-------------------------------------
    @Select("SELECT * FROM report as r " +
            "LEFT OUTER JOIN feedback as f ON r.report_id=f.report_id " +
            "WHERE " +
            "(#{reportSortInput.date} IS NULL OR DATE_FORMAT(r.date,'%Y-%m')=DATE_FORMAT(#{reportSortInput.date},'%Y-%m')) " +
            "AND " +
            "(r.employee_code=#{reportSortInput.employeeCode}) " +
            "AND " +
            "((#{reportSortInput.feedback} IS NULL) " +
            "OR (#{reportSortInput.feedback} IS TRUE AND f.feedback_id IS NOT NULL) " +
            "OR (#{reportSortInput.feedback} IS FALSE AND f.feedback_id IS NULL))")
    List<Report> sortReport(@Param("reportSortInput")ReportSortInput reportSortInput);


}
