package dhi.training.dev.ticket;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TicketManagerTest {

    @Test
    void shouldManageTicketLifecycle() {
        TicketManager manager = new TicketManager(new InMemoryTicketRepository());

        Ticket ticket = manager.openTicket(
                "VPN indisponible",
                "Impossible de se connecter au reseau distant",
                Priority.HIGH,
                "Nadia",
                "Support",
                LocalDateTime.now().minusDays(1)
        );

        manager.assignTicket(ticket.getId(), "Marc");
        manager.markTicketPending(ticket.getId());
        manager.resolveTicket(ticket.getId());
        Ticket closedTicket = manager.closeTicket(ticket.getId());

        assertEquals(TicketStatus.CLOSED, closedTicket.getStatus());
        assertEquals("Marc", closedTicket.getAssignedTo());
        assertNotNull(closedTicket.getAssignedAt());
        assertNotNull(closedTicket.getResolvedAt());
        assertNotNull(closedTicket.getClosedAt());
    }

    @Test
    void shouldFindTicketsByTitleIgnoringCase() {
        TicketManager manager = new TicketManager(new InMemoryTicketRepository());

        Ticket ticket = manager.openTicket(
                "Imprimante HP en panne",
                "Aucun document ne sort",
                Priority.MEDIUM,
                "Sonia",
                "RH",
                LocalDateTime.now().minusHours(3)
        );

        Set<Ticket> tickets = manager.findAllByTitle("hp");
        Optional<Ticket> foundTicket = manager.findTicketById(ticket.getId());

        assertEquals(1, tickets.size());
        assertTrue(foundTicket.isPresent());
    }

    @Test
    void shouldRejectClosingTicketBeforeResolution() {
        TicketManager manager = new TicketManager(new InMemoryTicketRepository());

        Ticket ticket = manager.openTicket(
                "Compte bloque",
                "Le compte utilisateur est verrouille",
                Priority.HIGH,
                "Jean",
                "Finance",
                LocalDateTime.now().minusMinutes(30)
        );

        assertThrows(IllegalStateException.class, () -> manager.closeTicket(ticket.getId()));
    }
}
