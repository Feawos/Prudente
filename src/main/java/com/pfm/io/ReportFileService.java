package com.pfm.io;

import com.pfm.report.FinancialReportService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.YearMonth;

@Service
public class ReportFileService {

    private final FinancialReportService financialReportService;

    public ReportFileService(FinancialReportService financialReportService) {
        this.financialReportService = financialReportService;
    }

    public Path exportMonthlySummary(YearMonth yearMonth) throws IOException {
        String fileName = "monthly-summary-%d-%02d.txt".formatted(yearMonth.getYear(), yearMonth.getMonthValue());
        String content = financialReportService.buildMonthlySummaryText(yearMonth);
        return exportTextReport(fileName, content);
    }

    public Path exportTextReport(String fileName, String content) throws IOException {
        Path exportDirectory = Path.of("exports");
        Files.createDirectories(exportDirectory);
        Path outputPath = exportDirectory.resolve(fileName);
        return Files.writeString(outputPath, content);
    }
}
