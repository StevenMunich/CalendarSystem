import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.*;

public class CalendarSystem {

    private final Map<LocalDate, List<CalendarEvent>> eventMap = new HashMap<>();
    private final Scanner scanner = new Scanner(System.in);
    private final DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    //basic helper functions
    public Map<LocalDate, List<CalendarEvent>> getEventMap(){return eventMap;}
    // Get events on a specific date
    public List<CalendarEvent> getEventsForDate(LocalDate date) { return eventMap.getOrDefault(date, List.of());}

    private static void println(String s){ System.out.println(s);}
    public List<CalendarEvent> getAllEvents() { return eventMap.values().stream().flatMap(List::stream).toList();}

    // Add a full event
    public void addEvent(CalendarEvent event) {
        LocalDate date = event.getStart().toLocalDate();
        eventMap.computeIfAbsent(date, d -> new ArrayList<>()).add(event);
    }

    // Remove event by title (for demo simplicity)
    public void removeEvent(LocalDate date, String title) {
        List<CalendarEvent> events = eventMap.getOrDefault(date, List.of());
        boolean removed = events.removeIf(e -> e.getTitle().equalsIgnoreCase(title));
        if (removed) println("Event removed.");
        else println("Event not found.");
    }

    // Show monthly calendar
    public void printMonth(int year, int month) {
        LocalDate first = LocalDate.of(year, month, 1);
        System.out.printf("\n     %s %d\n", first.getMonth().getDisplayName(TextStyle.FULL, Locale.US), year);
        println("Su Mo Tu We Th Fr Sa");
        int dayOfWeek = first.getDayOfWeek().getValue() % 7;
        System.out.print("   ".repeat(dayOfWeek));

        int daysInMonth = first.lengthOfMonth();
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate current = LocalDate.of(year, month, day);
            boolean hasEvent = eventMap.containsKey(current);

            // Print day number
            System.out.printf("%2d", day);

            // Indicate event with "*"
            if (hasEvent) {
                System.out.print("*");
            } else {
                System.out.print(" ");
            }
            // Handle line breaks after each Saturday
            if ((dayOfWeek + day) % 7 == 0) {
                System.out.println();
            } else {
                System.out.print(" ");
            }
        }
        System.out.println();
    }

    // 📋 List all events for a specific date
    public void listEvents(LocalDate date) {
        List<CalendarEvent> events = eventMap.getOrDefault(date, List.of());
        if (events.isEmpty()) {
            println("No events for " + date);
        } else {
            println("Events on " + date + ":");
            events.forEach(e -> System.out.printf("• %s (%s–%s) @ %s\n",
                    e.getTitle(),
                    e.getStart().toLocalTime(),
                    e.getEnd().toLocalTime(),
                    e.getLocation()));
        }
    }//End listEvents


    /**
     * Lists all calendar events between two dates (inclusive), sorted by date and time.
     */
    public void viewEventsBetween(LocalDate start, LocalDate end) {
        System.out.printf("\nEvents from %s to %s:\n", start, end);
        boolean found = false;

        List<LocalDate> sortedDates = new ArrayList<>(eventMap.keySet());
        Collections.sort(sortedDates);

        for (LocalDate date : sortedDates) {
            if (!date.isBefore(start) && !date.isAfter(end)) {
                List<CalendarEvent> events = eventMap.get(date);
                if (events != null && !events.isEmpty()) {
                    found = true;
                    println("\n🗓 " + date);
                    events.stream()
                            .sorted(Comparator.comparing(CalendarEvent::getStart))
                            .forEach(e -> System.out.printf("• %s (%s–%s) @ %s\n",
                                    e.getTitle(),
                                    e.getStart().toLocalTime(),
                                    e.getEnd().toLocalTime(),
                                    e.getLocation()));
                }
            }
        }

        if (!found) {
            println("No events found in that range.");
        }
    }//End viewEventsBetween

    public void promptCreateEvent() {
        String title = null;
        String description = null;
        String location = null;
        LocalDateTime start = null;
        LocalDateTime end = null;

        // Title
        while (title == null || title.isBlank()) {
            System.out.print("Title: ");
            title = scanner.nextLine().trim();
            if (title.isEmpty()) println("Title cannot be empty.");
        }

        // Description
        while (description == null || description.isBlank()) {
            System.out.print("Description: ");
            description = scanner.nextLine().trim();
            if (description.isEmpty()) println("Description cannot be empty.");
        }

        // Location
        while (location == null || location.isBlank()) {
            System.out.print("Location: ");
            location = scanner.nextLine().trim();
            if (location.isEmpty()) println("Location cannot be empty.");
        }

        // Start DateTime
        while (start == null) {
            System.out.print("Start DateTime (yyyy-MM-dd HH:mm): ");
            try {
                start = LocalDateTime.parse(scanner.nextLine().trim(), dtFormat);
            } catch (Exception e) {
                println("Invalid start datetime format.");
            }
        }

        // End DateTime
        while (end == null) {
            System.out.print("End DateTime (yyyy-MM-dd HH:mm): ");
            try {
                end = LocalDateTime.parse(scanner.nextLine().trim(), dtFormat);
                if (end.isBefore(start)) {
                    println("End must be after start.");
                    end = null;
                }
            } catch (Exception e) {
                println("Invalid end datetime format.");
            }
        }

        // All valid — add event
        addEvent(new CalendarEvent(title, description, location, start, end));
        println("Event added.");
    }//End of PromptCreateEvent


//===============================EXPORT TO A FORMAT USED BY OTHER SYSTEMS========================================
    //First is .Ics There is a library for this but it has many dependencies and we just want to do this
    // .ics is used by various calendar apps.
    public void exportEventsToIcs(String fileName) {
        StringBuilder builder = new StringBuilder();
        builder.append("BEGIN:VCALENDAR\n")
                .append("VERSION:2.0\n")
                .append("PRODID:-//CalendarSystem//EN\n")
                .append("CALSCALE:GREGORIAN\n");

        eventMap.values().stream()
                .flatMap(List::stream)
                .sorted(Comparator.comparing(CalendarEvent::getStart))
                .forEach(event -> builder.append(createIcsBlock(event)));

        builder.append("END:VCALENDAR");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(builder.toString());
            println("Exported events to " + fileName);
        } catch (IOException e) {
            println("Failed to write to file: " + e.getMessage());
        }
    }//End
    //help function
    private String createIcsBlock(CalendarEvent event) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");
        ZoneId systemZone = ZoneId.systemDefault();
        String start = event.getStart().atZone(systemZone).withZoneSameInstant(ZoneOffset.UTC).format(fmt);
        String end = event.getEnd().atZone(systemZone).withZoneSameInstant(ZoneOffset.UTC).format(fmt);
        String uid = event.getId();
        //

        return new StringBuilder()
                .append("BEGIN:VEVENT\n")
                .append("UID:").append(uid).append("\n")
                .append("DTSTAMP:").append(LocalDateTime.now().atZone(systemZone).withZoneSameInstant(ZoneOffset.UTC).format(fmt)).append("\n")
                .append("DTSTART:").append(start).append("\n")
                .append("DTEND:").append(end).append("\n")
                .append("SUMMARY:").append(event.getTitle()).append("\n")
                .append("DESCRIPTION:").append(event.getDescription()).append("\n")
                .append("LOCATION:").append(event.getLocation()).append("\n")
                .append("STATUS:CONFIRMED\n")
                .append("END:VEVENT\n")
                .toString();
    }//End helper Function for Export to ICS
//============================End of Format to ICS====================================
//Csv is EZ and is cool like scv from sc bw. The svc is the builder,miner,workman that gets resources and builds barracks and farms for your army. In the custom game "DONT FUCKING MOVE" the psycho renamed the unit "This guy fucked your mom" no joke.
//Trust me there is more to the story. It is a joke of a game. 4-5players are forced to remain still in a box while 1 player has a massive army and nukes. If you laeve the box it triggers suicide bombers named "Jihads". When the game starts the creator screams "DONT FUCKING MOVE".mp3
    public void exportToCsv(String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("Title,Description,Location,Start,End");
            for (CalendarEvent event : getAllEvents()) {
                writer.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                        event.getTitle(), event.getDescription(), event.getLocation(),
                        event.getStart(), event.getEnd());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }//End export2CSV

    //JSON
    public void exportToJson(String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("[");
            List<CalendarEvent> events = getAllEvents();
            for (int i = 0; i < events.size(); i++) {
                CalendarEvent e = events.get(i);
                writer.printf("  {\n    \"title\": \"%s\",\n    \"description\": \"%s\",\n    \"location\": \"%s\",\n    \"start\": \"%s\",\n    \"end\": \"%s\"\n  }%s\n",
                        e.getTitle(), e.getDescription(), e.getLocation(), e.getStart(), e.getEnd(),
                        (i < events.size() - 1) ? "," : "");
            }
            writer.println("]");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }//End exportToJson

    //Because PDF files are opened in browsers and requires a dependency I decided to do it in HTML.
    public void exportToHtml(String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("<!DOCTYPE html>");
            writer.println("<html><head>");
            writer.println("<meta charset='UTF-8'>");
            writer.println("<title>Calendar Events</title>");
            writer.println("<style>");
            writer.println("body { font-family: Arial, sans-serif; background: #f6f6f6; padding: 20px; }");
            writer.println("h1 { color: #333; }");
            writer.println("table { width: 100%; border-collapse: collapse; background: white; }");
            writer.println("th, td { border: 1px solid #ccc; padding: 8px; text-align: left; }");
            writer.println("th { background-color: #e0e0e0; }");
            writer.println("tr:nth-child(even) { background-color: #f9f9f9; }");
            writer.println("</style></head><body>");
            writer.println("<h1>Calendar Events</h1>");
            writer.println("<table id='allEventsTable'>");
            writer.println("<tr><th>Title</th><th>Description</th><th>Location</th><th>Start</th><th>End</th></tr>");

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            for (CalendarEvent event : getAllEvents()) {
                writer.println("  <tr>");
                writer.printf("    <td>%s</td>%n", escapeHtml(event.getTitle()));
                writer.printf("    <td>%s</td>%n", escapeHtml(event.getDescription()));
                writer.printf("    <td>%s</td>%n", escapeHtml(event.getLocation()));
                writer.printf("    <td>%s</td>%n", event.getStart().format(fmt));
                writer.printf("    <td>%s</td>%n", event.getEnd().format(fmt));
                writer.println("  </tr>");
            }

            writer.println("</table></body></html>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }//End ()
    private String escapeHtml(String input) {
        if (input == null) return "";

        StringBuilder out = new StringBuilder();
        for (char c : input.toCharArray()) {
            switch (c) {
                case '<': out.append("&lt;"); break;
                case '>': out.append("&gt;"); break;
                case '&': out.append("&amp;"); break;
                case '"': out.append("&quot;"); break;
                case '\'': out.append("&#x27;"); break;
                case '/': out.append("&#x2F;"); break; // optional, prevents </script> injection
                default:
                    if (c > 127) out.append("&#").append((int) c).append(";");else out.append(c);
                    //End If
            }//End Switch
        }//End Loop
        return out.toString();
    }//End ()

//===================================END OF EXPORT FUNCTIONS====================================
//==============================================================================================

//=================================IMPORT FUNCTIONS==============================================

    //CSV
    public void importFromCsv(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine(); // skip header
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("\",\"");
                if (tokens.length != 5) continue;

                String title = tokens[0].replaceFirst("^\"", "");
                String description = tokens[1];
                String location = tokens[2];
                String start = tokens[3];
                String end = tokens[4].replaceAll("\"$", "");

                CalendarEvent event = new CalendarEvent(
                        title.trim(), description.trim(), location.trim(),
                        LocalDateTime.parse(start.trim()), LocalDateTime.parse(end.trim())
                );
                addEvent(event);
            }
        } catch (IOException | DateTimeParseException e) {
            e.printStackTrace();
        }
    }//End ()

    //JSON
    public void importFromJson(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) json.append(line.trim());

            String content = json.toString();
            DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

            String[] entries = content.split("\\},\\s*\\{");
            for (String block : entries) {
                String cleaned = block.replaceAll("[\\[\\]\\{\\}]", "");
                String[] fields = cleaned.split("\",\\s*\"");
                String title = "", description = "", location = "", start = "", end = "";

                for (String field : fields) {
                    String[] pair = field.replace("\"", "").split(":", 2);
                    if (pair.length < 2) continue;
                    switch (pair[0].trim()) {
                        case "title" -> title = pair[1].trim();
                        case "description" -> description = pair[1].trim();
                        case "location" -> location = pair[1].trim();
                        case "start" -> start = pair[1].trim();
                        case "end" -> end = pair[1].trim();
                    }
                }

                CalendarEvent event = new CalendarEvent(
                        title, description, location,
                        LocalDateTime.parse(start, fmt),
                        LocalDateTime.parse(end, fmt)
                );
                addEvent(event);
            }
        } catch (IOException | DateTimeParseException e) {
            e.printStackTrace();
        }
    }//End ()

    //ICS
    public void importFromIcs(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            CalendarEvent event = null;

            String title = "", description = "", location = "", dtStart = "", dtEnd = "";

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.equals("BEGIN:VEVENT")) {
                    title = description = location = dtStart = dtEnd = "";
                } else if (line.startsWith("SUMMARY:")) {
                    title = line.substring(8);
                } else if (line.startsWith("DESCRIPTION:")) {
                    description = line.substring(12);
                } else if (line.startsWith("LOCATION:")) {
                    location = line.substring(9);
                } else if (line.startsWith("DTSTART:")) {
                    dtStart = line.substring(8);
                } else if (line.startsWith("DTEND:")) {
                    dtEnd = line.substring(6);
                } else if (line.equals("END:VEVENT")) {
                    //DateTimeFormatter icsFmt = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'X'");
                    DateTimeFormatter icsFmt = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssX");
                    LocalDateTime start = OffsetDateTime.parse(dtStart, icsFmt).toLocalDateTime();
                    LocalDateTime end = OffsetDateTime.parse(dtEnd, icsFmt).toLocalDateTime();
                    System.out.println("Parsed event: " + title + " from " + dtStart + " to " + dtEnd);

                    CalendarEvent e = new CalendarEvent(title, description, location, start, end);
                    addEvent(e);
                }
            }
        } catch (IOException | DateTimeParseException e) {
            e.printStackTrace();
        }
    }//End ()

    //HTML with validation
    public void importFromHtml(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean inTable = false;
            boolean headerValidated = false;
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // Start of table with any id
                if (!inTable && line.matches(".*<table[^>]*id\\s*=\\s*['\"].+['\"].*")) {
                    inTable = true;
                    continue;
                }

                // Wait for header to validate table structure
                // Loosened: assume the next <tr> is header, skip it regardless of content
                if (inTable && !headerValidated && line.toLowerCase().contains("<tr>") && line.toLowerCase().contains("<th>")) {
                    System.out.println("HTML HEADER BLOCK: " + line); // optional for debug
                    if (!isValidHtmlTableHeader(line)) continue;
                    headerValidated = true;
                    continue;
                }




                /*  if (inTable && !headerValidated && line.toLowerCase().contains("<tr>")) {
                    StringBuilder headerBlock = new StringBuilder();
                    for (int i = 0; i < 5; i++) {
                        String h = reader.readLine();
                        if (h != null) headerBlock.append(h);
                    }
                }//End If
                */
                //if (!isValidHtmlTableHeader(headerBlock.toString())) continue;//Helper function called. If this Helper function starts acting up, giving problems switch back to old code Delete the helper function & replace this line // if (headers == null || !headers.toLowerCase().contains("title")) continue;
                // if (headers == null || !headers.toLowerCase().contains("title")) continue;
                // headerValidated = true;
                //   continue;


                // Store event rows into cells
                if (inTable && line.contains("<tr>")) {
                    String[] cells = new String[5];
                    for (int i = 0; i < 5; i++) {
                        line = reader.readLine();
                        if (line == null) break; //skip if line is null
                        cells[i] = line.replaceAll("<.*?>", "").trim();

                    }//End Loop
                    System.out.println("IMPORTING HTML EVENT: " + Arrays.toString(cells));
                    //anyMatch(...) checks if any of the cells in the row are null or empty strings "".
                    if (Arrays.stream(cells).anyMatch(s -> s == null || s.isEmpty())){
                        System.out.println("NULL OR BLANK EVENT " + Arrays.toString(cells));
                        continue; //If so Skip to Next Row
                    }


                    try {
                        CalendarEvent event = new CalendarEvent(
                                cells[0], cells[1], cells[2],
                                LocalDateTime.parse(cells[3], fmt),
                                LocalDateTime.parse(cells[4], fmt)
                        );
                        addEvent(event);
                    } catch (Exception ex) {
                        System.err.println("Skipping invalid row: " + Arrays.toString(cells));
                    }//End Try
                }//End If
            }//End White
        } catch (IOException e) {
            e.printStackTrace();
        }//End Try
    }//End HTML import ()
    private boolean isValidHtmlTableHeader(String headerLine) {
        if (headerLine == null) return false;
        // Normalize casing and remove HTML tags
        String normalized = headerLine.replaceAll("(?i)<[^>]+>", "").toLowerCase();
        return normalized.contains("title")
                && normalized.contains("description")
                && normalized.contains("location")
                && normalized.contains("start")
                && normalized.contains("end");
    }//End isValid()



//=================================END OF IMPORT FUNCTIONS=========================================

    // 🧭 Console menu system
    public void runMenu() {
        while (true) {
            println("\n==== Calendar Menu ====");
            println("1. View Month");
            println("2. Add Event");
            println("3. View Events on Date");
            println("4. Remove Event");
            println("5. Exit");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> {
                    System.out.print("Enter year and month (YYYY MM): ");
                    int y = scanner.nextInt();
                    int m = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                    printMonth(y, m);
                }
                case "2" -> promptCreateEvent();
                case "3" -> {
                    System.out.print("Enter date (yyyy-MM-dd): ");
                    LocalDate date = LocalDate.parse(scanner.nextLine());
                    listEvents(date);
                }
                case "4" -> {
                    System.out.print("Enter date (yyyy-MM-dd): ");
                    LocalDate date = LocalDate.parse(scanner.nextLine());
                    System.out.print("Enter title to remove: ");
                    String title = scanner.nextLine();
                    removeEvent(date, title);
                }
                case "5" -> {
                    System.out.print("Enter the start date (yyyy-MM-dd): ");
                    LocalDate startDate = LocalDate.parse(scanner.nextLine());
                    System.out.print("Enter the end date (yyyy-MM-dd): ");
                    LocalDate endDate = LocalDate.parse(scanner.nextLine());
                    viewEventsBetween(startDate, endDate);
                }
                case "6" -> {
                    println("Goodbye!");
                    return;
                }
                default -> println("Invalid option.");
            }//End Switch
        }//end While
    }//End Menu
}//End Class