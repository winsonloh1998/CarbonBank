package com.cb.carbonbank;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "getUsernameNPassword";
    private TextInputLayout textInputUsername;
    private TextInputLayout textInputPassword;
    private Button btnSignIn;
    private ImageView imageViewLogo;
    private TextView textViewCreateAcc;
    long animateDuration = 4000; //milliseconds
    long animateLogoDuration = 2000;
    private int countForLogo;

    //USER VALIDATION
    List<Users> usersList;
    private ProgressDialog pDialog;
    private static String GET_URL = "https://crocodilian-trade.000webhostapp.com/SelectUsers.php";
    RequestQueue queue;

    //Authenticated User
    private static final String prefName = "AuthenticatedUser";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textInputUsername = findViewById(R.id.text_input_username);
        textInputPassword = findViewById(R.id.text_input_password);
        imageViewLogo = findViewById(R.id.imgLogo);
        textViewCreateAcc = findViewById(R.id.textView);

        countForLogo = 1;

        btnSignIn = findViewById(R.id.btnSignIn);


        //Animation for Logo
        ObjectAnimator animator = ObjectAnimator.ofFloat(imageViewLogo,View.ALPHA,0.0f,1.0f);
        animator.setDuration(animateDuration);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(animator);
        animatorSet.start();


        if (!isConnected()) {
            Toast.makeText(getApplicationContext(), "Network Service Not Available", Toast.LENGTH_LONG).show();
        }
        pDialog = new ProgressDialog(this);
        usersList = new ArrayList<>();

    }

    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public void logoAnimate(View view){
        //Animation for Logo
        countForLogo++;
        if(countForLogo %2 ==0){
            ObjectAnimator animator = ObjectAnimator.ofFloat(imageViewLogo,"rotation",0f,360f);
            animator.setDuration(animateLogoDuration);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(animator);
            animatorSet.start();
        }else{
            ObjectAnimator animator = ObjectAnimator.ofFloat(imageViewLogo,"rotation",360f,0f);
            animator.setDuration(animateLogoDuration);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(animator);
            animatorSet.start();
        }

    }

    public void toCreateAccount(View view){
        Intent intent = new Intent(this,RegisterActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }

    public void toCreateCompanyAccount(View view){
        Intent intent = new Intent(this,CompanyRegisterActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }


    private boolean validateUser(){

        String usernameInput = null;
        if(textInputUsername.getEditText().getText().toString().trim() != null){
            usernameInput = textInputUsername.getEditText().getText().toString().trim();
        }

        if(usernameInput.isEmpty()){
            textInputUsername.setError("Username can't be empty");
            return false;
        }else{
            textInputUsername.setError(null);
            textInputUsername.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePassword(){

        String passwordInput = null;
        if(textInputPassword.getEditText().getText().toString().trim() != null){
            passwordInput = textInputPassword.getEditText().getText().toString().trim();
        }

        if(passwordInput.isEmpty()){
            textInputPassword.setError("Password can't be empty");
            return false;
        }else{
            textInputPassword.setError(null);
            textInputPassword.setErrorEnabled(false);
            return true;
        }
    }

    public void confirmInput(View view){
        downloadUsers(getApplicationContext(),GET_URL);
        if(!validateUser() | !validatePassword()){
            return;
        }

        String result;
        boolean valid = false;

        for(int i = 0;i<usersList.size();i++){
            if(usersList.get(i).getUsername().equals(textInputUsername.getEditText().getText().toString()) &&
                    usersList.get(i).getPassword().equals(textInputPassword.getEditText().getText().toString())){
                valid = true;
                break;
            }
        }

        if(valid){
            result = "Successfully";
            Toast.makeText(this,result,Toast.LENGTH_SHORT).show();

            //Set the user to authenticated
            sharedPreferences = getSharedPreferences(prefName,MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            boolean authen = true;
            editor.putBoolean("authenticated",authen);
            editor.commit();

            Intent intent = new Intent(this,HomeActivity.class);
            startActivity(intent);
        }else{
            result = "Invalid Username & Password";
            Toast.makeText(this,result,Toast.LENGTH_SHORT).show();
        }
    }


    public void downloadUsers(Context context, String url){
        // Instantiate the RequestQueue
        queue = Volley.newRequestQueue(context);

        if (!pDialog.isShowing())
            pDialog.setMessage("Validating...");
        pDialog.show();

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            usersList.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject usersResponse = (JSONObject) response.get(i);
                                String username = usersResponse.getString("Username");
                                String password = usersResponse.getString("Password");
                                Users user = new Users(username,password);
                                usersList.add(user);
//                                if(username.equals(textInputUsername.getEditText().getText().toString()) &&
//                                        password.equals(textInputPassword.getEditText().getText().toString())) {
//                                    valid = true;
//                                    break;
//                                }
//                                valid = false;
                            }
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

    @Override
    protected void onPause() {
        super.onPause();
        if (queue != null) {
            queue.cancelAll(TAG);
        }
    }
}
