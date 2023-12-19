package analix.DHIT.repository;

import analix.DHIT.model.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface UserRepository {
    User selectByEmployeeCode(int employeeCode);
    List<User> selectAllMember();
    List<User> selectMemberBySearchCharacters(String searchCharacters);
    User save(User user);
    List<User> selectAllEmployeeInfomation();
    String getUserName(int employeeCode);

    void saveAll(Set<User> users);
}
