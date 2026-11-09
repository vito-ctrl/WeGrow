package ma.wegrow.exception;

/**
 * Exception levée lorsqu'on tente de supprimer un département qui contient des employés (HTTP 409).
 */
public class DepartmentNotEmptyException extends RuntimeException {

    public DepartmentNotEmptyException(String message) {
        super(message);
    }
}
