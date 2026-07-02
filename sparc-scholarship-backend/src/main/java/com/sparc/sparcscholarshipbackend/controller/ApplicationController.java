package com.sparc.sparcscholarshipbackend.controller;

import com.sparc.sparcscholarshipbackend.dto.ApplicationRequest;
import com.sparc.sparcscholarshipbackend.entity.Application;
import com.sparc.sparcscholarshipbackend.entity.User;
import com.sparc.sparcscholarshipbackend.enums.Status;
import com.sparc.sparcscholarshipbackend.repository.ApplicationRepository;
import com.sparc.sparcscholarshipbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private UserRepository userRepository;

    //to display user's application
    @GetMapping("/me")
    public ResponseEntity<?> getMyApplication() {
        //Get the currently logged in user email from the JWT
        String email = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();

        //Find the user in the database
        Optional<com.sparc.sparcscholarshipbackend.entity.User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Error: Unauthorized");
        }

        //Find their application use rpository
        Optional<Application> appOpt = applicationRepository.findByUserId(userOpt.get().getId());

        if (appOpt.isPresent()) {
            return ResponseEntity.ok(appOpt.get()); // Return the application if it exists
        } else {
            return ResponseEntity.notFound().build(); // Return 404 if they haven't applied yet
        }
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitApplication(@RequestBody ApplicationRequest request) {

        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate openDate = java.time.LocalDate.of(2026, 6, 1);
        java.time.LocalDate closeDate = java.time.LocalDate.of(2026, 9, 10);

        //enforce the date rule. (open - close timing of application).
        if (today.isBefore(openDate) || today.isAfter(closeDate)) {
            return ResponseEntity.badRequest().body("Error: The SPARC Scholarship portal is only open from July 1, 2026, to September 10, 2026.");
        }

        //authenticate using JWT token, using which get username -> get the email and other details.
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        //enforce rule of One Application Per User
        Optional<Application> existingApp = applicationRepository.findByUserId(currentUser.getId());
        if (existingApp.isPresent()) {
            return ResponseEntity.badRequest().body("Error: You have already submitted an application.");
        }

        //Age must be 13 to 19 as of July 15th 2026
        java.time.LocalDate cutoffDate = java.time.LocalDate.of(2026, 6, 15);
        int age = Period.between(request.getDateOfBirth(), cutoffDate).getYears();

        if (age < 13 || age > 19) {
            return ResponseEntity.badRequest().body("Error: Applicant must be strictly between 13 and 19 years of age as of July 15, 2026.");
        }

        boolean isValidPerformance = false;

        String level = request.getScholarshipLevel().toString();
        String tourney = request.getTournamentLevel().toString();
        String achievement = request.getAchievement().toString();

        if (level.equals("ELITE_SCHOLAR")) {
            // International participation OR National representation / Top 1-5
            if (tourney.equals("INTERNATIONAL") && achievement.equals("PARTICIPATION")) {
                isValidPerformance = true;
            } else if (tourney.equals("NATIONAL") && (achievement.equals("TOP_1_5") || achievement.equals("REPRESENTATION"))) {
                isValidPerformance = true;
            }
        } else if (level.equals("SCHOLAR")) {
            // State representation / medalist OR National Rank 6-10 / Quarter-finalist
            if (tourney.equals("STATE") && (achievement.equals("STATE_MEDALIST") || achievement.equals("REPRESENTATION"))) {
                isValidPerformance = true;
            } else if (tourney.equals("NATIONAL") && (achievement.equals("RANK_6_10") || achievement.equals("QUARTER_FINALIST"))) {
                isValidPerformance = true;
            }
        }

        if (!isValidPerformance) {
            return ResponseEntity.badRequest().body("Error: Your tournament level and achievement do not meet the criteria for the selected Scholarship Tier.");
        }

        //DTO to Entity
        Application application = new Application();
        application.setUser(currentUser);
        application.setFirstName(request.getFirstName());
        application.setLastName(request.getLastName());
        application.setGender(request.getGender());
        application.setDateOfBirth(request.getDateOfBirth());
        application.setSportName(request.getSportName());
        application.setScholarshipLevel(request.getScholarshipLevel());
        application.setTournamentLevel(request.getTournamentLevel());
        application.setAchievement(request.getAchievement());
        application.setFederationName(request.getFederationName());

        //change status
        application.setStatus(Status.SUBMITTED);

        //save to db
        application.setSubmittedAt(java.time.LocalDateTime.now());
        applicationRepository.save(application);

        return ResponseEntity.ok(application.getId());
    }
}