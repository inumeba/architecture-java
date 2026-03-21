package exception.metier;

/**
 * EXCEPTION CHECKED — Entité non trouvée
 *
 * Lancée quand on cherche une ressource qui n'existe pas.
 * C'est une exception CHECKED car l'appelant PEUT réagir :
 * → Afficher "produit non trouvé"
 * → Créer la ressource manquante
 * → Rediriger vers une autre page
 *
 * BONNE PRATIQUE : créer des exceptions spécifiques plutôt que
 * d'utiliser Exception ou RuntimeException partout.
 * Cela permet de :
 * 1. Catcher de façon ciblée (catch spécifique avant catch général)
 * 2. Donner un nom parlant à l'erreur (auto-documentation)
 * 3. Ajouter des données contextuelles (id, type de ressource)
 */
public class RessourceNonTrouveeException extends ApplicationException {

    private final String typeRessource;
    private final Object identifiant;

    public RessourceNonTrouveeException(String typeRessource, Object identifiant) {
        super(
                "RESSOURCE_NON_TROUVEE",
                "%s avec l'identifiant '%s' n'existe pas".formatted(typeRessource, identifiant)
        );
        this.typeRessource = typeRessource;
        this.identifiant = identifiant;
    }

    public String getTypeRessource() {
        return typeRessource;
    }

    public Object getIdentifiant() {
        return identifiant;
    }
}
