package com.pfetracker.config.module1;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("appPropertiesM1")
@ConfigurationProperties(prefix = "app")
@Data
public class AppProperties {
	 private Cors cors = new Cors();
	    private Email email = new Email();
	    private Security security = new Security();
	    private Audit audit = new Audit();

	    @Data
	    public static class Cors {
	        private List<String> allowedOrigins;
	    }

	    @Data
	    public static class Email {
	        private String domaineAutorise = "@institution.edu";
	    }

	    @Data
	    public static class Security {
	        private int maxTentativesConnexion = 5;
	        private int dureeVerrouillageMinutes = 30;
	        private int dureeResetTokenHeures = 1;
	        private int bcryptStrength = 12;
	    }

	    @Data
	    public static class Audit {
	        private int conservationMois = 6;
	    }
}
