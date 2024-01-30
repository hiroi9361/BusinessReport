package analix.DHIT.input;

import analix.DHIT.model.Assignment;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AssignmentAllCreateInput {
    private String memberList;

    public String getMemberList() {
        return memberList;
    }

    public void setMemberList(String memberList) {
        this.memberList = memberList;
    }

    public String getManagerList() {
        return managerList;
    }

    public void setManagerList(String managerList) {
        this.managerList = managerList;
    }

    private String managerList;
}
