package com.pfetracker.service.impl.module1;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import com.pfetracker.dto.module1.DashboardDepartementResponse;
import com.pfetracker.dto.module1.DirecteurDashboardResponse;
import com.pfetracker.dto.module1.EncadrantChargeResponse;
import com.pfetracker.dto.module1.EtudiantStageResponse;
import com.pfetracker.entity.module1.Departement;
import com.pfetracker.entity.module1.Encadrant;
import com.pfetracker.entity.module1.Etudiant;
import com.pfetracker.exception.module1.BusinessException;
import com.pfetracker.repository.module1.DepartementRepository;
import com.pfetracker.repository.module1.EncadrantRepository;
import com.pfetracker.repository.module1.EtudiantRepository;
import com.pfetracker.service.module1.DirecteurService;
import com.pfetracker.service.module1.ExportService;
import com.pfetracker.service.module1.ResponsableService;

import lombok.RequiredArgsConstructor;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExportServiceImpl implements ExportService {

    private final EtudiantRepository etudiantRepo;
    private final EncadrantRepository encadrantRepo;
    private final DepartementRepository departementRepo;
    private final ResponsableService responsableService;
    private final DirecteurService directeurService;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public byte[] exporterDepartementExcel(Long departementId) {
        Departement dept = departementRepo.findById(departementId)
                .orElseThrow(() -> new BusinessException("Département introuvable"));

        List<Etudiant> etudiants = etudiantRepo.findByDepartementId(departementId);
        List<Encadrant> encadrants = encadrantRepo.findByDepartementId(departementId);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet feuilleEtudiants = workbook.createSheet("Étudiants - " + dept.getNom());
            String[] entetesEtudiants = {"Nom complet", "Email", "Téléphone", "Niveau", "Année",
                    "Encadrant", "Compte actif", "Début stage", "Fin stage"};
            ecrireEntetes(feuilleEtudiants, entetesEtudiants);

            int ligne = 1;
            for (Etudiant e : etudiants) {
                Row row = feuilleEtudiants.createRow(ligne++);
                row.createCell(0).setCellValue(e.getNomComplet());
                row.createCell(1).setCellValue(e.getEmail());
                row.createCell(2).setCellValue(nvl(e.getTelephone()));
                row.createCell(3).setCellValue(nvl(e.getNiveauEtudes()));
                row.createCell(4).setCellValue(nvl(e.getAnnee()));
                row.createCell(5).setCellValue(e.getEncadrant() != null ? e.getEncadrant().getNomComplet() : "Aucun");
                row.createCell(6).setCellValue(e.isEnabled() ? "Oui" : "Non");
                row.createCell(7).setCellValue(formatDate(e.getDateDebutStage()));
                row.createCell(8).setCellValue(formatDate(e.getDateFinStage()));
            }
            autoDimensionner(feuilleEtudiants, entetesEtudiants.length);

            Sheet feuilleEncadrants = workbook.createSheet("Encadrants - " + dept.getNom());
            String[] entetesEncadrants = {"Nom complet", "Email", "Grade", "Spécialité", "Code", "Étudiants encadrés"};
            ecrireEntetes(feuilleEncadrants, entetesEncadrants);

            ligne = 1;
            for (Encadrant enc : encadrants) {
                Row row = feuilleEncadrants.createRow(ligne++);
                row.createCell(0).setCellValue(enc.getNomComplet());
                row.createCell(1).setCellValue(enc.getEmail());
                row.createCell(2).setCellValue(nvl(enc.getGrade()));
                row.createCell(3).setCellValue(nvl(enc.getSpecialite()));
                row.createCell(4).setCellValue(enc.getCodeId());
                row.createCell(5).setCellValue(etudiantRepo.countByEncadrantId(enc.getId()));
            }
            autoDimensionner(feuilleEncadrants, entetesEncadrants.length);

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new BusinessException("Erreur lors de la génération de l'export Excel : " + e.getMessage());
        }
    }

    @Override
    public byte[] exporterDepartementPdf(Long departementId) {
        Departement dept = departementRepo.findById(departementId)
                .orElseThrow(() -> new BusinessException("Département introuvable"));

        DashboardDepartementResponse dashboard = responsableService.getDashboard(departementId);
        List<EncadrantChargeResponse> charges = responsableService.getChargeEncadrants(departementId);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            Font titre = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font sousTitre = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font normal = FontFactory.getFont(FontFactory.HELVETICA, 10);

            document.add(new Paragraph("Rapport de synthèse — Département " + dept.getNom(), titre));
            document.add(new Paragraph("Généré le " + LocalDate.now().format(DATE_FMT), normal));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Indicateurs généraux", sousTitre));
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            ajouterLigne(table, "Total étudiants", String.valueOf(dashboard.getTotalEtudiants()));
            ajouterLigne(table, "Étudiants sans encadrant", String.valueOf(dashboard.getEtudiantsSansEncadrant()));
            ajouterLigne(table, "Total encadrants", String.valueOf(dashboard.getTotalEncadrants()));
            ajouterLigne(table, "Limite étudiants/encadrant", String.valueOf(dashboard.getLimiteEtudiantsParEncadrant()));
            ajouterLigne(table, "PFE actifs", String.valueOf(dashboard.getPfeActifs()));
            ajouterLigne(table, "PFE terminés", String.valueOf(dashboard.getPfeTermines()));
            ajouterLigne(table, "PFE en retard", String.valueOf(dashboard.getPfeEnRetard()));
            ajouterLigne(table, "PFE non démarrés", String.valueOf(dashboard.getPfeNonDemarres()));
            ajouterLigne(table, "Progression moyenne", String.format("%.1f %%", dashboard.getProgressionMoyenne()));
            ajouterLigne(table, "PFE inactifs > 7 jours", String.valueOf(dashboard.getPfeInactifsSept()));
            document.add(table);

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Charge des encadrants", sousTitre));
            PdfPTable tableEncadrants = new PdfPTable(3);
            tableEncadrants.setWidthPercentage(100);
            tableEncadrants.addCell(new PdfPCell(new Paragraph("Encadrant", sousTitre)));
            tableEncadrants.addCell(new PdfPCell(new Paragraph("Étudiants", sousTitre)));
            tableEncadrants.addCell(new PdfPCell(new Paragraph("Limite", sousTitre)));
            for (EncadrantChargeResponse c : charges) {
                tableEncadrants.addCell(new Paragraph(c.getNomComplet(), normal));
                tableEncadrants.addCell(new Paragraph(String.valueOf(c.getNombreEtudiantsActuels()), normal));
                tableEncadrants.addCell(new Paragraph(c.isChargePleine() ? "Complet" : "Disponible", normal));
            }
            document.add(tableEncadrants);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new BusinessException("Erreur lors de la génération du PDF : " + e.getMessage());
        }
    }

    @Override
    public byte[] exporterGlobalExcel() {
        List<EtudiantStageResponse> etudiants = directeurService.getTousLesEtudiants();

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet feuille = workbook.createSheet("Tous les étudiants");
            String[] entetes = {"Nom complet", "Email", "Département", "Encadrant", "Affecté",
                    "Affectation forcée", "Début stage", "Fin stage", "Jours depuis début", "En alerte"};
            ecrireEntetes(feuille, entetes);

            int ligne = 1;
            for (EtudiantStageResponse e : etudiants) {
                Row row = feuille.createRow(ligne++);
                row.createCell(0).setCellValue(e.getNomComplet());
                row.createCell(1).setCellValue(e.getEmail());
                row.createCell(2).setCellValue(nvl(e.getDepartementNom()));
                row.createCell(3).setCellValue(e.getEncadrantNom() != null ? e.getEncadrantNom() : "Aucun");
                row.createCell(4).setCellValue(e.isAffecte() ? "Oui" : "Non");
                row.createCell(5).setCellValue(e.isAffectationForcee() ? "Oui" : "Non");
                row.createCell(6).setCellValue(formatDate(e.getDateDebutStage()));
                row.createCell(7).setCellValue(formatDate(e.getDateFinStage()));
                row.createCell(8).setCellValue(e.getJoursDepuisDebut());
                row.createCell(9).setCellValue(e.isEnAlerte() ? "Oui" : "Non");
            }
            autoDimensionner(feuille, entetes.length);

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new BusinessException("Erreur lors de la génération de l'export Excel : " + e.getMessage());
        }
    }

    @Override
    public byte[] exporterRapportGlobalPdf() {
        DirecteurDashboardResponse dashboard = directeurService.getDashboard();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            Font titre = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font sousTitre = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font normal = FontFactory.getFont(FontFactory.HELVETICA, 10);

            document.add(new Paragraph("Rapport de synthèse global — Tous départements", titre));
            document.add(new Paragraph("Généré le " + LocalDate.now().format(DATE_FMT), normal));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Indicateurs consolidés", sousTitre));
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            ajouterLigne(table, "Total étudiants", String.valueOf(dashboard.getTotalEtudiants()));
            ajouterLigne(table, "Étudiants affectés", String.valueOf(dashboard.getEtudiantsAffectes()));
            ajouterLigne(table, "Étudiants non affectés", String.valueOf(dashboard.getEtudiantsNonAffectes()));
            ajouterLigne(table, "Étudiants en alerte", String.valueOf(dashboard.getEtudiantsEnAlerte()));
            document.add(table);

            if (dashboard.getStagesProchesExpiration() != null && !dashboard.getStagesProchesExpiration().isEmpty()) {
                document.add(new Paragraph(" "));
                document.add(new Paragraph("Stages proches de l'expiration", sousTitre));
                PdfPTable tableStages = new PdfPTable(3);
                tableStages.setWidthPercentage(100);
                tableStages.addCell(new PdfPCell(new Paragraph("Étudiant", sousTitre)));
                tableStages.addCell(new PdfPCell(new Paragraph("Département", sousTitre)));
                tableStages.addCell(new PdfPCell(new Paragraph("Fin de stage", sousTitre)));
                for (EtudiantStageResponse s : dashboard.getStagesProchesExpiration()) {
                    tableStages.addCell(new Paragraph(s.getNomComplet(), normal));
                    tableStages.addCell(new Paragraph(nvl(s.getDepartementNom()), normal));
                    tableStages.addCell(new Paragraph(formatDate(s.getDateFinStage()), normal));
                }
                document.add(tableStages);
            }

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new BusinessException("Erreur lors de la génération du PDF : " + e.getMessage());
        }
    }

    // ── Utilitaires ──────────────────────────────────────────────────────────

    private void ecrireEntetes(Sheet feuille, String[] entetes) {
        Row header = feuille.createRow(0);
        for (int i = 0; i < entetes.length; i++) {
            header.createCell(i).setCellValue(entetes[i]);
        }
    }

    private void autoDimensionner(Sheet feuille, int nbColonnes) {
        for (int i = 0; i < nbColonnes; i++) {
            feuille.autoSizeColumn(i);
        }
    }

    private void ajouterLigne(PdfPTable table, String libelle, String valeur) {
        table.addCell(libelle);
        table.addCell(valeur);
    }

    private String nvl(String valeur) {
        return valeur != null ? valeur : "";
    }

    private String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FMT) : "";
    }
}
