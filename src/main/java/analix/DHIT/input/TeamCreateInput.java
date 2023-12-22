package analix.DHIT.input;
import analix.DHIT.model.Assignment;
import analix.DHIT.model.Team;

import java.util.ArrayList;
import java.util.List;

public class TeamCreateInput {

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public boolean getRelase(){
        return release;
    }
    public void setRelease(boolean release){
        this.release=release;
    }

    private String name;
    private boolean release;

}
