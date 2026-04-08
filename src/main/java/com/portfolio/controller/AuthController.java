package com.portfolio.controller;

import com.portfolio.entity.User;
import com.portfolio.entity.Project;
import com.portfolio.repo.UserRepo;
import com.portfolio.security.JwtUtil;
import com.portfolio.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5174")
public class AuthController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    // =========================
    // REGISTER
    // =========================
    @PostMapping("/register")
    public User register(@RequestBody User user) {
        if (userRepo.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists");
        }
        return userRepo.save(user);
    }

    // =========================
    // LOGIN
    // =========================
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User user) {
        User existing = userRepo.findByEmail(user.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!existing.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(existing.getEmail());

        return Map.of(
                "token", token,
                "email", existing.getEmail(),
                "name", existing.getName(),
                "role", existing.getRole()
        );
    }

    // =========================
    // UPLOAD PROJECT
    // =========================
    @PostMapping("/uploadProjects")
    public List<Project> uploadProjects(
            @RequestParam("studentId") Long studentId,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("files") MultipartFile[] files) throws IOException {

        List<Project> uploadedProjects = new ArrayList<>();

        for (MultipartFile file : files) {
            // Save file to disk
            String fileName = UserService.saveFileToDisk(file);
            // Save metadata in DB
            Project project = userService.saveProject(studentId, title, description, fileName);
            uploadedProjects.add(project);
        }

        return uploadedProjects;
    }

    // =========================
    // GET UPLOADED FILE
    // =========================
    @GetMapping("/files/{filename}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) throws IOException {
        Path path = Paths.get(System.getProperty("user.dir") + "/uploads/" + filename);
        if (!Files.exists(path)) {
            throw new RuntimeException("File not found: " + filename);
        }

        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }

    // =========================
    // GET PROJECTS BY STUDENT
    // =========================
    @GetMapping("/projects/{studentId}")
    public List<Project> getProjects(@PathVariable Long studentId) {
        return userService.getProjectsByStudent(studentId);
    }

    // =========================
    // UPDATE USER (PUT)
    // =========================
    @PutMapping("/update/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        Optional<User> optionalUser = userRepo.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setName(updatedUser.getName());
            user.setEmail(updatedUser.getEmail());
            user.setPassword(updatedUser.getPassword());
            user.setRole(updatedUser.getRole());
            userRepo.save(user);
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // =========================
    // DELETE USER (DELETE)
    // =========================
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        Optional<User> optionalUser = userRepo.findById(id);
        if (optionalUser.isPresent()) {
            userRepo.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}