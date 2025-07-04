import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AddEventPanel extends JPanel {

    String nowFormatted = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    String sixHours = LocalDateTime.now().plusHours(6).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

    private final JTextField titleField = new JTextField(20);
    private final JTextArea descArea = new JTextArea(5, 20);
    private final JTextField locationField = new JTextField(20);
    private final JTextField startField = new JTextField(nowFormatted, 20);
    private final JTextField endField = new JTextField(sixHours, 20);
    private final JButton submitButton = new JButton("Add Event");



    private final CalendarSystem controller;


    //==========Java Swing does not have PlaceHolders
    private void setupPlaceholder(JTextComponent field, String placeholder) {
        field.setForeground(Color.GRAY);
        field.setText(placeholder);

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(placeholder);
                }
            }
        });
    }
    //PLACE


    public AddEventPanel(CalendarSystem controller) {
        this.controller = controller;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addField(gbc, row++, "Title:", titleField);
        addField(gbc, row++, "Description:", new JScrollPane(descArea));
        addField(gbc, row++, "Location:", locationField);
        addField(gbc, row++, "Start DateTime:", startField);
        addField(gbc, row++, "End DateTime:", endField);

        gbc.gridx = 1;
        gbc.gridy = row;
        add(submitButton, gbc);

        submitButton.addActionListener(e -> handleAddEvent());
        setupPlaceholder(titleField, "Meeting with client");
        setupPlaceholder(locationField, "e.g. Office, Zoom");


    }//End Constructor

    private void addField(GridBagConstraints gbc, int row, String label, Component field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        add(new JLabel(label), gbc);
        gbc.gridx = 1;
        add(field, gbc);
    }

    private void handleAddEvent() {
        try {
            String title = titleField.getText().trim();
            String description = descArea.getText().trim();
            String location = locationField.getText().trim();
            LocalDateTime start = LocalDateTime.parse(startField.getText().trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            LocalDateTime end = LocalDateTime.parse(endField.getText().trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

            if (title.isEmpty() || description.isEmpty() || location.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields must be filled.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (end.isBefore(start)) {
                JOptionPane.showMessageDialog(this, "End time must be after start time.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            CalendarEvent event = new CalendarEvent(title, description, location, start, end);
            controller.addEvent(event);
            JOptionPane.showMessageDialog(this, "Event added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            clearFields();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid date/time format.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {

        String nowFormatted = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        String sixHours = LocalDateTime.now().plusHours(6).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        titleField.setText("");
        descArea.setText("");

        locationField.setText("");
        startField.setText(nowFormatted);
        endField.setText(sixHours);
    }
}//End Class