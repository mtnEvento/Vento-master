package com.mtn.evento.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.florent37.viewanimator.AnimationListener;
import com.github.florent37.viewanimator.ViewAnimator;
import com.mtn.evento.R;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        final ImageView image = (ImageView) findViewById(R.id.mtn_logo);
        final TextView eventoText = (TextView) findViewById(R.id.eventoText);

        ViewAnimator.animate(image)
                .bounce().duration(1500).interpolator(new BounceInterpolator())
                .onStop(new AnimationListener.Stop() {
                    @Override
                    public void onStop() {
                        ViewAnimator.animate(eventoText).textColor(Color.BLUE,Color.GREEN,Color.BLACK).interpolator(new LinearInterpolator())
                                .shake().duration(1000).interpolator(new LinearInterpolator())
                                .onStop(new AnimationListener.Stop() {
                                    @Override
                                    public void onStop() {
                                        ViewAnimator.animate(image).bounce().duration(1500).interpolator(new BounceInterpolator())
                                                .onStop(new AnimationListener.Stop() {
                                                    @Override
                                                    public void onStop() {
                                                        Intent intent = new Intent(SplashScreenActivity.this,HomeScreenActivity.class);
                                                        startActivity(intent);
                                                    }
                                                }).start();
                                    }
                                }).start();
                    }
                }).start();

    }
}
