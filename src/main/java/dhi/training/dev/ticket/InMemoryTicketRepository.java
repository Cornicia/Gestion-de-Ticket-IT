package dhi.training.dev.ticket;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class InMemoryTicketRepository implements TicketRepository {

    private final Set<Ticket> tickets = new LinkedHashSet<>();

    @Override
    public Set<Ticket> getTickets() {
        return new LinkedHashSet<>(tickets);
    }

    @Override
    public void saveTicket(Ticket ticket) {
        tickets.add(ticket);
    }

    @Override
    public void deleteTicket(Ticket ticket) {
        tickets.remove(ticket);
    }

    @Override
    public void saveTickets(Collection<Ticket> tickets) {
        this.tickets.clear();
        this.tickets.addAll(tickets);
    }
}
