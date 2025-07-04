import javax.swing.*;
import java.awt.*;
import java.io.File;

public class CalendarController {
    private final CalendarSystem system;

    public CalendarController(CalendarSystem system) {
        this.system = system;
    }

    public void exportCalendar(String format, Component parent) {
        JFileChooser chooser = new JFileChooser(new java.io.File("."));
        chooser.setDialogTitle("Export to " + format.toUpperCase());
        chooser.setSelectedFile(new File("calendar-export." + format));

        int result = chooser.showSaveDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            switch (format.toLowerCase()) {
                case "csv"  -> system.exportToCsv(file.getAbsolutePath());
                case "html" -> system.exportToHtml(file.getAbsolutePath());
                case "ics"  -> system.exportEventsToIcs(file.getAbsolutePath());
                case "json" -> system.exportToJson(file.getAbsolutePath());
                default -> showError("Unsupported export format: " + format, parent);
            }
        }
    }

    public void importCalendar(String format, Component parent) {
        JFileChooser chooser = new JFileChooser(new java.io.File("."));
        chooser.setDialogTitle("Import from " + format.toUpperCase());

        int result = chooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            switch (format.toLowerCase()) {
                case "csv"  -> system.importFromCsv(file.getAbsolutePath());
                case "html" -> system.importFromHtml(file.getAbsolutePath());
                case "ics"  -> system.importFromIcs(file.getAbsolutePath());
                case "json" -> system.importFromJson(file.getAbsolutePath());
                default -> showError("Unsupported import format: " + format, parent);
            }
        }
    }

    private void showError(String message, Component parent) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}