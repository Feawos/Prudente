package com.pfm.web;

import com.pfm.concurrency.BatchReportService;
import com.pfm.io.ReportFileService;
import com.pfm.report.FinancialReportService;
import com.pfm.report.dto.MonthlySummaryDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final FinancialReportService financialReportService;
    private final ReportFileService reportFileService;
    private final BatchReportService batchReportService;

    public ReportController(
            FinancialReportService financialReportService,
            ReportFileService reportFileService,
            BatchReportService batchReportService
    ) {
        this.financialReportService = financialReportService;
        this.reportFileService = reportFileService;
        this.batchReportService = batchReportService;
    }

    @GetMapping("/monthly")
    public MonthlySummaryDTO monthly(
            @RequestParam int year,
            @RequestParam int month
    ) {
        return financialReportService.buildMonthlySummary(YearMonth.of(year, month));
    }

    @GetMapping("/monthly/export")
    public ResponseEntity<byte[]> exportMonthly(
            @RequestParam int year,
            @RequestParam int month
    ) throws IOException {
        Path filePath = reportFileService.exportMonthlySummary(YearMonth.of(year, month));
        byte[] bytes = Files.readAllBytes(filePath);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filePath.getFileName())
                .contentType(MediaType.TEXT_PLAIN)
                .body(bytes);
    }

    @PostMapping("/monthly/batch")
    public Map<String, MonthlySummaryDTO> batchMonthly(@RequestBody List<String> months)
            throws ExecutionException, InterruptedException {
        List<YearMonth> yearMonths = months.stream()
                .map(YearMonth::parse)
                .toList();
        return batchReportService.buildMonthlySummaries(yearMonths);
    }
}
