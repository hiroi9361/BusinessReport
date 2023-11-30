package analix.DHIT.config;


import analix.DHIT.model.User;
import analix.DHIT.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public LoginUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String employeeCode) throws UsernameNotFoundException {
        User loginUser = userRepository.selectByEmployeeCode(Integer.parseInt(employeeCode));
        return CustomUser.withName(employeeCode).password(loginUser.getPassword()).fullName(loginUser.getName()).authorities(AuthorityUtils.createAuthorityList("ROLE_" + loginUser.getRole())).build();
    }

}
