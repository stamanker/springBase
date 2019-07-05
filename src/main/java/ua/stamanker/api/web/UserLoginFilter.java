package ua.stamanker.api.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

import static java.util.Collections.singletonList;

@Slf4j
public class UserLoginFilter extends AbstractAuthenticationProcessingFilter {

    public UserLoginFilter(String defaultFilterProcessesUrl) {
        super(new AntPathRequestMatcher("/api/**", "GET"));
        log.info("defaultFilterProcessesUrl = " + defaultFilterProcessesUrl);
        setAuthenticationSuccessHandler((request, response, authentication) -> {
            log.info("skip...");
        });
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        log.info("uri = {}", request.getRequestURI());
        String userLogin = Optional.ofNullable(request.getHeader("X-USER"))
                .orElseGet(() -> request.getParameter("login"));
        if (userLogin != null) {
            return new Authentication() {
                @Override
                public Collection<? extends GrantedAuthority> getAuthorities() {
                    return singletonList(new SimpleGrantedAuthority("USER"));
                }

                /**
                 * The credentials that prove the principal is correct. This is usually a password,
                 * but could be anything relevant to the <code>AuthenticationManager</code>. Callers
                 * are expected to populate the credentials.
                 *
                 * @return the credentials that prove the identity of the <code>Principal</code>
                 */
                @Override
                public Object getCredentials() {
                    return "credentials".getBytes();
                }

                /**
                 * Stores additional details about the authentication request. These might be an IP
                 * address, certificate serial number etc.
                 *
                 * @return additional details about the authentication request, or <code>null</code>
                 * if not used
                 */
                @Override
                public Object getDetails() {
                    return "details";
                }

                /**
                 * The identity of the principal being authenticated. In the case of an authentication
                 * request with username and password, this would be the username. Callers are
                 * expected to populate the principal for an authentication request.
                 * <p>
                 * The <tt>AuthenticationManager</tt> implementation will often return an
                 * <tt>Authentication</tt> containing richer information as the principal for use by
                 * the application. Many of the authentication providers will create a
                 * {@code UserDetails} object as the principal.
                 *
                 * @return the <code>Principal</code> being authenticated or the authenticated
                 * principal after authentication.
                 */
                @Override
                public Object getPrincipal() {
                    return userLogin;
                }

                @Override
                public boolean isAuthenticated() {
                    return true;
                }

                @Override
                public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
                    System.out.println("UserLoginFilter.setAuthenticated");
                }

                @Override
                public String getName() {
                    return userLogin;
                }
            };
        }
        throw new BadCredentialsException("fail");
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);
        // to continue process
        chain.doFilter(request, response);
    }

}
