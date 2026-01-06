package dev.dimitrov;

import java.io.*;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.*;

public class Manager {
    public static Scanner sc = new Scanner(System.in);
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
    public static DateTimeFormatter LocalDateTimef = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    ArrayList<Watch> watches;
    private DecimalFormat decimalFormat;
    public static String f = "watches.json";
    private Properties prop;
    
    private static String fprop = "settings.properties";
    public Manager(){
        loadProperties();

        decimalFormat = new DecimalFormat("#.##");
        int status = loadWatchesJson();
        if(status == -1){
            watches = new ArrayList<>();

        }
    }  

    private void loadProperties(){
        Properties prop = new Properties();
        try {
            prop.load(new FileReader(fprop));
            
            updateColors();
        } catch (IOException e) {
            System.err.println("Ocurrió un error mientras se cargaban las propiedas");
            prop = null;
            e.printStackTrace();

        }
    }

    private void saveProperties(){
        try {
            prop.store(new FileWriter(fprop), null);
            updateColors();
        } catch (IOException e) {
            System.err.println("Hubo un error mientras se guardaban los ajustes");
            e.printStackTrace();
        }
    }

    // loads the list of watches and returns -1 if it fails.
    public int loadWatchesBin(){
        try(ObjectInputStream o = new ObjectInputStream(new FileInputStream(f))){
            watches = (ArrayList<Watch>) o.readObject();
        }
        catch(ClassNotFoundException | IOException ex){
            ex.printStackTrace();
            return -1;
        }
        return 0;
    }

    // Save the list of watches
    public void saveWatchesBin(){
        try(ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(f))){
            o.writeObject(watches);
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    }

    public void updateColors(){
        if(prop != null){
            Visual.updateColors(prop.getProperty("color1")+"-"+prop.getProperty("color2"));
        }
        else{
            System.err.println("No properties file found. Creating one...\n");
            setColors(new String[]{"PURPLE","CYAN"});
            saveProperties();
            loadProperties();
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
                    seeFullWatch(split[1]);
                } else {
                    Visual.error();
                }
            }
            case "7" -> {
                if (split.length == 2) {
                    Visual.clear();
                    removeWatch(split[1]);
                } else {
                    Visual.error();
                }
            }
            case "8" -> {Visual.clear();changeColors();}
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
            this.saveWatchesJson();
            System.out.println(Visual.GREEN+"Successfully added!"+Visual.END);
        }
    }


    public void checkAccuracy(String id){
        Watch w = getWatch(id); // get the specified watch
        if(w != null){
            System.out.println("Checking "+w.getName());
            LocalDateTime last = w.getLastAdjust(); // The last adjustment
            LocalDateTime nowDay = LocalDateTime.now();
            LocalTime now = LocalTime.now();
            now = now.plusMinutes(1); // The actual time with one minute more
            now = now.minusSeconds(now.getSecond()); // Remove the seconds, to do properly the dif later
            Visual.ask4Time(now.format(formatter)+":00");
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
            saveWatchesJson(); // important to save the watches to keep the log updated
        }
    }

    public void adjustWatch(String id){
        Watch w = getWatch(id);
        if(w != null){
            System.out.println("Adjusting "+w.getName());
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
            saveWatchesJson();
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
                saveWatchesJson();
                Visual.success("Watch data successfully changed!");
            }
        }
    }

    public void showWatchHistory(String id){
        String input = "";
        Watch w = getWatch(id);
        if(w != null){
            // if this returns 0 means that the log is not empty

            System.out.println("Showing logs of "+w.getName());
            w.showHistory();
            Visual.logMenu();
            input = getInput(false);
            switch(input) {
                case "1" -> this.removeAllLogs(w);
                case "2" -> {
                    int status = w.removeLastEntry();
                    if (status == 0) {
                        saveWatchesJson();
                        Visual.success("Successfully removed the last entry");

                    } else {
                        Visual.error("Something wrong happened. Maybe removing when It's already empty?");
                    }
                    getInput(true);
                }
                case "3" -> {} // Empty for going to the main menu
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
                saveWatchesJson();
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
            setColors(choice.split("-"));
            Visual.success("Successfully changed the colors!");
        }
    }

    private void setColors(String[] colors){
        prop.setProperty("color1", colors[0]);
        prop.setProperty("color2", colors[1]);
        saveProperties();;
    }

    public void removeAllLogs(Watch w){
        System.out.println(Visual.color1 +"You sure [Y/n]"+Visual.END);
        String input = getInput(false).toLowerCase();
        if(input.equals("y")){
            w.clearLog();
            saveWatchesJson();
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
            Visual.fullLine();
        }

    }

    private boolean sameDay(LocalDateTime d1, LocalDateTime d2){
        return d1.getDayOfYear() == d2.getDayOfYear() && d1.getYear() == d2.getYear();
    }

    public void saveWatchesJson(){
        ObjectNode root = null;
        ArrayNode w = null;
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        try {
            root = mapper.createObjectNode();

            w = mapper.valueToTree(watches);
            System.out.println(w.size());
            root.set("watches",w);
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(f), root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int loadWatchesJson(){
        int status = 0;
        ObjectNode root = null;
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);    

        try {
            root = (ObjectNode) mapper.readTree(new File(f));
            ArrayNode w = (ArrayNode) root.get("watches");
            watches = new ArrayList<>(mapper.convertValue(w, new TypeReference<List<Watch>>(){}));
        } catch (IOException e) {
            status = -1;
            e.printStackTrace();
        }
        return status;
    }
}
