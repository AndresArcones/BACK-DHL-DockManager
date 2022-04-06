package com.andres.Proyecto_Fin_de_Grado.Security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
public class FiltroAutentificacion extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    //falta añadir que se mande la info por el body con objectmapper
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String email = request.getParameter("username");
        String password = request.getParameter("password");

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);

        return authenticationManager.authenticate(authenticationToken);
    }

    //creacion del JWT (Json Web Token)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        org.springframework.security.core.userdetails.User usuario = (org.springframework.security.core.userdetails.User) authResult.getPrincipal(); //obtenemos el usuario autenticado
        Algorithm algoritmo_de_cifra = Algorithm.HMAC256("clave secreta".getBytes()); //inseguro esconder la clave en otro fichero
        String tokenAcceso = JWT.create()
                .withSubject(usuario.getUsername()) //añadimos el usuario al token
                .withExpiresAt(new Date(System.currentTimeMillis() + 1 * 60 * 60 * 1000)) //añadimos el tiempo de expiración a 1 hora
                .withIssuer(request.getRequestURL().toString()) //añadimos la url de la api rest como el creador del token
                .withClaim("roles", usuario.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList())) //añadimos los roles
                .sign(algoritmo_de_cifra);

        //response.setHeader("token", tokenAcceso);

        Map<String, String> token = new HashMap<>();
        token.put("token", tokenAcceso);
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(),token);

    }
}
