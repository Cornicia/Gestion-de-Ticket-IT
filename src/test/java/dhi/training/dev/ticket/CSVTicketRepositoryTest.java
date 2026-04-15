package dhi.training.dev.ticket;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CSVTicketRepositoryTest {

    @TempDir
    Path tempDirectory;

    @Test
    void shouldPersistAndReloadTicketsFromCsvFile() throws Exception {
        Path csvFile = tempDirectory.resolve("tickets.csv");
        CSVTicketRepository repository = new CSVTicketRepository(csvFile);

        Ticket ticket = Ticket.openTicket(
                "Serveur indisponible",
                "Le serveur principal ne repond plus, verification urgente",
                Priority.CRITICAL,
                "Alice",
                "Production",
                LocalDateTime.now().minusHours(2)
        );
        ticket.assignTo("Bob");
        ticket.markPending();

        repository.saveTicket(ticket);

        Set<Ticket> reloadedTickets = repository.getTickets();

        assertEquals(1, reloadedTickets.size());

        Ticket reloadedTicket = reloadedTickets.iterator().next();
        assertEquals(ticket.getId(), reloadedTicket.getId());
        assertEquals(ticket.getTitle(), reloadedTicket.getTitle());
        assertEquals(ticket.getDescription(), reloadedTicket.getDescription());
        assertEquals(ticket.getPriority(), reloadedTicket.getPriority());
        assertEquals(ticket.getStatus(), reloadedTicket.getStatus());
        assertEquals(ticket.getAssignedTo(), reloadedTicket.getAssignedTo());
        assertTrue(Files.exists(csvFile));
    }
}
