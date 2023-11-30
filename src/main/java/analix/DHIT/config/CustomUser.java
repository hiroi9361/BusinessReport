package analix.DHIT.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CustomUser extends User {

    private String fullName;

    public CustomUser(String username, String password, Collection<? extends GrantedAuthority> authorities, String fullName) {
        super(username, password, authorities);
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public static CustomUserBuilder withName(String username) {
        return new CustomUserBuilder(username);
    }

    public static class CustomUserBuilder {

        private final String username;
        private String password;
        private Collection<? extends GrantedAuthority> authorities;
        private String fullName;

        public CustomUserBuilder(String username) {
            this.username = username;
        }

        public CustomUserBuilder password(String password) {
            this.password = password;
            return this;
        }

        public CustomUserBuilder authorities(Collection<? extends GrantedAuthority> authorities) {
            this.authorities = authorities;
            return this;
        }

        public CustomUserBuilder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public CustomUser build() {
            return new CustomUser(username, password, authorities, fullName);
        }
    }
}