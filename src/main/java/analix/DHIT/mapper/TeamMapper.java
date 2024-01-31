package analix.DHIT.mapper;

import analix.DHIT.model.*;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface TeamMapper {

    @Select("SELECT * FROM team")
    List<Team> selectAllTeam();

    @Select("SELECT * FROM team WHERE team_id = #{teamId}")
    Team SelectById(int teamId);

    @Insert("INSERT INTO team(name,`release`) " + "VALUES(#{name},#{release})")
    @Options(useGeneratedKeys = true, keyColumn = "team_id", keyProperty = "teamId")
    void insertTeam(Team team);

    @Update("UPDATE team SET name=#{name},`release`=#{release} WHERE team_id = #{teamId}")
    void updateTeam(Team team);

    @Delete("DELETE FROM team WHERE team_id = #{teamId}")
    void deleteById(int teamId);

    //csv関係
    //一括登録時に取得したチーム名と一致するチームが存在するかどうか
    @Select("select team_id from team where team.name = #{name}")
    Integer selectTeamIdByName(String name);

//    ここからAssignment

    @Select("SELECT * FROM assignment")
    List<Assignment> allAssignments();

    @Select("SELECT * FROM assignment WHERE employee_code = #{employeeCode}")
    List<Assignment> selectByEmployeeCode(int employeeCode);

    @Select("SELECT * FROM assignment WHERE employee_code = #{employeeCode} AND is_manager = true")
    List<Assignment> selectAsManagerByEmployeeCode(int employeeCode);

    @Select("SELECT * FROM assignment WHERE team_id = #{teamId}")
    List<Assignment> selectByTeamId(int teamId);

    @Insert("INSERT INTO assignment(is_manager, team_id, employee_code) " +
            "VALUES(#{isManager}, #{teamId}, #{employeeCode})")
    @Options(useGeneratedKeys = true, keyColumn = "assignment_id", keyProperty = "assignmentId")
    void insertAssignment(Assignment assignment);

    @Update("UPDATE assignment SET is_manager=#{isManager} WHERE employee_code=#{employeeCode} AND team_id=#{teamId}")
    void updateAssignment(Assignment assignment);

    @Delete("DELETE FROM assignment WHERE assignment_id = #{assignmentId}")
    void deleteAssignmentById(int assignmentId);

    @Delete("DELETE FROM assignment WHERE team_id = #{teamId}")
    void deleteAllAssignmentByTeamId(int teamId);

    @Delete("DELETE FROM assignment WHERE employee_code = #{employeeCode}")
    void deleteAssignmentByUser(int employeeCode);

    @Select("SELECT COUNT(*) FROM assignment WHERE employee_code = #{employeeCode} AND team_id = #{teamId}")
    int countAssignmentByEmployeeCodeAndTeamId(int employeeCode, int teamId);

    @Select("SELECT COUNT(*) FROM assignment WHERE employee_code = #{employeeCode} AND is_manager = 1")
    int countAssignmentByEmployeeCode(int employeeCode);

    @Select("SELECT * FROM assignment WHERE employee_code = #{employeeCode} AND is_manager = 1")
    List<Assignment> selectByEmployeeCodeIsManager(int employeeCode);

    //test****************************
    @Select("SELECT is_manager FROM assignment WHERE employee_code = #{employeeCode} AND team_id = #{teamId}")
    boolean getIsManager(int employeeCode, int teamId);
    //test****************************
//    @Select("SELECT COUNT(*) AS count " +
//            "FROM report.assignment AS r " +
//            "WHERE r.team_id IN ( " +
//            "    SELECT team_id " +
//            "    FROM assignment " +
//            "    WHERE employee_code = #{employeeCode} " +
//            ") " +
//            "AND r.employee_code = ( " +
//            "    SELECT employee_code " +
//            "    FROM report.report " +
//            "    WHERE report_id = #{reportId} " +
//            ") " +
//            "AND r.is_manager = false;")
    @Select("SELECT COUNT(*) AS count " +
            "FROM assignment AS r " +
            "WHERE r.team_id IN ( " +
            "    SELECT team_id " +
            "    FROM assignment " +
            "    WHERE employee_code = #{employeeCode} " +
            ") " +
            "AND r.employee_code = ( " +
            "    SELECT employee_code " +
            "    FROM report " +
            "    WHERE report_id = #{reportId} " +
            ") " +
            "AND r.is_manager = false;")
    int countIsManager(int employeeCode, int reportId);
    //test****************************
//    @Select("SELECT user.employee_code FROM user JOIN assignment ON assignment.employee_code = user.employee_code WHERE assignment.is_manager = 1 AND assignment.team_id = 1;")
//    void allManagersByAssignment(int teamId);
//
//    @Select("SELECT user.employee_code FROM user JOIN assignment ON assignment.employee_code = user.employee_code WHERE assignment.is_manager = 0 AND assignment.team_id = 1;")
//    void allMembersByAssignment(int teamId);


}
