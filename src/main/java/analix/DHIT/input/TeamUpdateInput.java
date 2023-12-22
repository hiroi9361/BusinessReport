package analix.DHIT.input;

import analix.DHIT.model.Team;
public class TeamUpdateInput {

    private int teamId;
    private String name;
    private boolean release;

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getRelease(){
        return release;
    }

    public void setRelease(boolean release){this.release=release;}
}
