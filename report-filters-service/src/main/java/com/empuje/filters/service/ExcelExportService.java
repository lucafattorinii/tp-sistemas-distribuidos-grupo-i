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
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelExportService {

    private final ReportDataService reportDataService;

    public byte[] generateExcelReport(ExcelExportRequest request) throws IOException {
        log.info("Generating Excel report");

        List<DonationRecord> records = reportDataService.getDonationRecords(request);

        try (Workbook workbook = new XSSFWorkbook()) {

            // Agrupar registros por categoría
            Map<String, List<DonationRecord>> recordsByCategory = records.stream()
                    .collect(Collectors.groupingBy(DonationRecord::getCategory));

            // Crear una hoja por cada categoría
            for (Map.Entry<String, List<DonationRecord>> entry : recordsByCategory.entrySet()) {
                String category = entry.getKey();
                List<DonationRecord> categoryRecords = entry.getValue();

                Sheet sheet = workbook.createSheet(category);
                createHeaderRow(sheet);
                fillDataRows(sheet, categoryRecords);
                autoSizeColumns(sheet);
            }

            return writeWorkbookToBytes(workbook);
        }
    }

    private void autoSizeColumns(Sheet sheet) {
        int columnCount = sheet.getRow(0).getPhysicalNumberOfCells();
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
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

    private byte[] writeWorkbookToBytes(Workbook workbook) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            workbook.write(outputStream);
            workbook.close();
            return outputStream.toByteArray();
        }
    }
}
