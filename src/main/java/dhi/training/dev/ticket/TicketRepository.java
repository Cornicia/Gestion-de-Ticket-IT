package dhi.training.dev.ticket;

import java.util.Collection;
import java.util.Set;

public interface TicketRepository {

    Set<Ticket> getTickets();

    void saveTicket(Ticket ticket);

    void deleteTicket(Ticket ticket);

    void saveTickets(Collection<Ticket> tickets);
}
