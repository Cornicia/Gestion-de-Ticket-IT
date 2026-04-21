package dhi.training.dev.ticket;

import java.util.Objects;

/**
 * Role qui prend en charge un ticket et fait avancer son traitement.
 */
public class Technicien {

    private final String name;
    private final TicketManager manager;

    public Technicien(String name, TicketManager manager) {
        this.name = requireText(name, "name");
        this.manager = Objects.requireNonNull(manager, "manager");
    }

    public String getName() {
        return name;
    }

    public Ticket assignTicket(String ticketId) {
        return manager.assignTicket(ticketId, name);
    }

    public Ticket markTicketPending(String ticketId) {
        return manager.markTicketPending(ticketId);
    }

    public Ticket resolveTicket(String ticketId) {
        return manager.resolveTicket(ticketId);
    }

    public Ticket closeTicket(String ticketId) {
        return manager.closeTicket(ticketId);
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("The given " + fieldName + " is null or empty");
        }

        return value.trim();
    }
}
