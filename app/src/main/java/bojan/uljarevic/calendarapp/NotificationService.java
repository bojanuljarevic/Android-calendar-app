package bojan.uljarevic.calendarapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;


public class NotificationService extends Service {

    private static final String LOG_TAG = "ExampleService";
    private static final long PERIOD = 10000L;

    private NotifThread mThread;
    private NotifRunnable mRunnable;

    private String nsName = "";
    private String nsDate = "";
    NotificationManager mNotifyMgr;
    NotificationCompat.Builder mBuilder;
    Intent resultIntent;
    PendingIntent resultPendingIntent;
    int id = 0;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        nsName = intent.getStringExtra("name");
        nsDate = intent.getStringExtra("date");

        mBuilder = new NotificationCompat.Builder(NotificationService.this)
                .setSmallIcon(R.drawable.alert)
                .setContentTitle(nsName)
                .setContentText(nsDate)
                .setAutoCancel(true);

        resultIntent = new Intent(NotificationService.this, EventActivity.class);
        resultIntent.putExtra(getString(R.string.Date), nsDate);
        resultIntent.putExtra(getString(R.string.eventTag), nsName);

        // Because clicking the notification opens a new ("special") activity, there's
        resultPendingIntent =
                PendingIntent.getActivity(
                        NotificationService.this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        mNotifyMgr = getSystemService(NotificationManager.class);

        mThread = new NotifThread();
        mThread.start();

        mRunnable = new NotifRunnable();
        mRunnable.start();

        Log.d(LOG_TAG, "Service " + String.valueOf(nsName) + " activated.");

        return flags;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mThread.exit();
        mRunnable.stop();

        Log.d(LOG_TAG, "Service " + String.valueOf(nsName) + " stopped.");

        mThread.interrupt();
        mThread = null;

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class NotifThread extends Thread {
        private boolean mRun = true;

        @Override
        public synchronized void start() {
            mRun = true;
            super.start();
        }

        public synchronized void exit() {
            mRun = false;
        }

        @Override
        public void run() {
            while(mRun) {
                try {
                    Thread.sleep(PERIOD); //milliseconds
                    Log.d(LOG_TAG, String.valueOf(nsName) + " notification.");
                    mNotifyMgr.notify(id, mBuilder.build());
                    id++;
                } catch (InterruptedException e) {
                    // interrupted finish thread
                }

            }
        }
    }

    private class NotifRunnable implements Runnable {
        private Handler mHandler;
        private boolean mRun = false;

        public NotifRunnable() {
            mHandler = new Handler(getMainLooper());
        }

        public void start() {
            mRun = true;
            mHandler.postDelayed(this, PERIOD);
        }

        public void stop() {
            mRun = false;
            mHandler.removeCallbacks(this);
        }

        @Override
        public void run() {
            if (!mRun) {
                return;
            }

            mHandler.postDelayed(this, PERIOD);
        }
    }



}
