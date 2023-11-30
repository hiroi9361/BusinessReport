package analix.DHIT.repository;

import analix.DHIT.mapper.UserMapper;
import analix.DHIT.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;

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

}
