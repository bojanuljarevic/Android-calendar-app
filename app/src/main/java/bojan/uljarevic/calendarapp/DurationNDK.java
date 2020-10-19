package bojan.uljarevic.calendarapp;

public class DurationNDK {

    static {
        System.loadLibrary("DurationLib");
    }

    public native int DurationCalculate(String t1, String t2);

}
