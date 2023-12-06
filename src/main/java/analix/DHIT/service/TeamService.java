package analix.DHIT.service;

import analix.DHIT.exception.ReportNotFoundException;
import analix.DHIT.input.ReportUpdateInput;
import analix.DHIT.input.TeamUpdateInput;
import analix.DHIT.mapper.TeamMapper;
import analix.DHIT.model.Report;
import analix.DHIT.model.Team;
import analix.DHIT.repository.TeamRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;

    public TeamService(TeamRepository teamRepository, TeamMapper teamMapper) {
        this.teamRepository = teamRepository;
        this.teamMapper = teamMapper;
    }

    public List<Team> getAllTeam(){return this.teamRepository.selectAllTeam();}


    public Team getTeamById(int teamId) {
        //↓ @Select("SELECT * FROM report WHERE id = #{reportId}")これが入る
        Team team = teamRepository.findById(teamId);
        if (team == null) {
            throw new ReportNotFoundException("Team Not Found");
        }
        return team;
    }

    public int create(
            String name
    ) {
        Team newTeam = new Team();
        newTeam.setName(name);

        this.teamRepository.save(newTeam);

        return newTeam.getTeamId();
    }

    public void update(TeamUpdateInput teamUpdateInput){

        Team team = this.teamMapper.SelectById(teamUpdateInput.getTeamId());

        team.setName(teamUpdateInput.getName());

        this.teamRepository.update(team);

    }

    public void deleteById(int teamId) {
        this.teamRepository.deleteById(teamId);
    }

}
