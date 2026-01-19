package com.pfm.web;

import com.pfm.service.CsvExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CsvExportController {

    private final CsvExportService csv;

    public CsvExportController(CsvExportService csv) {
        this.csv = csv;
    }

    @GetMapping("/export/transactions")
    public ResponseEntity<byte[]> export() {
        String csvContent = csv.exportAllTransactions();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transactions.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvContent.getBytes());
    }
}
