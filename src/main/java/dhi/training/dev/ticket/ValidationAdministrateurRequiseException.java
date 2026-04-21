package dhi.training.dev.ticket;

/**
 * Signale qu'un ticket non critique doit etre valide par un administrateur avant son assignation.
 */
public class ValidationAdministrateurRequiseException extends IllegalStateException {

    public ValidationAdministrateurRequiseException(String message) {
        super(message);
    }
}
