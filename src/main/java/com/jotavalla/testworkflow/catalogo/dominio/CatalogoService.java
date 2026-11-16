package com.jotavalla.testworkflow.catalogo.dominio;

import com.jotavalla.testworkflow.catalogo.infraestructura.LibroRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CatalogoService {

    /** Restricción única del ISBN definida en V1__catalogo_libros.sql. */
    static final String RESTRICCION_ISBN_UNICO = "uk_libro_isbn";

    private final LibroRepository libroRepository;

    public CatalogoService(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    @Transactional
    public Libro registrarLibro(String isbn, String titulo, String autor) {
        if (libroRepository.existsByIsbn(isbn)) {
            throw new IsbnDuplicadoException(isbn);
        }
        try {
            return libroRepository.saveAndFlush(new Libro(isbn, titulo, autor));
        } catch (DataIntegrityViolationException ex) {
            // Dos altas simultáneas con el mismo ISBN: la restricción única de la base decide.
            // Cualquier otra violación de integridad no es un duplicado y se propaga tal cual.
            if (violaRestriccion(ex, RESTRICCION_ISBN_UNICO)) {
                throw new IsbnDuplicadoException(isbn, ex);
            }
            throw ex;
        }
    }

    @Transactional(readOnly = true)
    public Libro obtenerLibro(Long id) {
        return libroRepository.findById(id)
                .orElseThrow(() -> new LibroNoEncontradoException(id));
    }

    private static boolean violaRestriccion(Throwable ex, String restriccion) {
        for (Throwable causa = ex; causa != null; causa = causa.getCause()) {
            if (causa instanceof ConstraintViolationException violacion
                    && restriccion.equalsIgnoreCase(violacion.getConstraintName())) {
                return true;
            }
        }
        return false;
    }

}
