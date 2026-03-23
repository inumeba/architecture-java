package com.monentreprise.monapp;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String direBonjour() {
        return "👋 Bonjour ! L'application Spring Boot a été déployée avec succès via notre pipeline CI/CD automatisé !";
    }
}
