package joa.oasis.product.controller;

import joa.oasis.product.security.JwtTokenProvider;
import joa.oasis.product.service.S3Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    private final S3Service s3Service;
    private final JwtTokenProvider jwtTokenProvider;

    public VideoController(S3Service s3Service, JwtTokenProvider jwtTokenProvider) {
        this.s3Service = s3Service;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadVideo(@RequestParam("file") MultipartFile file,
                                              @RequestHeader("Authorization") String token) {
        if (token == null || !jwtTokenProvider.validateToken(token.replace("Bearer ", ""))) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        try {
            String fileUrl = s3Service.uploadFile(file);
            return new ResponseEntity<>("File uploaded successfully. Access it at: " + fileUrl, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("File upload failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}