package com.edu.neu.csye6225.application.picture;

import com.edu.neu.csye6225.application.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

@Entity(name = "Picture")
@Table( name = "picture",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "picture_fileurl_unique",
                        columnNames = "file_url"
                )
        }
)
public class Picture {

    @Id
    private UUID id;

    @Column(
            name = "file_name",
            columnDefinition = "TEXT"
    )
    private String filename;

    @Column(
            name = "file_url",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String fileurl;

    @Column(
            name = "upload_date",
            columnDefinition = "DATE"
    )
    private LocalDate uploaddate;

    UUID user_id;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFileurl() {
        return fileurl;
    }

    public void setFileurl(String fileurl) {
        this.fileurl = fileurl;
    }

    public LocalDate getUploaddate() {
        return uploaddate;
    }

    public void setUploaddate(LocalDate uploaddate) {
        this.uploaddate = uploaddate;
    }

    public UUID getUser_id() {
        return user_id;
    }

    public void setUser_id(UUID user_id) {
        this.user_id = user_id;
    }

    public Picture(UUID id, String filename, String fileurl, LocalDate uploaddate) {
        this.id = id;
        this.filename = filename;
        this.fileurl = fileurl;
        this.uploaddate = uploaddate;
    }

    public Picture(UUID id, String filename, String fileurl, LocalDate uploaddate, UUID user_id) {
        this.id = id;
        this.filename = filename;
        this.fileurl = fileurl;
        this.uploaddate = uploaddate;
        this.user_id = user_id;
    }

    public Picture() {
    }

    @Override
    public String toString() {
        return "Picture{" +
                "id=" + id +
                ", filename='" + filename + '\'' +
                ", fileurl='" + fileurl + '\'' +
                ", uploaddate=" + uploaddate +
//                ", user=" + user +
                '}';
    }
}
