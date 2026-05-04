package com.pfetracker.dto.module1;
import com.pfetracker.entity.module1.enums.Role;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreerCompteRequest {
	 @NotBlank @Email
	    private String email;

	    @NotBlank
	    private String nomComplet;

	    @NotNull
	    private Role role;
}
