package com.pfm.api;

import com.pfm.dto.AccountDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface AccountApi {

    @GetMapping
    List<AccountDTO> list();

    @GetMapping("/{id}")
    ResponseEntity<AccountDTO> get(@PathVariable String id);

    @PostMapping
    ResponseEntity<AccountDTO> create(@RequestBody AccountDTO dto);
}

