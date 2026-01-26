package com.alexr.ecommerce.service;

import com.alexr.ecommerce.dto.AuthRequestDTO;
import com.alexr.ecommerce.dto.AuthResponseDTO;
import com.alexr.ecommerce.exception.PasswordInvalidaException;
import com.alexr.ecommerce.exception.UsuarioNoRegistradoException;
import com.alexr.ecommerce.exception.UsuarioYaExisteException;
import com.alexr.ecommerce.model.Rol;
import com.alexr.ecommerce.model.Usuario;
import com.alexr.ecommerce.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private UsuarioRepository repository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponseDTO registrar(AuthRequestDTO authRequestDTO){

        if (repository.existsByNombre(authRequestDTO.getNombre())){
            throw new UsuarioYaExisteException("Este usuario ya esta registrado");
        }

        String password = passwordEncoder.encode(authRequestDTO.getPassword());
        Usuario usuario = new Usuario(authRequestDTO.getNombre(), password, Rol.ROLE_USUARIO);
        repository.save(usuario);

        return new AuthResponseDTO(jwtService.generateToken(usuario));

    }

    public AuthResponseDTO login(AuthRequestDTO authRequestDTO){
        Usuario usuario = repository.findByNombre(authRequestDTO.getNombre())
                .orElseThrow(() -> new UsuarioNoRegistradoException("Nombre o contraseña incorrecto"));



        if (passwordEncoder.matches(authRequestDTO.getPassword(), usuario.getPassword())){
            return new AuthResponseDTO(jwtService.generateToken(usuario));

        } else throw new PasswordInvalidaException("Nombre o contraseña incorrecto");

    }
}
