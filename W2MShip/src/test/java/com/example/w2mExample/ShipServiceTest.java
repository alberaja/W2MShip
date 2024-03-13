package com.example.w2mExample;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import com.example.w2mExample.dto.ResponseDTO;
import com.example.w2mExample.dto.ShipDTO;
import com.example.w2mExample.entity.ShipEntity;
import com.example.w2mExample.exception.ShipNotFoundException;
import com.example.w2mExample.repository.ShipRepository;
import com.example.w2mExample.service.ShipService;

@ExtendWith(MockitoExtension.class)
public class ShipServiceTest {

	private static final String WING = "wing";
	private static final String X_WING = "x-wing";
	private static final Integer ID1 = 1;
	private static final Integer ID2 = 2;
	private static final Integer ID3 = 3;
	private static final String NAME1 = "Ship1";
	private static final String NAME2 = "Ship2b";
	private static final String NAME3 = X_WING;

	private final ModelMapper modelMapper = new ModelMapper();

	protected ShipEntity ent1;
	protected ShipEntity ent2;
	protected ShipEntity ent3;

	@InjectMocks
	private ShipService shipService;

	@Mock
	private ShipRepository shipRepository;

	@BeforeEach
	private void setUp() {
		ent1 = ShipEntity.builder().id(ID1).name(NAME1).build();
		ent2 = ShipEntity.builder().id(ID2).name(NAME2).build();
		ent3 = ShipEntity.builder().id(ID3).name(NAME3).build();
	}

	@Test
	public final void findAll_FindAllPaginated_GetContentofPaging() {
		// given
		int page = 0;
		int size = 3;
		ResponseDTO dto1 = ResponseDTO.builder().id(ID1).name(NAME1).build();
		ResponseDTO dto2 = ResponseDTO.builder().id(ID2).name(WING).build();
		Page<ResponseDTO> expectedDTO = new PageImpl<>(List.of(dto1, dto2));
		List<ShipEntity> shipEntities = Arrays.asList(ent1, ent2);
		Page<ShipEntity> expectedPage = new PageImpl<>(shipEntities);
		given(shipRepository.findAll(PageRequest.of(page, size))).willReturn(expectedPage);

		// when
		Page<ResponseDTO> responseDTO = shipService.findAll(page, size);

		// then
		then(shipRepository).should(times(1)).findAll(PageRequest.of(page, size));
		assertThat(responseDTO).isNotNull();
		assertThat(responseDTO).hasSameSizeAs(expectedPage.getContent());
		assertEquals(expectedDTO.getContent().get(0), dto1);
		assertEquals(expectedDTO.getContent().get(1), dto2);
	}

	@Test
	public final void getById_GetOneShipById_GetContentOftheShipwiththatId() {
		// given
		Integer id = 1;
		Optional<ResponseDTO> expectedShipDTO = Optional.of(ResponseDTO.builder().id(id).name(NAME1).build());
		Optional<ShipEntity> optShipEntity = expectedShipDTO.map(shipDTO -> modelMapper.map(shipDTO, ShipEntity.class));
		given(shipRepository.findById(id)).willReturn(optShipEntity);

		// when
		ResponseDTO responseDTO = shipService.getById(id);

		// then
		then(shipRepository).should(times(1)).findById(id);
		assertNotNull(responseDTO);
		assertEquals(responseDTO, expectedShipDTO.get());
	}

	@Test
	public final void getByNameContaining_wing_xwing() {
		// given
		List<ShipEntity> expectedEntities = Arrays.asList(ent3);
		given(shipRepository.findByNameContainingIgnoreCase(WING)).willReturn(expectedEntities);

		// when
		List<ResponseDTO> responseDTO = shipService.getByNameContaining(WING);

		// then
		then(shipRepository).should(times(1)).findByNameContainingIgnoreCase(WING);
		assertNotNull(responseDTO);
		assertEquals(X_WING, responseDTO.get(0).getName());
	}

	@Test
	public final void add_SaveEntity_SavedEntity() {
		// given
		ShipDTO NewshipDto = ShipDTO.builder().name("newShip").build();
		ShipEntity newEntityWithId = ShipEntity.builder().id(5).name("newShip").build();
		given(shipRepository.save(Mockito.any(ShipEntity.class))).willReturn(newEntityWithId);

		// when
		ResponseDTO responseDTO = shipService.add(NewshipDto);

		// then
		then(shipRepository).should(times(1)).save(modelMapper.map(NewshipDto, ShipEntity.class));
		assertThat(responseDTO).isNotNull();
		assertEquals(modelMapper.map(newEntityWithId, ResponseDTO.class), responseDTO);
	}

	@Test
	public final void update_ShipKOdoesnExists_ShipNotFoundException() {
		// given
		Integer id = 6;
		ShipDTO updatedShipDTO = ShipDTO.builder().name("UpdatedShip").build();
		given(shipRepository.findById(id))
				.willThrow(new ShipNotFoundException("Ship not found trying updating", HttpStatus.NOT_FOUND));

		ShipNotFoundException exception = assertThrows(ShipNotFoundException.class, () -> shipService.update(id, updatedShipDTO));

		// when + then
		assertThrows(ShipNotFoundException.class, () -> shipService.update(id, updatedShipDTO));
		assertEquals("Ship not found trying updating", exception.getMessage());
	}

	@Test
	public final void delete_ShipKOdoesnExists_ShipNotFoundException() {
		// given
		Integer id = 6;
		given(shipRepository.findById(id))
				.willThrow(new ShipNotFoundException("Ship not found trying deleting", HttpStatus.NOT_FOUND));

		ShipNotFoundException exception = assertThrows(ShipNotFoundException.class, () -> shipService.delete(id));

		// when + then
		assertThrows(ShipNotFoundException.class, () -> shipService.delete(id));
		assertEquals("Ship not found trying deleting", exception.getMessage());
	}

}
