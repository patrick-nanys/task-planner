package com.kivik.taskplanner.config;

import com.kivik.taskplanner.entities.Privilege;
import com.kivik.taskplanner.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true
)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    private final static Logger LOGGER = Logger.getLogger(SecurityConfig.class.getName());

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
        LOGGER.log(Level.INFO, "User configured");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/", "/register", "/register_success", "/logout", "/css/**", "/img/**").permitAll()
                .antMatchers("/user/**").hasAuthority(Privilege.USER_ADMIN)
                .antMatchers("/task/**").hasAuthority(Privilege.TASK_ADMIN)
                .antMatchers("/team/**").hasAuthority(Privilege.TASK_ADMIN)
                .antMatchers("/role/**").hasAuthority(Privilege.ROLE_ADMIN)
                .antMatchers("/privilege/**").hasAuthority(Privilege.PRIVILEGE_ADMIN)
                .antMatchers("/profile/**").hasAuthority(Privilege.PROFILE_ADMIN)
                .anyRequest().authenticated()
                .and().logout().invalidateHttpSession(true).deleteCookies("JSESSIONID").logoutSuccessUrl("/")
                .and().formLogin().and().httpBasic();
        LOGGER.log(Level.INFO, "Authorities and privileges configured");
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        LOGGER.log(Level.INFO, "Request configuration setup is done");

        return source;
    }

    @Bean
    public PasswordEncoder encoder() {
        LOGGER.log(Level.INFO, "Password encoded");
        return new BCryptPasswordEncoder();
    }
}
