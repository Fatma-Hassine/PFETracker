package com.pfetracker.exception.module1;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
	 private int status;
	    private String code;
	    private String message;
	    private LocalDateTime timestamp;

	    // Détail des erreurs de validation 
	    private List<FieldError> errors;

	    @Data
	    @AllArgsConstructor
	    public static class FieldError {
	        private String champ;
	        private String message;
	    }
}
