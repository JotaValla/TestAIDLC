package com.jotavalla.testworkflow.catalogo.dominio;

public class LibroNoEncontradoException extends RuntimeException {

    public LibroNoEncontradoException(Long id) {
        super("No existe un libro con id " + id);
    }

}
