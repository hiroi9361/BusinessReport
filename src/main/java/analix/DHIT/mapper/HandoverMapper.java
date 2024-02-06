package analix.DHIT.mapper;

import analix.DHIT.input.TaskDetailInput;
import analix.DHIT.input.TaskHandoverCreateInput;
import analix.DHIT.input.TaskSearchInput;
import analix.DHIT.model.TaskLog;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface HandoverMapper {

    //引継ぎ情報を登録する
    @Insert("INSERT INTO handover(`task_log_id`, `sorting`, `task_before`, `task_after`, `delete_key`, `report_id`) " +
            "VALUES(#{taskLogId}, #{sorting}, #{taskBefore}, #{taskAfter}, #{deleteKey}, #{reportId})")
    @Options(useGeneratedKeys = true, keyProperty = "handover_id")
    void save(TaskHandoverCreateInput taskHandoverCreateInput);

    //reportテーブルのdelete_keyのデータを書き換える
    @Update("UPDATE report SET delete_key = #{deleteKey} WHERE report_id = #{reportId}")
    void updateByDeleteKey(boolean deleteKey, int reportId);

    //引き継いだタスクを取得する
//    @Select("SELECT DISTINCT t.* FROM task_log AS t " +
//            "LEFT JOIN handover AS h ON t.task_id = h.task_log_id " +
//            "WHERE h.task_after = #{employeeCode}")
    //sortingの重複無し、、、のはず
    @Select("SELECT t.* " +
            "FROM report.task_log AS t " +
            "LEFT JOIN report.handover AS h ON t.task_id = h.task_log_id " +
            "WHERE h.task_before = 6 " +
            "AND t.sorting IN ( " +
            "    SELECT sorting " +
            "    FROM report.task_log " +
            "    GROUP BY sorting " +
            "    HAVING COUNT(*) > 1 " +
            ") " +
            "AND t.task_id IN ( " +
            "    SELECT MIN(task_id) AS task_id " +
            "    FROM report.task_log " +
            "    GROUP BY sorting " +
            "    HAVING COUNT(*) > 1 " +
            ") " +
            "LIMIT 0, 1000")
    List<TaskLog> selectTaskByAfter(int employeeCode);



    //引き継がれたタスクを取得する
    @Select("SELECT t.* FROM task_log AS t " +
            "LEFT JOIN handover AS h ON t.task_id = h.task_log_id " +
            "WHERE h.task_before = #{employeeCode}")
    List<TaskLog> selectTaskByBefore(int employeeCode);

    //引継いだタスクが存在するか
    @Select("SELECT COUNT(*) FROM handover WHERE task_after = #{employeeCode}")
    int countByAfter(int employeeCode);
}
