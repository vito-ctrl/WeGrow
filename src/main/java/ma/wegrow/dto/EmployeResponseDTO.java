package ma.wegrow.dto;

import ma.wegrow.entity.Contrat;

/**
 * DTO de réponse pour les données d'un employé.
 */
public class EmployeResponseDTO {

    private Long id;
    private String matricule;
    private String nom;
    private String prenom;
    private String poste;
    private Contrat contrat;

    public EmployeResponseDTO() {
    }

    public EmployeResponseDTO(Long id, String matricule, String nom, String prenom, String poste, Contrat contrat) {
        this.id = id;
        this.matricule = matricule;
        this.nom = nom;
        this.prenom = prenom;
        this.poste = poste;
        this.contrat = contrat;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMatricule() {
        return matricule;
    }

    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getPoste() {
        return poste;
    }

    public void setPoste(String poste) {
        this.poste = poste;
    }

    public Contrat getContrat() {
        return contrat;
    }

    public void setContrat(Contrat contrat) {
        this.contrat = contrat;
    }
}
