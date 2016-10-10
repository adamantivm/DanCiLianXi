package com.ekumen.dancilianxi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ekumen.dancilianxi.domain.Vocabulary;

public class QuizAnswerReceiver extends BroadcastReceiver {
    private static final String TAG = QuizAnswerReceiver.class.getSimpleName();

    Vocabulary mVocabulary;

    public QuizAnswerReceiver() {
        mVocabulary = Vocabulary.getVocabulary();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: " + intent);
        int entryId = intent.getIntExtra(MainActivity.EXTRA_ENTRY_ID, -1);
        if (entryId == -1) {
            Log.e(TAG, "Missing entry ID on notification");
            return;
        }

        switch(intent.getIntExtra(MainActivity.EXTRA_RESPONSE, 0)) {
            case MainActivity.RESPONSE_YES:
                mVocabulary.setSuccess(entryId);
                break;
            case MainActivity.RESPONSE_NO:
                mVocabulary.setFailure(entryId);
                break;
            default:
                Log.e(TAG, "Unrecognized or missing answer value on notification.");
                return;
        }
    }
}
