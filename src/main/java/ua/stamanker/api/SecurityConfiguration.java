package ua.stamanker.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ua.stamanker.api.services.UserService;
import ua.stamanker.api.web.UserLoginFilter;

import javax.servlet.Filter;
import java.util.Collection;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Slf4j
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private UserService userService;

    @Autowired
    public SecurityConfiguration(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        log.info("Configure security...");
        httpSecurity.userDetailsService(userService);
        httpSecurity
                .addFilterBefore(createUserLoginFilter(), UsernamePasswordAuthenticationFilter.class)
                .cors().disable()
                .csrf().disable()
                .authorizeRequests()
//                .antMatchers("/favicon.ico").anonymous()
                .antMatchers("/api/*").hasAnyRole("USER")
        ;
        httpSecurity.headers()
                .frameOptions().sameOrigin()
                .httpStrictTransportSecurity().disable();
    }

    private Filter createUserLoginFilter() {
        return new UserLoginFilter("/");
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(new AuthenticationProvider() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                log.debug("auth: " + authentication);
                if (authentication.isAuthenticated()) {
                    log.trace("already done");
                    return authentication;
                }
                UserDetails userDetails = userService.loadUserByUsername(authentication.getName());
                if (userDetails != null) {
                    authentication.setAuthenticated(true);
                    return authentication;
                }
                throw new BadCredentialsException("Invalid login/password");
            }

            @Override
            public boolean supports(Class<?> authentication) {
                return true;
            }
        });
    }
}
