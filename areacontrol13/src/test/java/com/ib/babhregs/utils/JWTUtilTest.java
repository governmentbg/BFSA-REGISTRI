package com.ib.babhregs.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.junit.Ignore;
import org.junit.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class JWTUtilTest {



    @Test
    public void generateJWT() {

        JWTUtil jwtUtil = new JWTUtil();

        String[] roles = {"ADMIN", "USER"};
        List rolesL = Arrays.asList("ROLE_USER", "ROLE_ADMIN");

        jwtUtil.getClaims().put("ROLES",rolesL);
        jwtUtil.getClaims().put("userId",-1);
        jwtUtil.getClaims().put("names","Ali Baba");
        String s = jwtUtil.setIssuer("AliBaba").generateJWT();
        assertNotNull(s);
        System.out.println(s);
        Jws<Claims> claimsJws = jwtUtil.decodeJWT(s);
        System.out.println(claimsJws.getPayload());
        System.out.println("Not Before:"+claimsJws.getPayload().getNotBefore());
        System.out.println("Expiration:"+claimsJws.getPayload().getExpiration());
        System.out.println("names:"+claimsJws.getPayload().get("names"));
        System.out.println("userId:"+claimsJws.getPayload().get("userId"));
        System.out.println("Roles:"+claimsJws.getPayload().get("ROLES"));

    }

    @Test
    public void decodeJWT() {
        JWTUtil jwtUtil = new JWTUtil();
        String s = jwtUtil.setIssuer("AliBaba").generateJWT();
        assertNotNull(s);
        System.out.println(s);
        Jws<Claims> claimsJws = jwtUtil.decodeJWT(s);
        assertEquals(claimsJws.getPayload().getIssuer(),"AliBaba");
    }

    @Test
    public void validate() {

    }

    @Ignore("the testcase is under development")
    @Test
    public void decodeBULSi() {
        String token="eyJhbGciOiJIUzI1NiJ9.eyJST0xFUyI6WyJST0xFX0FETUlOIl0sInN1YiI6ImFkbWluIiwianRpIjoiODA3ZjhhN2QtMTUwYS00ZWRmLTkzZDYtYmEyNmYyNThhODJhIiwiYXVkIjpbImJmc2EtZmUiXSwiaXNzIjoiYmZzYS1iZSIsImlhdCI6MTcwMTI2MzM3MiwiZXhwIjoxNzAxMjY2OTcyfQ.qlcLE41eFkfFogyJb-LQ1rr21KJBHu7_0YVNC5vT_BM";
        String secretKey="secretPFwciNdvrrPc5E3kTTJaqg4iCiQ";
        try {
            JWTUtil jwtUtil = new JWTUtil(secretKey);
            //Jws<Claims> claimsJws = jwtUtil.decodeJWT(token);
//            System.out.println("Header:"+claimsJws.getPayload());
//            System.out.println("Body:"+claimsJws.getPayload());
            SecretKey secretKey1 = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
            Jwt tmpJwt = Jwts.parser()
                    .verifyWith(secretKey1)
                    .build()
                    .parse(token);
            System.out.println(tmpJwt.getPayload());

        }catch (ExpiredJwtException e) {
            System.out.println("---" + e.getClaims().getExpiration());
            e.printStackTrace();
            fail();
        }catch (SignatureException e){
            e.printStackTrace();
            fail();
        }catch (Exception e){
            e.printStackTrace();
            fail();
        }
    }
    @Test
    public void validateBulSi(){
        String token="eyJhbGciOiJIUzI1NiJ9.eyJST0xFUyI6WyJST0xFX0FETUlOIl0sInN1YiI6ImFkbWluIiwianRpIjoiODA3ZjhhN2QtMTUwYS00ZWRmLTkzZDYtYmEyNmYyNThhODJhIiwiYXVkIjpbImJmc2EtZmUiXSwiaXNzIjoiYmZzYS1iZSIsImlhdCI6MTcwMTI2MzM3MiwiZXhwIjoxNzAxMjY2OTcyfQ.qlcLE41eFkfFogyJb-LQ1rr21KJBHu7_0YVNC5vT_BM";
        String secretKey="secretPFwciNdvrrPc5E3kTTJaqg4iCiQ";
        try {
            JWTUtil jwtUtil = new JWTUtil(secretKey);
            assertTrue(jwtUtil.validate(token));
            Jws<Claims> claimsJws = jwtUtil.decodeJWT(token);
            System.out.println("Header:"+claimsJws.getPayload());
            System.out.println("Body:"+claimsJws.getPayload());
        }catch (Exception e){
            e.printStackTrace();
            fail();
        }
    }
}