package com.ekumen.dancilianxi.domain;

import android.opengl.ETC1;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Julian Cerruti <jcerruti@creativa77.com> on 10/9/16.
 */

public class Vocabulary {
    private static final String TAG = Vocabulary.class.getSimpleName();
    private static Vocabulary sVocabulary = null;

    private static int sNextId = 0;

    public class Entry {
        public String hanzi;
        public String pinyin;
        public String spanish;
        public int id;

        public Entry(String hanzi, String pinyin, String spanish) {
            this.hanzi = hanzi;
            this.pinyin = pinyin;
            this.spanish = spanish;
            id = sNextId++;
        }
    }

    /**
     * Only count consecutive failures or successes (and total trials).
     * After three consecutive successess, consider it learned.
     */
    class Score {
        public int failures = 0;
        public int success = 0;
        public int tries = 0;
    }

    List<Entry> mEntries = new ArrayList<>();
    // Entries are moved to Learned after they are considered learned.
    List<Entry> mLearned = new ArrayList<>();
    Map<Entry, Score> mScores = new HashMap<>();

    public synchronized static Vocabulary getVocabulary() {
        if (sVocabulary == null) {
            sVocabulary = new Vocabulary();
        }
        return sVocabulary;
    }

    Vocabulary() {
        Log.d(TAG, "<new>");

        // TEMP: hardcoded vocabulary.
        loadHardcoded();
    }

    private void loadHardcoded() {
        addEntry(new Entry("如果", "rú guǒ", "Ejemplo"));
        addEntry(new Entry("发现", "fā xiàn", "Darse cuenta"));
        addEntry(new Entry("于是", "yú shì", "Por eso"));
        addEntry(new Entry("而且", "ér qiě", "Además"));
        addEntry(new Entry("陪", "péi", "Acompañar"));
    }

    private void addEntry(Entry entry) {
        mEntries.add(entry.id, entry);
    }

    public Entry getRandomWord() {
        if (mEntries.size() == 0) {
            Log.e(TAG, "No more words available!");
        }
        return mEntries.get((int)Math.floor(Math.random() * mEntries.size()));
    }

    public void setSuccess(int id) {
        Entry entry = mEntries.get(id);
        Score score = mScores.getOrDefault(entry, new Score());
        score.tries++;
        score.success++;
        score.failures = 0;
        if (score.success >= 3) {
            mScores.remove(entry);
            mLearned.add(entry);
        }
        mScores.put(entry, score);
        Log.d(TAG, "Set success. Now:  [" + entry.hanzi + ":" + score.success + "/" +
                score.failures + "]");
    }

    public void setFailure(int id) {
        Entry entry = mEntries.get(id);
        Score score = mScores.getOrDefault(entry, new Score());
        score.tries++;
        score.failures++;
        score.success = 0;
        mScores.put(entry, score);
        Log.d(TAG, "Set failure. Now:  [" + entry.hanzi + ":" + score.success + "/" +
                score.failures + "]");
    }
}
