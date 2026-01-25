package com.alexr.ecommerce.handler;

import com.alexr.ecommerce.dto.ErrorResponse;
import com.alexr.ecommerce.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductoNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductoNotFound(ProductoNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                404,
                ex.getMessage(),
                "Producto no encontrado"

        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(CategoriaNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlecategoriaNotFound(CategoriaNotFoundException e){
        ErrorResponse error = new ErrorResponse(
          404,
                e.getMessage(),
                "Categoria no encontrada"

        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(CategoriaConProductosException.class)
    public ResponseEntity<ErrorResponse> handleCategoriaConProductos(CategoriaConProductosException e){
        ErrorResponse error = new ErrorResponse(
          400,
                e.getMessage(),
                "Operacion no permitida"
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage())
        );

        String mensaje = errores.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(", "));

        if (errores.size() == 1) {
            mensaje = errores.values().iterator().next();
        }

        ErrorResponse error = new ErrorResponse(400, mensaje, "Error de validación");
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                500,
                "Ocurrió un error inesperado: " + ex.getMessage(),
                "Error interno del servidor"

        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(UsuarioYaExisteException.class)
    public ResponseEntity<ErrorResponse> handleUsuarioYaExisteException(UsuarioYaExisteException e){

        ErrorResponse error = new ErrorResponse(
                409,
                e.getMessage(),
                "Registro invalido"
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(UsuarioNoRegistradoException.class)
    public ResponseEntity<ErrorResponse> handleUsuarioNoRegistradoException(UsuarioNoRegistradoException e){
        ErrorResponse error = new ErrorResponse(
                401,
                e.getMessage(),
                "Login invalido"
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(PasswordInvalidaException.class)
    public ResponseEntity<ErrorResponse> handlePasswordInvalidaException(PasswordInvalidaException e){
        ErrorResponse error = new ErrorResponse(
                401,
                e.getMessage(),
                "Login invalida"
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
}

