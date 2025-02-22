package edu.uclm.esi.backend;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class Test1 {

	@Autowired
	private MockMvc server;

	@Test
	void test1() throws Exception{
		RequestBuilder request = MockMvcRequestBuilders.
				get("/users/getAllusers?email=asas&pwd=1234");
		
		this.server.perform(request).andExpect(status().isOk());
	
	}

	
}
