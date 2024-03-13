package com.example.w2mExample.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.w2mExample.dto.ResponseDTO;
import com.example.w2mExample.dto.ShipDTO;
import com.example.w2mExample.service.ShipService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;

@Validated
@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/ships")
public class ShipController {

	@Autowired
	private ShipService shipService;

	@Operation(summary = "Get Ships Paged", description = " Get Ships paging By page and size", tags = {})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successful response", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))) })
	@ResponseStatus(HttpStatus.OK)
	@GetMapping
	public Page<ResponseDTO> getAll(@RequestParam(defaultValue = "0") @NotNull(message = "page param is null") Integer page,
			@RequestParam(defaultValue = "5") @NotNull(message = "size param is null") @Min(value = 1, message = "Page size must not be less than one") Integer size) {
		return shipService.findAll(page, size);
	}

	@Operation(summary = "Get Ships by ID", description = " Get Ships by given ID", tags = {})
	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/{id}")
	public ResponseDTO getById(@PathVariable @NotNull @Min(value = 1, message = "ID must be >=1") Integer id) {
		log.info("Search Ship with Id" + id);
		return shipService.getById(id);
	}

	@Operation(summary = "Get containing Ships", description = " Get Ships containing a given name", tags = {})
	@ResponseStatus(HttpStatus.OK)
	@GetMapping ("/search")
	public List<ResponseDTO> getByNameContaining(@RequestParam @NotBlank @Size(max = 20) String name) {
		log.info("Search Ship using NameContaining, with name: " + name);
		return shipService.getByNameContaining(name);
		// ResponseEntity.ok(shipService.getShipByNameContaining(name));
	}

	@Operation(summary = "Add containing Ships", description = " Add Ships given in body", tags = {})
	@PostMapping
	public ResponseEntity<ResponseDTO> addShip(@RequestBody @NotNull @Valid final ShipDTO shipDTO) {
		log.info("addShip() " + shipDTO);
		try {
			return ResponseEntity.status(HttpStatus.CREATED).body(shipService.add(shipDTO));
		} catch (ResponseStatusException exception) {
			throw new ResponseStatusException(exception.getStatusCode(), exception.getMessage());
		}
	}

	@Operation(summary = "Update Ships", description = " Update Ships which ve that ID of the Path and update with new ship of the body", tags = {})
	@ResponseStatus(HttpStatus.OK)
	@PutMapping("/{id}")
	public ResponseDTO updateShip(@PathVariable @NotNull @Min(value = 1, message = "ID must be >=1") Integer id,
			@RequestBody @Valid final ShipDTO updatedShip) throws NotFoundException {
		log.info("changedShip() " + updatedShip);
		return shipService.update(id, updatedShip);
	}

	@Operation(summary = "Delete Ships", description = " Delete Ships which ve that ID of the Path", tags = {})
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping("/{id}")
	public void deleteShip(
			@PathVariable(name = "id") @NotNull @Min(value = 1, message = "ID must be >=1") final Integer id)
			throws NotFoundException {
		log.info("Delete of ship with id" + id);
		shipService.delete(id);
	}

}