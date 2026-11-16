package com.jotavalla.testworkflow;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Minimal REST controller used to verify that the application is up and serving HTTP requests.
 */
@RestController
public class HelloWorldController {

    /**
     * Health-style greeting endpoint.
     *
     * @return a plain-text message confirming the service is running
     */
    @GetMapping("/hola")
    public String hola() {
        return "¡Funciona! 🚀";
    }

}
