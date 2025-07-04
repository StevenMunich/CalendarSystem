import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main {
    public static void main(String[] args) {

    CalendarSystem systemC = new CalendarSystem();

    systemC.runMenu();

    }//End main()

    private static  void print(String p){
        System.out.println(p);
    }
}//End Class