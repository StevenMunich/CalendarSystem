import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class SearchPanel extends JPanel {
    private final CalendarSystem calendarSystem;
    private final JTextField keywordField = new JTextField(20);
    private final JTextField locationField = new JTextField(20);
    private final JButton searchBtn = new JButton("Search");
    private final JTextArea resultArea = new JTextArea(12, 40);

    public SearchPanel(CalendarSystem calendarSystem) {
        this.calendarSystem = calendarSystem;
        setLayout(new BorderLayout(10, 10));

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        inputPanel.add(new JLabel("Keyword (title/description):"));
        inputPanel.add(keywordField);
        inputPanel.add(new JLabel("Location (optional):"));
        inputPanel.add(locationField);
        inputPanel.add(new JLabel());  // spacing
        inputPanel.add(searchBtn);

        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        searchBtn.addActionListener(e -> performSearch());
    }

    private void performSearch() {
        String keyword = keywordField.getText().trim().toLowerCase();
        String locationFilter = locationField.getText().trim().toLowerCase();

        List<CalendarEvent> matches = calendarSystem.getAllEvents().stream()
                .filter(e -> (e.getTitle().toLowerCase().contains(keyword)
                        || e.getDescription().toLowerCase().contains(keyword)))
                .filter(e -> locationFilter.isEmpty() || e.getLocation().toLowerCase().contains(locationFilter))
                .collect(Collectors.toList());

        if (matches.isEmpty()) {
            resultArea.setText("No matching events found.");
        } else {
            StringBuilder sb = new StringBuilder("Search Results:\n");
            matches.forEach(e -> sb.append(String.format("• %s (%s to %s)\n  %s @ %s\n\n",
                    e.getTitle(),
                    e.getStart(),
                    e.getEnd(),
                    e.getDescription(),
                    e.getLocation())));
            resultArea.setText(sb.toString());
        }
    }
}