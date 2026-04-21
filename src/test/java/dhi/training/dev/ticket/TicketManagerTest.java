package dhi.training.dev.ticket;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

        Administrateur administrateur = new Administrateur("Claire", manager);
        Technicien technicien = new Technicien("Marc", manager);

        administrateur.validateTicketAssignment(ticket.getId());
        technicien.assignTicket(ticket.getId());
        technicien.markTicketPending(ticket.getId());
        technicien.resolveTicket(ticket.getId());
        Ticket closedTicket = technicien.closeTicket(ticket.getId());

        assertEquals(TicketStatus.CLOSED, closedTicket.getStatus());
        assertEquals("Marc", closedTicket.getAssignedTo());
        assertTrue(closedTicket.isAdminValidated());
        assertEquals("Claire", closedTicket.getValidatedByAdmin());
        assertNotNull(closedTicket.getAssignedAt());
        assertNotNull(closedTicket.getValidatedAt());
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

    @Test
    void shouldRequireAdministratorValidationBeforeAssigningNonCriticalTicket() {
        TicketManager manager = new TicketManager(new InMemoryTicketRepository());
        Ticket ticket = manager.openTicket(
                "Boite mail inaccessible",
                "Connexion impossible depuis Outlook",
                Priority.HIGH,
                "Lea",
                "Marketing",
                LocalDateTime.now().minusMinutes(45)
        );

        Technicien technicien = new Technicien("Olivier", manager);

        assertThrows(ValidationAdministrateurRequiseException.class, () -> technicien.assignTicket(ticket.getId()));
    }

    @Test
    void shouldAssignCriticalTicketWithoutAdministratorValidation() {
        TicketManager manager = new TicketManager(new InMemoryTicketRepository());
        Ticket ticket = manager.openTicket(
                "Serveur ERP indisponible",
                "L'application metier principale est inaccessible",
                Priority.CRITICAL,
                "Amine",
                "Production",
                LocalDateTime.now().minusMinutes(15)
        );

        Technicien technicien = new Technicien("Moussa", manager);
        Ticket assignedTicket = technicien.assignTicket(ticket.getId());

        assertEquals("Moussa", assignedTicket.getAssignedTo());
        assertFalse(assignedTicket.isAdminValidated());
    }

    @Test
    void shouldOrderTicketsByPriorityForAdministrator() {
        TicketManager manager = new TicketManager(new InMemoryTicketRepository());
        Employer employer = new Employer("Sonia", "Finance", manager);

        Ticket lowTicket = employer.submitAlert(
                "Demande de souris",
                "Remplacement d'un accessoire",
                Priority.LOW,
                LocalDateTime.now().minusHours(3)
        );
        Ticket criticalTicket = employer.submitAlert(
                "Cluster hors ligne",
                "Interruption totale du service",
                Priority.CRITICAL,
                LocalDateTime.now().minusHours(1)
        );

        Administrateur administrateur = new Administrateur("Awa", manager);

        assertEquals(
                criticalTicket.getId(),
                administrateur.getTicketsOrderedByPriority().getFirst().getId()
        );
        assertTrue(
                administrateur.getTicketsOrderedByPriority()
                        .stream()
                        .anyMatch(ticket -> lowTicket.getId().equals(ticket.getId()))
        );
    }
}
