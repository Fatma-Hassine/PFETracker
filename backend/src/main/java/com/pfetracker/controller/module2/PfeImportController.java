package com.pfetracker.controller.module2;

import com.pfetracker.dto.module2.PfeImportResultDTO;
import com.pfetracker.service.module2.PfeImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v2/pfeTrack/import")
@RequiredArgsConstructor
public class PfeImportController {

    private final PfeImportService pfeImportService;

    @PostMapping("/affectations")
    public PfeImportResultDTO importAffectations(@RequestParam("file") MultipartFile file) {
        return pfeImportService.importAffectations(file);
    }
}