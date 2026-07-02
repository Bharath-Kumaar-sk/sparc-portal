package com.sparc.sparcscholarshipbackend.controller;

import com.sparc.sparcscholarshipbackend.entity.Application;
import com.sparc.sparcscholarshipbackend.entity.Document;
import com.sparc.sparcscholarshipbackend.enums.DocumentType;
import com.sparc.sparcscholarshipbackend.repository.ApplicationRepository;
import com.sparc.sparcscholarshipbackend.repository.DocumentRepository;
import com.sparc.sparcscholarshipbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private UserRepository userRepository;
    //local storage path
    private final String UPLOAD_DIR = "uploads/";

    //the "/me" in application gets the application, this gets the document.
    @GetMapping("/me/{docType}")
    public ResponseEntity<byte[]> getMyDocument(@PathVariable String docType) throws Exception {
        // Get the current user
        String email = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        com.sparc.sparcscholarshipbackend.entity.User user = userRepository.findByEmail(email).get();

        //Get their application
        Application app = applicationRepository.findByUserId(user.getId()).get();

        //Get the specific document
        Optional<Document> docOpt = documentRepository.findByApplicationIdAndDocumentType(app.getId(), com.sparc.sparcscholarshipbackend.enums.DocumentType.valueOf(docType.toUpperCase()));

        if (docOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Document doc = docOpt.get();
        java.nio.file.Path path = java.nio.file.Paths.get(doc.getFilePath());
        byte[] fileBytes = java.nio.file.Files.readAllBytes(path);

        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + doc.getOriginalName() + "\"")
                .contentType(org.springframework.http.MediaType.parseMediaType(doc.getMimeType()))
                .body(fileBytes);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("applicationId") Long applicationId,
            @RequestParam("documentType") String documentType) {

        try {
            // Validate the file type and size (Keep your existing validation logic here if you have it)
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Error: File is empty");
            }

            // --- NEW: STRICT BACKEND FORMAT LIMIT ---
            String mimeType = file.getContentType();
            if (mimeType == null || (!mimeType.equals("application/pdf") &&
                    !mimeType.equals("image/jpeg") &&
                    !mimeType.equals("image/png"))) {
                return ResponseEntity.badRequest().body("Error: Only PDF, JPG, and PNG formats are allowed.");
            }

            long maxFileSize = 2 * 1024 * 1024; // 2MB in bytes
            if (file.getSize() > maxFileSize) {
                return ResponseEntity.badRequest().body("Error: File " + file.getOriginalFilename() + " exceeds the 2MB limit.");}

            //create uploads dir if it does not exist and then using application id create
            //sub directories and save file in them.
            String uploadDir = System.getProperty("user.dir") + "/uploads/" + applicationId + "/";
            File directory = new File(uploadDir);

            if (!directory.exists()) {
                directory.mkdirs(); //create the subdirectorty based on the ID.
            }

            //Save the physical file into the new subfolder
            String filePath = uploadDir + file.getOriginalFilename();
            file.transferTo(new File(filePath));

            //Save the document metadata to the database
            Optional<Application> applicationOpt = applicationRepository.findById(applicationId);
            if (applicationOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Error: Application not found");
            }

            Document document = new Document();
            document.setApplication(applicationOpt.get());
            document.setDocumentType(com.sparc.sparcscholarshipbackend.enums.DocumentType.valueOf(documentType));
            document.setOriginalName(file.getOriginalFilename());
            document.setMimeType(file.getContentType());
            document.setFilePath(filePath);

            documentRepository.save(document);

            return ResponseEntity.ok("File uploaded successfully to Application ID: " + applicationId);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Could not upload the file: " + e.getMessage());
        }
    }
}