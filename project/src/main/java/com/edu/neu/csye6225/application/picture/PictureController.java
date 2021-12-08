package com.edu.neu.csye6225.application.picture;

import com.amazonaws.util.IOUtils;
import com.edu.neu.csye6225.application.user.UserService;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@RestController
@RequestMapping(path = "v1/user")
@Transactional
public class PictureController {
    @Autowired
    PictureService pictureService;

    @Autowired
    UserService userService;

    Logger logger = LoggerFactory.getLogger(PictureController.class);

    private StatsDClient statsd = new NonBlockingStatsDClient("statsd", "localhost", 8125);

    @PostMapping(value = "/self/pic")
    public ResponseEntity<Object> uploadPicture(HttpServletRequest request) throws IOException {

        long start_uploadPicture_controller = System.currentTimeMillis();
        logger.info("Inside uploadPicture controller.");
        statsd.incrementCounter("uploadPictureController");
        statsd.incrementCounter("apiCall");

        ResponseEntity<Object> header_authentication_result = userService.authenticateHeader(request);

        if (header_authentication_result.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
            logger.warn("Request header did not get authenticated");
            long end_uploadPicture_controller = System.currentTimeMillis();
            long elapsedTime = end_uploadPicture_controller - start_uploadPicture_controller;
            statsd.recordExecutionTime("uploadPicture_controller_et", elapsedTime);
            return header_authentication_result;
        } else {
            logger.info("Validating file");
            String contentType = request.getContentType();
            if(contentType == null)
            {
                logger.warn("No file was provided to upload");
                long end_uploadPicture_controller = System.currentTimeMillis();
                long elapsedTime = end_uploadPicture_controller - start_uploadPicture_controller;
                statsd.recordExecutionTime("uploadPicture_controller_et", elapsedTime);
                return new ResponseEntity<Object>(
                        "There should be a file or atleast correct file",
                        HttpStatus.BAD_REQUEST);
            }
            if(contentType.equals("image/jpeg") || contentType.equals("image/png"))
            {
                logger.info("Getting user credentials from header");
                String userHeader = request.getHeader("Authorization");
                String[] userCredentials = userService.getUserCredentials(userHeader);

                if(!userService.checkIfUserIsVerified(userCredentials[0])){
                    return new ResponseEntity<Object>(
                            "User is not verified. Please check your email and verify your account.",
                            HttpStatus.FORBIDDEN);
                }

                else {

                    logger.info("Retrieving image from request");
                    InputStream pictureIS = request.getInputStream();
                    byte[] pictureBA = IOUtils.toByteArray(pictureIS);

                    String name = "file.txt";
                    String originalFileName = "file.txt";

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
                    long end_uploadPicture_controller = System.currentTimeMillis();
                    long elapsedTime = end_uploadPicture_controller - start_uploadPicture_controller;
                    statsd.recordExecutionTime("uploadPicture_controller_et", elapsedTime);
                    return new ResponseEntity<>(
                            responseBody,
                            HttpStatus.OK
                    );
                }
            } else {
                logger.warn("File sent via request is not an image.");
                long end_uploadPicture_controller = System.currentTimeMillis();
                long elapsedTime = end_uploadPicture_controller - start_uploadPicture_controller;
                statsd.recordExecutionTime("uploadPicture_controller_et", elapsedTime);
                return new ResponseEntity<Object>("File should be Image", HttpStatus.BAD_REQUEST);
            }
        }
    }

    @DeleteMapping("/self/pic/")
    public ResponseEntity<Object> deleteFile(HttpServletRequest request) {

        long start_deleteFile_controller = System.currentTimeMillis();
        logger.info("Inside deleteFile controller.");
        logger.info("Authenticating request header.");
        statsd.incrementCounter("deleteFileController");
        statsd.incrementCounter("apiCall");

        ResponseEntity<Object> header_authentication_result = userService.authenticateHeader(request);

        if (header_authentication_result.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
            logger.warn("Request header not authenticated");
            long end_deleteFile_controller = System.currentTimeMillis();
            long elapsedTime = end_deleteFile_controller - start_deleteFile_controller;
            statsd.recordExecutionTime("deleteFile_controller_et", elapsedTime);
            return header_authentication_result;
        } else {

            logger.info("Getting credentials part of request header");
            String userHeader = request.getHeader("Authorization");

            logger.info("Getting user credentials from header.");
            String[] userCredentials = userService.getUserCredentials(userHeader);

            if(!userService.checkIfUserIsVerified(userCredentials[0])){
                return new ResponseEntity<Object>(
                        "User is not verified. Please check your email and verify your account.",
                        HttpStatus.FORBIDDEN);
            }

            else {
                logger.info("Deleting picture from S3 bucket.");
                ResponseEntity<Object> responseEntity = pictureService.deletePicture(userCredentials[0]);
                logger.info("Picture deleted successfully. Returning response to user");
                long end_deleteFile_controller = System.currentTimeMillis();
                long elapsedTime = end_deleteFile_controller - start_deleteFile_controller;
                statsd.recordExecutionTime("deleteFile_controller_et", elapsedTime);
                return responseEntity;
            }
        }
    }

    @GetMapping("/self/pic/")
    @Transactional(readOnly = true)
    public ResponseEntity<Object> getPicture(HttpServletRequest request){

        long start_getPicture_controller = System.currentTimeMillis();
        logger.info("Inside getPicture controller.");
        logger.info("Authenticating user credentials from request header.");
        statsd.incrementCounter("getPictureController");
        statsd.incrementCounter("apiCall");

        ResponseEntity<Object> header_authentication_result = userService.authenticateHeader(request);

        if (header_authentication_result.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
            logger.warn("User credentials from request header are not authenticated.");
            long end_getPicture_controller = System.currentTimeMillis();
            long elapsedTime = end_getPicture_controller - start_getPicture_controller;
            statsd.recordExecutionTime("getPicture_controller_et", elapsedTime);
            return header_authentication_result;
        } else {
            logger.info("User credentials authenticated successfully. Getting picture information.");
            logger.info("Retrieve user credentials from header");
            String userHeader = request.getHeader("Authorization");
            String[] userCredentials = userService.getUserCredentials(userHeader);

            if(!userService.checkIfUserIsVerified(userCredentials[0])){
                return new ResponseEntity<Object>(
                        "User is not verified. Please check your email and verify your account.",
                        HttpStatus.FORBIDDEN);
            }

            else {
                logger.info("Generating picture information response body.");
                Map<String, String> responseBody = pictureService.getPictureBodyByUsername(userCredentials[0]);

                if (responseBody == null) {
                    logger.warn("User dont have a picture");
                    long end_getPicture_controller = System.currentTimeMillis();
                    long elapsedTime = end_getPicture_controller - start_getPicture_controller;
                    statsd.recordExecutionTime("getPicture_controller_et", elapsedTime);
                    return new ResponseEntity<>("User dont have a picture", HttpStatus.NOT_FOUND);
                }

                logger.info("Returning picture information response body");
                long end_getPicture_controller = System.currentTimeMillis();
                long elapsedTime = end_getPicture_controller - start_getPicture_controller;
                statsd.recordExecutionTime("getPicture_controller_et", elapsedTime);

                return new ResponseEntity<>(responseBody, HttpStatus.OK);
            }
        }
    }
}
