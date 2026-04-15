package dhi.training.dev.ticket;

import java.util.Collection;
import java.util.Set;

public class ExcelTicketRepository implements TicketRepository {

    @Override
    public Set<Ticket> getTickets() {
        throw unsupported();
    }

    @Override
    public void saveTicket(Ticket ticket) {
        throw unsupported();
    }

    @Override
    public void deleteTicket(Ticket ticket) {
        throw unsupported();
    }

    @Override
    public void saveTickets(Collection<Ticket> tickets) {
        throw unsupported();
    }

    private UnsupportedOperationException unsupported() {
        return new UnsupportedOperationException("ExcelTicketRepository is not implemented");
    }
}
