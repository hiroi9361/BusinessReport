package analix.DHIT.input;

import analix.DHIT.model.Assignment;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AssignmentAllCreateInput {
    private String memberList;
    private String managerList;
}
