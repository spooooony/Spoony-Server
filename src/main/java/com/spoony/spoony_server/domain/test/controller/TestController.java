package com.spoony.spoony_server.domain.test.controller;

import com.spoony.spoony_server.common.dto.ResponseDTO;
import com.spoony.spoony_server.common.exception.BusinessException;
import com.spoony.spoony_server.common.message.BusinessErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/test/default")
    public ResponseEntity<ResponseDTO<Void>> testDefault() {
        throw new RuntimeException();
    }

    @GetMapping("/test/business")
    public ResponseEntity<ResponseDTO<Void>> testBusiness() {
        throw new BusinessException(BusinessErrorMessage.BAD_REQUEST);
    }

    @GetMapping("/test/success")
    public ResponseEntity<ResponseDTO<String>> testSuccess() {
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success("dummy"));
    }
}
