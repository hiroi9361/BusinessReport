package analix.DHIT.repository;

import analix.DHIT.model.Report;
import analix.DHIT.model.Team;
import analix.DHIT.model.Assignment;
import java.util.List;
public interface TeamRepository {

    List<Team> selectAllTeam();
    Team selectByTeamId(int teamId);
    void save(Team team);

    void update(Team team);

    Team findById(int teamId);

    void deleteById(int teamtId);

    List<Team>selectTeamByEmployeeCode(int employeeCode);

    //csv関係
    Integer selectTeamIdByName(String name);

}
