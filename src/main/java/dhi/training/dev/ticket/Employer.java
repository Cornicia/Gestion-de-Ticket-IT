package dhi.training.dev.ticket;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Role qui soumet une alerte au systeme de tickets.
 */
public class Employer {

    private final String name;
    private final String service;
    private final TicketManager manager;

    public Employer(String name, String service, TicketManager manager) {
        this.name = requireText(name, "name");
        this.service = requireText(service, "service");
        this.manager = Objects.requireNonNull(manager, "manager");
    }

    public String getName() {
        return name;
    }

    public String getService() {
        return service;
    }

    public Ticket submitAlert(String title,
                              String description,
                              Priority priority,
                              LocalDateTime occurredAt) {
        return manager.openTicket(title, description, priority, name, service, occurredAt);
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("The given " + fieldName + " is null or empty");
        }

        return value.trim();
    }
}
