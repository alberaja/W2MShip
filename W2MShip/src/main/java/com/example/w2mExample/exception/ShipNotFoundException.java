package com.example.w2mExample.exception;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShipNotFoundException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;
	private HttpStatus status;
    public ShipNotFoundException(String message , HttpStatus status){ 
        super(message);
        this.status = status;
    }

	
}
