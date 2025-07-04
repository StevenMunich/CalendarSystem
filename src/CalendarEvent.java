import java.time.LocalDateTime;
import java.util.UUID;

public class CalendarEvent {
    private final String id = UUID.randomUUID().toString();
    private final String title;
    private final String description;
    private final String location;
    private final LocalDateTime start;
    private final LocalDateTime end;

    public CalendarEvent(String title, String description, String location,
                         LocalDateTime start, LocalDateTime end) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.start = start;
        this.end = end;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public LocalDateTime getStart() { return start; }
    public LocalDateTime getEnd() { return end; }
}