package com.jotavalla.testworkflow.catalogo.infraestructura;

import com.jotavalla.testworkflow.catalogo.dominio.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibroRepository extends JpaRepository<Libro, Long> {

    boolean existsByIsbn(String isbn);

}
