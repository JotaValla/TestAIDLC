package com.jotavalla.testworkflow.catalogo.dominio;

import com.jotavalla.testworkflow.catalogo.infraestructura.LibroRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogoServiceTest {

    @Mock
    private LibroRepository libroRepository;

    @InjectMocks
    private CatalogoService catalogoService;

    @Test
    void debeRegistrarLibroCuandoElIsbnNoExiste() {
        when(libroRepository.existsByIsbn("9780000000001")).thenReturn(false);
        when(libroRepository.saveAndFlush(any(Libro.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        Libro libro = catalogoService.registrarLibro("9780000000001", "Rayuela", "Julio Cortázar");

        assertThat(libro.getIsbn()).isEqualTo("9780000000001");
        assertThat(libro.getTitulo()).isEqualTo("Rayuela");
        assertThat(libro.getAutor()).isEqualTo("Julio Cortázar");
        verify(libroRepository).saveAndFlush(any(Libro.class));
    }

    @Test
    void debeLanzarIsbnDuplicadoCuandoElIsbnYaExiste() {
        when(libroRepository.existsByIsbn("9780000000002")).thenReturn(true);

        assertThatThrownBy(() -> catalogoService.registrarLibro("9780000000002", "Ficciones", "Jorge Luis Borges"))
                .isInstanceOf(IsbnDuplicadoException.class)
                .hasMessageContaining("9780000000002");
        verify(libroRepository, never()).saveAndFlush(any(Libro.class));
    }

    @Test
    void debeLanzarIsbnDuplicadoConservandoLaCausaCuandoLaBaseRechazaElIsbnRepetido() {
        DataIntegrityViolationException rechazo = violacionDe("uk_libro_isbn");
        when(libroRepository.existsByIsbn("9780000000004")).thenReturn(false);
        when(libroRepository.saveAndFlush(any(Libro.class))).thenThrow(rechazo);

        assertThatThrownBy(() -> catalogoService.registrarLibro("9780000000004", "Ficciones", "Jorge Luis Borges"))
                .isInstanceOf(IsbnDuplicadoException.class)
                .hasMessageContaining("9780000000004")
                .hasCause(rechazo);
    }

    @Test
    void debePropagarLaViolacionCuandoLaBaseRechazaOtraRestriccion() {
        DataIntegrityViolationException rechazo = violacionDe("libro_titulo_not_null");
        when(libroRepository.existsByIsbn("9780000000005")).thenReturn(false);
        when(libroRepository.saveAndFlush(any(Libro.class))).thenThrow(rechazo);

        assertThatThrownBy(() -> catalogoService.registrarLibro("9780000000005", "Ficciones", "Jorge Luis Borges"))
                .isSameAs(rechazo);
    }

    private static DataIntegrityViolationException violacionDe(String restriccion) {
        return new DataIntegrityViolationException("rechazo de la base",
                new ConstraintViolationException("violación", null, restriccion));
    }

    @Test
    void debeDevolverLibroCuandoElIdExiste() {
        Libro existente = new Libro("9780000000003", "El Aleph", "Jorge Luis Borges");
        when(libroRepository.findById(7L)).thenReturn(Optional.of(existente));

        assertThat(catalogoService.obtenerLibro(7L)).isSameAs(existente);
    }

    @Test
    void debeLanzarLibroNoEncontradoCuandoElIdNoExiste() {
        when(libroRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> catalogoService.obtenerLibro(99L))
                .isInstanceOf(LibroNoEncontradoException.class)
                .hasMessageContaining("99");
    }

}
