
package com.portfolio.service;
import org.springframework.stereotype.Service;
import com.portfolio.repo.UserRepo;
import com.portfolio.entity.User;
import java.util.*;
@Service
public class UserService{
private final UserRepo repo;
public UserService(UserRepo r){this.repo=r;}
public User register(User u){return repo.save(u);}
public Optional<User> login(String email,String pass){
Optional<User> u=repo.findByEmail(email);
if(u.isPresent() && u.get().getPassword().equals(pass)) return u;
return Optional.empty();
}
}
