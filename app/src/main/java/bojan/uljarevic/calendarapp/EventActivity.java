package bojan.uljarevic.calendarapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;

public class EventActivity extends AppCompatActivity {

    private TextView datum;
    private TextView eventName;
    private EditText insertLocation;
    private Button okButton;
    private ImageView weatherImg;
    private TextView tempTxt;
    private HttpHelper httpHelper;
    private Button saveButton;
    private String location;
    private EventDbHelper mDbHelper;
    private TimePicker time;
    private TimePicker time2;
    private CheckBox checkbox;
    private boolean reminder;
    private static DecimalFormat df = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        datum = findViewById(R.id.date);
        eventName = findViewById(R.id.eventName);
        insertLocation = findViewById(R.id.insertLocation);
        okButton = findViewById(R.id.okButton);
        tempTxt = findViewById(R.id.tempTxt);
        weatherImg = findViewById(R.id.weatherIcon);

        saveButton = findViewById(R.id.saveButton);
        checkbox = findViewById(R.id.check);

        Intent receivedIntent = getIntent();
        final String eventNameStr = receivedIntent.getStringExtra(getString(R.string.eventTag));
        final String dateText = receivedIntent.getStringExtra(getString(R.string.Date));
        datum.setText(dateText);
        eventName.setText(eventNameStr);

        httpHelper = new HttpHelper();

        mDbHelper = new EventDbHelper(this);
        time = findViewById(R.id.time);

        time2 = findViewById(R.id.time2);


        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(insertLocation.getText().toString())) {
                    insertLocation.setError(getString(R.string.error_empty_input));
                    return;
                }

                try {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                location = insertLocation.getText().toString();
                final String apiCall = getString(R.string.BASE_URL)  + location + getString(R.string.API_KEY);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {


                            JSONObject jsonObject = httpHelper.getJSONObjectFromURL(apiCall);

                            JSONObject jsonMain = jsonObject.getJSONObject("main");
                            float temp = getFloat("temp", jsonMain);
                            temp -= 273;
                            final String tempString = df.format(temp) + "°C";
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tempTxt.setText(tempString);
                                }
                            });


                            JSONArray weatherArr = jsonObject.getJSONArray("weather");
                            JSONObject weatherObj = weatherArr.getJSONObject(0);
                            final String weatherIconCall = getString(R.string.ICON_URL) + weatherObj.getString("icon") + ".png";
                            final Drawable icon;
                            InputStream is = (InputStream) new URL(weatherIconCall).getContent();
                            icon = Drawable.createFromStream(is, "src");

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    weatherImg.setImageDrawable(icon);
                                }
                            });

                        } catch (Exception e) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    weatherImg.setImageResource(R.drawable.error);
                                    tempTxt.setText(R.string.noLocation);
                                }
                            });

                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(location == null) {
                    Context context = getApplicationContext();
                    CharSequence text = getString(R.string.ToastInfo);
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                } else {

                    String timeString = time.getCurrentHour() + ":" + time.getCurrentMinute();
                    String timeString2 = time2.getCurrentHour() + ":" + time2.getCurrentMinute();
                    mDbHelper.deleteEvent(eventNameStr, dateText);

                    final Intent serviceIntent = new Intent(EventActivity.this.getApplicationContext(), NotificationService.class);
                    serviceIntent.putExtra(getString(R.string.name), eventNameStr);
                    serviceIntent.putExtra(getString(R.string.date), dateText);

                    if(checkbox.isChecked()) {
                        reminder = true;
                        startService(serviceIntent);
                    } else {
                        reminder = false;
                        stopService(serviceIntent);
                    }

                    DurationNDK myNdk = new DurationNDK();
                    int duration = myNdk.DurationCalculate(timeString, timeString2);

                    Event event = new Event(dateText, eventNameStr, timeString, location, reminder, timeString2, duration);

                    mDbHelper.insert(event);

                    // Zbog notifikacije
                    Intent intent = new Intent(EventActivity.this, DayActivity.class);
                    intent.putExtra(getString(R.string.Date), datum.getText());
                    startActivity(intent);
                }
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume(){
        super.onResume();
        Event e = mDbHelper.readEvent(eventName.getText().toString(), datum.getText().toString());
        if (e != null) {
            String[] s = e.getTime().split(":");
            time.setHour(Integer.parseInt(s[0]));
            time.setMinute(Integer.parseInt(s[1]));
            s =  e.getTime2().split(":");
            time2.setHour(Integer.parseInt(s[0]));
            time2.setMinute(Integer.parseInt(s[1]));
            insertLocation.setText(e.getLoc());
            location = e.getLoc();
            checkbox.setChecked(e.getReminder());
        }

    }

    private static float getFloat(String tagName, JSONObject jObj) throws JSONException {
        return (float) jObj.getDouble(tagName);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EventActivity.this, DayActivity.class);
        intent.putExtra(getString(R.string.Date), datum.getText());
        startActivity(intent);
    }



}
