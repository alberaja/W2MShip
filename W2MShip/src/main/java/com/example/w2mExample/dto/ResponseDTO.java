package com.example.w2mExample.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;

	// @JsonProperty("shipId")
	private Integer id;

	// @JsonProperty("shipName")
	@NotNull
	@NotBlank
	@Size(max = 20)
	private String name;
}
