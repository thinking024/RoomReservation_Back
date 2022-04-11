package com.example.roomreservation.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JWTUtil {
    private static final String KEY = "thinking024";

    // todo token中仅存放id，还是Admin与User的父类Person
    // todo 是否采用用户密码对token进行加密签名
    // todo 以声明的方式放入id，还是以audience的方式放入
    public static String createToken(Integer id){
        /*Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,TIME_OUT_DAY);*/
        String token = JWT.create()
                .withClaim("id", id)
//                .withClaim("key", DigestUtils.md5DigestAsHex(user.getPassword().getBytes()))
//                .withExpiresAt(calendar.getTime())
                .sign(Algorithm.HMAC256(KEY));
        return token;
    }

    public static Map<String, Integer> parseToken(String token) {
        HashMap<String, Integer> map = new HashMap<>();
        DecodedJWT decodedjwt = JWT.require(Algorithm.HMAC256(KEY)).build().verify(token);
        Claim id = decodedjwt.getClaim("id");
        log.info("token id=" + id);
        map.put("id", id.asInt());
        return map;
    }
}
