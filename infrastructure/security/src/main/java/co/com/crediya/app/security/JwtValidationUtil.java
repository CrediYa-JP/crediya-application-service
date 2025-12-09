package co.com.crediya.app.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPublicKey;

@Component
@RequiredArgsConstructor
public class JwtValidationUtil {

    private final RSAPublicKey publicKey;

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseClaimsJws(token)
                .getPayload();
    }

    public String getRole(String token) {
        return extractAllClaims(token).get("scope", String.class);
    }

    public String getEmail(String token) {
        return extractAllClaims(token).getSubject();
    }
}
