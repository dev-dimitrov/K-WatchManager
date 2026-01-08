package dev.dimitrov;

import java.io.IOException;
import java.util.Map;

public class Visual {
    public static String color1 = "";
    public static String color2 = "";

    public static final String END = "\033[0m";
    public static String PIPE = color2 + " | " + END;
    public static String AT = color2 + "@" + color1;
    public static Map<String,String> colors = Map.of(
        "RED","\033[0;31m",
        "GREEN","\033[0;32m",
        "YELLOW","\033[0;33m",
        "BLUE","\033[0;34m",
        "PURPLE","\033[0;35m",
        "CYAN","\033[0;36m",
        "WHITE",""
    );
    public static void showTitle() {
        System.out.println(color1 + "/////////// K-WatchManager v4.3 /////////// || github.com/dev-dimitrov\n" + END);
    }

    public static void showMain() {
        System.out.println(color1 + "[1] Add watch\n" +
                "[2] Check watch accuracy\n" +
                "[3] Adjust watch\n" +
                "[4] Show watch logs\n" +
                "[5] Modify watch data\n" +
                "[6] Check watch full info\n" +
                "[7] Delete watch\n" +
                "[8] Change program colors\n" +
                "[E] Exit\n" +
                "Concatenate the option and the ID with and '-', for example to adjust watch 4 type: '3-4'." + END);
    }

    public static void showAddWatch() {
        System.out.println(color1 + "Add the a watch with the following format:\n" + "Brand" + AT + "Model" + AT + "MovementType" + AT + "Caliber" + AT + "TheoreticAccuracy" + AT + "MoreInformation\n" + "If you can not specify one of the fields, just put a '*' instead." + END);
    }

    public static void ask4Time(String now) {
        System.out.println(color1 + "What time (" + color2 + "hh:mm:ss" + color1 + ") is it in your watch at: " + color2 + now + color1 + " ?\nYou can check the exact time at " + color2 + "https://time.is" + END);
    }

    public static void line() {
        System.out.println(color2 + "+--------------------------------------------------------------------------+" + END);
    }

    public static void fullLine() {
        System.out.println(color2 + "+-------------------------------------------------------------------------------------------------------------------+" + END);
    }

    public static void header() {
        System.out.println(color2 + "|  ID  |  BRAND  |  MODEL  |  MOVEMENT  |          LAST  ADJUSTED          |" + END);
    }

    public static void shortHeader() {
        System.out.println(color2 + "BRAND | MODEL | MOVEMENT | CALIBER | THEORETIC ACCURACY | MORE INFO" + END);
    }

    public static void fullHeader(){
        System.out.println(color2 + "|  ID  |  BRAND  |  MODEL  |  MOVEMENT  |  CALIBER  |  THEORETIC ACCURACY  |  LAST ADJUSTED  |  MORE INFO" + END);
    }


    public static void error() {
        System.out.println(colors.get("RED") + "Please type in the correct format..." + Visual.END);
    }

    // Custom error message
    public static void error(String message) {
        System.out.println(colors.get("RED") + message + Visual.END);
    }

    // Clears the terminal
    public static void clear() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public static void success(String message) {
        System.out.println(colors.get("GREEN") + message + Visual.END);
    }

    public static int updateColors(String colors) {
        String c[] = colors.split("-");
        
        if (c.length == 2) {
            color1 = Visual.colors.get(c[0].toUpperCase());
            color2 = Visual.colors.get(c[1].toUpperCase());
            // Updating the color icons
            AT =color2+"@"+color1;
            PIPE =color2+" | "+END;
            return 0;
        }

        return -1;
    }

    public static void logMenu(){
        System.out.println(color1+"[1] Clear all the log\n[2] Clear last entry\n[3] Main menu"+END);
    }
}

