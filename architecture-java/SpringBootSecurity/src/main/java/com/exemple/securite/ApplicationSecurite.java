package com.exemple.securite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  POINT D'ENTRÉE DE L'APPLICATION                           ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
@SpringBootApplication
public class ApplicationSecurite {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationSecurite.class, args);
        
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║  APPLICATION DÉMARRÉE SUR http://localhost:8080          ║");
        System.out.println("║                                                          ║");
        System.out.println("║  Points de test :                                        ║");
        System.out.println("║  1. GET /api/public/bonjour     (Sans mot de passe)      ║");
        System.out.println("║  2. GET /api/prive/profil       (francois ou alice_admin)║");
        System.out.println("║  3. GET /api/admin/statistiques (Uniquement alice_admin) ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
    }
}
