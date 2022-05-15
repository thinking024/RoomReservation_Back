package com.example.roomreservation.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.roomreservation.common.CustomException;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JWTUtil {
    private static final String KEY = "thinking024";

    public static String createToken(Integer id, Integer type) {
        LocalDateTime localDateTime = LocalDateTime.now().plusDays(1);
        ZoneId zoneId = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zoneId).toInstant();
        Date date = Date.from(instant);
        return JWT.create()
                .withClaim("id", id)
                .withClaim("type", type)
                .withExpiresAt(date)
                .sign(Algorithm.HMAC256(KEY));
    }

    public static Map<String, Integer> parseToken(String token) {
        HashMap<String, Integer> map = new HashMap<>();
        try {
            DecodedJWT decodedjwt = JWT.require(Algorithm.HMAC256(KEY)).build().verify(token);
            Claim id = decodedjwt.getClaim("id");
            map.put("id", id.asInt());
            Claim type = decodedjwt.getClaim("type");
            map.put("type", type.asInt());
            log.info("token id=" + id);
            log.info("token type=" + type);
        } catch (Exception e) {
            log.error("parse token error: {}", e.getMessage());
            throw new CustomException(203, "token无效");
        }
        return map;
    }
}
