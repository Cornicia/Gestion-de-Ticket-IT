package dhi.training.dev.ticket;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Ticket {

    private final String id;
    private final String title;
    private final String description;
    private final Priority priority;
    private final String requester;
    private final String service;
    private final LocalDateTime occurredAt;
    private final LocalDateTime openedAt;

    private TicketStatus status;
    private LocalDateTime closedAt;
    private LocalDateTime resolvedAt;

    private String assignedTo;
    private LocalDateTime assignedAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Ticket(String id,
                  String title,
                  String description,
                  Priority priority,
                  String requester,
                  String service,
                  LocalDateTime occurredAt,
                  TicketStatus status,
                  LocalDateTime openedAt,
                  LocalDateTime closedAt,
                  LocalDateTime resolvedAt,
                  String assignedTo,
                  LocalDateTime assignedAt,
                  LocalDateTime createdAt,
                  LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.requester = requester;
        this.service = service;
        this.occurredAt = occurredAt;
        this.status = status;
        this.openedAt = openedAt;
        this.closedAt = closedAt;
        this.resolvedAt = resolvedAt;
        this.assignedTo = assignedTo;
        this.assignedAt = assignedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Ticket openTicket(String title,
                                    String description,
                                    Priority priority,
                                    String requester,
                                    String service,
                                    LocalDateTime occurredAt) {
        validateRequiredDate(occurredAt, "occurredAt");
        validatePastOrPresentDate(occurredAt, "occurredAt");
        validateRequiredText(title, "title");
        validateRequiredText(description, "description");
        validateRequiredText(requester, "requester");
        validateRequiredText(service, "service");

        LocalDateTime now = LocalDateTime.now();

        return new Ticket(
                UUID.randomUUID().toString(),
                title,
                description,
                priority == null ? Priority.LOW : priority,
                requester,
                service,
                occurredAt,
                TicketStatus.OPEN,
                now,
                null,
                null,
                null,
                null,
                now,
                now
        );
    }

    public void assignTo(String technician) {
        ensureNotClosed();
        validateRequiredText(technician, "technician");

        assignedTo = technician.trim();
        assignedAt = LocalDateTime.now();
        touch();
    }

    public void markPending() {
        ensureNotClosed();
        ensureNotResolved();

        status = TicketStatus.PENDING;
        touch();
    }

    public void resolve() {
        ensureNotClosed();
        ensureNotResolved();

        status = TicketStatus.RESOLVED;
        resolvedAt = LocalDateTime.now();
        touch();
    }

    public void close() {
        ensureNotClosed();
        if (status != TicketStatus.RESOLVED) {
            throw new IllegalStateException("A ticket must be resolved before it can be closed");
        }

        status = TicketStatus.CLOSED;
        closedAt = LocalDateTime.now();
        touch();
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Priority getPriority() {
        return priority;
    }

    public String getRequester() {
        return requester;
    }

    public String getService() {
        return service;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public LocalDateTime getOpenedAt() {
        return openedAt;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    private void ensureNotClosed() {
        if (status == TicketStatus.CLOSED) {
            throw new IllegalStateException("The ticket is already closed");
        }
    }

    private void ensureNotResolved() {
        if (status == TicketStatus.RESOLVED) {
            throw new IllegalStateException("The ticket is already resolved");
        }
    }

    private void touch() {
        updatedAt = LocalDateTime.now();
    }

    private static void validateRequiredText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("The given " + fieldName + " is null or empty");
        }
    }

    private static void validateRequiredDate(LocalDateTime value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException("The given " + fieldName + " is null");
        }
    }

    private static void validatePastOrPresentDate(LocalDateTime value, String fieldName) {
        if (value.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("The given " + fieldName + " is after the current date");
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Ticket ticket)) {
            return false;
        }
        return Objects.equals(id, ticket.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Ticket{"
                + "id='" + id + '\''
                + ", title='" + title + '\''
                + ", description='" + description + '\''
                + ", priority=" + priority
                + ", requester='" + requester + '\''
                + ", service='" + service + '\''
                + ", occurredAt=" + occurredAt
                + ", openedAt=" + openedAt
                + ", status=" + status
                + ", closedAt=" + closedAt
                + ", resolvedAt=" + resolvedAt
                + ", assignedTo='" + assignedTo + '\''
                + ", assignedAt=" + assignedAt
                + ", createdAt=" + createdAt
                + ", updatedAt=" + updatedAt
                + '}';
    }
}
