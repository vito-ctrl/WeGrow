package ma.wegrow.config;

import ma.wegrow.entity.Contrat;
import ma.wegrow.entity.Departement;
import ma.wegrow.entity.Employe;
import ma.wegrow.repository.DepartementRepository;
import ma.wegrow.repository.EmployeRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Bean d'initialisation des données au démarrage de l'application (phase init du cycle de vie du bean).
 */
public class DataInitializer {

    private final DepartementRepository departementRepository;
    private final EmployeRepository employeRepository;

    /**
     * Injection par constructeur.
     */
    public DataInitializer(DepartementRepository departementRepository, EmployeRepository employeRepository) {
        this.departementRepository = departementRepository;
        this.employeRepository = employeRepository;
    }

    /**
     * Méthode appelée lors de la phase d'initialisation (init-method).
     */
    @Transactional
    public void init() {
        // Initialiser uniquement si la base de données est vide
        if (departementRepository.count() == 0) {
            // Création des départements de test
            Departement rh = new Departement("Ressources humaines", "Gestion du personnel");
            Departement it = new Departement("Informatique", "Développement");

            rh = departementRepository.save(rh);
            it = departementRepository.save(it);

            // Création des employés de test conformément au brief
            Employe emp1 = new Employe(
                    "EMP001",
                    "Alami",
                    "Sara",
                    "Responsable RH",
                    Contrat.CDI,
                    rh
            );

            Employe emp2 = new Employe(
                    "EMP002",
                    "Bennani",
                    "Youssef",
                    "Assistant RH",
                    Contrat.CDD,
                    rh
            );

            Employe emp3 = new Employe(
                    "EMP003",
                    "Chraibi",
                    "Amine",
                    "Développeur Fullstack",
                    Contrat.CDI,
                    it
            );

            employeRepository.save(emp1);
            employeRepository.save(emp2);
            employeRepository.save(emp3);
        }
    }
}
