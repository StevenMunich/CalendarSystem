import javax.swing.*;
import java.awt.*;

public class CalendarApp extends JFrame {
    private final JPanel cardPanel;
    private final CardLayout cardLayout;
    private final CalendarController controller;

    public void exportCalendar(String format) {
        controller.exportCalendar(format, this);
    }

    public void importCalendar(String format) {
        controller.importCalendar(format, this);
    }

    public CalendarApp() {
        super("Swing Calendar System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        CalendarSystem calendarSystem = new CalendarSystem();
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        //Add Controller
        controller = new CalendarController(calendarSystem);

        // Add your panels
        cardPanel.add(new WelcomePanel(this), "welcome");
        cardPanel.add(new AddEventPanel(calendarSystem), "add");
        cardPanel.add(new CalendarMonthPanel(calendarSystem), "calendar");
        cardPanel.add(new SearchPanel(calendarSystem), "search");

        setJMenuBar(new CalendarMenuBar(this));


        add(cardPanel);
        setVisible(true);
    }

    public void showCard(String name) {
        cardLayout.show(cardPanel, name);
    }
    public static void main(String args[]){
        CalendarApp C = new CalendarApp();

    }
}//End Class

/*
public class CalendarApp extends JFrame {
    public CalendarApp() {
        super("Swing Calendar System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Main layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        JMenuBar menuBar = new CalendarMenuBar();
        mainPanel.add(new WelcomePanel(), BorderLayout.CENTER);

        setJMenuBar(menuBar);
        add(mainPanel);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CalendarApp::new);
    }
}

*/