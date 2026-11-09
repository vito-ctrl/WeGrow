package ma.wegrow.dto;

/**
 * DTO pour la réponse d'un département avec le nombre d'employés.
 */
public class DepartementResponseDTO {

    private Long id;
    private String nom;
    private String description;
    private Long nombreEmployes;

    public DepartementResponseDTO() {
    }

    public DepartementResponseDTO(Long id, String nom, String description, Long nombreEmployes) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.nombreEmployes = (nombreEmployes != null) ? nombreEmployes : 0L;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getNombreEmployes() {
        return nombreEmployes;
    }

    public void setNombreEmployes(Long nombreEmployes) {
        this.nombreEmployes = nombreEmployes;
    }
}
