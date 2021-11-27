package com.edu.neu.csye6225.application.picture;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.edu.neu.csye6225.application.user.User;
import com.edu.neu.csye6225.application.user.UserReadOnlyService;
import com.edu.neu.csye6225.application.user.UserRepository;
import com.edu.neu.csye6225.application.user.UserService;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PictureService {

    @Value("${amazonProperties.bucketName}")
    private String s3BucketName;

    @Autowired
    private AmazonS3 s3Client;

//    @Autowired
//    private UserService userService;

    @Autowired
    private UserReadOnlyService userReadOnlyService;

    private PictureRepository pictureRepository;

    private StatsDClient statsd = new NonBlockingStatsDClient("statsd", "localhost", 8125);

    Logger logger = LoggerFactory.getLogger(PictureService.class);

    @Autowired
    PictureService(PictureRepository pictureRepository){
        this.pictureRepository = pictureRepository;
    }


    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        logger.info("Converting multipart file to File");
        File convertedFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convertedFile);
        fos.write(file.getBytes());
        fos.close();
        logger.info("Returning File");
        return convertedFile;
    }

//    @Transactional(readOnly = true)
    public List<Picture> getPictureInfo(){
        return pictureRepository.findAll();
    }

//    @Transactional
    public boolean checkIfPictureExists(UUID user_id){
        logger.info("Checking if Picture Exists");
        List<Picture> pictures_list = getPictureInfo();
        for(Picture p:pictures_list){
            if(p.getUser_id().equals(user_id)){
                logger.info("Picture found");
                return true;
            };
        }
        logger.warn("Picture dont exist");
        return false;
    }

//    @Transactional
    public String uploadPicture(MultipartFile picture, String username) {

        logger.info("Uploading picture to S3 bucket");
        // Convert multipart file to file object
        File fileObject = null;
        try {
            fileObject = convertMultiPartToFile(picture);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Get File metadata
        String content_type = picture.getContentType();
        long file_size = picture.getSize();

        logger.info("content_type"+content_type+"file_size"+file_size);

        // Get User
        User u = userReadOnlyService.getUserByUsername(username);

        logger.info("Collecting file information");
        // from here you will get file metadata picture.get*
        String filename = "";
        if(content_type.equals("image/png")){
            filename = u.getId().toString()+".png";
        } else if(content_type.equals("image/jpeg")){
            filename = u.getId().toString()+".jpeg";
        } else {
            filename = u.getId().toString();
        }

        // Get Current Date
        LocalDate datetoday = LocalDate.now();

        // Build file URL
        String file_url = s3BucketName+"/"+u.getId()+"/"+filename;

        // Build Picture object to add to data
        Picture p = new Picture(
                UUID.randomUUID(),
                filename,
                file_url,
                datetoday,
                u.getId()
        );
        logger.info(p.toString());

        logger.info("Delete picture if it exists for user");
        if(checkIfPictureExists(u.getId())){
            deletePicture(u.getUsername());
        }

        // Add picture to s3 bucket
        logger.info("Uploading picture to S3 bucket");
        long start_time_upload_picture_to_s3 = System.currentTimeMillis();
        s3Client.putObject(new PutObjectRequest(s3BucketName, filename, fileObject));
        long end_time_upload_picture_to_s3 = System.currentTimeMillis();
        long elapsedTime1 = end_time_upload_picture_to_s3 - start_time_upload_picture_to_s3;
        statsd.recordExecutionTime("upload_picture_to_s3_et", elapsedTime1);

        fileObject.delete();
        // Add picture object to database
        logger.info("Saving picture information to database");

        long start_time_save_picture_info = System.currentTimeMillis();
        pictureRepository.save(p);
        long end_time_save_picture_info = System.currentTimeMillis();
        long elapsedTime2 = end_time_save_picture_info - start_time_save_picture_info;
        statsd.recordExecutionTime("save_picture_info_et", elapsedTime2);


        logger.info("Picture uploaded successfully");
        return filename+" uploaded successfully.";
    }


//    @Transactional
    public Picture getPictureByUserId(UUID user_id){
        logger.info("Getting picture by user id");
        long start_time_getall_picture_info = System.currentTimeMillis();
        List<Picture> pictures_list = getPictureInfo();
        long end_time_getall_picture_info = System.currentTimeMillis();
        long elapsedTime = end_time_getall_picture_info - start_time_getall_picture_info;
        statsd.recordExecutionTime("getall_picture_info_et", elapsedTime);

        for(Picture p:pictures_list){
            if(p.getUser_id().equals(user_id)){
                logger.info("Returning retrieved picture");
                return p;
            };
        }
        logger.warn("Picture for user not found");
        return null;
    }

//    @Transactional
    public ResponseEntity<Object> deletePicture(String username){
        logger.info("Deleting picture by username");
        User u = userReadOnlyService.getUserByUsername(username);
        UUID user_id = u.getId();
        Picture p = getPictureByUserId(user_id);

        if(p == null){
            return new ResponseEntity<>("User dont have picture", HttpStatus.NOT_FOUND);
        }

        logger.info("Deleting picture from S3 Bucket.");
        long start_time_delete_picture_from_s3 = System.currentTimeMillis();
        s3Client.deleteObject(s3BucketName, p.getFilename());
        long end_time_delete_picture_from_s3 = System.currentTimeMillis();
        long elapsedTime1 = end_time_delete_picture_from_s3 - start_time_delete_picture_from_s3;
        statsd.recordExecutionTime("delete_picture_from_s3_et", elapsedTime1);

        logger.info("Deleting picture record from database");
        long start_time_delete_picture_info = System.currentTimeMillis();
        pictureRepository.deleteById(p.getId());
        long end_time_delete_picture_info = System.currentTimeMillis();
        long elapsedTime2 = end_time_delete_picture_info - start_time_delete_picture_info;
        statsd.recordExecutionTime("delete_picture_from_s3_et", elapsedTime2);


        String response_body_message = p.getFilename()+" deleted successfully";
        logger.info("Returning response for successful deletion of picture");
        return new ResponseEntity<>(response_body_message, HttpStatus.NO_CONTENT);
    }

//    @Transactional
    public Map<String, String> getPictureBodyByUsername(String username) {
        logger.info("Getting picture information by username");
        User u = userReadOnlyService.getUserByUsername(username);
        Picture p = getPictureByUserId(u.getId());

        if(p == null){
            logger.warn("No picture was found for user");
            return null;
        }

        logger.info("Creating picture information response body");
        Map<String, String> pictureDetails = new HashMap<>();
        pictureDetails.put("file_name",p.getFilename());
        pictureDetails.put("id",p.getId().toString());
        pictureDetails.put("url",p.getFileurl());
        pictureDetails.put("upload_date",p.getUploaddate().toString());
        pictureDetails.put("user_id",p.getUser_id().toString());

        logger.info("Returning picture information response body");
         return pictureDetails;
    }


}
