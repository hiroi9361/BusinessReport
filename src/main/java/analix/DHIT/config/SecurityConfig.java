package analix.DHIT.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new MessageDigestPasswordEncoder("SHA-256");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.formLogin(login -> login
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/")
                .permitAll()

        ).logout(
                logout -> logout

                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")

        ).authorizeHttpRequests(auth -> auth
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
//                .requestMatchers("/login").permitAll()
                .requestMatchers("/common/**").permitAll()
                .requestMatchers("/manager/create").hasAnyRole("ADMIN")
                .requestMatchers("/manager/employeeList").hasAnyRole("ADMIN")
                .requestMatchers("/manager/employeeList-edit").hasAnyRole("ADMIN")
                .requestMatchers("/manager/team-create").hasAnyRole("ADMIN", "USER")
                .requestMatchers("/manager/team-detail").hasAnyRole("ADMIN", "USER")
                .requestMatchers("/manager/team-edit").hasAnyRole("ADMIN", "USER")
                .requestMatchers("/manager/teamlist").hasAnyRole("ADMIN", "USER")
                .requestMatchers("/manager/teams/**").hasAnyRole("ADMIN", "USER")
                .requestMatchers("/manager/assignment/**").hasAnyRole("ADMIN", "USER")
//                .requestMatchers("/manager/**").hasRole("ADMIN")
                .requestMatchers("/member/**").hasRole("USER")
                .requestMatchers("/member/**").hasRole("USER")
                .requestMatchers("/manager/home/**").hasRole("USER")
                .requestMatchers("/manager/report-search").hasRole("USER")
                .anyRequest().authenticated()

        ).exceptionHandling(ex -> ex.accessDeniedPage("/"));

        return http.build();

    }
}


