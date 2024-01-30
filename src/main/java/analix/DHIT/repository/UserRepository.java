package analix.DHIT.repository;

import analix.DHIT.input.UserCreateInput;
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

    //csv関係
    int countByEmployeeCode(int employeeCode);
    int countByEmail(String email);
    User selectUserById(int employeeCode);
    void updateEmployee(UserCreateInput userCreateInput);
}
