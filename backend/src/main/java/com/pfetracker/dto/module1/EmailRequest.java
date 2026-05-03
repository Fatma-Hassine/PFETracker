package com.pfetracker.dto.module1;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequest {
	@NotBlank
    @Email
    private String email;
}
