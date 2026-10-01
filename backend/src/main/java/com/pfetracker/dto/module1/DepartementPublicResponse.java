package com.pfetracker.dto.module1;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Vue publique minimale d'un département — utilisée par le formulaire d'inscription. */
@Getter
@AllArgsConstructor
public class DepartementPublicResponse {
    private Long id;
    private String nom;
}
