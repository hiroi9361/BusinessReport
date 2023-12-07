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

    @Insert("INSERT INTO team(name) " + "VALUES(#{name})")
    @Options(useGeneratedKeys = true, keyColumn = "team_id", keyProperty = "teamId")
    void insertTeam(Team team);

    @Update("UPDATE team SET name=#{name} WHERE team_id = #{teamId}")
    void updateTeam(Team team);

    @Delete("DELETE FROM team WHERE team_id = #{teamId}")
    void deleteById(int teamId);

//    ここからAssignment
    @Select("SELECT * FROM assignment WHERE employee_code = #{employeeCode}")
    List<Assignment> selectByEmployeeCode(int employeeCode);

    @Select("SELECT * FROM assignment WHERE team_id = #{teamId}")
    List<Assignment> selectByTeamId(int teamId);

    @Insert("INSERT INTO assignment(is_manager, team_id, employee_code) " +
            "VALUES(#{isManager}, #{teamId}, #{employeeCode})")
    @Options(useGeneratedKeys = true, keyColumn = "assignment_id", keyProperty = "assignmentId")
    void insertAssignment(Assignment assignment);

    @Update("UPDATE assignment SET is_manager=#{isManager},team_id=#{teamId} WHERE employee_code=#{employeeCode}")
    void updateAssignment(Assignment assignment);

    @Delete("DELETE FROM assignment WHERE assignment_id = #{assignmentId}")
    void deleteAssignmentById(int assignmentId);

    @Select("SELECT COUNT(*) FROM assignment WHERE employee_code = #{employeeCode} AND team_id = #{teamId}")
    int countAssignmentByEmployeeCodeAndTeamId(int employeeCode, int teamId);

}
