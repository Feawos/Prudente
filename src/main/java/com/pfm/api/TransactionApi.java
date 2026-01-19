package com.pfm.api;

import com.pfm.dto.TransactionDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface TransactionApi {

    @GetMapping
    List<TransactionDTO> list();

    @PostMapping("/debit")
    ResponseEntity<TransactionDTO> debit(@RequestBody TransactionDTO dto);

    @PostMapping("/credit")
    ResponseEntity<TransactionDTO> credit(@RequestBody TransactionDTO dto);

    @PostMapping("/transfer")
    ResponseEntity<TransactionDTO> transfer(@RequestBody TransactionDTO dto);
}

