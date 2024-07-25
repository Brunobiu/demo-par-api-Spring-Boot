package com.mballem.demo_par_api.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;


@Slf4j
public class JwtUtils {
    public static final String JWT_BEARER = "Bearer";
    public static final String JWT_AUTHORIZATION = "Authorization";
    public static final String SECRET_KEY = "0123456789-0123456789-0123456789";
    public static final long EXPIRE_DAYS = 0;
    public static final long EXPIRE_HOURS = 0;
    public static final long EXPIRE_MINUTES = 2;
    private static final Logger log = LoggerFactory.getLogger(JwtUtils.class);

    private JwtUtils() {
    }

    /** Gera uma chave para assinar tokens JWT. @return a chave gerada */
    private static Key generateKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    /** Calcula a data de expiração para o token com base na data inicial. @param start a data inicial @return a data de expiração */
    private static Date toExpireDate(Date start) {
        LocalDateTime dateTime = start.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime end = dateTime.plusDays(EXPIRE_DAYS).plusHours(EXPIRE_HOURS).plusMinutes(EXPIRE_MINUTES);
        return Date.from(end.atZone(ZoneId.systemDefault()).toInstant());
    }

    /** Cria um token JWT com o nome de usuário e função fornecidos. @param username o nome de usuário @param role a função @return um objeto JwtToken contendo o token */
    public static JwtToken createToken(String username, String role) {
        Date issedAt = new Date();
        Date limit = toExpireDate(issedAt);

        String token = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(username)
                .setIssuedAt(issedAt)
                .setExpiration(limit)
                .signWith(generateKey(), SignatureAlgorithm.HS256)
                .claim("role", role)
                .compact();

        return new JwtToken(token);
    }

    /** Extrai as reivindicações do token JWT fornecido. @param token o token JWT @return as reivindicações extraídas do token, ou null se inválido */
    private static Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(generateKey()).build()
                    .parseClaimsJws(refactorToken(token)).getBody();
        } catch (JwtException ex) {
            log.error(String.format("Token inválido %s", ex.getMessage()));
        }
        return null;
    }

    /** Extrai o nome de usuário do token JWT fornecido. @param token o token JWT @return o nome de usuário extraído do token */
    public static String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    /** Valida o token JWT fornecido. @param token o token JWT @return true se o token for válido, false caso contrário */
    public static boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(generateKey()).build()
                    .parseClaimsJws(refactorToken(token));
            return true;
        } catch (JwtException ex) {
            log.error(String.format("Token inválido %s", ex.getMessage()));
        }
        return false;
    }

    /** Remove o prefixo "Bearer" do token, se presente. @param token o token JWT @return o token sem o prefixo "Bearer" */
    private static String refactorToken(String token) {
        if (token.contains(JWT_BEARER)) {
            return token.substring(JWT_BEARER.length());
        }
        return token;
    }
}



