package com.cb.carbonbank;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.UnicodeSetSpanner;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Layout;
import android.util.Base64;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    private static final String prefName = "AuthenticatedUser";
    private SharedPreferences sharedPreferences;
    private static final String getCCPref = "getCC";
    private CircleImageView drawerProfilePic;

    private TextView tvDrawerDisplayName;
    private TextView tvDrawerEmail;

    //Variable Get User Information
    private static final String TAG = "getUserByUsername";
    private static List<Users> userList;
    private ProgressDialog pDialog;
    private static String GET_URL = "https://crocodilian-trade.000webhostapp.com/SelectUsers.php";
    RequestQueue queue;
    private boolean doubleBackToExitPressedOnce = false;
    private String authUser;

    private int countAlert = 0;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private Button btnView;
    private TextView content;

    public static int cc=0;
    public static int ct=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sharedPreferences = getSharedPreferences(prefName,MODE_PRIVATE);
        boolean authenticated = sharedPreferences.getBoolean("authenticated",false);

        if(!authenticated){
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.disable_slide,R.anim.disable_slide);
        }else {
            if (!isConnected()) {
                Toast.makeText(getApplicationContext(), "Network Service Not Available", Toast.LENGTH_LONG).show();
            }
            //Retrieve Information
            pDialog = new ProgressDialog(this);
            userList = new ArrayList<>();

            mDrawerLayout = findViewById(R.id.drawer);
            mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
            mDrawerLayout.addDrawerListener(mToggle);
            mToggle.syncState();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            mNavigationView = (NavigationView)findViewById(R.id.navView);
            mNavigationView.setNavigationItemSelectedListener(this);

            View headerView = mNavigationView.getHeaderView(0);
            tvDrawerDisplayName = (TextView) headerView.findViewById(R.id.drawerDisplayName);
            tvDrawerEmail = (TextView) headerView.findViewById(R.id.drawerEmail);
            drawerProfilePic = headerView.findViewById(R.id.drawerProfilePic);

            drawerProfilePic.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HomeActivity.this,ProfilePicActivity.class);
                    startActivity(intent);
                }
            });

            authUser = sharedPreferences.getString("authenticatedUser", "Anonymous");
            downloadUsers(getApplicationContext(), authUser);


        }



    }

    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    //For the drawer toggle on left hand side to operate
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }

        int id = item.getItemId();
        if (id == R.id.qr_scanner) {
            Intent intent = new Intent(this,QrScannerActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();

        if(id == R.id.home){

        }else if(id == R.id.cc){

        }else if(id == R.id.ct){

        }else if(id == R.id.scanQrCode){
            Intent intent = new Intent(this,QrScannerActivity.class);
            startActivity(intent);
        }else if(id == R.id.qrCode){
            //1. Create a Fragment Manager
            FragmentManager fragmentManager = getSupportFragmentManager();

            //2. Create a Fragment Transaction
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

            //3. Create an instance of a fragment
            ScanQrFragment fragment = new ScanQrFragment();

            //4. Perform fragment transaction
            fragmentTransaction.replace(R.id.fragment_container,fragment);

            //5. Commit a transaction
            fragmentTransaction.commit();
        }else if(id == R.id.nfc){
            //1. Create a Fragment Manager
            FragmentManager fragmentManager = getSupportFragmentManager();

            //2. Create a Fragment Transaction
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

            //3. Create an instance of a fragment
            NFCFragment fragment = new NFCFragment();

            //4. Perform fragment transaction
            fragmentTransaction.replace(R.id.fragment_container,fragment);

            //5. Commit a transaction
            fragmentTransaction.commit();
        }else if(id == R.id.setting){
            Intent intent = new Intent(HomeActivity.this,SettingsActivity.class);
            startActivity(intent);
        }else if(id == R.id.logout){
            AlertDialog.Builder quitAlert = new AlertDialog.Builder(HomeActivity.this);
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
        }else{

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    private void signOutFunction(){
        Toast.makeText(this, "Sign Out Successfully", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }

    public void downloadUsers(Context context, String username){
        // Instantiate the RequestQueue
        queue = Volley.newRequestQueue(context);
        String url = GET_URL + "?Username=" + username;

        if (!pDialog.isShowing())
            pDialog.setMessage("Sync With Server...");
        pDialog.show();

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            userList.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject usersResponse = (JSONObject) response.get(i);
                                String username = usersResponse.getString("Username");
                                String email = usersResponse.getString("Email");
                                String displayName = usersResponse.getString("DisplayName");
                                String gender = usersResponse.getString("Gender");
                                String dob = usersResponse.getString("DoB");
                                cc = Integer.parseInt(usersResponse.getString("CarbonCredit"));
                                ct = Integer.parseInt(usersResponse.getString("CarbonTax"));
                                String profilePic = usersResponse.getString("ProfilePic");
                                String phoneNo = usersResponse.getString("PhoneNo");
                                String firstLogin = usersResponse.getString("FirstLogin");

                                Users user = new Users(username,email,displayName,gender,dob,cc,ct,profilePic,phoneNo,firstLogin);
                                userList.add(user);

                            }
                            setInformation();
                            if (pDialog.isShowing())
                                pDialog.dismiss();
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Error:" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getApplicationContext(), "Error: " + volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        if (pDialog.isShowing())
                            pDialog.dismiss();
                    }
                });

        // Set the tag on the request.
        jsonObjectRequest.setTag(TAG);

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    private void setInformation(){
        if(userList.size() > 0){
            mSectionsPagerAdapter=new SectionsPagerAdapter(getSupportFragmentManager());
            mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);
            btnView=findViewById(R.id.viewDetails);
            tvDrawerDisplayName.setText(userList.get(0).getDisplayName());
            tvDrawerEmail.setText(userList.get(0).getEmail());

            byte[] decodedStringImg = Base64.decode(userList.get(0).getProfilePic(),Base64.DEFAULT);
            Bitmap myBitmap = BitmapFactory.decodeByteArray(decodedStringImg, 0, decodedStringImg.length);

            if(myBitmap != null){
                drawerProfilePic.setImageBitmap(myBitmap);
            }else{
                drawerProfilePic.setImageResource(R.drawable.testimg);
            }

            //tvAmtCarbonCredit.setText(String.format("%d",userList.get(0).getCarbonCredit()));

            sharedPreferences = getSharedPreferences(getCCPref,MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            int valueCC = userList.get(0).getCarbonCredit();
            editor.putInt("ccValue",valueCC);
            editor.commit();

            if(userList.get(0).getFirstLogin().equals("F")){
                return;
            }

            if(userList.get(0).getFirstLogin().equals("T")){
                if(countAlert == 0){
                    AlertDialog.Builder firstTimeAlert = new AlertDialog.Builder(HomeActivity.this);
                    firstTimeAlert.setMessage("Welcome new user, I would like to know more about you! Fill in your personal detail " +
                            "to receive free 1000 Carbon Credit.").setCancelable(false)
                            .setPositiveButton("Take Me There", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getApplicationContext(),EditProfileActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("May Be Next Time", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    firstTimeAlert.setIcon(R.drawable.ic_mood_black_24dp);

                    countAlert++;
                    AlertDialog alert = firstTimeAlert.create();
                    alert.setTitle("Sign Out");
                    alert.show();
                }
            }
        }else{
            sharedPreferences = getSharedPreferences(prefName,MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            boolean unauthen = false;
            editor.putBoolean("authenticated",unauthen);
            editor.commit();

            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
        }
    }


    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tool_bar_item, menu);
        return super.onCreateOptionsMenu(menu);
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }
    }



    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.home_viewpage, null);
            //Textview and image view from fragment.xml
            TextView titleTv=(TextView)rootView.findViewById(R.id.titleTv) ;
            TextView content=(TextView)rootView.findViewById(R.id.content);
            TextView description=(TextView)rootView.findViewById(R.id.description);
            Button viewDetails=(Button)rootView.findViewById(R.id.viewDetails);
            SharedPreferences sharedPreferences;



            if(getArguments().getInt(ARG_SECTION_NUMBER)==1) {
                titleTv.setText(R.string.carboncredit);
                content.setText(cc+" cc");
                description.setText(R.string.cc_description);
                viewDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(getActivity(),ProfilePicActivity.class);
                        startActivity(intent);
                    }
                });
            }
            else if(getArguments().getInt(ARG_SECTION_NUMBER)==2){
                titleTv.setText(R.string.carbontax);
                content.setText(String.format("RM %d",ct));
                description.setText(R.string.ct_descriptoin);
                viewDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(getActivity(),ProfilePicActivity.class);
                        startActivity(intent);
                    }
                });
            }

            return rootView;
        }
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        downloadUsers(getApplicationContext(),authUser);
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        downloadUsers(getApplicationContext(),authUser);
//    }
}
