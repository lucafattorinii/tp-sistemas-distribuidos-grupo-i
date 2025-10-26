package com.empuje.filters.service;

import com.empuje.filters.dto.DonationRecord;
import com.empuje.filters.dto.ExcelExportRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelExportService {

    private final ReportDataService reportDataService;

    public byte[] generateExcelReport(ExcelExportRequest request) throws IOException {
        log.info("Generating Excel report");

        List<DonationRecord> records = reportDataService.getDonationRecords(request);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Donaciones");
            createHeaderRow(sheet);
            fillDataRows(sheet, records);

            for (int i = 0; i < 7; i++) {
                sheet.autoSizeColumn(i);
            }

            return writeWorkbookToBytes(workbook);
        }
    }

    private void createHeaderRow(Sheet sheet) {
        Row headerRow = sheet.createRow(0);

        String[] headers = {
            "Fecha de Alta",
            "Descripción",
            "Cantidad",
            "Eliminado",
            "Usuario Alta",
            "Usuario Modificación",
            "Organización"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(createHeaderStyle(sheet.getWorkbook()));
        }
    }

    private void fillDataRows(Sheet sheet, List<DonationRecord> records) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (int i = 0; i < records.size(); i++) {
            DonationRecord record = records.get(i);
            Row row = sheet.createRow(i + 1);

            if (record.getCreatedDate() != null) {
                row.createCell(0).setCellValue(record.getCreatedDate().format(formatter));
            }

            row.createCell(1).setCellValue(record.getDescription());
            row.createCell(2).setCellValue(record.getQuantity());
            row.createCell(3).setCellValue(record.getIsDeleted() ? "Sí" : "No");
            row.createCell(4).setCellValue(record.getCreatedBy() != null ? record.getCreatedBy() : "");
            row.createCell(5).setCellValue(record.getModifiedBy() != null ? record.getModifiedBy() : "");
            row.createCell(6).setCellValue(record.getOrganizationId() != null ? record.getOrganizationId() : "");
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        return headerStyle;
    }

    private byte[] writeWorkbookToBytes(Workbook workbook) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            workbook.write(outputStream);
            workbook.close();
            return outputStream.toByteArray();
        }
    }
}
