import javax.swing.*;
import java.awt.*;
import java.time.*;
import java.util.List;
import java.util.stream.Collectors;

public class CalendarMonthPanel extends JPanel {
    private final CalendarSystem calendarSystem;
    private final JComboBox<Integer> yearBox;
    private final JComboBox<Month> monthBox;
    private final JPanel calendarGrid;


    public CalendarMonthPanel(CalendarSystem calendarSystem) {
        this.calendarSystem = calendarSystem;
        setLayout(new BorderLayout());

        // Top controls
        JPanel controlPanel = new JPanel();
        yearBox = new JComboBox<>();
        monthBox = new JComboBox<>(Month.values());

        for (int y = 2000; y <= 2100; y++) yearBox.addItem(y);
        yearBox.setSelectedItem(LocalDate.now().getYear());
        monthBox.setSelectedItem(LocalDate.now().getMonth());
        //Event Listeners to Objects.
        yearBox.addActionListener(e -> renderCalendar());
        monthBox.addActionListener(e -> renderCalendar());

        JButton loadButton = new JButton("View Active Days");

        controlPanel.add(new JLabel("Year:"));
        controlPanel.add(yearBox);
        controlPanel.add(new JLabel("Month:"));
        controlPanel.add(monthBox);
        controlPanel.add(loadButton);
        add(controlPanel, BorderLayout.NORTH);

        // Calendar grid
        calendarGrid = new JPanel(new GridLayout(0, 7, 5, 5));
        add(calendarGrid, BorderLayout.CENTER);

        loadButton.addActionListener(e -> renderCalendar());
        renderCalendar();
    }

    private void renderCalendar() {
        calendarGrid.removeAll();

        // Add day headers
        for (DayOfWeek day : DayOfWeek.values()) {
            JLabel lbl = new JLabel(day.toString().substring(0, 2), SwingConstants.CENTER);
            lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
            calendarGrid.add(lbl);
        }

        int year = (int) yearBox.getSelectedItem();
        Month month = (Month) monthBox.getSelectedItem();
        LocalDate first = LocalDate.of(year, month, 1);
        int firstDayIndex = first.getDayOfWeek().getValue() % 7;
        int daysInMonth = month.length(Year.isLeap(year));

        // Blank cells
        for (int i = 0; i < firstDayIndex; i++) calendarGrid.add(new JLabel());

        // Fill in days
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate current = LocalDate.of(year, month, day);


            List<CalendarEvent> events = calendarSystem.getEventsForDate(current);
            boolean hasEvent = !events.isEmpty();

            JButton dayBtn = new JButton(String.valueOf(day));
            dayBtn.setFocusPainted(false);

            if (hasEvent) {
                boolean isOffDay = events.stream()
                        .anyMatch(e -> e.getTitle().equalsIgnoreCase("OFF"));

                if (isOffDay) {
                    dayBtn.setBackground(new Color(255, 200, 200)); // light red
                } else {
                    int eventCount = events.size();
                    int greenShade = Math.max(255 - (eventCount * 20), 180); // clamp brightness
                    dayBtn.setBackground(new Color(200, greenShade, 200)); // darker with more events
                }

                dayBtn.setFont(dayBtn.getFont().deriveFont(Font.BOLD));

                String tooltip = events.stream()
                        .map(e -> "• " + e.getTitle())
                        .collect(Collectors.joining("<br>"));
                dayBtn.setToolTipText("<html>" + tooltip + "</html>");
            }//End If(hasEvent)






            dayBtn.setFocusPainted(false);
            dayBtn.addActionListener(e -> showEventsOnDate(current));
            calendarGrid.add(dayBtn);
        }

        revalidate();
        repaint();
    }

    private void showEventsOnDate(LocalDate date) {
        List<CalendarEvent> events = calendarSystem.getEventsForDate(date);
        if (events.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No events for " + date);
        } else {
            StringBuilder sb = new StringBuilder();
            for (CalendarEvent e : events) {
                sb.append("• ")
                        .append(e.getTitle())
                        .append(" (")
                        .append(e.getStart().toLocalTime())
                        .append("–")
                        .append(e.getEnd().toLocalTime())
                        .append(" @ ")
                        .append(e.getLocation())
                        .append(")\n");
            }
            JOptionPane.showMessageDialog(this, sb.toString(), "Events on " + date, JOptionPane.INFORMATION_MESSAGE);
        }
    }
}//End Class


//=======Scrap code

/*
From RenderCalender()
            if (hasEvent) {
                dayBtn.setFont(dayBtn.getFont().deriveFont(Font.BOLD));
                dayBtn.setBackground(new Color(220, 255, 220)); // light green
                String tooltip = events.stream()
                        .map(e -> "• " + e.getTitle())
                        .collect(Collectors.joining("<br>"));
                dayBtn.setToolTipText("<html>" + tooltip + "</html>");
            }
//replaced by logic that colors red if "OFF"



 */

