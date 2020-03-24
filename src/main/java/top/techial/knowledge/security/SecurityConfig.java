package top.techial.knowledge.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import top.techial.knowledge.security.handler.LogoutHandler;


/**
 * @author techial
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final LogoutHandler logoutHandler;
    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final AuthenticationFailureHandler authenticationFailureHandler;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(
            LogoutHandler logoutHandler,
            AuthenticationSuccessHandler authenticationSuccessHandler,
            AuthenticationFailureHandler authenticationFailureHandler,
            AuthenticationEntryPoint authenticationEntryPoint,
            UserDetailsService userDetailsService
    ) {
        this.logoutHandler = logoutHandler;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.authenticationFailureHandler = authenticationFailureHandler;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
            .formLogin()
            .loginPage("/login")
            .loginProcessingUrl("/api/user/login")
            .permitAll()
            .successHandler(authenticationSuccessHandler)
            .failureHandler(authenticationFailureHandler)
        .and()
            .logout()
            .logoutUrl("/api/user/logout")
            .permitAll()
            .logoutSuccessHandler(logoutHandler)
        .and()
            .exceptionHandling()
            .defaultAuthenticationEntryPointFor(authenticationEntryPoint, new AntPathRequestMatcher("/api/**"))
        .and()
            .authorizeRequests()
            .antMatchers("/api/user/me", "/api/register/**").permitAll()
            .antMatchers(HttpMethod.GET, "/api/storage/text/**").permitAll()
            .antMatchers(HttpMethod.GET, "/api/storage/preview/**").permitAll()
            .antMatchers(HttpMethod.GET, "/api/node/**").permitAll()
            .antMatchers(HttpMethod.GET, "/api/search/**").permitAll()
            .antMatchers("/api/**").authenticated()
        .and()
            .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        .and()
            .rememberMe().useSecureCookie(true).userDetailsService(userDetailsService);
        // @formatter:on
    }
}

