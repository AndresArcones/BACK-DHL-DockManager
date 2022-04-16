package com.andres.Proyecto_Fin_de_Grado.Security;

import com.andres.Proyecto_Fin_de_Grado.Security.filter.FiltroAutentificacion;
import com.andres.Proyecto_Fin_de_Grado.Security.filter.FiltroAutorizacion;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import static org.springframework.http.HttpMethod.GET;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();
        http.cors().configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues());
        FiltroAutentificacion filtroAutentificacion = new FiltroAutentificacion(authenticationManager());
        filtroAutentificacion.setFilterProcessesUrl("/api/login");

        http.sessionManagement().sessionCreationPolicy(STATELESS);

        /*//users
        http.authorizeRequests().antMatchers("/api/register").permitAll();
        http.authorizeRequests().antMatchers("/api/users").hasAuthority("ROL_ADMIN");
        http.authorizeRequests().antMatchers("/api/role/**").hasAuthority("ROL_ADMIN");
        http.authorizeRequests().antMatchers("/api/user/**").hasAuthority("ROL_ADMIN");
        http.authorizeRequests().antMatchers("/api/user/**").hasAuthority("ROL_USER");

        //videos
        http.authorizeRequests().antMatchers("/api/videos").hasAuthority("ROL_ADMIN");
        http.authorizeRequests().antMatchers("/api/videos").hasAuthority("ROL_USER");
        http.authorizeRequests().antMatchers("/api/video/**").hasAuthority("ROL_ADMIN");
        http.authorizeRequests().antMatchers("/api/video/**").hasAuthority("ROL_USER");*/

        //http.authorizeRequests().antMatchers("/api/reservas").hasAuthority("ROL_ADMIN");
        http.authorizeRequests().antMatchers("**").permitAll();


        http.authorizeRequests().anyRequest().authenticated();
        http.addFilter(filtroAutentificacion);
        http.addFilterBefore(new FiltroAutorizacion(), UsernamePasswordAuthenticationFilter.class); //filtro de autorización para cada llamada


    }

    @Bean
    @Override
    public AuthenticationManager authenticationManager() throws Exception{
        return super.authenticationManager();
    }
}
