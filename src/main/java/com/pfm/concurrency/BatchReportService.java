package com.pfm.concurrency;

import com.pfm.report.FinancialReportService;
import com.pfm.report.dto.MonthlySummaryDTO;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class BatchReportService {

    private final FinancialReportService financialReportService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    public BatchReportService(FinancialReportService financialReportService) {
        this.financialReportService = financialReportService;
    }

    public Map<String, MonthlySummaryDTO> buildMonthlySummaries(List<YearMonth> months)
            throws InterruptedException, ExecutionException {
        List<Callable<MonthlySummaryDTO>> tasks = months.stream()
                .map(month -> (Callable<MonthlySummaryDTO>) () -> financialReportService.buildMonthlySummary(month))
                .toList();

        List<Future<MonthlySummaryDTO>> futures = executorService.invokeAll(tasks);
        Map<String, MonthlySummaryDTO> results = new LinkedHashMap<>();

        for (int index = 0; index < months.size(); index++) {
            results.put(months.get(index).toString(), futures.get(index).get());
        }

        return results;
    }

    @PreDestroy
    public void shutdown() {
        executorService.shutdown();
    }
}
