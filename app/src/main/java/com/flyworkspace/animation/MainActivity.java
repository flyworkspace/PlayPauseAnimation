package com.flyworkspace.animation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private PlayPauseDrawable playPauseDrawable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        playPauseDrawable = new PlayPauseDrawable(PlayPauseDrawable.STATE_PLAYING);
        ImageView imageView = (ImageView) findViewById(R.id.image_button);
        imageView.setImageDrawable(playPauseDrawable);
    }

    public void viewClick(View view) {
        if (playPauseDrawable.getCurrentStatus() == PlayPauseDrawable.STATE_PLAYING) {
            playPauseDrawable.setPause(true);
        } else {
            playPauseDrawable.setPlay(true);
        }
    }
}
