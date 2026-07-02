package com.sparc.sparcscholarshipbackend.controller;

import com.sparc.sparcscholarshipbackend.entity.Application;
import com.sparc.sparcscholarshipbackend.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import com.sparc.sparcscholarshipbackend.entity.Document;
import com.sparc.sparcscholarshipbackend.enums.DocumentType;
import com.sparc.sparcscholarshipbackend.repository.DocumentRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private DocumentRepository documentRepository;

    //quota
    private int boysQuota = 10;
    private int girlsQuota = 10;

    //current quotas
    @GetMapping("/quotas")
    public ResponseEntity<?> getQuotas() {
        return ResponseEntity.ok(java.util.Map.of("boys", boysQuota, "girls", girlsQuota));
    }

    //update quotas
    @PutMapping("/quotas")
    public ResponseEntity<?> updateQuotas(@RequestParam int boys, @RequestParam int girls) {
        //Prevent exceeding total maximum
        if (boys + girls != 20) {
            return ResponseEntity.badRequest().body("Error: Total scholarships must equal exactly 20. If you reduce one quota, you must reallocate those slots to the other gender.");
        }

        // Prevent shrinking quota below the currently approved count
        long approvedBoys = applicationRepository.countByGenderAndStatus(
                com.sparc.sparcscholarshipbackend.enums.Gender.MALE,
                com.sparc.sparcscholarshipbackend.enums.Status.APPROVED
        );

        long approvedGirls = applicationRepository.countByGenderAndStatus(
                com.sparc.sparcscholarshipbackend.enums.Gender.FEMALE,
                com.sparc.sparcscholarshipbackend.enums.Status.APPROVED
        );

        if (boys < approvedBoys) {
            return ResponseEntity.badRequest().body("Error: Cannot reduce Boys Quota to " + boys + " because " + approvedBoys + " boys are already approved. You must reject an application first.");
        }

        if (girls < approvedGirls) {
            return ResponseEntity.badRequest().body("Error: Cannot reduce Girls Quota to " + girls + " because " + approvedGirls + " girls are already approved. You must reject an application first.");
        }


        this.boysQuota = boys;
        this.girlsQuota = girls;
        return ResponseEntity.ok("Quotas updated successfully!");
    }

    @GetMapping("/applications/{id}/documents/{docType}")
    public ResponseEntity<byte[]> viewDocument(@PathVariable Long id, @PathVariable String docType) throws IOException {

        Optional<Document> docOpt = documentRepository.findByApplicationIdAndDocumentType(id, DocumentType.valueOf(docType.toUpperCase()));

        if (docOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Document doc = docOpt.get();
        Path path = Paths.get(doc.getFilePath());

        // Convert the physical file into bytes
        byte[] fileBytes = Files.readAllBytes(path);

        // Send the file back to the browser with the correct format (PDF/JPG)
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + doc.getOriginalName() + "\"")
                .contentType(MediaType.parseMediaType(doc.getMimeType()))
                .body(fileBytes);
    }

    @Autowired
    private ApplicationRepository applicationRepository;

    @GetMapping("/applications")
    public ResponseEntity<List<Application>> getAllApplications() {
        // Fetches every application from the database
        List<Application> applications = applicationRepository.findAll();
        return ResponseEntity.ok(applications);
    }

    @PutMapping("/applications/{id}/status")
    public ResponseEntity<?> updateApplicationStatus(@PathVariable Long id, @RequestParam String newStatus) {
        Optional<Application> applicationOpt = applicationRepository.findById(id);

        if (applicationOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Error: Application not found.");
        }

        Application app = applicationOpt.get();
        com.sparc.sparcscholarshipbackend.enums.Status statusEnum;

        try {
            statusEnum = com.sparc.sparcscholarshipbackend.enums.Status.valueOf(newStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: Invalid status type.");
        }

        //change the status, check it by the quotas.
        if (statusEnum == com.sparc.sparcscholarshipbackend.enums.Status.APPROVED) {
            long currentApprovedCount = applicationRepository.countByGenderAndStatus(
                    app.getGender(),
                    com.sparc.sparcscholarshipbackend.enums.Status.APPROVED
            );

            // Determine which dynamic quota applies to this applicant
            int applicableQuota = app.getGender().name().equals("MALE") ? boysQuota : girlsQuota;

            // Block the approval if the dynamic quota is reached
            if (currentApprovedCount >= applicableQuota) {
                return ResponseEntity.badRequest().body("Quota Error: The maximum limit of " + applicableQuota + " scholarships for " + app.getGender() + "s has already been reached. Reallocate a slot before approving.");
            }
        }

        app.setStatus(statusEnum);
        applicationRepository.save(app);
        return ResponseEntity.ok("Application " + id + " status updated to " + newStatus);
    }
}