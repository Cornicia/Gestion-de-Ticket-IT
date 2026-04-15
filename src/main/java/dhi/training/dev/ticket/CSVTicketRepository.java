package dhi.training.dev.ticket;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class CSVTicketRepository implements TicketRepository {

    private static final String HEADER = String.join(
            ",",
            "id",
            "title",
            "description",
            "priority",
            "requester",
            "service",
            "occurredAt",
            "openedAt",
            "status",
            "closedAt",
            "resolvedAt",
            "assignedTo",
            "assignedAt",
            "createdAt",
            "updatedAt"
    );
    private final Path filePath;

    public CSVTicketRepository() {
        this(Path.of("data", "tickets.csv"));
    }

    public CSVTicketRepository(Path filePath) {
        this.filePath = Objects.requireNonNull(filePath, "filePath");
    }

    @Override
    public Set<Ticket> getTickets() {
        Set<Ticket> tickets = new LinkedHashSet<>();

        if (Files.notExists(filePath)) {
            return tickets;
        }

        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                if (isFirstLine && HEADER.equals(line)) {
                    isFirstLine = false;
                    continue;
                }

                isFirstLine = false;
                tickets.add(parseTicket(line));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read tickets from " + filePath, e);
        }

        return tickets;
    }

    private static Ticket parseTicket(String line) {
        List<String> values = parseCsvLine(line);
        if (values.size() != 15) {
            throw new IllegalArgumentException("Invalid CSV line: " + line);
        }

        String[] strings = values.toArray(new String[0]);

        return new Ticket(
                strings[0],
                strings[1],
                strings[2],
                Priority.valueOf(strings[3]),
                strings[4],
                strings[5],
                parseDateTime(strings, 6),
                TicketStatus.valueOf(strings[8]),
                parseDateTime(strings, 7),
                parseDateTime(strings, 9),
                parseDateTime(strings, 10),
                parseNullableString(strings[11]),
                parseDateTime(strings, 12),
                parseDateTime(strings, 13),
                parseDateTime(strings, 14)
        );
    }

    private static LocalDateTime parseDateTime(String[] strings, int position) {
        return Optional.ofNullable(strings[position])
                .filter(value -> !value.isBlank())
                .map(LocalDateTime::parse)
                .orElse(null);
    }

    private static List<String> parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder currentValue = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char currentChar = line.charAt(i);

            if (currentChar == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    currentValue.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (currentChar == ',' && !inQuotes) {
                values.add(currentValue.toString());
                currentValue.setLength(0);
            } else {
                currentValue.append(currentChar);
            }
        }

        if (inQuotes) {
            throw new IllegalArgumentException("Invalid CSV line: " + line);
        }

        values.add(currentValue.toString());
        return values;
    }

    private static String parseNullableString(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    @Override
    public void saveTicket(Ticket ticket) {
        ensureFileIsInitialized();

        try {
            Files.writeString(
                    filePath,
                    writeTicket(ticket) + System.lineSeparator(),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            throw new IllegalStateException("Unable to save ticket to " + filePath, e);
        }
    }

    private String writeTicket(Ticket ticket) {
        String[] strings = new String[15];

        strings[0] = escapeCsv(ticket.getId());
        strings[1] = escapeCsv(ticket.getTitle());
        strings[2] = escapeCsv(ticket.getDescription());
        strings[3] = escapeCsv(ticket.getPriority().name());
        strings[4] = escapeCsv(ticket.getRequester());
        strings[5] = escapeCsv(ticket.getService());
        strings[6] = escapeCsv(ticket.getOccurredAt().toString());
        strings[7] = escapeCsv(ticket.getOpenedAt().toString());
        strings[8] = escapeCsv(ticket.getStatus().name());
        strings[9] = escapeCsv(ticket.getClosedAt() == null ? "" : ticket.getClosedAt().toString());
        strings[10] = escapeCsv(ticket.getResolvedAt() == null ? "" : ticket.getResolvedAt().toString());
        strings[11] = escapeCsv(ticket.getAssignedTo() == null ? "" : ticket.getAssignedTo());
        strings[12] = escapeCsv(ticket.getAssignedAt() == null ? "" : ticket.getAssignedAt().toString());
        strings[13] = escapeCsv(ticket.getCreatedAt().toString());
        strings[14] = escapeCsv(ticket.getUpdatedAt().toString());

        return String.join(",", strings);
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }

        String escapedValue = value.replace("\"", "\"\"");
        if (escapedValue.contains(",") || escapedValue.contains("\"") || escapedValue.contains("\n") || escapedValue.contains("\r")) {
            return "\"" + escapedValue + "\"";
        }

        return escapedValue;
    }

    @Override
    public void deleteTicket(Ticket ticket) {
        Set<Ticket> tickets = new LinkedHashSet<>(getTickets());
        if (tickets.remove(ticket)) {
            saveTickets(tickets);
        }
    }

    @Override
    public void saveTickets(Collection<Ticket> tickets) {
        try {
            ensureStorageDirectoryExists();

            StringBuilder content = new StringBuilder(HEADER).append(System.lineSeparator());

            for (Ticket ticket : tickets) {
                content.append(writeTicket(ticket)).append(System.lineSeparator());
            }

            Files.writeString(
                    filePath,
                    content.toString(),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE
            );
        } catch (IOException e) {
            throw new IllegalStateException("Unable to save tickets to " + filePath, e);
        }
    }

    private void ensureFileIsInitialized() {
        try {
            ensureStorageDirectoryExists();
            if (Files.notExists(filePath) || Files.size(filePath) == 0L) {
                Files.writeString(
                        filePath,
                        HEADER + System.lineSeparator(),
                        StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING,
                        StandardOpenOption.WRITE
                    );
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to initialize " + filePath, e);
        }
    }

    private void ensureStorageDirectoryExists() throws IOException {
        Path parentDirectory = filePath.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }
    }
}
