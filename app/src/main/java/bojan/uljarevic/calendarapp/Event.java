package bojan.uljarevic.calendarapp;


public class Event {

    public boolean mCheck;
    public String mText;
    public String mDate;
    public String mLoc;
    public String mTime;
    public String mTime2;
    public int mDuration;

    public Event(String text) {
        mText = text;
    }

    public Event(String date, String name, String time, String loc, boolean reminder, String time2, int duration) {
        mDate = date;
        mText = name;
        mTime = time;
        mLoc = loc;
        mCheck = reminder;
        mTime2 = time2;
        mDuration = duration;
    }

    public String getTime() {
        return mTime;
    }

    public String getName() {
        return mText;
    }

    public String getLoc() {
        return mLoc;
    }

    public String getDate() {
        return mDate;
    }

    public boolean getReminder() {
        return mCheck;
    }

    public String getTime2() { return mTime2; }

    public int getmDuration() { return mDuration; }
}
