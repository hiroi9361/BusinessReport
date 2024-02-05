package analix.DHIT.repository;

import analix.DHIT.mapper.TeamMapper;
import analix.DHIT.model.Report;
import analix.DHIT.model.Team;
import analix.DHIT.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MysqlTeamRepository implements TeamRepository{

    private final TeamMapper teamMapper;

    public MysqlTeamRepository(TeamMapper teamMapper) {
        this.teamMapper = teamMapper;
    }

    @Override
    public List<Team> selectAllTeam() {
        return this.teamMapper.selectAllTeam();
    }

    @Override
    public Team selectByTeamId(int teamId){
        return this.teamMapper.SelectById(teamId);
    }

    @Override
    public Team findById(int teamId) {
        return this.teamMapper.SelectById(teamId);
    }

    @Override
    public void save(Team team) {
        this.teamMapper.insertTeam(team);
    }

    @Override
    public void update(Team team) {
        this.teamMapper.updateTeam(team);
    }

    @Override
    public void deleteById(int teamId) {
        this.teamMapper.deleteById(teamId);
    }

    @Override
    public List<Team>selectTeamByEmployeeCode(int employeeCode){
        return this.teamMapper.selectTeamByEmployeeCode(employeeCode);
    }

    //csv関係
    @Override
    public Integer selectTeamIdByName(String name){
        return this.teamMapper.selectTeamIdByName(name);
    }

}
