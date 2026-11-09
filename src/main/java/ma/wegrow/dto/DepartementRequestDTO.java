package ma.wegrow.dto;

/**
 * DTO pour la création d'un département.
 */
public class DepartementRequestDTO {

    private String nom;
    private String description;

    public DepartementRequestDTO() {
    }

    public DepartementRequestDTO(String nom, String description) {
        this.nom = nom;
        this.description = description;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
