package com.portfolio.repo;

import com.portfolio.entity.Project;
import com.portfolio.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProjectRepo extends JpaRepository<Project, Long> {
    List<Project> findByStudent(User student);
}