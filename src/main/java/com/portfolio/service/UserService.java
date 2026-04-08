package com.portfolio.service;

import org.springframework.stereotype.Service;
import com.portfolio.repo.UserRepo;
import com.portfolio.repo.ProjectRepo;
import com.portfolio.entity.User;
import com.portfolio.entity.Project;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.util.*;

@Service
public class UserService {

    private final UserRepo repo;
    private final ProjectRepo projectRepo;  // Repository for projects

    public UserService(UserRepo r, ProjectRepo p) {
        this.repo = r;
        this.projectRepo = p;
    }

    // =========================
    // EXISTING: Register user
    // =========================
    public User register(User u) {
        return repo.save(u);
    }

    // =========================
    // EXISTING: Login
    // =========================
    public Optional<User> login(String email, String pass) {
        Optional<User> u = repo.findByEmail(email);
        if (u.isPresent() && u.get().getPassword().equals(pass)) return u;
        return Optional.empty();
    }
    // =========================
// NEW: Get all projects for a student
// =========================
public List<Project> getProjectsByStudent(Long studentId) {
    User student = repo.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found"));
    return projectRepo.findByStudent(student);
}

    // =========================
    // NEW: Save student project (metadata to DB)
    // =========================
    public Project saveProject(Long studentId, String title, String description, String fileName) {
        // 1️⃣ Fetch the student from the DB
        User student = repo.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // 2️⃣ Create a new Project
        Project project = new Project();
        project.setTitle(title);
        project.setDescription(description);
        project.setFilePath(fileName);

        // 3️⃣ Associate project with student
        project.setStudent(student);

        // 4️⃣ Save project metadata in DB
        return projectRepo.save(project);
    }

    // =========================
    // NEW: Utility to save file on disk
    // =========================
    public static String saveFileToDisk(MultipartFile file) {
        try {
            String uploadDir = System.getProperty("user.dir") + "/uploads/";
            Files.createDirectories(Paths.get(uploadDir)); // create folder if missing

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + fileName);

            file.transferTo(filePath.toFile()); // save file physically
            System.out.println("File saved at: " + filePath.toAbsolutePath());
            return fileName;
        } catch (Exception e) {
            throw new RuntimeException("Failed to save file: " + e.getMessage());
        }
    }
}