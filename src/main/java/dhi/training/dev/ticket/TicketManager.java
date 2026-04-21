package dhi.training.dev.ticket;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class TicketManager {

    private final TicketRepository repository;

    public TicketManager(TicketRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Ticket openTicket(String title,
                             String description,
                             Priority priority,
                             String requester,
                             String service,
                             LocalDateTime occurredAt) {

        Ticket newTicket = Ticket.openTicket(title, description, priority, requester, service, occurredAt);
        repository.saveTicket(newTicket);
        return newTicket;
    }

    public Set<Ticket> findAllTickets() {
        return new LinkedHashSet<>(repository.getTickets());
    }

    public List<Ticket> findAllOrderedByPriority() {
        return repository.getTickets()
                .stream()
                .sorted(
                        Comparator.comparingInt((Ticket ticket) -> ticket.getPriority().ordinal()).reversed()
                                .thenComparing(Ticket::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()))
                                .thenComparing(Ticket::getOpenedAt, Comparator.nullsLast(Comparator.naturalOrder()))
                                .thenComparing(Ticket::getId)
                )
                .toList();
    }

    public Optional<Ticket> findTicketById(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }

        for (Ticket ticket : repository.getTickets()) {
            if (ticket.getId().equalsIgnoreCase(id.trim())) {
                return Optional.of(ticket);
            }
        }

        return Optional.empty();
    }

    public Optional<Ticket> findTicketByTitle(String query) {
        return findAllByTitle(query).stream().findFirst();
    }

    public Set<Ticket> findAllByTitle(String query) {
        if (query == null || query.isBlank()) {
            return Set.of();
        }

        Set<Ticket> result = new LinkedHashSet<>();
        String normalizedQuery = query.trim().toLowerCase();

        for (Ticket ticket : repository.getTickets()) {
            if (ticket.getTitle().toLowerCase().contains(normalizedQuery)) {
                result.add(ticket);
            }
        }

        return result;
    }

    public Set<Ticket> findAllByPriority(Priority priority) {
        Set<Ticket> result = new LinkedHashSet<>();
        for (Ticket ticket : repository.getTickets()) {
            if (ticket.getPriority() == priority) {
                result.add(ticket);
            }
        }

        return result;
    }

    public Set<Ticket> findAllByStatus(TicketStatus status) {
        Set<Ticket> result = new LinkedHashSet<>();
        for (Ticket ticket : repository.getTickets()) {
            if (ticket.getStatus() == status) {
                result.add(ticket);
            }
        }

        return result;
    }

    public Ticket assignTicket(String ticketId, String technician) {
        Set<Ticket> tickets = new LinkedHashSet<>(repository.getTickets());
        Ticket ticket = findTicketByIdOrThrow(tickets, ticketId);
        ticket.assignTo(technician);
        repository.saveTickets(tickets);
        return ticket;
    }

    public Ticket validateTicketAssignment(String ticketId, String administrator) {
        Set<Ticket> tickets = new LinkedHashSet<>(repository.getTickets());
        Ticket ticket = findTicketByIdOrThrow(tickets, ticketId);
        ticket.validateAssignmentByAdmin(administrator);
        repository.saveTickets(tickets);
        return ticket;
    }

    public Ticket markTicketPending(String ticketId) {
        Set<Ticket> tickets = new LinkedHashSet<>(repository.getTickets());
        Ticket ticket = findTicketByIdOrThrow(tickets, ticketId);
        ticket.markPending();
        repository.saveTickets(tickets);
        return ticket;
    }

    public Ticket resolveTicket(String ticketId) {
        Set<Ticket> tickets = new LinkedHashSet<>(repository.getTickets());
        Ticket ticket = findTicketByIdOrThrow(tickets, ticketId);
        ticket.resolve();
        repository.saveTickets(tickets);
        return ticket;
    }

    public Ticket closeTicket(String ticketId) {
        Set<Ticket> tickets = new LinkedHashSet<>(repository.getTickets());
        Ticket ticket = findTicketByIdOrThrow(tickets, ticketId);
        ticket.close();
        repository.saveTickets(tickets);
        return ticket;
    }

    public void deleteTicket(String ticketId) {
        Set<Ticket> tickets = new LinkedHashSet<>(repository.getTickets());
        Ticket ticket = findTicketByIdOrThrow(tickets, ticketId);
        tickets.remove(ticket);
        repository.saveTickets(tickets);
    }

    public void presentAllTickets() {
        for (Ticket ticket : repository.getTickets()) {
            System.out.println(ticket);
        }
    }

    private Ticket findTicketByIdOrThrow(Set<Ticket> tickets, String ticketId) {
        if (ticketId == null || ticketId.isBlank()) {
            throw new IllegalArgumentException("The given ticketId is null or empty");
        }

        for (Ticket ticket : tickets) {
            if (ticket.getId().equalsIgnoreCase(ticketId.trim())) {
                return ticket;
            }
        }

        throw new IllegalArgumentException("No ticket found for id " + ticketId);
    }
}
