import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;
import java.io.*;
import java.time.*;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

public class CalendarSystemTest {
    private CalendarSystem calendar;

    @BeforeEach
    void setup() {
        calendar = new CalendarSystem();
        calendar.addEvent(new CalendarEvent("Test", "Desc", "Here",
                LocalDateTime.of(2025, 7, 4, 10, 0),
                LocalDateTime.of(2025, 7, 4, 12, 0)));
    }

    //Value FALSE unless you are debugging the files
    boolean debugKeepFiles = true; //for clean() which deletes the files. switch to false when you are done.

    @AfterEach
    void cleanup() {
        if (!debugKeepFiles) {
            File[] files = new File("test-output").listFiles();
            if (files != null) for (File f : files) f.delete();
            new File("test-output").delete();
        }
    }



    private void ensureTestDir() {
        File dir = new File("test-output");
        if (!dir.exists()) dir.mkdirs();
    }
    //====================EXPORT TEST========================
    @Test void exportToCsv_createsValidFile() {
        ensureTestDir();
        File out = new File("test-output/test.csv");
        calendar.exportToCsv(out.getPath());
        assertTrue(out.exists());
        assertTrue(out.length() > 0);
    }

    @Test void exportToJson_createsValidFile() {
        ensureTestDir();
        File out = new File("test-output/test.json");
        calendar.exportToJson(out.getPath());
        assertTrue(out.exists());
        assertTrue(out.length() > 0);
    }

    @Test void exportToHtml_createsValidFile() {
        ensureTestDir();
        File out = new File("test-output/test.html");
        calendar.exportToHtml(out.getPath());
        assertTrue(out.exists());
        assertTrue(out.getPath().contains(".html"));
    }

    @Test void exportToIcs_createsValidFile() {
        ensureTestDir();
        File out = new File("test-output/test.ics");
        calendar.exportEventsToIcs(out.getPath());
        assertTrue(out.exists());
        assertTrue(out.getName().endsWith(".ics"));
    }
    //=====================IMPORT TEST========================
    @Test
    public void importFromCsv_reloadsSameEvent() {
        ensureTestDir();
        File f = new File("test-output/event.csv");
        calendar.exportToCsv(f.getPath());

        CalendarSystem imported = reloadFrom(f, "csv");
        assertEquals(1, imported.getAllEvents().size());
    }

    @Test
    public void importFromJson_reloadsSameEvent() {
        ensureTestDir();
        File f = new File("test-output/event.json");
        calendar.exportToJson(f.getPath());

        CalendarSystem imported = reloadFrom(f, "json");
        assertEquals("Test", imported.getAllEvents().get(0).getTitle());
    }

    @Test
    public void importFromHtml_reloadsSameEvent() {
        ensureTestDir();
        File f = new File("test-output/event.html");
        calendar.exportToHtml(f.getPath());

        CalendarSystem imported = reloadFrom(f, "html");

        System.out.println("File exists? " + f.exists());
        System.out.println("File size: " + f.length());
        System.out.println("Imported events: " + imported.getAllEvents().size());

        List<CalendarEvent> events = imported.getAllEvents();
        assertFalse(events.isEmpty(), "No events were imported from HTML");

        assertEquals("Here", events.get(0).getLocation());
    }

    @Test
    public void importFromIcs_reloadsSameEvent() {
        ensureTestDir();
        File f = new File("test-output/event.ics");
        calendar.exportEventsToIcs(f.getPath());

        CalendarSystem imported = reloadFrom(f, "ics");
        assertEquals("Test", imported.getAllEvents().get(0).getTitle());
    }

    private CalendarSystem reloadFrom(File f, String type) {
        CalendarSystem c = new CalendarSystem();
        switch (type) {
            case "csv" -> c.importFromCsv(f.getPath());
            case "json" -> c.importFromJson(f.getPath());
            case "ics"  -> c.importFromIcs(f.getPath());
            case "html" -> c.importFromHtml(f.getPath());
        }
        return c;
    }//End Reload
}