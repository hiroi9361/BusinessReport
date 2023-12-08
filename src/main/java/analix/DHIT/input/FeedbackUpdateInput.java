package analix.DHIT.input;

import analix.DHIT.model.Report;
import analix.DHIT.model.TaskLog;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class FeedbackUpdateInput {

    private int feedbackId;
    private String name;
    private int rating;
    private String comment;
    private Report report;

    public int getFeedbackId(){
        return feedbackId;
    }
    public void setFeedbackId(int feedbackId){
        this.feedbackId=feedbackId;
    }
    public String getName(){
        return name;
    }
    public void setName(String name) {
        this.name=name;
    }
    public int getRating(){
        return rating;
    }
    public void setRating(int rating) {
        this.rating = rating;
    }
    public String getComment(){
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public Report getReport(){
        return report;
    }
    public void setReport(Report report) {
        this.report = report;
    }
}
