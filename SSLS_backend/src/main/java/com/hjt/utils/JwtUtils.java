package com.hjt.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Map;

public class JwtUtils {

    // The signing key's size >= 256 bits
    private static String signKey = "SorceressSorceressSorceressSorceressSorceressSorceressSorceressSorceressSorceressSorceress";
    private static Long expire = 86400000L;

    /**
     * 生成jwt令牌
     *
     * @param claims 载荷
     * @return JWT
     */
    public static String generateJwt(Map<String, Object> claims) {
        String jwt = Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, signKey)
                .setExpiration(new Date(System.currentTimeMillis() + expire))
                .compact();
        return jwt;
    }

    /**
     * 解析jwt令牌
     *
     * @param jwt jwt令牌
     * @return 载荷
     */
    public static Claims parseJwt(String jwt) {
        Claims claims = Jwts.parser()
                .setSigningKey(signKey)
                .parseClaimsJws(jwt)
                .getBody();
        return claims;
    }
}
