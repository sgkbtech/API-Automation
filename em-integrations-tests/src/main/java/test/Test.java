package test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ge.current.em.persistenceapi.dto.EnterpriseDTO;

public class Test {

	public static void main(String[] args) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		EnterpriseDTO dto = new EnterpriseDTO();
		long unixTime = System.currentTimeMillis() % 1000000000L;
		dto.setName("Test_Root_Enterprise"+unixTime);
		String body = objectMapper.writeValueAsString(dto);
		System.out.println(body);
	}

}
