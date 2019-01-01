package com.cb.carbonbank;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    private static final String prefName = "AuthenticatedUser";
    private SharedPreferences sharedPreferences;
    private ImageView drawerProfilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Check Whether is Authenticated
        sharedPreferences = getSharedPreferences(prefName,MODE_PRIVATE);
        boolean authenticated = sharedPreferences.getBoolean("authenticated",false);
        if(!authenticated){
            finish();
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
        }

        mDrawerLayout = findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //For the drawer toggle on left hand side to operate
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id){
            case R.id.logout:
//                sharedPreferences = getSharedPreferences(prefName,MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                boolean unauthen = false;
//                editor.putBoolean("authenticated",unauthen);
//                editor.commit();
                Intent intent = new Intent(this,LoginActivity.class);
                startActivity(intent);
                break;
        }
        return false;
    }

}
