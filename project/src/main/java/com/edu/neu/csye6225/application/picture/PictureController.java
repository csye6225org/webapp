package com.edu.neu.csye6225.application.picture;

import com.amazonaws.http.HttpResponse;
import com.amazonaws.util.IOUtils;
import com.edu.neu.csye6225.application.user.User;
import com.edu.neu.csye6225.application.user.User;

import com.edu.neu.csye6225.application.user.UserService;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    Logger logger = LoggerFactory.getLogger(PictureController.class);

//    private StatsDClient statsd;

    @PostMapping(value = "/self/pic")
    public ResponseEntity<Object> uploadPicture(HttpServletRequest request) throws IOException {

        logger.info("Inside uploadPicture controller.");
//        statsd.incrementCounter("uploadPictureController");
//        statsd.incrementCounter("apiCall");

        ResponseEntity<Object> header_authentication_result = userService.authenticateHeader(request);

        if (header_authentication_result.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
            logger.error("Request header did not get authenticated");
            return header_authentication_result;
        } else {
            logger.info("Validating file");
            String contentType = request.getContentType();
            if(contentType == null)
            {
                logger.error("No file was provided to upload");
                return new ResponseEntity<Object>(
                        "There should be a file or atleast correct file",
                        HttpStatus.BAD_REQUEST);
            }
            if(contentType.equals("image/jpeg") || contentType.equals("image/png"))
            {
                logger.info("Retrieving image from request");
                InputStream pictureIS = request.getInputStream();
                byte[] pictureBA = IOUtils.toByteArray(pictureIS);

                String name = "file.txt";
                String originalFileName = "file.txt";

                logger.info("Getting user credentials from header");
                String userHeader = request.getHeader("Authorization");
                String[] userCredentials = userService.getUserCredentials(userHeader);

                MultipartFile pictureMPF = new MockMultipartFile(
                        name,
                        originalFileName,
                        contentType,
                        pictureBA
                );

                logger.info("Uploading picture");
                pictureService.uploadPicture(pictureMPF, userCredentials[0]);
                Map<String, String> responseBody = pictureService.getPictureBodyByUsername(userCredentials[0]);

                logger.info("Returning response for uploaded picture");
                return new ResponseEntity<>(
                        responseBody,
                        HttpStatus.OK
                );
            } else {
                logger.warn("File sent via request is not an image.");
                return new ResponseEntity<Object>("File should be Image", HttpStatus.BAD_REQUEST);
            }
        }
    }


    @DeleteMapping("/self/pic/")
    public ResponseEntity<Object> deleteFile(HttpServletRequest request) {

        logger.info("Inside deleteFile controller.");
        logger.info("Authenticating request header.");
//        statsd.incrementCounter("deleteFileController");
//        statsd.incrementCounter("apiCall");

        ResponseEntity<Object> header_authentication_result = userService.authenticateHeader(request);

        if (header_authentication_result.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
            logger.error("Request header not authenticated");
            return header_authentication_result;
        } else {

            logger.info("Getting credentials part of request header");
            String userHeader = request.getHeader("Authorization");

            logger.info("Getting user credentials from header.");
            String[] userCredentials = userService.getUserCredentials(userHeader);

            logger.info("Deleting picture from S3 bucket.");
            ResponseEntity<Object> responseEntity = pictureService.deletePicture(userCredentials[0]);
            logger.info("Picture deleted successfully. Returning response to user");
            return responseEntity;
        }
    }

    @GetMapping("/self/pic/")
    public ResponseEntity<Object> getPicture(HttpServletRequest request){
        logger.info("Inside getPicture controller.");
        logger.info("Authenticating user credentials from request header.");
//        statsd.incrementCounter("getPictureController");
//        statsd.incrementCounter("apiCall");

        ResponseEntity<Object> header_authentication_result = userService.authenticateHeader(request);

        if (header_authentication_result.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
            logger.error("User credentials from request header are not authenticated.");
            return header_authentication_result;
        } else {
            logger.info("User credentials authenticated successfully. Getting picture information.");
            logger.info("Retrieve user credentials from header");
            String userHeader = request.getHeader("Authorization");
            String[] userCredentials = userService.getUserCredentials(userHeader);

            logger.info("Generating picture information response body.");
            Map<String, String> responseBody = pictureService.getPictureBodyByUsername(userCredentials[0]);

            if(responseBody == null){
                logger.warn("User dont have a picture");
                return new ResponseEntity<>("User dont have a picture", HttpStatus.NOT_FOUND);
            }

            logger.info("Returning picture information response body");
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        }
    }
}
