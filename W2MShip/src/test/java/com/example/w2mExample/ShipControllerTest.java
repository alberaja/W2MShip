package com.example.w2mExample;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;

import com.example.w2mExample.controller.ShipController;
import com.example.w2mExample.dto.ResponseDTO;
import com.example.w2mExample.service.ShipService;

@ExtendWith(MockitoExtension.class)
public class ShipControllerTest {

	private static final String WING = "wing";
	private static final String X_WING = "x-wing";
	private static final Integer ID1 = 1;
	private static final Integer ID2 = 2;
	private static final Integer ID3 = 3;
	private static final String NAME1 = "Ship1";
	private static final String NAME2 = "Ship2b";
	private static final String NAME3 = X_WING;

	@InjectMocks
	private ShipController shipController;

	@Mock
	private ShipService shipService;

	@Test
	public final void getAll_FindAllPaginated_GetContentofPaging() {
		// given
		int page = 0;
		int size = 3;
		ResponseDTO ent1 = ResponseDTO.builder().id(ID1).name(NAME1).build();
		ResponseDTO ent2 = ResponseDTO.builder().id(ID2).name(NAME2).build();
		ResponseDTO ent3 = ResponseDTO.builder().id(ID3).name(NAME3).build();
		List<ResponseDTO> expectedDTO = Arrays.asList(ent1, ent2, ent3);
		Page<ResponseDTO> expectedPage = new PageImpl<>(expectedDTO);
		given(shipService.findAll(page, size)).willReturn(expectedPage);

		// when
		Page<ResponseDTO> responseDTO = shipController.getAll(page, size);

		// then
		then(shipService).should().findAll(page, size);
		assertNotNull(responseDTO);
		assertEquals(size, responseDTO.getNumberOfElements());
		assertEquals(expectedDTO, responseDTO.getContent());
	}

	@Test
	public final void getById_getWithId_ShipWithId() {
		// given
		ResponseDTO dto1 = ResponseDTO.builder().id(ID1).name(NAME1).build();
		given(shipService.getById(ID1)).willReturn(dto1);

		// when
		ResponseDTO response = shipController.getById(ID1);

		// then
		then(shipService).should().getById(ID1);
		assertThat(HttpStatus.OK);
		assertEquals(dto1, response);
	}

	@Test
	public final void getByNameContaining_wing_xwing() {
		// given
		ResponseDTO ent3 = ResponseDTO.builder().id(3).name(X_WING).build();
		List<ResponseDTO> expectedEntities = new ArrayList<>();
		expectedEntities.add(ent3);
		given(shipService.getByNameContaining(WING)).willReturn(expectedEntities);

		// when
		List<ResponseDTO> response = shipController.getByNameContaining(WING);

		// then
		then(shipService).should().getByNameContaining(WING);
		assertNotNull(response);
		assertEquals(X_WING, response.get(0).getName());
	}

}
