
package com.portfolio.repo;
import org.springframework.data.jpa.repository.JpaRepository;
import com.portfolio.entity.User;
import java.util.Optional;
public interface UserRepo extends JpaRepository<User,Long>{
Optional<User> findByEmail(String email);
}
