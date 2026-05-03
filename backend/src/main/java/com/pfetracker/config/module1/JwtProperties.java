package com.pfetracker.config.module1;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component("jwtPropertiesM1")
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtProperties {
	 private String secret;
	    private long accessTokenExpiration;
	    private long refreshTokenExpiration;

}
