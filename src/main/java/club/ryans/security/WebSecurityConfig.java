package club.ryans.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.bind.annotation.ControllerAdvice;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@ControllerAdvice
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.csrf().ignoringAntMatchers("/api/public/**")
                .and().authorizeRequests()
                .antMatchers("/login", "/foo").authenticated()
                .antMatchers("/admin", "/admin/**", "/api/admin/**", "/script/admin.js")
                .access("hasRole('ADMIN')")
                .antMatchers("/**")
                .permitAll();
        /*
        http.authorizeRequests().antMatchers("/", "/index.html", "/scripts/**", "/styles/**", "faveicon.ico", "/images/**").permitAll()
            .anyRequest().authenticated()
            .and().formLogin().loginPage("/login").permitAll()
            .and().logout() .permitAll();
         */
    }

    /*
    @Bean
    public AccessDecisionManager accessDecisionManager(
            final WebsiteSecurityExpressionHandler securityExpressionHandler) {
        WebExpressionVoter webExpressionVoter = new WebExpressionVoter();
        webExpressionVoter.setExpressionHandler(securityExpressionHandler);
        List<AccessDecisionVoter<?>> decisionVoters =
                Arrays.asList(webExpressionVoter, new RoleVoter(), new AuthenticatedVoter());
        return new AffirmativeBased(decisionVoters);
    }
     */
}
