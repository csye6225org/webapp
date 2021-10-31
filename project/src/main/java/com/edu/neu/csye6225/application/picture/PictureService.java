package com.edu.neu.csye6225.application.picture;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.edu.neu.csye6225.application.user.User;
import com.edu.neu.csye6225.application.user.UserRepository;
import com.edu.neu.csye6225.application.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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

    @Autowired
    private UserService userService;

    private PictureRepository pictureRepository;

    @Autowired
    PictureService(PictureRepository pictureRepository){
        this.pictureRepository = pictureRepository;
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convertedFile);
        fos.write(file.getBytes());
        fos.close();
        return convertedFile;
    }

    public boolean checkIfPictureExists(UUID user_id){
        List<Picture> pictures_list = pictureRepository.findAll();
        for(Picture p:pictures_list){
            if(p.getUser_id().equals(user_id)){
                return true;
            };
        }
        return false;
    }

    public String uploadPicture(MultipartFile picture, String username) {

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

        System.out.println("content_type"+content_type+"file_size"+file_size);

        // Get User
        User u = userService.getUserByUsername(username);

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
        System.out.println(p.toString());

        if(checkIfPictureExists(u.getId())){
            deletePicture(u.getUsername());
        }

        // Add picture to s3 bucket
        s3Client.putObject(new PutObjectRequest(s3BucketName, filename, fileObject));
        fileObject.delete();
        // Add picture object to database
        pictureRepository.save(p);

        return filename+" uploaded successfully.";
    }



    public Picture getPictureByUserId(UUID user_id){
        List<Picture> pictures_list = pictureRepository.findAll();
        for(Picture p:pictures_list){
            if(p.getUser_id().equals(user_id)){
                return p;
            };
        }
        return null;
    }

    public ResponseEntity<Object> deletePicture(String username){

        User u = userService.getUserByUsername(username);
        UUID user_id = u.getId();
        Picture p = getPictureByUserId(user_id);

        if(p == null){
            return new ResponseEntity<>("User dont have picture", HttpStatus.NOT_FOUND);
        }

        s3Client.deleteObject(s3BucketName, p.getFilename());
        pictureRepository.deleteById(p.getId());

        String response_body_message = p.getFilename()+" deleted successfully";
        return new ResponseEntity<>(response_body_message, HttpStatus.NO_CONTENT);
    }

    public Map<String, String> getPictureBodyByUsername(String username) {
        User u = userService.getUserByUsername(username);
        Picture p = getPictureByUserId(u.getId());

        if(p == null){
            return null;
        }

        Map<String, String> pictureDetails = new HashMap<>();
        pictureDetails.put("file_name",p.getFilename());
        pictureDetails.put("id",p.getId().toString());
        pictureDetails.put("url",p.getFileurl());
        pictureDetails.put("upload_date",p.getUploaddate().toString());
        pictureDetails.put("user_id",p.getUser_id().toString());

         return pictureDetails;
    }


}
