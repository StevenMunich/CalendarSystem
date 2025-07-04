import javax.swing.*;

public class CalendarMenuBar extends JMenuBar {
    public CalendarMenuBar(CalendarApp app) {
        JMenu viewMenu = new JMenu("View");

        JMenuItem welcomeItem = new JMenuItem("Welcome Screen");
        welcomeItem.addActionListener(e -> app.showCard("welcome"));

        JMenuItem addItem = new JMenuItem("Add Event");
        addItem.addActionListener(e -> app.showCard("add"));

        JMenuItem calendarItem = new JMenuItem("Calendar View");
        calendarItem.addActionListener(e -> app.showCard("calendar"));

        JMenuItem searchItem = new JMenuItem("Search Events");
        searchItem.addActionListener(e -> app.showCard("search"));

        viewMenu.add(welcomeItem);
        viewMenu.add(addItem);
        viewMenu.add(calendarItem);
        viewMenu.add(searchItem);

        add(viewMenu);
    }
}//End Class



/*
public class CalendarMenuBar extends JMenuBar {
    public CalendarMenuBar(CalendarApp calendarApp) {
        JMenu fileMenu = new JMenu("File");

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        add(fileMenu);

        JMenuItem calItem = new JMenuItem("Calendar View");
        calItem.addActionListener(e -> app.showCard("calendar"));
        someMenu.add(calItem); // e.g., View or Navigation



        // Add more menus (Edit, View, Export, etc.) as needed
    }
}
*/
