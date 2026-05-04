package com.pfetracker.service.module2;

import com.pfetracker.dto.module2.CreatePfeRequest;
import com.pfetracker.dto.module2.PfeImportResultDTO;
import com.pfetracker.repository.module2.PfeRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PfeImportService {

    private final PfeService pfeService;
    private final PfeRepository pfeRepository;

    public PfeImportResultDTO importAffectations(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Le fichier Excel est vide.");
        }

        String fileName = file.getOriginalFilename();

        if (fileName == null || !fileName.endsWith(".xlsx")) {
            throw new RuntimeException("Format non supporté. Utilisez uniquement un fichier Excel .xlsx.");
        }

        try {
            return importExcel(file.getInputStream());
        } catch (Exception exception) {
            throw new RuntimeException("Erreur lors de l'import Excel : " + exception.getMessage(), exception);
        }
    }

    public PfeImportResultDTO importAffectationsFromClasspath(String resourcePath) {
        try {
            ClassPathResource resource = new ClassPathResource(resourcePath);

            if (!resource.exists()) {
                throw new RuntimeException("Fichier introuvable dans resources : " + resourcePath);
            }

            try (InputStream inputStream = resource.getInputStream()) {
                return importExcel(inputStream);
            }
        } catch (Exception exception) {
            throw new RuntimeException("Erreur lors de l'import automatique : " + exception.getMessage(), exception);
        }
    }

    private PfeImportResultDTO importExcel(InputStream inputStream) throws Exception {
        List<String> messages = new ArrayList<>();

        int totalRows = 0;
        int createdCount = 0;
        int skippedCount = 0;
        int errorCount = 0;

        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);

                if (row == null || isBlankRow(row)) {
                    continue;
                }

                totalRows++;

                try {
                    String[] columns = new String[]{
                            getCellValue(row.getCell(0)),
                            getCellValue(row.getCell(1)),
                            getCellValue(row.getCell(2)),
                            getCellValue(row.getCell(3)),
                            getCellValue(row.getCell(4)),
                            getCellValue(row.getCell(5)),
                            getCellValue(row.getCell(6)),
                            getCellValue(row.getCell(7)),
                            getCellValue(row.getCell(8)),
                            getCellValue(row.getCell(9)),
                            getCellValue(row.getCell(10)),
                            getCellValue(row.getCell(11)),
                            getCellValue(row.getCell(12)),
                            getCellValue(row.getCell(13))
                    };

                    CreatePfeRequest request = buildRequestFromColumns(columns);
                    boolean created = createIfNotExists(request);

                    if (created) {
                        createdCount++;
                        messages.add("Ligne " + totalRows + " : PFE créé pour étudiant " + request.getStudentId());
                    } else {
                        skippedCount++;
                        messages.add("Ligne " + totalRows + " : PFE déjà existant pour étudiant " + request.getStudentId());
                    }
                } catch (Exception exception) {
                    errorCount++;
                    messages.add("Ligne " + totalRows + " : erreur - " + exception.getMessage());
                }
            }
        }

        return new PfeImportResultDTO(totalRows, createdCount, skippedCount, errorCount, messages);
    }

    private boolean createIfNotExists(CreatePfeRequest request) {
        boolean alreadyExists = !pfeRepository.findByStudentId(request.getStudentId()).isEmpty();

        if (alreadyExists) {
            return false;
        }

        pfeService.createPfe(request);
        return true;
    }

    private CreatePfeRequest buildRequestFromColumns(String[] columns) {
        if (columns.length < 14) {
            throw new RuntimeException("Nombre de colonnes invalide. Il faut 14 colonnes.");
        }

        CreatePfeRequest request = new CreatePfeRequest();

        request.setStudentId(parseLong(columns[0], "studentId"));
        request.setStudentName(columns[1]);
        request.setStudentEmail(columns[2]);

        request.setSupervisorId(parseLong(columns[3], "supervisorId"));
        request.setSupervisorName(columns[4]);
        request.setSupervisorEmail(columns[5]);

        request.setDepartment(columns[6]);

        request.setTitle(required(columns[7], "title"));
        request.setDescription(columns[8]);
        request.setProblemStatement(columns[9]);
        request.setObjectives(columns[10]);
        request.setTechnologies(columns[11]);
        request.setStartDate(parseDate(columns[12]));
        request.setDefenseDate(parseDate(columns[13]));

        return request;
    }

    private Long parseLong(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new RuntimeException("Champ obligatoire manquant : " + fieldName);
        }

        return Long.parseLong(value.trim());
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return LocalDate.parse(value.trim());
    }

    private String required(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new RuntimeException("Champ obligatoire manquant : " + fieldName);
        }

        return value.trim();
    }

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue().trim();
        }

        if (cell.getCellType() == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                return cell.getLocalDateTimeCellValue().toLocalDate().toString();
            }

            double numericValue = cell.getNumericCellValue();

            if (numericValue == Math.floor(numericValue)) {
                return String.valueOf((long) numericValue);
            }

            return String.valueOf(numericValue);
        }

        if (cell.getCellType() == CellType.BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        }

        if (cell.getCellType() == CellType.FORMULA) {
            try {
                return cell.getStringCellValue().trim();
            } catch (Exception exception) {
                return String.valueOf(cell.getNumericCellValue());
            }
        }

        return "";
    }


    private boolean isBlankRow(Row row) 
    {
        if (row == null) {
            return true;
        }

        for (int cellIndex = 0; cellIndex <= 13; cellIndex++) {
            Cell cell = row.getCell(cellIndex);

            if (cell != null && !getCellValue(cell).isBlank()) {
                return false;
            }
        }

        return true;
    }
}