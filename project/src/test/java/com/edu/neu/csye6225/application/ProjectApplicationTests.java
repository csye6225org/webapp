package com.edu.neu.csye6225.application;

import com.edu.neu.csye6225.application.user.User;
import com.edu.neu.csye6225.application.user.UserController;
import com.edu.neu.csye6225.application.user.UserRepository;
import com.edu.neu.csye6225.application.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

//@SpringBootTest

class ProjectApplicationTests {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper mapper;

	@MockBean
	UserRepository patientRecordRepository;

//	@Test
//	void contextLoads() {
//	}

//	@Autowired
//	private UserController controller;

//	@Test
//	public void contextLoads_1() throws Exception {
//		assertThat(controller).isNotNull();
//	}






}
