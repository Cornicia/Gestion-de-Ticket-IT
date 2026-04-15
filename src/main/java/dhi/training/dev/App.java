package dhi.training.dev;

import dhi.training.dev.ticket.CSVTicketRepository;
import dhi.training.dev.ticket.TicketManager;

import java.nio.file.Path;

public class App {

    private static final String DATA_FILE_PROPERTY = "ticket.data.path";
    private static final String DATA_FILE_ENVIRONMENT = "TICKET_DATA_PATH";
    private static final Path DEFAULT_DATA_FILE = Path.of("data", "tickets.csv");

    public static void main(String[] args) {
        TicketManager manager = new TicketManager(new CSVTicketRepository(resolveDataFile()));
        new TicketConsoleApp(manager).run();
    }

    private static Path resolveDataFile() {
        String configuredPath = System.getProperty(DATA_FILE_PROPERTY);
        if (configuredPath == null || configuredPath.isBlank()) {
            configuredPath = System.getenv(DATA_FILE_ENVIRONMENT);
        }

        if (configuredPath == null || configuredPath.isBlank()) {
            return DEFAULT_DATA_FILE;
        }

        return Path.of(configuredPath.trim());
    }
}
