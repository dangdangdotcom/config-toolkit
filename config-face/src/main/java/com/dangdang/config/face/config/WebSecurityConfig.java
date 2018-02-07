package com.dangdang.config.face.config;

import com.dangdang.config.face.service.INodeService;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private INodeService nodeService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/css/**", "/js/**", "/image/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .successHandler(new SavedRequestAwareAuthenticationSuccessHandler(){
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest request,
                                                        HttpServletResponse response, Authentication authentication)
                            throws ServletException, IOException {
                        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                        nodeService.logLogin(userDetails.getUsername());
                        super.onAuthenticationSuccess(request, response, authentication);
                    }
                })
                .and()
                .logout()
                .permitAll();
        http.csrf().disable();
    }

    @Bean
    public UserDetailsService getUserDetailsService() {
        return username -> {
            final String pass = nodeService.getValue(username);
            if (Strings.isNullOrEmpty(pass)) {
                throw new UsernameNotFoundException(username);
            }

            return new User(username, pass, Lists.newArrayList(new SimpleGrantedAuthority("ADMIN")));
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return Hashing.sha1().hashString(rawPassword, Charsets.UTF_8).toString();
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return Objects.equals(encodedPassword, encode(rawPassword));
            }
        };
    }

}