package com.example.karim.troupia;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import jp.shts.android.storiesprogressview.StoriesProgressView;

public class Stroies extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    StoriesProgressView storiesProgressView;
    ImageView image;
    int count = 0;
    private static final int PROGRESS_COUNT = 3;
    private final long[] durations = new long[]{
            500L, 1000L, 1500L, 4000L, 5000L, 1000,
    };
    int[] resuorce = new int[]{
            R.drawable.bk1,
            R.drawable.bk2,
            R.drawable.bk2
    };
    long pressTime = 0L;
    long limit = 500L;

    @Override
    protected void onDestroy() {
        storiesProgressView.destroy();
        finish();
        super.onDestroy();
    }

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pressTime = System.currentTimeMillis();
                    storiesProgressView.pause();
                    return false;
                case MotionEvent.ACTION_UP:
                    long now = System.currentTimeMillis();
                    storiesProgressView.resume();
                    return limit < now - pressTime;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stroies);
        storiesProgressView = (StoriesProgressView) findViewById(R.id.stories);
        storiesProgressView.setStoriesCount(PROGRESS_COUNT);
        storiesProgressView.setStoryDuration(3000L);
        // or
        // storiesProgressView.setStoriesCountWithDurations(durations);
        storiesProgressView.setStoriesListener(this);
        storiesProgressView.startStories();

        image = (ImageView) findViewById(R.id.image);
        image.setImageResource(resuorce[count]);

        // bind reverse view
        View reverse = findViewById(R.id.reverse);
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.reverse();
            }
        });
        reverse.setOnTouchListener(onTouchListener);

        // bind skip view
        View skip = findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.skip();
            }
        });
        skip.setOnTouchListener(onTouchListener);
    }
    @Override
    public void onNext() {
        image.setImageResource(resuorce[++count]);
    }

    @Override
    public void onPrev() {
        if ((count - 1) < 0) return;
        image.setImageResource(resuorce[--count]);
    }

    @Override
    public void onComplete() {
        finish();
    }
}