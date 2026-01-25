package com.alexr.ecommerce.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


@Entity
@Table(name = "usuario")
public class Usuario {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 20, message = "El nombre debe tener entre 3 y 20 caracteres")
    private String nombre;

    @Column(nullable = false)
    @NotBlank(message = "la contraseña es obligatoria")
    @Size(min = 5, message = "La contraseña debe tener minimio 5")
    private String password;

    @Column(nullable = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    private Rol rol;

    public Usuario(){}

    public Usuario(String nombre, String password, Rol rol) {
        this.nombre = nombre;
        this.password = password;
        this.rol = rol;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
}
