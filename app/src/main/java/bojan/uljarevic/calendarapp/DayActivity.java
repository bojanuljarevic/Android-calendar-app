package bojan.uljarevic.calendarapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class DayActivity extends AppCompatActivity {

    private Button dodajButton;
    private EditText dodajEdit;
    private EventAdapter adapter;
    public AlertDialog.Builder builder;
    private TextView dateView;
    public String dateText;
    private EventDbHelper mDbHelper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);

        dodajButton = findViewById(R.id.dodajButton);
        dodajEdit = findViewById(R.id.dodajEdit);

        dateView = findViewById(R.id.date);
        Intent receivedIntent = getIntent();
        dateText = receivedIntent.getStringExtra(getString(R.string.Date));
        dateView.setText(dateText);

        adapter = new EventAdapter(this);

        ListView list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new MyOnItemClickListener());
        list.setOnItemLongClickListener(new MyOnItemLongClickListener());

        builder = new AlertDialog.Builder(this);

        mDbHelper = new EventDbHelper(this);


        dodajButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(dodajEdit.getText().toString()) ||  (mDbHelper.readEvent(dodajEdit.getText().toString(), dateText) != null &&
                        mDbHelper.readEvent(dodajEdit.getText().toString(), dateText).mDate == dateText)) {
                    dodajEdit.setError(getString(R.string.error_empty_input));
                    return;
                }
                adapter.addEvent(new Event(dodajEdit.getText().toString()));

                Intent sentIntent = new Intent(DayActivity.this, EventActivity.class);
                sentIntent.putExtra(getString(R.string.eventTag), dodajEdit.getText().toString());
                sentIntent.putExtra(getString(R.string.Date), dateText);
                dodajEdit.setText("");
                startActivity(sentIntent);

            }
        });

        //mDbHelper.DeleteDatabase(this);

    }


    @Override
    protected void onResume() {
        super.onResume();

        Event[] events = mDbHelper.readEvents(dateText);
        adapter.update(events);

    }



    private class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            Intent sentIntent = new Intent(DayActivity.this, EventActivity.class);
            Event e = (Event) adapter.getItem(position);
            sentIntent.putExtra(getString(R.string.eventTag), e.mText);
            sentIntent.putExtra(getString(R.string.Date), dateText);
            startActivity(sentIntent);
        }
    }

    private class MyOnItemLongClickListener implements AdapterView.OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
            final Event e = (Event) adapter.getItem(position);
            final int pos = position;

            builder = DayActivity.this.builder.setMessage(R.string.dialogMessage)
                    .setCancelable(true)
                    .setPositiveButton(R.string.izmijeni, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent sentIntent = new Intent(DayActivity.this, EventActivity.class);
                            Event e = (Event) adapter.getItem(pos);
                            sentIntent.putExtra(getString(R.string.eventTag), e.mText);
                            sentIntent.putExtra(getString(R.string.Date), dateText);
                            startActivity(sentIntent);
                        }
                    })
                    .setNegativeButton(R.string.obrisi, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            mDbHelper.deleteEvent(e.mText, dateText);
                            adapter.remove(e);
                        }
                    });
            AlertDialog alert = DayActivity.this.builder.create();
            alert.setTitle(e.mText);
            alert.show();

            return true;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DayActivity.this, MainActivity.class);
        startActivity(intent);
    }

}
