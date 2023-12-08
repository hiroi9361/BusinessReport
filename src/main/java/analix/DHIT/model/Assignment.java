package analix.DHIT.model;

import static jakarta.persistence.FetchType.*;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="assignment")
public class Assignment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int assignmentId;

//    @ManyToOne(fetch = LAZY)
//    @JoinColumn(name="employee_code")
//    private User user;
//
//    @ManyToOne(fetch = LAZY)
//    @JoinColumn(name = "team_id")
//    private Team team;

    private boolean isManager;

//    entity使えない場合の利用 ここから

    private int employeeCode;

    private int teamId;

    public int getEmployeeCode() {
        return employeeCode;
    }

    //    entity使えない場合の利用 ここまで


    public void setEmployeeCode(int employeeCode) {
        this.employeeCode = employeeCode;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public int getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(int assignmentId) {
        this.assignmentId = assignmentId;
    }

    public boolean getIsManager() {
        return isManager;
    }

    public void setIsManager(boolean manager) {
        isManager = manager;
    }

    //    public User getUser() {
//        return user;
//    }
//
//    public void setUser(User user) {
//        this.user = user;
//    }
//
//    public Team getTeam() {
//        return team;
//    }
//
//    public void setTeam(Team team) {
//        this.team = team;
//    }

}