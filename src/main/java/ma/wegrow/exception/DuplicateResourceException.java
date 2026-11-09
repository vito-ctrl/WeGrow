package ma.wegrow.exception;

/**
 * Exception levée en cas de violation d'unicité (nom ou matricule déjà existant, HTTP 409).
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
