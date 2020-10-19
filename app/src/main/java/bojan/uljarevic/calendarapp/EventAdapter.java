package bojan.uljarevic.calendarapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class EventAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Event> mEvents;

    SharedPreferences.Editor editor;

    public EventAdapter(Context context) {
        mContext = context;
        mEvents = new ArrayList<Event>();
    }

    public void addEvent(Event event) {
        mEvents.add(event);
        notifyDataSetChanged();
    }

    public void remove(Event event) {
        mEvents.remove(event);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() { return mEvents.size(); }

    @Override
    public Object getItem(int position) {
        Object retVal = null;
        try {
            retVal = mEvents.get(position);
        } catch(IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return retVal;
    }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if(view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.event_row, null);
            ViewHolder holder = new ViewHolder();
            holder.image = (ImageView) view.findViewById(R.id.eventImage);
            holder.name = (TextView) view.findViewById(R.id.eventText);
            holder.box = view.findViewById(R.id.eventBox);
            holder.image.setImageResource(R.drawable.alert);
            holder.duration = view.findViewById(R.id.duration);
            view.setTag(holder);
        }

        final Event event = (Event )getItem(position);
        final ViewHolder holder = (ViewHolder) view.getTag();
        holder.name.setText(event.mText);
        String dur = null;
        if(event.mDuration == 0) {
            dur = "NA";
        } else if (event.mDuration < 60) {
            dur = event.mDuration + " min";
        } else {
            dur =  (event.mDuration / 60) + " h " + (event.mDuration % 60) + " min";
        }
        holder.duration.setText(dur);

        if(event.getReminder() == true) holder.image.setImageResource(R.drawable.alert2);
        if(event.getReminder() == false) holder.image.setImageResource(R.drawable.alert);

        SharedPreferences sharedPrefs = view.getContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
        editor = sharedPrefs.edit();

        holder.box.setChecked(sharedPrefs.getBoolean("CheckValue"+position, false));
        final int pos = position;

        if(holder.box.isChecked()) holder.name.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        else holder.name.setPaintFlags(0);

        holder.box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor.putBoolean("CheckValue"+pos, holder.box.isChecked());
                editor.commit();

                final Intent intent = new Intent(mContext.getApplicationContext(), NotificationService.class);
                intent.putExtra("name", event.getName());
                intent.putExtra("date", event.getDate());

                if(holder.box.isChecked()) {
                    holder.name.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);

                    CharSequence text = event.getName() + " " + mContext.getString(R.string.disableNotifications);
                    Toast toast = Toast.makeText(v.getContext(), text, Toast.LENGTH_SHORT);
                    toast.show();

                    mContext.stopService(intent);

                } else {
                    holder.name.setPaintFlags(0);

                }
            }
        });

        return view;
    }

    private class ViewHolder {
        public ImageView image = null;
        public TextView name = null;
        public CheckBox box = null;
        public TextView duration = null;
    }


    public void update(Event[] events) {
        mEvents.clear();
        if(events != null) {
            for(Event event : events) {
                addEvent(event);
            }
        }

    }


}
