package com.alexr.ecommerce.controller;


import com.alexr.ecommerce.dto.AuthRequestDTO;
import com.alexr.ecommerce.dto.AuthResponseDTO;
import com.alexr.ecommerce.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private UsuarioService usuarioService;


    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> registrar(@Valid @RequestBody AuthRequestDTO authRequestDTO){
        AuthResponseDTO authResponseDTO = usuarioService.registrar(authRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(authResponseDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO authRequestDTO){
        AuthResponseDTO authResponseDTO = usuarioService.login(authRequestDTO);
        return ResponseEntity.ok(authResponseDTO);
    }


}
