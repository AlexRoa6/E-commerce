package com.alexr.ecommerce.controller;


import com.alexr.ecommerce.dto.AuthRequestDTO;
import com.alexr.ecommerce.repository.UsuarioRepository;
import com.alexr.ecommerce.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private UsuarioRepository usuarioRepository;


    public AuthController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }




}
