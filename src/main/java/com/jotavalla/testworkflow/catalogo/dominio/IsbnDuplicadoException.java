package com.jotavalla.testworkflow.catalogo.dominio;

public class IsbnDuplicadoException extends RuntimeException {

    public IsbnDuplicadoException(String isbn) {
        super("Ya existe un libro con ISBN " + isbn);
    }

    public IsbnDuplicadoException(String isbn, Throwable causa) {
        super("Ya existe un libro con ISBN " + isbn, causa);
    }

}
