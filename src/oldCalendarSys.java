import java.time.*;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;
@Deprecated
public class oldCalendarSys {

    private final Map<LocalDate, List<String>> eventMap = new HashMap<>();

    //Add an event to a date
    public void addEvent(LocalDate date, String eventTitle) {
        eventMap.computeIfAbsent(date, d -> new ArrayList<>()).add(eventTitle);
    }

    //Print a calendar for the given month and year
    public void printMonth(int year, int month) {
        LocalDate first = LocalDate.of(year, month, 1);
        System.out.printf("\n     %s %d\n", first.getMonth().getDisplayName(TextStyle.FULL, Locale.US), year);
        System.out.println("Su Mo Tu We Th Fr Sa");

        int dayOfWeek = first.getDayOfWeek().getValue() % 7;
        System.out.print("   ".repeat(dayOfWeek));

        int daysInMonth = first.lengthOfMonth();
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate current = LocalDate.of(year, month, day);
            boolean hasEvent = eventMap.containsKey(current);
            System.out.printf("%2d%s", day, hasEvent ? "*" : " ");
            System.out.print((dayOfWeek + day) % 7 == 0 ? "\n" : " ");
        }
        System.out.println();
    }

    //List events for a specific date
    public void listEvents(LocalDate date) {
        List<String> events = eventMap.getOrDefault(date, List.of());
        if (events.isEmpty()) {
            System.out.println("No events for " + date);
        } else {
            System.out.println("Events on " + date + ":");
            events.forEach(event -> System.out.println("• " + event));
        }
    }//End listEvents

    //Lists all events scheduled between two dates (inclusive).
    public void listBetween(LocalDate start, LocalDate end) {
        System.out.printf("🗂️ Events from %s to %s:\n", start, end);

        List<LocalDate> sortedDates = new ArrayList<>(eventMap.keySet());
        Collections.sort(sortedDates);

        boolean found = false;
        for (LocalDate date : sortedDates)
            if (!date.isBefore(start) && !date.isAfter(end)) {
                found = true;
                System.out.println("Date: " + date);
                for (String event : eventMap.get(date))
                    System.out.println("   • " + event);
            }//End If
        //End Loop
        if (!found) System.out.println("No events found in this range.");
    }//End listBetween

    //Countdown to next event TIMLINE
    public void showNextEventCountdown() {
        LocalDate today = LocalDate.now();
        returnUpcoming().ifPresentOrElse(next -> {
            long days = ChronoUnit.DAYS.between(today, next);
            System.out.printf("Next event is on %s (%d day%s from now)\n",
                    next, days, days == 1 ? "" : "s");
            listEvents(next);
        }, () -> System.out.println("🎉 No upcoming events."));
    }//End function

    //Helper: find the next event date
    private Optional<LocalDate> returnUpcoming() {
        return eventMap.keySet().stream()
                .filter(d -> !d.isBefore(LocalDate.now()))
                .sorted()
                .findFirst();

    }//End Optional


}