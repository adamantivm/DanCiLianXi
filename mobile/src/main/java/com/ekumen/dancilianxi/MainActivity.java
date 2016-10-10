package com.ekumen.dancilianxi;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.ekumen.dancilianxi.domain.Vocabulary;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    // TODO(adamantivm) Don't duplicate this String here and in AndroidManifest.xml, use resources.
    public static final String ACTION_QUIZ_ANSWER = "com.ekumen.dancilianxi.QUIZ_ANSWER";

    public static final String EXTRA_ENTRY_ID = "ENTRY_ID";
    public static final String EXTRA_RESPONSE = "RESPONSE";
    public static final int RESPONSE_YES = 111;
    public static final int RESPONSE_NO = 222;

    Vocabulary mVocabulary;

    NotificationCompat.Builder mBuilder;
    Random mRandom = new Random(System.currentTimeMillis());
    Timer mTimer = new Timer();

    Intent mIntentOpen;

    class QuizTimerTask extends TimerTask {
        @Override
        public void run() {
            sendQuizNotification();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mVocabulary = Vocabulary.getVocabulary();

        Intent received = getIntent();
        Log.d(TAG, "intent: " + received);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendQuizNotification();
            }
        });

        prepareNotifications();
        prepareTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        cleanupTimer();
    }

    private void cleanupTimer() {
        mTimer.cancel();
    }

    private void prepareTimer() {
        //mTimer.schedule(new QuizTimerTask(), 3000, 20000);
    }

    private void prepareNotifications() {
        mIntentOpen = new Intent(this, this.getClass());
        mIntentOpen.setAction("OPEN");

    }

    private void sendQuizNotification() {
        // Choose a word from the vocabulary trainer.
        Vocabulary.Entry entry = mVocabulary.getRandomWord();

        // Create 'yes' and 'no' notifications that reference the ID
        Intent intentYes = new Intent(ACTION_QUIZ_ANSWER);
        intentYes.putExtra(EXTRA_ENTRY_ID, entry.id);
        intentYes.putExtra(EXTRA_RESPONSE, RESPONSE_YES);
        NotificationCompat.Action yesAction = new NotificationCompat.Action.Builder(
                R.drawable.cast_ic_notification_play, getString(R.string.action_yes),
                PendingIntent.getBroadcast(this, 1, intentYes, 0)).build();

        Intent intentNo = new Intent(ACTION_QUIZ_ANSWER);
        intentNo.putExtra(EXTRA_ENTRY_ID, entry.id);
        intentNo.putExtra(EXTRA_RESPONSE, RESPONSE_NO);
        NotificationCompat.Action noAction = new NotificationCompat.Action.Builder(
                R.drawable.cast_ic_notification_stop_live_stream, getString(R.string.action_no),
                PendingIntent.getBroadcast(this, 2, intentNo, 0)).build();

        // Compose the notification body text so that it requires scrolling to see the answer.
        String quizText = "\n";
        int quizId = mRandom.nextInt();
        if( Math.random() * 2 > 1) {
            // Guess translation
            quizText += "Significado";
            quizText += "\n\n\n\n\n";
            quizText += entry.spanish;

        } else {
            // Guess pinyin
            quizText += "Pinyin";
            quizText += "\n\n\n\n\n";
            quizText += entry.pinyin;
        }
        // Add a random text to the quiz message to force re-display on the watch.
        quizText += "\n\n(" + quizId + ")";

        // Build the notification and dispatch it.
        mBuilder = new NotificationCompat.Builder(this)
                // TODO(adamantivm) Notification icon
                .setSmallIcon(R.drawable.ic_cast_light)
                // We set the hanzi in the title.
                .setContentTitle(entry.hanzi)
                .setContentText(quizText)
                .setContentIntent(PendingIntent.getActivity(
                        this, -1, mIntentOpen, PendingIntent.FLAG_UPDATE_CURRENT))
                .addAction(noAction).addAction(yesAction)
                .extend(new NotificationCompat.WearableExtender().
                        addAction(noAction).addAction(yesAction));

        NotificationManagerCompat nmc = NotificationManagerCompat.from(this);
        nmc.notify(quizId, mBuilder.build());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "Intent received: " + intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
