import com.sun.source.tree.Tree;

import java.io.*;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.time.*;

public class Manager {
    public static Scanner sc = new Scanner(System.in);
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
    public static DateTimeFormatter LocalDateTimef = DateTimeFormatter.ofPattern("y-MM-dd HH:mm:ss");
    ArrayList<Watch> watches;
    private DecimalFormat decimalFormat;
    String colors;
    public static String f = "watches.bin";
    public Manager(){
        decimalFormat = new DecimalFormat("#.##");
        int status = loadWatches();
        loadColors();
        if(status == -1){
            watches = new ArrayList<>();

        }
    }

    // loads the list of watches and returns -1 if it fails.
    public int loadWatches(){
        try(ObjectInputStream o = new ObjectInputStream(new FileInputStream(f))){
            watches = (ArrayList<Watch>) o.readObject();
        }
        catch(ClassNotFoundException | IOException ex){
            return -1;
        }
        return 0;
    }

    // Save the list of watches
    public void saveWatches(){
        try(ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(f))){
            o.writeObject(watches);
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    }

    public void saveColors(){
        try(BufferedWriter b = new BufferedWriter(new FileWriter("colors.txt"))){
            b.write(colors);
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    }

    public void loadColors(){
        try(BufferedReader b = new BufferedReader(new FileReader("colors.txt"))){
            Visual.updateColors(b.readLine());
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    }

    // show main menu
    public String mainMenu(){
        Visual.showTitle();
        showWatches();
        Visual.showMain();
        String input = getInput(false).toLowerCase();
        String[] split = input.split("-");
        switch (split[0]){
            case "1" -> {Visual.clear(); addWatch();}
            case "2" -> {
                if (split.length == 2) {
                    Visual.clear();
                    checkAccuracy(split[1]);
                } else {
                    Visual.error();
                }
            }
            case "3" -> {
                if (split.length == 2) {
                    Visual.clear();
                    adjustWatch(split[1]);
                } else {
                    Visual.error();
                }
            }
            case "4" -> {
                if (split.length == 2) {
                    Visual.clear();
                    showWatchHistory(split[1]);
                } else {
                    Visual.error();
                }
            }
            case "5" -> {
                if (split.length == 2) {
                    Visual.clear();
                    modifyWatch(split[1]);
                } else {
                    Visual.error();
                }
            }
            case "6" -> {
                if (split.length == 2) {
                    Visual.clear();
                    removeWatch(split[1]);
                } else {
                    Visual.error();
                }
            }
            case "7" -> {Visual.clear();changeColors();}
            case "8" -> {
                if (split.length == 2) {
                    Visual.clear();
                    seeFullWatch(split[1]);
                } else {
                    Visual.error();
                }
            }
            case "e" -> System.out.println("Exiting...\n");
            default -> System.out.println(Visual.RED+"Invalid option...\n"+Visual.END);
        }

        if(!split[0].equals("e") && !split[0].equals("4")){
            getInput(true);
        }
        Visual.clear();
        return input;
    }

    // adding a watch to the list
    public void addWatch(){
        
        Visual.showAddWatch();
        String watchInput = Manager.getInput(false);
        Watch w = Watch.makeWatch(watchInput);
        if(w != null){
            watches.add(w);
            this.saveWatches();
            System.out.println(Visual.GREEN+"Successfully added!"+Visual.END);
        }
    }


    public void checkAccuracy(String id){
        Watch w = getWatch(id); // get the specified watch
        if(w != null){
            
            LocalDateTime last = w.getLastAdjust(); // The last adjustment
            LocalDateTime nowDay = LocalDateTime.now();
            LocalTime now = LocalTime.now();
            now = now.plusMinutes(1); // The actual time with one minute more
            now = now.minusSeconds(now.getSecond()); // Remove the seconds, to do properly the dif later
            String input = getInput(false);
            LocalTime watchHour = null;

            try{
               watchHour = LocalTime.parse(input); // parse the watch time
            }
            catch(DateTimeParseException ex){
                Visual.error("Invalid time format, please write it correctly.");
                return;
            }


            // Getting the difference between the real time and the watch one
            String diff = now.until(watchHour, ChronoUnit.SECONDS)+"";
            diff = !diff.contains("-") ? "+"+diff : diff; // Putting a + sign if its not negative

            System.out.println(Visual.color1 +"Your watch has a "+Visual.color2+diff+Visual.color1 +" seconds deviation."+Visual.END);

            // if there is a last adjustment, make an approximate deviation per day
            if(last != null){
                // The days difference between the last adjustment and today date.
                int days = 1; // It will stay at 1 if the day of last adjustment is the same as the checking accuracy

                if(!sameDay(last,LocalDateTime.now())){
                    days = (int) last.until(nowDay,ChronoUnit.DAYS);
                }

                // avoiding negative value
                days = Math.abs(days);

                // Round the double seconds to 2 decimals
                String deviationPerDay = decimalFormat.format(Double.parseDouble(diff)/days).replace(",",".");
                System.out.println(Visual.color1 +"The last adjustment was in "+Visual.color2+last.format(Manager.LocalDateTimef)+Visual.color1 +" "+getDaysAgo(last) +
                        ". That's a round "+Visual.color2+deviationPerDay+Visual.color1 +" seconds per day."+Visual.END);

                // Record a log on the watch with the seconds deviation per day.
                w.addLog(LocalDateTime.now(),diff+" seconds deviation. A round "+deviationPerDay+"s per day.");
            }
            else{
                // Without the deviation per day
                w.addLog(LocalDateTime.now(),diff+" seconds deviation.");
            }
            saveWatches(); // important to save the watches to keep the log updated
        }
    }

    public void adjustWatch(String id){
        Watch w = getWatch(id);
        if(w != null){
            
            System.out.println(Visual.color1 +"Write 'now' if it was adjusted now or write a date("+Visual.color2+"YYYY-MM-DD hh:mm:ss"+Visual.color1 +")"+Visual.END);

            String input = getInput(false).toLowerCase();
            // Simply put the lastAdjustment to today
            if(input.equals("now")){
                LocalDateTime now = LocalDateTime.now();
                w.setLastAdjust(now);
                w.addLog(now, "Adjusted.");
                Visual.success("Successfully adjusted the watch!");

            }
            else{
                try { // trying to parse the input to LocalDateTime;
                    LocalDateTime d = LocalDateTime.parse(input,LocalDateTimef);
                    if(d.isAfter(LocalDateTime.now())){
                        Visual.error("You can't put a date in the future...");
                    }
                    else{
                        w.setLastAdjust(d);
                        w.addLog(d,"Adjusted.");
                        Visual.success("Successfully adjusted the watch!");
                    }

                }
                catch(Exception ex){
                    Visual.error();
                }
            }
            saveWatches();
        }
    }

    // simple method to show the watch list
    public void showWatches(){
        Visual.header();
        Visual.line();
        if(watches.isEmpty()){
            System.out.println("No saved watches");
        }
        for(int i = 0; i<watches.size(); i++){
            System.out.println("  "+i+" "+Visual.PIPE+watches.get(i));
        }
        Visual.line();
    }

    // just returns the user input
    public static String getInput(boolean cont){
        System.out.print(cont ? "\nPress enter..." : Visual.color1 +"> "+Visual.color2);
        String input = sc.nextLine().trim();
        System.out.print(Visual.END);
        return input;
    }

    public static String getDaysAgo(LocalDateTime date){
        long diff = date.until(LocalDateTime.now(), ChronoUnit.DAYS);
        if(diff == 0){
            return "(today)";
        }
        return diff == 1 ? "("+diff+" day ago)" : "("+diff+" days ago)";
    }

    public void modifyWatch(String id){
        Watch w = getWatch(id);
        if(w != null){
            
            Visual.shortHeader();
            Visual.line();
            System.out.println(w.shortString());
            System.out.println(Visual.color1 +"Write all the changes in order separated with a "+Visual.AT+", if you want to maintain a field unchanged, write an '*'"+Visual.END);
            String result[] = getInput(false).split("@");
            int status = w.modifyData(result);
            if(status == 0){
                saveWatches();
                Visual.success("Watch data successfully changed!");
            }
        }
    }

    public void showWatchHistory(String id){
        String input = "";
        Watch w = getWatch(id);
        if(w != null){
            // if this returns 0 means that the log is not empty
            int r = w.showHistory();
            Visual.logMenu();
            input = getInput(false);
            switch(input) {
                case "1" -> this.removeAllLogs(w);
                case "2" -> {
                    int status = w.removeLastEntry();
                    if (status == 0) {
                        saveWatches();
                        Visual.success("Successfully removed the last entry");

                    } else {
                        Visual.error("Something wrong happened");
                    }
                    getInput(true);
                }
                case "3" -> {}
                default -> Visual.error();
            }
        }
    }

    // A method to access securely to the arraylist avoiding possible exceptions like numberformatexception or indexoutofbounds
    public Watch getWatch(String i){
        Watch w = null;
        int id = -1;
        try{
            id = Integer.valueOf(i);
            w = watches.get(id);
        }
        catch (NumberFormatException | IndexOutOfBoundsException ex){
            Visual.error("Invalid watch ID, please check the watchlist.");
        }

        return w;
    }

    public void removeWatch(String i){
        Watch w = getWatch(i);
        if(w!= null){
            System.out.println(Visual.color1 +"Are you sure that you want to delete this watch? [Y/n]:"+Visual.END);
            System.out.println(w.toString());
            String choice = getInput(false).toLowerCase();
            if(choice.equals("y")){
                watches.remove(w);
                saveWatches();
                Visual.success("Watch removed...");
            }
            else{
                Visual.error("Watch not removed.");
            }
        }
    }

    public void changeColors(){
        System.out.println("Select the color1 and color2 for the program:\n" +
                Visual.RED+"RED "+Visual.GREEN+"GREEN"+Visual.YELLOW+" YELLOW"+Visual.BLUE+" BLUE"+Visual.CYAN+" CYAN"+Visual.PURPLE+" PURPLE"+Visual.END);
        String choice = getInput(false).toUpperCase();
        int status = Visual.updateColors(choice);

        if(status == -1){
            Visual.error();
        }
        else{
            this.colors = choice;
            saveColors();
            Visual.success("Successfully changed the colors!");
        }
    }


    public void removeAllLogs(Watch w){
        System.out.println(Visual.color1 +"You sure [Y/n]"+Visual.END);
        String input = getInput(false).toLowerCase();
        if(input.equals("y")){
            w.clearLog();
            saveWatches();
            Visual.success("Watch logs removed.");
            getInput(true);
        }
    }

    public void seeFullWatch(String watchId){
        Watch w = getWatch(watchId);
        if(w != null){
            Visual.fullHeader();
            Visual.fullLine();
            System.out.printf(w.fullString()+"\n");
        }

    }

    private boolean sameDay(LocalDateTime d1, LocalDateTime d2){
        return d1.getDayOfYear() == d2.getDayOfYear() && d1.getYear() == d2.getYear();
    }
}
