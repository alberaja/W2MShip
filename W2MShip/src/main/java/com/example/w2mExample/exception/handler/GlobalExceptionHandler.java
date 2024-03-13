package com.example.w2mExample.exception.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.w2mExample.dto.ErrorDTO;
import com.example.w2mExample.exception.ShipNotFoundException;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(ShipNotFoundException.class)
    //@ResponseStatus(HttpStatus.NOT_FOUND)   //no necessary using the wrapper ResponseEntity<>
	public ResponseEntity<ErrorDTO> getShipNotFoundException(ShipNotFoundException exception) {
		log.error("getShipNotFoundException {}", exception.getMessage());
		ErrorDTO error = ErrorDTO.builder()/*.status(HttpStatus.NOT_FOUND)*/.message(exception.getMessage()).build();
		return new ResponseEntity<ErrorDTO>(error, exception.getStatus());
	}
	
}
