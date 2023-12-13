package analix.DHIT.mapper;

import analix.DHIT.input.ReportSortInput;
import analix.DHIT.model.Feedback;
import analix.DHIT.model.Report;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface FeedbackMapper {

    //フィードバックの情報を取得
    @Select("SELECT * FROM feedback WHERE report_id=#{reportId}")
    Feedback SelectById(int reportId);

    //フィードバックに情報を入力
    @Insert("INSERT INTO feedback(name, rating, comment, report_id) " +
            "VALUES(#{name}, #{rating}, #{comment}, #{reportId})")
    void insertFeedback(Feedback feedback);

    //フィードバックの情報を編集
    @Update("UPDATE feedback SET " +
            "name = #{name}, " +
            "rating = #{rating}, " +
            "comment = #{comment}, " +
            "report_id = #{report.reportId} " +
            "WHERE feedback_id = #{feedbackId}")
    void updateFeedback(Feedback feedback);

    //フィードバックの情報を削除
    @Delete("DELETE FROM feedback WHERE report_id = #{reportId}")
    void deleteById(int reportId);

    @Select("SELECT COUNT(*) FROM feedback WHERE report_id = #{reportId}")
    int countFeedback(int reportId);
}
