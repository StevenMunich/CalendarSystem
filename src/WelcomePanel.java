import javax.swing.*;
import java.awt.*;

public class WelcomePanel extends JPanel {
    public WelcomePanel(CalendarApp app) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Welcome to the Calendar System", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setAlignmentX(CENTER_ALIGNMENT);

        JButton addEventBtn = new JButton("Add Event");
        addEventBtn.setAlignmentX(CENTER_ALIGNMENT);
        addEventBtn.addActionListener(e -> app.showCard("add"));

        JButton viewCalendarBtn = new JButton("View Calendar");
        viewCalendarBtn.setAlignmentX(CENTER_ALIGNMENT);
        viewCalendarBtn.addActionListener(e -> app.showCard("calendar"));

        // Export Buttons
        JButton exportCsvBtn = new JButton("Export to CSV");
        exportCsvBtn.setAlignmentX(CENTER_ALIGNMENT);
        exportCsvBtn.addActionListener(e -> app.exportCalendar("csv"));

        JButton exportHtmlBtn = new JButton("Export to HTML");
        exportHtmlBtn.setAlignmentX(CENTER_ALIGNMENT);
        exportHtmlBtn.addActionListener(e -> app.exportCalendar("html"));

        JButton exportIcsBtn = new JButton("Export to ICS");
        exportIcsBtn.setAlignmentX(CENTER_ALIGNMENT);
        exportIcsBtn.addActionListener(e -> app.exportCalendar("ics"));

        JButton exportJsonBtn = new JButton("Export to JSON");
        exportJsonBtn.setAlignmentX(CENTER_ALIGNMENT);
        exportJsonBtn.addActionListener(e -> app.exportCalendar("json"));

        // Import Buttons
        JButton importCsvBtn = new JButton("Import from CSV");
        importCsvBtn.setAlignmentX(CENTER_ALIGNMENT);
        importCsvBtn.addActionListener(e -> app.importCalendar("csv"));

        JButton importHtmlBtn = new JButton("Import from HTML");
        importHtmlBtn.setAlignmentX(CENTER_ALIGNMENT);
        importHtmlBtn.addActionListener(e -> app.importCalendar("html"));

        JButton importIcsBtn = new JButton("Import from ICS");
        importIcsBtn.setAlignmentX(CENTER_ALIGNMENT);
        importIcsBtn.addActionListener(e -> app.importCalendar("ics"));

        JButton importJsonBtn = new JButton("Import from JSON");
        importJsonBtn.setAlignmentX(CENTER_ALIGNMENT);
        importJsonBtn.addActionListener(e -> app.importCalendar("json"));

        // Layout
        add(Box.createVerticalStrut(40));
        add(title);
        add(Box.createVerticalStrut(20));
        add(addEventBtn);
        add(viewCalendarBtn);

        add(Box.createVerticalStrut(30));
        JLabel exportLabel = new JLabel("Export Calendar:", SwingConstants.CENTER);
        exportLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(exportLabel);
        add(exportCsvBtn);
        add(exportHtmlBtn);
        add(exportIcsBtn);
        add(exportJsonBtn);

        add(Box.createVerticalStrut(20));
        JLabel importLabel = new JLabel("Import Calendar:", SwingConstants.CENTER);
        importLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(importLabel);
        add(importCsvBtn);
        add(importHtmlBtn);
        add(importIcsBtn);
        add(importJsonBtn);
    }//===============================End JPanel===========================
}//End Class