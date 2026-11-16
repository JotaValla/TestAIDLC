package com.jotavalla.testworkflow.catalogo.api;

import com.jotavalla.testworkflow.TestcontainersConfiguration;
import com.jotavalla.testworkflow.catalogo.infraestructura.LibroRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.matchesPattern;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class LibroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoSpyBean
    private LibroRepository libroRepository;

    /** ISBN único por prueba, para no depender del orden ni de datos de otras pruebas. */
    private static String isbnUnico() {
        return "979" + String.format("%010d", ThreadLocalRandom.current().nextLong(10_000_000_000L));
    }

    private String registrar(String isbn, String titulo, String autor) throws Exception {
        return mockMvc.perform(post("/libros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(isbn, titulo, autor)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");
    }

    private static String json(String isbn, String titulo, String autor) {
        return """
                {"isbn": "%s", "titulo": "%s", "autor": "%s"}
                """.formatted(isbn, titulo, autor);
    }

    @Test
    void debeResponder201ConUbicacionCuandoLosDatosSonValidos() throws Exception {
        String isbn = isbnUnico();

        mockMvc.perform(post("/libros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(isbn, "Rayuela", "Julio Cortázar")))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", matchesPattern(".*/libros/\\d+$")))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.isbn").value(isbn))
                .andExpect(jsonPath("$.titulo").value("Rayuela"))
                .andExpect(jsonPath("$.autor").value("Julio Cortázar"));
    }

    @Test
    void debeResponder409CuandoElIsbnYaExiste() throws Exception {
        String isbn = isbnUnico();
        registrar(isbn, "Ficciones", "Jorge Luis Borges");

        mockMvc.perform(post("/libros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(isbn, "Otro título", "Otro autor")))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.detail").value(containsString(isbn)));
    }

    @Test
    void debeResponder409CuandoLaBaseRechazaUnIsbnRepetidoEnUnaCarrera() throws Exception {
        String isbn = isbnUnico();
        registrar(isbn, "Ficciones", "Jorge Luis Borges");
        // Simula la carrera: la verificación previa no ve el libro y decide la restricción única de Postgres.
        doReturn(false).when(libroRepository).existsByIsbn(isbn);

        mockMvc.perform(post("/libros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(isbn, "Otro título", "Otro autor")))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value(containsString(isbn)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"978100000000", "97810000000031", "978-100000003", "978100000000X", ""})
    void debeResponder400CuandoElIsbnNoTiene13Digitos(String isbnInvalido) throws Exception {
        mockMvc.perform(post("/libros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(isbnInvalido, "Título", "Autor")))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void debeResponder400CuandoElTituloEstaVacio() throws Exception {
        mockMvc.perform(post("/libros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(isbnUnico(), " ", "Autor")))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
    }

    @Test
    void debeResponder400CuandoElAutorEstaVacio() throws Exception {
        mockMvc.perform(post("/libros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(isbnUnico(), "Título", "")))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
    }

    @Test
    void debeResponder400CuandoElTituloSuperaLos255Caracteres() throws Exception {
        mockMvc.perform(post("/libros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(isbnUnico(), "a".repeat(256), "Autor")))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void debeDevolverLibroCuandoExiste() throws Exception {
        String isbn = isbnUnico();
        String ubicacion = registrar(isbn, "El Aleph", "Jorge Luis Borges");
        assertThat(ubicacion).isNotNull();

        mockMvc.perform(get(ubicacion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn").value(isbn))
                .andExpect(jsonPath("$.titulo").value("El Aleph"))
                .andExpect(jsonPath("$.autor").value("Jorge Luis Borges"));
    }

    @Test
    void debeResponder404CuandoElLibroNoExiste() throws Exception {
        mockMvc.perform(get("/libros/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value(containsString(String.valueOf(Long.MAX_VALUE))));
    }

}
