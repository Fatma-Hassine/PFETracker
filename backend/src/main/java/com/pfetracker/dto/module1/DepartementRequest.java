package com.pfetracker.dto.module1;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartementRequest {
	@NotBlank
    private String nom;

    @NotBlank
    private String code;
}
