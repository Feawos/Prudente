package com.pfm.service;

import com.pfm.model.Transaction;
import com.pfm.util.TransactionMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CsvExportService {

    private final TransactionService txService;

    public CsvExportService(TransactionService txService) {
        this.txService = txService;
    }

    /**
     * Exports all transactions as a CSV string.
     */
    public String exportCsv() {

        List<Transaction> transactions = txService.all();

        String header = "date,amount,currency,type,category,fromAccount,toAccount";

        String rows = transactions.stream()
                .map(TransactionMapper::toMap)
                .map(map -> String.join(",",
                        map.getOrDefault("date", ""),
                        map.getOrDefault("amount", ""),
                        map.getOrDefault("currency", ""),
                        map.getOrDefault("type", ""),
                        map.getOrDefault("category", ""),
                        map.getOrDefault("fromAccount", ""),
                        map.getOrDefault("toAccount", "")
                ))
                .collect(Collectors.joining("\n"));

        return header + "\n" + rows;
    }

    public String exportAllTransactions() {
        return exportCsv();
    }
}
