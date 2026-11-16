package com.jotavalla.testworkflow.catalogo.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Datos para registrar un libro. El ISBN son exactamente 13 dígitos, sin dígito de control (S-1).
 */
public record RegistrarLibroRequest(
        @NotBlank @Pattern(regexp = "\\d{13}", message = "debe tener exactamente 13 dígitos") String isbn,
        @NotBlank @Size(max = 255) String titulo,
        @NotBlank @Size(max = 255) String autor) {
}
