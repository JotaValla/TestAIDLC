package com.jotavalla.testworkflow.catalogo.api;

import com.jotavalla.testworkflow.catalogo.dominio.CatalogoService;
import com.jotavalla.testworkflow.catalogo.dominio.Libro;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/libros")
public class LibroController {

    private final CatalogoService catalogoService;

    public LibroController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @PostMapping
    public ResponseEntity<LibroResponse> registrar(@Valid @RequestBody RegistrarLibroRequest request) {
        Libro libro = catalogoService.registrarLibro(request.isbn(), request.titulo(), request.autor());
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(libro.getId())
                .toUri();
        return ResponseEntity.created(ubicacion).body(LibroResponse.desde(libro));
    }

    @GetMapping("/{id}")
    public LibroResponse obtener(@PathVariable Long id) {
        return LibroResponse.desde(catalogoService.obtenerLibro(id));
    }

}
