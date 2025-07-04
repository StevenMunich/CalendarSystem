//import your.package.name.CalendarSystem;
//import PatternMatching.CalendarEvent;

import java.time.LocalDateTime;
@Deprecated
public class CalendarTest {
    public static void main(String[] args) {
        CalendarSystem calendar = new CalendarSystem();

        // Sample events
        calendar.addEvent(new CalendarEvent(
                "🎂 Steven's Birthday Bash",
                "Don't forget the cake and sunscreen!",
                "Beach Pavilion, FL",
                LocalDateTime.of(2025, 7, 5, 18, 0),
                LocalDateTime.of(2025, 7, 5, 22, 0)
        ));

        calendar.addEvent(new CalendarEvent(
                "Phone Meeting Team Sync-Up",
                "Monthly strategy call with project leads",
                "Zoom",
                LocalDateTime.of(2025, 7, 10, 14, 0),
                LocalDateTime.of(2025, 7, 10, 15, 0)
        ));

        calendar.addEvent(new CalendarEvent(
                "Zoom Meeting - Brainstorm Workshop",
                "Creative session on UI/UX redesigns",
                "Innovation Lab, Floor 3",
                LocalDateTime.of(2025, 7, 12, 9, 30),
                LocalDateTime.of(2025, 7, 12, 11, 0)
        ));

        // Export to .ics
        calendar.exportEventsToIcs("my-calendar.ics");

        System.out.println("Done! You can now import 'my-calendar.ics' into most calendar apps.");
    }
}