package analix.DHIT.mapper;

import analix.DHIT.input.TaskDetailInput;
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

    //報告書作成で、同じtaskの場合、counterが増加する。counterが一番大きい順にsortingが重複しないレコードを取得する
    @Select("SELECT * " +
            "FROM ( " +
            "SELECT t.sorting, MAX(t.counter) AS max_counter " +
            "FROM task_log AS t " +
            "LEFT JOIN report AS r ON t.report_id = r.report_id " +
            "WHERE r.employee_code = #{employeeCode} " +
            "GROUP BY t.sorting " +
            ") AS max_counters " +
            "JOIN report.task_log AS t ON max_counters.sorting = t.sorting AND max_counters.max_counter = t.counter " +
            "LEFT JOIN report.report AS r ON t.report_id = r.report_id;")
    List<TaskLog> tasklogList(int employeeCode);

    //タスク詳細：sortingを基にtask_logから進捗率とタスク名をreportから日付を取得する
    @Select("select * " +
            "from report.task_log as t " +
            "left join report.report as r on t.report_id = r.report_id " +
            "where t.sorting = 13 " +
            "order by r.date;")
    TaskDetailInput taskDetail(int sorting);
}
