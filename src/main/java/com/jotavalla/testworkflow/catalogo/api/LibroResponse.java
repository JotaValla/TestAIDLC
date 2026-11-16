package com.jotavalla.testworkflow.catalogo.api;

import com.jotavalla.testworkflow.catalogo.dominio.Libro;

public record LibroResponse(Long id, String isbn, String titulo, String autor) {

    static LibroResponse desde(Libro libro) {
        return new LibroResponse(libro.getId(), libro.getIsbn(), libro.getTitulo(), libro.getAutor());
    }

}
