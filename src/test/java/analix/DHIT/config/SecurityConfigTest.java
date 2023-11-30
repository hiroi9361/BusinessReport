package analix.DHIT.config;

import com.fasterxml.jackson.databind.JsonSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class SecurityConfigTest {

    @Test
    void test(){
        System.out.println(new MessageDigestPasswordEncoder("SHA-256").encode("aaa"));
    }

}