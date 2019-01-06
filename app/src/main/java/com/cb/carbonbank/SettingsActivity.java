package com.cb.carbonbank;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class SettingsActivity extends AppCompatActivity {

    private ConstraintLayout signOutPress;
    private ConstraintLayout securityPress;
    private ConstraintLayout themePress;

    private Switch themeSwitch;

    private static final String prefName = "AuthenticatedUser";
    private SharedPreferences sharedPreferences;
    private final int strokeSize = 10;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        themeSwitch = findViewById(R.id.switchChangeTheme);

        themePress = findViewById(R.id.themePress);
        final GradientDrawable themePressBackground = (GradientDrawable) themePress.getBackground();
        themePress.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    themePressBackground.setStroke(strokeSize, getResources().getColor(R.color.colorPrimary));
                    themePressBackground.setColor(getResources().getColor(R.color.textFieldBg));
                }else{
                    themePressBackground.setStroke(0, getResources().getColor(R.color.colorPrimary));
                    themePressBackground.setColor(getResources().getColor(R.color.colorAccent));
                }
                return false;
            }
        });

        themeSwitch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    themePressBackground.setStroke(strokeSize, getResources().getColor(R.color.colorPrimary));
                    themePressBackground.setColor(getResources().getColor(R.color.textFieldBg));
                }else{
                    themePressBackground.setStroke(0, getResources().getColor(R.color.colorPrimary));
                    themePressBackground.setColor(getResources().getColor(R.color.colorAccent));
                }
                return false;
            }
        });

        securityPress = findViewById(R.id.securityPress);
        final GradientDrawable securityPressBackground = (GradientDrawable) securityPress.getBackground();
        securityPress.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    securityPressBackground.setStroke(strokeSize, getResources().getColor(R.color.colorPrimary));
                    securityPressBackground.setColor(getResources().getColor(R.color.textFieldBg));
                }else{
                    securityPressBackground.setStroke(0, getResources().getColor(R.color.colorPrimary));
                    securityPressBackground.setColor(getResources().getColor(R.color.colorAccent));
                }
                return false;
            }
        });

        signOutPress = findViewById(R.id.signOutPress);
        final GradientDrawable signOutPressBackground = (GradientDrawable) signOutPress.getBackground();
        signOutPress.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    signOutPressBackground.setStroke(strokeSize, getResources().getColor(R.color.colorPrimary));
                    signOutPressBackground.setColor(getResources().getColor(R.color.textFieldBg));
                }else{
                    signOutPressBackground.setStroke(0, getResources().getColor(R.color.colorPrimary));
                    signOutPressBackground.setColor(getResources().getColor(R.color.colorAccent));
                }
                return false;
            }
        });

        //TODO: PUT COMPANY ACCESS HERE
        themePress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:SWITCH
            }
        });

        securityPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: SECURITY
            }
        });

        signOutPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder quitAlert = new AlertDialog.Builder(SettingsActivity.this);
                quitAlert.setMessage("Are you sure you want to sign out?").setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sharedPreferences = getSharedPreferences(prefName,MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                boolean unauthen = false;
                                editor.putBoolean("authenticated",unauthen);
                                editor.commit();
                                signOutFunction();
                                overridePendingTransition(R.anim.slide_in_down,R.anim.slide_out_up);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                quitAlert.setIcon(R.drawable.ic_exit_to_app_black_24dp);

                AlertDialog alert = quitAlert.create();
                alert.setTitle("Sign Out");
                alert.show();
            }
        });
    }

    private void signOutFunction(){
        Toast.makeText(this, "Sign Out Successfully", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }


}
