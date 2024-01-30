package analix.DHIT.repository;

import analix.DHIT.input.UserCreateInput;
import analix.DHIT.mapper.UserMapper;
import analix.DHIT.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository

public class MysqlUserRepository implements UserRepository {

    private final UserMapper userMapper;

    public MysqlUserRepository(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public User selectByEmployeeCode(int employeeCode) {
        return this.userMapper.findByEmployeeCode(employeeCode);
    }

    @Override
    public List<User> selectAllMember() {
        return this.userMapper.selectAllMember();
    }

    @Override
    public List<User> selectMemberBySearchCharacters(String searchCharacters){
        return this.userMapper.selectMemberBySearchCharacters(searchCharacters);
    }
    @Override
    public User save(User user) {
        return null;
    }

    @Override
    public List<User> selectAllEmployeeInfomation(){return this.userMapper.selectAllEmployeeInfo();}

    @Override
    public String getUserName(int employeeCode){
        return this.userMapper.getUserName(employeeCode);

    }

    @Override
    public void saveAll(Set<User> users) {

    }

    //csv関係
    @Override
    public int countByEmployeeCode(int employeeCode) {
        return this.userMapper.countByEmployeeCode(employeeCode);
    }
    @Override
    public int countByEmail(String email){
        return this.userMapper.countByEmail(email);
    }

    @Override
    public User selectUserById(int employeeCode) {
        return this.userMapper.selectUserById(employeeCode);
    }

    @Override
    public void updateEmployee(UserCreateInput userCreateInput){
        this.userMapper.updateEmployee(userCreateInput);
    }
}
