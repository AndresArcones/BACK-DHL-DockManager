package com.andres.Proyecto_Fin_de_Grado.utilidades;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

public class DecodificarJWT {

    public static JWT decode(String authorizationHeader){
        String token = authorizationHeader.substring("Bearer ".length()); //solo queremos el token
        Algorithm algoritmo_de_descifra = Algorithm.HMAC256("clave secreta".getBytes()); //algoritmo para desencriptar//inseguro esconder la clave en otro fichero y refactorizar
        JWTVerifier verificadorJWT = com.auth0.jwt.JWT.require(algoritmo_de_descifra).build();
        DecodedJWT decodedJWT = verificadorJWT.verify(token);
        String usuario = decodedJWT.getSubject();
        String[] roles = decodedJWT.getClaim("roles").asArray(String.class);

        return new JWT(usuario,roles);
    }
}

