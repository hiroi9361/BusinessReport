package analix.DHIT.repository;

import analix.DHIT.model.Feedback;


public interface FeedbackRepository {

    Feedback findById(int reportId);

    void save(Feedback feedback);

    void deleteById(int reportId);

    void update(Feedback feedback);
}
