package com.example.w2mExample;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import com.example.w2mExample.dto.ResponseDTO;
import com.example.w2mExample.dto.ShipDTO;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT) // needed for TestRestTemplate
public class IntegrationTest {

	static final String API = "/api/ships";
	static final String WING = "wing";
	static final String X_WING = "x-wing";
	private final String URL_API_TEST_1 = API + "/1";
	private final String URL_API_TEST_MENOS1 = API + "/-1";

	private static HttpHeaders headers;
	private final ModelMapper modelMapper = new ModelMapper();

	@Autowired
	private TestRestTemplate testRestTemplate;

	@BeforeAll
	public static void init() {
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
	}

	@Test
	@Sql(statements = "INSERT INTO ships(id, name) VALUES (1, 'x-wing')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(statements = "DELETE FROM ships WHERE id='1'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	public final void getById_getById_ShipWithId() {
		// given
		ResponseDTO expectedDTO = ResponseDTO.builder().id(1).name(X_WING).build();
		
		// when
		ResponseEntity<ResponseDTO> response = testRestTemplate.getForEntity(URL_API_TEST_1, ResponseDTO.class);

		// then
		assertEquals(expectedDTO, response.getBody());
	}

	@Test
	public final void getById_getByNegativeId_Shipnull() {
		// given
		ShipDTO entNull = new ShipDTO();

		// when
		ResponseEntity<ShipDTO> response = testRestTemplate.getForEntity(URL_API_TEST_MENOS1, ShipDTO.class);

		// then
		assertEquals(response.getBody(), modelMapper.map(entNull, ShipDTO.class));
	}

	@Test
	@Sql(statements = "DELETE FROM ships WHERE id='1'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	public final void add_addShip_ShipAdded() {
		// given
		ResponseDTO expectedDTO = ResponseDTO.builder().id(1).name(X_WING).build();
		ShipDTO shipDTO = ShipDTO.builder().name(X_WING).build();
		
		// when
		ResponseEntity<ResponseDTO> responseDTO = testRestTemplate.postForEntity(API, shipDTO, ResponseDTO.class);

		// then
		assertEquals(expectedDTO, responseDTO.getBody());
		assertEquals(HttpStatus.CREATED, responseDTO.getStatusCode());
		assertNotNull(responseDTO.getBody());
		assertEquals(expectedDTO, responseDTO.getBody());
	}

	@Test
	@Sql(statements = "INSERT INTO ships(id, name) VALUES (1, 'x-wing')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(statements = "DELETE FROM ships WHERE id='1'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	public final void update_changeById_ShipChangedWithId() {
		// given
		ShipDTO updatedShip = ShipDTO.builder().name("updatedShip").build();
		
		// when
		testRestTemplate.put(URL_API_TEST_1, updatedShip);

		// then
		ResponseEntity<ShipDTO> response = testRestTemplate.getForEntity(URL_API_TEST_1, ShipDTO.class);
		assertEquals(updatedShip, response.getBody());
	}

}
