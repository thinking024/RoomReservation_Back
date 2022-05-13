package com.example.roomreservation.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JWTUtil {
    private static final String KEY = "thinking024";

    public static String createToken(Integer id, Integer type) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        return JWT.create()
                .withClaim("id", id)
                .withClaim("type", type)
                .withExpiresAt(calendar.getTime())
                .sign(Algorithm.HMAC256(KEY));
    }

    public static Map<String, Integer> parseToken(String token) {
        HashMap<String, Integer> map = new HashMap<>();
        DecodedJWT decodedjwt = JWT.require(Algorithm.HMAC256(KEY)).build().verify(token);
        Claim id = decodedjwt.getClaim("id");
        log.info("token id=" + id);
        map.put("id", id.asInt());

        Claim type = decodedjwt.getClaim("type");
        log.info("token type=" + type);
        map.put("type", type.asInt());
        return map;
    }
}
