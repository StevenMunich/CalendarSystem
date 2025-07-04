import java.util.regex.*;
import java.util.*;

public class DateFormatParser {

    private static final List<Pattern> datePatterns = List.of(
            Pattern.compile("^(?<day>0[1-9]|[12][0-9]|3[01])[-/.](?<month>0[1-9]|1[0-2])[-/.](?<year>19\\d{2}|20\\d{2})$"),
            Pattern.compile("^(?<year>19\\d{2}|20\\d{2})[-/.](?<month>0[1-9]|1[0-2])[-/.](?<day>0[1-9]|[12][0-9]|3[01])$"),
            Pattern.compile("^(?<month>0[1-9]|1[0-2])[-/.](?<day>0[1-9]|[12][0-9]|3[01])[-/.](?<year>19\\d{2}|20\\d{2})$")
    );

    /**
     * Parses a supported date format into standard YYYY-MM-DD.
     */
    public static String parseToIso(String rawDate) {
        for (Pattern pattern : datePatterns) {
            Matcher matcher = pattern.matcher(rawDate);
            if (matcher.matches()) {
                String year = matcher.group("year");
                String month = matcher.group("month");
                String day = matcher.group("day");
                return String.format("%s-%s-%s", year, month, day);
            }
        }
        return null;
    }//End parse()

    //Validates whether a date string matches any supported pattern.
    public static boolean isValidFormat(String rawDate) {
        return parseToIso(rawDate) != null;
    }//=============================END CORE FUNCTIONS===================================================

    //Detects the original format type (e.g., DMY, YMD, MDY).
    public static String detectFormat(String rawDate) {
        for (Pattern pattern : datePatterns) {
            Matcher matcher = pattern.matcher(rawDate);
            if (matcher.matches()) {
                if (matcher.group("year") != null && matcher.start("year") == 0) return "YMD";
                if (matcher.group("day") != null && matcher.start("day") == 0) return "DMY";
                if (matcher.group("month") != null && matcher.start("month") == 0) return "MDY";
            }//End If
        }//End Loop
        return "Unknown";
    }//End detectFormat

    //Returns the date in a more human-friendly form like "July 3, 2025".
    public static String toReadableFormat(String rawDate) {
        String iso = parseToIso(rawDate);
        if (iso == null) return null;

        String[] parts = iso.split("-");
        int monthNum = Integer.parseInt(parts[1]);
        String[] monthNames = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };
        String monthName = monthNames[monthNum - 1];
        return String.format("%s %d, %s", monthName, Integer.parseInt(parts[2]), parts[0]);
    }//End toReadableFormat

    //Compares two dates chronologically. Returns -1 if date1 < date2, 0 if equal, 1 if date1 > date2.
    public static int compare(String date1, String date2) {
        String iso1 = parseToIso(date1);
        String iso2 = parseToIso(date2);
        if (iso1 == null || iso2 == null) throw new IllegalArgumentException("Invalid date format.");
        return iso1.compareTo(iso2);
    }//End compare

    //Checks if a date is in the past relative to today.
    public static boolean isPast(String rawDate) {
        String iso = parseToIso(rawDate);
        if (iso == null) return false;
        return iso.compareTo(java.time.LocalDate.now().toString()) < 0;
    }//End function
}//End Class