package com.edu.neu.csye6225.application.picture;

import com.amazonaws.http.HttpResponse;
import com.amazonaws.util.IOUtils;
import com.edu.neu.csye6225.application.user.User;
import com.edu.neu.csye6225.application.user.User;

import com.edu.neu.csye6225.application.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;


import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@RestController
@RequestMapping(path = "v1/user")
public class PictureController {
    @Autowired
    PictureService pictureService;

    @Autowired
    UserService userService;

    @PostMapping(value = "/self/pic")
    public ResponseEntity<Object> uploadPicture(HttpServletRequest request) throws IOException {

        ResponseEntity<Object> header_authentication_result = userService.authenticateHeader(request);

        if (header_authentication_result.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
            return header_authentication_result;
        } else {
            String contentType = request.getContentType();
            if(contentType == null)
            {
                return new ResponseEntity<Object>(
                        "There should be a file or atlease correct file",
                        HttpStatus.BAD_REQUEST);
            }
            if(contentType.equals("image/jpeg") || contentType.equals("image/png"))
            {
                InputStream pictureIS = request.getInputStream();
                byte[] pictureBA = IOUtils.toByteArray(pictureIS);

                String name = "file.txt";
                String originalFileName = "file.txt";

                String userHeader = request.getHeader("Authorization");
                String[] userCredentials = userService.getUserCredentials(userHeader);

                MultipartFile pictureMPF = new MockMultipartFile(
                        name,
                        originalFileName,
                        contentType,
                        pictureBA
                );

                return new ResponseEntity<>(
                        pictureService.uploadPicture(pictureMPF, userCredentials[0]),
                        HttpStatus.OK
                );
            } else {
                return new ResponseEntity<Object>("File should be Image", HttpStatus.BAD_REQUEST);
            }
        }
    }


    @DeleteMapping("/self/pic/")
    public ResponseEntity<Object> deleteFile(HttpServletRequest request) {
        ResponseEntity<Object> header_authentication_result = userService.authenticateHeader(request);

        if (header_authentication_result.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
            return header_authentication_result;
        } else {
            String userHeader = request.getHeader("Authorization");
            String[] userCredentials = userService.getUserCredentials(userHeader);

            ResponseEntity<Object> responseEntity = pictureService.deletePicture(userCredentials[0]);
            return responseEntity;
        }
    }

    @GetMapping("/self/pic/")
    public ResponseEntity<Object> getPicture(HttpServletRequest request){
        ResponseEntity<Object> header_authentication_result = userService.authenticateHeader(request);

        if (header_authentication_result.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
            return header_authentication_result;
        } else {
            String userHeader = request.getHeader("Authorization");
            String[] userCredentials = userService.getUserCredentials(userHeader);

            Map<String, String> responseBody = pictureService.getPictureBodyByUsername(userCredentials[0]);

            if(responseBody == null){
                return new ResponseEntity<>("User dont have a picture", HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        }
    }
}
