package analix.DHIT.mapper;

import analix.DHIT.model.Report;
import analix.DHIT.model.TaskLog;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TaskLogMapper {
    @Select("SELECT * FROM task_log WHERE report_id = #{reportId}")
    List<TaskLog> selectByReportId(int reportId);

    @Select("SELECT * FROM task_log WHERE report_id = #{reportId} and progress_rate < 100")
    List<TaskLog> selectIncompleteByReportId(int reportId);

    @Insert("INSERT INTO task_log(report_id, name, progress_rate, counter, sorting) " +
            "VALUES(#{reportId}, #{name}, #{progressRate}, #{counter}, #{sorting})")
    void insertTaskLog(TaskLog taskLog);

    //全部消しメソッド
    @Delete("DELETE FROM task_log WHERE report_id = #{reportId}")
    void deleteByReportId(int reportId);

    //新規タスクの時に番号を振る
    @Select("SELECT MAX(sorting) FROM task_log")
    int maxTask();
}
