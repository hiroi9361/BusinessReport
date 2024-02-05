package analix.DHIT.mapper;

import analix.DHIT.input.TaskDetailInput;
import analix.DHIT.input.TaskSearchInput;
import analix.DHIT.model.Report;
import analix.DHIT.model.TaskLog;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface TaskLogMapper {
    @Select("SELECT * FROM task_log WHERE report_id = #{reportId}")
    List<TaskLog> selectByReportId(int reportId);

    @Select("SELECT * FROM task_log WHERE report_id = #{reportId} and progress_rate < 100")
    List<TaskLog> selectIncompleteByReportId(int reportId);

    @Insert("INSERT INTO task_log(report_id, name, progress_rate, counter, sorting, employee_code) " +
            "VALUES(#{reportId}, #{name}, #{progressRate}, #{counter}, #{sorting}, #{employeeCode})")
    void insertTaskLog(TaskLog taskLog);

    //直近ではなく、未達成のタスクを引っ張てくる
    @Select("SELECT t.* " +
            "FROM task_log AS t " +
            "JOIN ( " +
            "SELECT sorting, MAX(counter) AS max_counter " +
            "FROM task_log " +
            "WHERE employee_code = #{employeeCode} " +
            "GROUP BY sorting " +
            ") AS max_counters " +
            "ON t.sorting = max_counters.sorting AND t.counter = max_counters.max_counter;")
    List<TaskLog> selectByEmployeeCode(int employeeCode);

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
            "JOIN task_log AS t ON max_counters.sorting = t.sorting AND max_counters.max_counter = t.counter " +
            "LEFT JOIN report AS r ON t.report_id = r.report_id;")
    List<TaskLog> tasklogList(int employeeCode);

    //タスク詳細：sortingを基にtask_logから進捗率とタスク名をreportから日付を取得する
    @Select("select t.progress_rate, t.name, r.date " +
            "from task_log as t " +
            "left join report as r on t.report_id = r.report_id " +
            "where t.sorting = #{sorting} and t.employee_code = #{employeeCode} " +
            "order by r.date;")
    List<TaskDetailInput> taskDetail(@Param("sorting") int sorting, @Param("employeeCode") int employeeCode);

    //同名タスクの存在チェック
    @Select("select count(*) from task_log as t where t.name = #{name}")
    int countByName(String name);

    //同名タスクを全件取得
    @Select("select * from task_log as t where t.name = #{name}")
    List<TaskLog>taskListByName(String name);

    //counterを採番
    @Select("update task_log set counter = #{counter} where task_id = #{taskId}")
    void setCounter(TaskLog taskLog);



    //タスク一覧フィルター用
    @Select("SELECT * " +
            "FROM task_log as t " +
            "LEFT JOIN report AS r ON t.report_id = r.report_id " +
            "WHERE " +
            "  (#{taskSearchInput.state} = '達成' AND t.progress_rate = 100 AND r.employee_code = #{taskSearchInput.employeeCode}) " +
            "  OR " +
            "  ((#{taskSearchInput.state} = '' OR #{taskSearchInput.state} = '未達成') AND " +
            "   (#{taskSearchInput.progressRateAbove} = 0 OR t.progress_rate >= #{taskSearchInput.progressRateAbove}) AND " +
            "   (#{taskSearchInput.progressRateBelow} = 0 OR t.progress_rate <= #{taskSearchInput.progressRateBelow}) AND " +
            "   (r.employee_code = #{taskSearchInput.employeeCode}));")
    List<TaskLog> taskLogFilter(@Param("taskSearchInput") TaskSearchInput taskSearchInput);







}
