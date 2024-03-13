package com.example.w2mExample.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.example.w2mExample.dto.ResponseDTO;
import com.example.w2mExample.dto.ShipDTO;
import com.example.w2mExample.entity.ShipEntity;
import com.example.w2mExample.exception.ShipNotFoundException;
import com.example.w2mExample.repository.ShipRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShipService {

	private final ModelMapper modelMapper = new ModelMapper();

	private final ShipRepository shipRepository;

	public Page<ResponseDTO> findAll(int page, int size) {
		log.info("valores page y size: " + page + size);
		Page<ShipEntity> results = shipRepository.findAll(PageRequest.of(page, size)); // Sort.by("name").descending());
		Page<ResponseDTO> ships = results.map(shipEntity -> modelMapper.map(shipEntity, ResponseDTO.class)); // .getContent();
		return ships;
	}

	public ResponseDTO getById(int id) {
		ShipEntity ship = shipRepository.findById(id)
				.orElseThrow(() -> new ShipNotFoundException("Ship is not found", HttpStatus.NOT_FOUND));
		return modelMapper.map(ship, ResponseDTO.class);
	}

	@Cacheable(cacheNames = "shipsCache", key = "#name", condition = "#name.equals('wing')")
	public List<ResponseDTO> getByNameContaining(String name) {
		// filter terms until 1 contained character
		List<ShipEntity> matchingShips = shipRepository.findByNameContainingIgnoreCase(name);
		if (ObjectUtils.isEmpty(matchingShips)) {
			throw new ShipNotFoundException("Ship is not found", HttpStatus.OK);
		}

		List<ResponseDTO> result = matchingShips.stream()
				.map(shipEntity -> modelMapper.map(shipEntity, ResponseDTO.class)).collect(Collectors.toList());

		return result;
	}

	public ResponseDTO add(ShipDTO shipDto) {
		ShipEntity shipEntity = modelMapper.map(shipDto, ShipEntity.class);
		ResponseDTO response = modelMapper.map(shipRepository.save(shipEntity), ResponseDTO.class);
		return response;
	}

	public ResponseDTO update(Integer id, ShipDTO updatedShip) throws NotFoundException {
		final ShipEntity newShip = shipRepository.findById(id)
				.orElseThrow(() -> new ShipNotFoundException("Ship not found trying updating", HttpStatus.NOT_FOUND));
		mapDTOToEntity(updatedShip, newShip);
		return modelMapper.map(shipRepository.save(newShip), ResponseDTO.class);
	}

	private ShipEntity mapDTOToEntity(final ShipDTO updatedShip, final ShipEntity newShip) {
		newShip.setName(updatedShip.getName());
		return newShip;
	}

	public void delete(Integer id) throws NotFoundException {
		shipRepository.findById(id).orElseThrow(
				() -> new ShipNotFoundException("Ship is not found trying deleting", HttpStatus.NOT_FOUND));
		shipRepository.deleteById(id);
	}

}
