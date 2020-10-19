package bojan.uljarevic.calendarapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    RadioButton godinaRadioButton;
    RadioButton mjesecRadioButton;
    //RadioButton danRadioButton ;
    CalendarView calendar;
    LinearLayout godinaLayout;
    Button yearButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        godinaRadioButton = findViewById(R.id.godina);
        mjesecRadioButton = findViewById(R.id.mjesec);
        mjesecRadioButton.setChecked(true);
        //danRadioButton = findViewById(R.id.dan);

        godinaLayout = findViewById(R.id.godinaDisplay);
        godinaLayout.setVisibility(View.INVISIBLE);

        calendar = findViewById(R.id.Calendar);
        calendar.setVisibility(View.INVISIBLE);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                godinaLayout.setVisibility(View.INVISIBLE);
                calendar.setVisibility(View.INVISIBLE);
                yearButton.setVisibility(View.INVISIBLE);
                int monthUnbugged = month + 1;
                Intent intent = new Intent(MainActivity.this, DayActivity.class);
                intent.putExtra(getString(R.string.Date), dayOfMonth + "." + monthUnbugged + "." + year + ".");
                startActivity(intent);
            }
        });

        yearButton = findViewById(R.id.yearButton);
        yearButton.setVisibility(View.VISIBLE);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String selectedDate = sdf.format(new Date(calendar.getDate()));
        String[] dateParts = selectedDate.split("/");
        yearButton.setText(dateParts[2]);

        //yearButton.setText();

        godinaRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                godinaLayout.setVisibility(View.VISIBLE);
                calendar.setVisibility(View.INVISIBLE);
                yearButton.setVisibility(View.INVISIBLE);
            }
        });

        mjesecRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                godinaLayout.setVisibility(View.INVISIBLE);
                calendar.setVisibility(View.VISIBLE);
                yearButton.setVisibility(View.VISIBLE);
            }
        });

        /*danRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                godinaLayout.setVisibility(View.INVISIBLE);
                calendar.setVisibility(View.INVISIBLE);
                yearButton.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(MainActivity.this, DayActivity.class);
                startActivity(intent);
            }
        });*/

        yearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                godinaLayout.setVisibility(View.VISIBLE);
                calendar.setVisibility(View.INVISIBLE);
                yearButton.setVisibility(View.INVISIBLE);
                mjesecRadioButton.toggle();
                godinaRadioButton.toggle();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        calendar.setVisibility(View.VISIBLE);
        yearButton.setVisibility(View.VISIBLE);
    }
}
