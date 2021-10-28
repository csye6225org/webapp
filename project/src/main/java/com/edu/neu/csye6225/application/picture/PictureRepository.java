package com.edu.neu.csye6225.application.picture;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PictureRepository extends JpaRepository<Picture, UUID> {
}
