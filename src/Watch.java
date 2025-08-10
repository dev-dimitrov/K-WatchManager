import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

public class Watch implements Serializable {
    private String caliber;
    private String brand;
    private String model;
    private String theoreticAccuracy;
    private String type;
    private LocalDate lastAdjust;
    private TreeMap<LocalDate,String> log;
    private String moreInfo;
    private static final long serialVersionUID = -4126365443326262519L;
    public Watch(String b, String m, String ty, String c, String t, String mi){
        brand = b.toUpperCase();
        model = m;
        type = ty.toUpperCase();
        caliber = c;
        theoreticAccuracy = t;
        moreInfo = mi;

        lastAdjust = null;
        log = new TreeMap<>();
    }

    public void setBrand(String b){
        brand = b.toUpperCase();
    }

    public LocalDate getLastAdjust(){
        return lastAdjust;
    }

    public void setLastAdjust(LocalDate date){
        lastAdjust = date;
    }

    public void setModel(String m){
        model = m.toUpperCase();
    }

    @Override
    public String toString(){
        String s = brand+Visual.PIPE+model+Visual.PIPE+type+Visual.PIPE+caliber+Visual.PIPE+theoreticAccuracy;

        return lastAdjust == null ? s+Visual.PIPE+"N/a"+Visual.PIPE+moreInfo : s+Visual.PIPE+lastAdjust+" "+Manager.getDaysAgo(lastAdjust)+Visual.PIPE+moreInfo;
    }

    public String shortString(){
        return brand+Visual.PIPE+model+Visual.PIPE+type+Visual.PIPE+caliber+Visual.PIPE+theoreticAccuracy+Visual.PIPE+moreInfo;
    }

    public static Watch makeWatch(String input){
        if(input.contains("*")){
            input = input.replace("*","Not specified");
        }
        String[] att = input.split("@");
        if(att.length != 6){
            Visual.error();
            return null;
        }
        return new Watch(att[0], att[1], att[2], att[3], att[4], att[5]);
    }

    public int modifyData(String data[]){
        /* BRAND | MODEL | MOVEMENT | CALIBER | THEORETIC ACCURACY | MORE INFO */
        if (data.length == 6){
            this.brand = data[0].equals("*") ? this.brand : data[0].toUpperCase();
            this.model = data[1].equals("*") ? this.model : data[1];
            this.type = data[2].equals("*") ? this.type : data[2].toUpperCase();
            this.caliber = data[3].equals("*") ? this.caliber : data[3];
            this.theoreticAccuracy = data[4].equals("*") ? this.theoreticAccuracy : data[4];
            this.moreInfo = data[5].equals("*") ? this.moreInfo : data[5];
        }
        else{
            Visual.error();
            return 1;
        }

        return 0;
    }

    public int showHistory(){
        int result = -1;
        System.out.println(Visual.CYAN+"+-----------------------------------+"+Visual.END);
        if(!log.isEmpty()){
            for(LocalDate date: log.keySet()){
                System.out.println(Visual.color1 +date+" -> "+log.get(date)+Visual.END);
            }
            result = 0;
        }
        else{
            System.out.println(Visual.color1 +"No logs recorded"+Visual.END);
            result = 1;
        }
        System.out.println(Visual.CYAN+"+-----------------------------------+"+Visual.END);
        return result;
    }

    public void addLog(LocalDate date, String action){
        log.put(date,action);
    }

    public void clearLog(){
        log.clear();
        lastAdjust = null;
    }

    public int removeLastEntry(){
        LocalDate k = log.lastKey();
        return log.remove(k) == null ? -1 : 0;
    }
}
