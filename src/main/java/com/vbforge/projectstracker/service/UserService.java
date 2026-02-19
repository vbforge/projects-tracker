package com.vbforge.projectstracker.service;

import com.vbforge.projectstracker.dto.RegisterDTO;
import com.vbforge.projectstracker.entity.User;

import java.util.Optional;

public interface UserService {

    User register(RegisterDTO dto);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}




























































