package com.jotavalla.testworkflow.catalogo.api;

import com.jotavalla.testworkflow.catalogo.dominio.IsbnDuplicadoException;
import com.jotavalla.testworkflow.catalogo.dominio.LibroNoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Traduce los errores de negocio de Catálogo a ProblemDetail (RFC 7807).
 * Los 400 de validación los produce Spring MVC con {@code spring.mvc.problemdetails.enabled=true}.
 */
@RestControllerAdvice(basePackageClasses = CatalogoExceptionHandler.class)
public class CatalogoExceptionHandler {

    @ExceptionHandler(IsbnDuplicadoException.class)
    ProblemDetail isbnDuplicado(IsbnDuplicadoException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(LibroNoEncontradoException.class)
    ProblemDetail libroNoEncontrado(LibroNoEncontradoException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

}
