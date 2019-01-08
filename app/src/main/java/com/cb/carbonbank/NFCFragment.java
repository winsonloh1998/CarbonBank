package com.cb.carbonbank;


import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import static android.content.Context.VIBRATOR_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class NFCFragment extends Fragment {

    private CountDownTimer countDownTimer;

    private TextView countDownNumber;
    private ImageView refreshButton;
    private ImageView nfcLogo;

    private long timeLeftInMilliseconds = 30000;
    private boolean timerRunning;

    public NFCFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_nfc, container, false);

        countDownNumber = view.findViewById(R.id.countDownNumber);
        refreshButton = view.findViewById(R.id.ivRefreshTime);
        nfcLogo = view.findViewById(R.id.nfcLogo);

        startTimer();
        // Inflate the layout for this fragment
        return view;
    }

    private void startTimer(){
        countDownTimer = new CountDownTimer(timeLeftInMilliseconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMilliseconds = millisUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish() {
                refreshButton.setImageResource(R.drawable.ic_refresh_blue_24dp);
                refreshButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        timeLeftInMilliseconds = 30000;
                        countDownTimer.cancel();
                        startTimer();
                        refreshButton.setImageResource(R.drawable.edit_profile_header);
                    }
                });
            }
        }.start();
    }

    private void updateTimer(){
        int seconds = (int) timeLeftInMilliseconds / 1000;
        countDownNumber.setText(String.format("%d",seconds));

        if(seconds <= 10){
            refreshButton.setImageResource(R.drawable.ic_refresh_blue_24dp);
            Animation animation = new AlphaAnimation(1, 0); //to change visibility from visible to invisible
            animation.setDuration(1000); //1 second duration for each animation cycle
            animation.setInterpolator(new LinearInterpolator());
            animation.setRepeatCount(Animation.INFINITE); //repeating indefinitely
            animation.setRepeatMode(Animation.REVERSE); //animation will start from end point once ended.
            refreshButton.startAnimation(animation); //to start animation
            goVibrate();

            refreshButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    timeLeftInMilliseconds = 30000;
                    refreshButton.setImageResource(R.drawable.edit_profile_header);
                    countDownTimer.cancel();
                    startTimer();
                }
            });
        }
    }

    private void goVibrate(){
        if (Build.VERSION.SDK_INT >= 26) {
            ((Vibrator) getLayoutInflater().getContext().getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            ((Vibrator) getLayoutInflater().getContext().getSystemService(VIBRATOR_SERVICE)).vibrate(150);
        }
    }

}
