package com.edu.neu.csye6225.application.health_check;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping(path = "/")
public class HealthCheckController {

    @GetMapping
    public ResponseEntity<Object> uploadPicture(HttpServletRequest request) throws IOException {
        return new ResponseEntity<>("Everything OK", HttpStatus.OK);
    }

}
