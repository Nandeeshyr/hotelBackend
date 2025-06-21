	package com.hotel.JWT;
	
	import java.util.Date;
	import java.util.HashMap;
	import java.util.Map;
	import java.util.function.Function;
	
	import org.springframework.security.core.userdetails.UserDetails;
	import org.springframework.stereotype.Service;
	
	import io.jsonwebtoken.Claims;
	import io.jsonwebtoken.Jwts;
	import io.jsonwebtoken.SignatureAlgorithm;
	
	@Service
	public class JwtUtil {
		private String secret = "NandeeshYRNandeeshYRNandeeshYRNandeeshYRNandeeshYR"; //This is the secret key used to sign and verify JWTs.
	
		public String extractUsername(String token) { // Pulls the email (subject) from the token.
			return extractClaims(token, Claims::getSubject);
		}
	
		public Date extractExpiration(String token) {
			return extractClaims(token, Claims::getExpiration);
		}
	
		public <T> T extractClaims(String token, Function<Claims, T> claimsResolver) { //Generic method to extract specific fields from the JWT.
			final Claims claims = extractAllClaims(token);
			return claimsResolver.apply(claims);
		}
	
		public Claims extractAllClaims(String token) { // Parses and returns the full token payload (iat, exp, role, etc.).
			return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
		}
	
		private Boolean isTokenExpired(String token) {
			return extractExpiration(token).before(new Date());
		}
	
		public String generateToken(String username, String role) { //Creates a token with email and role embedded as claims.
			Map<String, Object> claims = new HashMap<>();
			claims.put("role", role);
			return createToken(claims, username);
		}
	
		private String createToken(Map<String, Object> claims, String subject) {
			return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis())) //Sets token lifetime (10 hours here).
					.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
					.signWith(SignatureAlgorithm.HS256, secret).compact(); //Hashes the token using HS256 and your secret.
		}
	
		public Boolean validateToken(String token, UserDetails userDetails) { //Returns true only if the token isn’t expired and belongs to the given user.
			final String username = extractUsername(token);
			return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
		}
	}
