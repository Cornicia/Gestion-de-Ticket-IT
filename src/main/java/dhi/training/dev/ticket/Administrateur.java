package dhi.training.dev.ticket;

import java.util.List;
import java.util.Objects;

/**
 * Role qui gere la file de priorite et valide l'assignation des tickets non critiques.
 */
public class Administrateur {

    private final String name;
    private final TicketManager manager;

    public Administrateur(String name, TicketManager manager) {
        this.name = requireText(name, "name");
        this.manager = Objects.requireNonNull(manager, "manager");
    }

    public String getName() {
        return name;
    }

    public List<Ticket> getTicketsOrderedByPriority() {
        return manager.findAllOrderedByPriority();
    }

    public Ticket validateTicketAssignment(String ticketId) {
        return manager.validateTicketAssignment(ticketId, name);
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("The given " + fieldName + " is null or empty");
        }

        return value.trim();
    }
}
