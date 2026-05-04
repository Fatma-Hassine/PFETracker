package com.pfetracker.security.module1;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfetracker.exception.module1.ErrorResponse;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component("customAccessDeniedHandlerM1")

@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
	 private final ObjectMapper objectMapper;

	    @Override
	    public void handle(HttpServletRequest request,
	                       HttpServletResponse response,
	                       AccessDeniedException ex) throws IOException {

	        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
	        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

	        ErrorResponse error = ErrorResponse.builder()
	                .status(403)
	                .code("ACCESS_DENIED")
	                .message("Vous n'avez pas les droits nécessaires pour cette action")
	                .timestamp(LocalDateTime.now())
	                .build();

	        objectMapper.writeValue(response.getOutputStream(), error);
	    }
}
