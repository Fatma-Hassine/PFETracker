package com.pfetracker.service.module1;

public interface ExportService {

    byte[] exporterDepartementExcel(Long departementId);

    byte[] exporterDepartementPdf(Long departementId);

    byte[] exporterGlobalExcel();

    byte[] exporterRapportGlobalPdf();
}
