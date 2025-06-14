package com.wexinc.purchasetransaction.controller;

import com.wexinc.purchasetransaction.service.ExchangeRateService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@RestController
@RequestMapping("v1/exchangeRate")
@RequiredArgsConstructor
public class ExchangeRateController {

    @NonNull
    private ExchangeRateService service;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadExchangeRateCsvData(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }
            if (!Objects.equals(file.getContentType(), "text/csv")) {
                return ResponseEntity.badRequest().body("Only CSV files are supported");
            }
            service.importData(file.getInputStream());
            return ResponseEntity.ok("File uploaded and processed successfully");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process file: " + ex.getMessage());
        }
    }
}
