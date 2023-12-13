package analix.DHIT.repository;

import analix.DHIT.input.ReportSortInput;
import analix.DHIT.mapper.FeedbackMapper;
import analix.DHIT.mapper.ReportMapper;
import analix.DHIT.model.Feedback;
import analix.DHIT.model.Report;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MysqlFeedbackRepository implements FeedbackRepository {
    private final FeedbackMapper feedbackMapper;

    public MysqlFeedbackRepository(FeedbackMapper feedbackMapper) {
        this.feedbackMapper = feedbackMapper;
    }

    @Override
    public Feedback findById(int reportId) {
        return this.feedbackMapper.SelectById(reportId);
    }

    @Override
    public void save(Feedback feedback){
        this.feedbackMapper.insertFeedback(feedback);
    }
    @Override
    public void deleteById(int reportId){
        this.feedbackMapper.deleteById(reportId);
    }
    @Override
    public void update(Feedback feedback){
        this.feedbackMapper.updateFeedback(feedback);
    }
    @Override
    public int count(int reportId){
        return this.feedbackMapper.countFeedback(reportId);
    }
}
