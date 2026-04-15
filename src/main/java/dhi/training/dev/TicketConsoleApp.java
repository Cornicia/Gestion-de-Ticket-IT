package dhi.training.dev;

import dhi.training.dev.ticket.Priority;
import dhi.training.dev.ticket.Ticket;
import dhi.training.dev.ticket.TicketManager;
import dhi.training.dev.ticket.TicketStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class TicketConsoleApp {

    private static final DateTimeFormatter INPUT_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String SEPARATOR = "--------------------------------------------------";

    private final TicketManager manager;
    private final BufferedReader reader;

    public TicketConsoleApp(TicketManager manager) {
        this.manager = manager;
        this.reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
    }

    public void run() {
        boolean running = true;

        while (running) {
            printMenu();
            String rawChoice = readLine("Choix : ");
            if (rawChoice == null) {
                System.out.println("Au revoir.");
                return;
            }

            String choice = rawChoice.trim();

            try {
                switch (choice) {
                    case "1" -> createTicket();
                    case "2" -> findTicketById();
                    case "3" -> findTicketsByTitle();
                    case "4" -> listAllTickets();
                    case "5" -> listTicketsByPriority();
                    case "6" -> listTicketsByStatus();
                    case "7" -> assignTicket();
                    case "8" -> markTicketPending();
                    case "9" -> resolveTicket();
                    case "10" -> closeTicket();
                    case "11" -> deleteTicket();
                    case "0" -> {
                        System.out.println("Au revoir.");
                        running = false;
                    }
                    default -> System.out.println("Choix invalide.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Erreur : " + e.getMessage());
            }

            if (running) {
                System.out.println();
            }
        }
    }

    private void printMenu() {
        System.out.println("=== Gestion des tickets IT ===");
        System.out.println("1. Creer un ticket");
        System.out.println("2. Rechercher un ticket par ID");
        System.out.println("3. Rechercher un ticket par titre");
        System.out.println("4. Afficher tous les tickets");
        System.out.println("5. Afficher les tickets par priorite");
        System.out.println("6. Afficher les tickets par statut");
        System.out.println("7. Assigner un ticket");
        System.out.println("8. Mettre un ticket en attente");
        System.out.println("9. Resoudre un ticket");
        System.out.println("10. Cloturer un ticket");
        System.out.println("11. Supprimer un ticket");
        System.out.println("0. Quitter");
    }

    private void createTicket() {
        System.out.println(SEPARATOR);
        System.out.println("Creation d'un ticket");

        String title = readRequired("Titre : ");
        String description = readRequired("Description : ");
        Priority priority = readPriority("Priorite [LOW/MEDIUM/HIGH/CRITICAL] : ");
        String requester = readRequired("Nom du demandeur : ");
        String service = readRequired("Service : ");
        LocalDateTime occurredAt = readDateTime("Date de l'incident [yyyy-MM-dd HH:mm] : ");

        Ticket ticket = manager.openTicket(title, description, priority, requester, service, occurredAt);
        System.out.println("Ticket cree avec succes.");
        printTicketDetails(ticket);
    }

    private void findTicketById() {
        String ticketId = readRequired("ID du ticket : ");
        Optional<Ticket> ticket = manager.findTicketById(ticketId);

        if (ticket.isPresent()) {
            printTicketDetails(ticket.get());
        } else {
            System.out.println("Aucun ticket ne correspond a cet ID.");
        }
    }

    private void findTicketsByTitle() {
        String query = readRequired("Titre ou partie du titre : ");
        Set<Ticket> tickets = manager.findAllByTitle(query);
        printTicketSummaries(tickets);
    }

    private void listAllTickets() {
        printTicketSummaries(manager.findAllTickets());
    }

    private void listTicketsByPriority() {
        Priority priority = readPriority("Priorite [LOW/MEDIUM/HIGH/CRITICAL] : ");
        printTicketSummaries(manager.findAllByPriority(priority));
    }

    private void listTicketsByStatus() {
        TicketStatus status = readStatus("Statut [OPEN/PENDING/RESOLVED/CLOSED] : ");
        printTicketSummaries(manager.findAllByStatus(status));
    }

    private void assignTicket() {
        String ticketId = readRequired("ID du ticket : ");
        String technician = readRequired("Nom du technicien : ");
        Ticket ticket = manager.assignTicket(ticketId, technician);
        System.out.println("Ticket assigne.");
        printTicketDetails(ticket);
    }

    private void markTicketPending() {
        String ticketId = readRequired("ID du ticket : ");
        Ticket ticket = manager.markTicketPending(ticketId);
        System.out.println("Ticket mis en attente.");
        printTicketDetails(ticket);
    }

    private void resolveTicket() {
        String ticketId = readRequired("ID du ticket : ");
        Ticket ticket = manager.resolveTicket(ticketId);
        System.out.println("Ticket resolu.");
        printTicketDetails(ticket);
    }

    private void closeTicket() {
        String ticketId = readRequired("ID du ticket : ");
        Ticket ticket = manager.closeTicket(ticketId);
        System.out.println("Ticket cloture.");
        printTicketDetails(ticket);
    }

    private void deleteTicket() {
        String ticketId = readRequired("ID du ticket : ");
        String confirmation = readRequired("Confirmer la suppression [O/N] : ");
        if (!"O".equalsIgnoreCase(confirmation)) {
            System.out.println("Suppression annulee.");
            return;
        }

        manager.deleteTicket(ticketId);
        System.out.println("Ticket supprime.");
    }

    private String readRequired(String prompt) {
        while (true) {
            String rawValue = readLine(prompt);
            if (rawValue == null) {
                throw new IllegalStateException("Console input closed");
            }

            String value = rawValue.trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("Cette valeur est obligatoire.");
        }
    }

    private Priority readPriority(String prompt) {
        while (true) {
            String value = readRequired(prompt);
            try {
                return Priority.valueOf(value.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Priorite invalide. Valeurs autorisees : LOW, MEDIUM, HIGH, CRITICAL.");
            }
        }
    }

    private TicketStatus readStatus(String prompt) {
        while (true) {
            String value = readRequired(prompt);
            try {
                return TicketStatus.valueOf(value.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Statut invalide. Valeurs autorisees : OPEN, PENDING, RESOLVED, CLOSED.");
            }
        }
    }

    private LocalDateTime readDateTime(String prompt) {
        while (true) {
            String value = readRequired(prompt);
            try {
                return LocalDateTime.parse(value, INPUT_DATE_TIME_FORMAT);
            } catch (DateTimeParseException e) {
                System.out.println("Format invalide. Utilisez yyyy-MM-dd HH:mm.");
            }
        }
    }

    private void printTicketSummaries(Collection<Ticket> tickets) {
        if (tickets.isEmpty()) {
            System.out.println("Aucun ticket trouve.");
            return;
        }

        for (Ticket ticket : sortTickets(tickets)) {
            System.out.println(
                    ticket.getId()
                            + " | "
                            + ticket.getStatus()
                            + " | "
                            + ticket.getPriority()
                            + " | "
                            + ticket.getTitle()
            );
        }
    }

    private void printTicketDetails(Ticket ticket) {
        System.out.println(SEPARATOR);
        System.out.println("ID : " + ticket.getId());
        System.out.println("Titre : " + ticket.getTitle());
        System.out.println("Description : " + ticket.getDescription());
        System.out.println("Priorite : " + ticket.getPriority());
        System.out.println("Demandeur : " + ticket.getRequester());
        System.out.println("Service : " + ticket.getService());
        System.out.println("Date de l'incident : " + formatDateTime(ticket.getOccurredAt()));
        System.out.println("Date d'ouverture : " + formatDateTime(ticket.getOpenedAt()));
        System.out.println("Statut : " + ticket.getStatus());
        System.out.println("Assigne a : " + formatNullable(ticket.getAssignedTo()));
        System.out.println("Date d'assignation : " + formatDateTime(ticket.getAssignedAt()));
        System.out.println("Date de resolution : " + formatDateTime(ticket.getResolvedAt()));
        System.out.println("Date de cloture : " + formatDateTime(ticket.getClosedAt()));
        System.out.println("Cree le : " + formatDateTime(ticket.getCreatedAt()));
        System.out.println("Derniere mise a jour : " + formatDateTime(ticket.getUpdatedAt()));
        System.out.println(SEPARATOR);
    }

    private List<Ticket> sortTickets(Collection<Ticket> tickets) {
        List<Ticket> sortedTickets = new ArrayList<>(tickets);
        sortedTickets.sort(
                Comparator.comparing(Ticket::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(Ticket::getOpenedAt, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(Ticket::getId)
        );
        return sortedTickets;
    }

    private String formatDateTime(LocalDateTime value) {
        return value == null ? "-" : value.format(INPUT_DATE_TIME_FORMAT);
    }

    private String formatNullable(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to read console input", e);
        }
    }
}
