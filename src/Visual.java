import java.io.IOException;

public class Visual {
    public static String color1 = "";
    public static String color2 = "";

    public static final String BLACK = "\033[0;30m";
    public static final String RED = "\033[0;31m";
    public static final String GREEN = "\033[0;32m";
    public static final String YELLOW = "\033[0;33m";
    public static final String BLUE = "\033[0;34m";
    public static final String PURPLE = "\033[0;35m";
    public static final String CYAN = "\033[0;36m";
    public static final String WHITE = "\033[0;37m";
    public static final String END = "\033[0m";
    public static String PIPE = color2 + " | " + END;
    public static String AT = color2 + "@" + color1;

    public static void showTitle() {
        System.out.println(color1 + "/////////////////// K-WatchManager v4.0.1 /////////////////// || github.com/dev-dimitrov\n" + END);
    }

    public static void showMain() {
        System.out.println(color1 + "[1] Add watch\n" +
                "[2] Check watch accuracy\n" +
                "[3] Adjust watch\n" +
                "[4] Show watch logs\n" +
                "[5] Modify watch data\n" +
                "[6] Delete watch\n" +
                "[7] Change program colors\n" +
                "[8] Check watch full info\n" +
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
        System.out.println(Visual.RED + "Please type in the correct format..." + Visual.END);
    }

    // Custom error message
    public static void error(String message) {
        System.out.println(Visual.RED + message + Visual.END);
    }

    // Clears the terminal
    public static void clear() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                new ProcessBuilder("clear").start();
            }
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public static void success(String message) {
        System.out.println(Visual.GREEN + message + Visual.END);
    }

    public static int updateColors(String colors) {
        String c[] = colors.split("-");
        if (c.length == 2) {
            int cont = 0;
            while (cont < 2) {
                switch (c[cont]) {
                    case "RED" -> {
                        Visual.color1 = cont == 0 ? Visual.RED : Visual.color1;
                        Visual.color2 = cont == 1 ? Visual.RED : Visual.color2;
                    }
                    case "CYAN" -> {
                        Visual.color1 = cont == 0 ? Visual.CYAN : Visual.color1;
                        Visual.color2 = cont == 1 ? Visual.CYAN : Visual.color2;
                    }
                    case "YELLOW" -> {
                        Visual.color1 = cont == 0 ? Visual.YELLOW : Visual.color1;
                        Visual.color2 = cont == 1 ? Visual.YELLOW : Visual.color2;
                    }
                    case "BLUE" -> {
                        Visual.color1 = cont == 0 ? Visual.BLUE : Visual.color1;
                        Visual.color2 = cont == 1 ? Visual.BLUE : Visual.color2;
                    }
                    case "GREEN" -> {
                        Visual.color1 = cont == 0 ? Visual.GREEN : Visual.color1;
                        Visual.color2 = cont == 1 ? Visual.GREEN : Visual.color2;
                    }
                    case "PURPLE" -> {
                        Visual.color1 = cont == 0 ? Visual.PURPLE : Visual.color1;
                        Visual.color2 = cont == 1 ? Visual.PURPLE : Visual.color2;
                    }
                    default -> {
                        return -1;
                    }
                }
                cont++;
            }
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

