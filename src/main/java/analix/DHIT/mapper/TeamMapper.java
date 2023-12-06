package analix.DHIT.mapper;

import analix.DHIT.model.Report;
import analix.DHIT.model.Team;
import analix.DHIT.model.User;
import analix.DHIT.model.Assignment;
import org.apache.ibatis.annotations.*;

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
}
