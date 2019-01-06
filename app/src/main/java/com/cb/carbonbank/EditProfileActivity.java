package com.cb.carbonbank;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {
    private CircleImageView civProfilePic;
    private EditText etDisplayName;
    private RadioGroup choiceGender;
    private RadioButton rbMale;
    private RadioButton rbFemale;
    private RadioButton rbOther;
    private TextView tvShowDob;
    private EditText etPhoneNo;
    private TextView tvErrorMsg;

    private static final String TAG = "EditProfileAcitivity";
    private static final String TAG_UPDATE = "UpdateProfile";
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private static final String prefName = "AuthenticatedUser";
    private SharedPreferences sharedPreferences;
    private String authUser;

    private static List<Users> userList;
    private ProgressDialog pDialog;
    private ProgressDialog progressDialog;
    private static String GET_URL = "https://crocodilian-trade.000webhostapp.com/SelectUsers.php";
    RequestQueue queue;

    private final int CODE_GALLERY_REQUEST = 999;
    private final int CODE_CAMERA_REQUEST = 998;
    private Bitmap bitmap;
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        civProfilePic = findViewById(R.id.editProfilePic);
        etDisplayName = findViewById(R.id.etDisplayName);
        choiceGender = findViewById(R.id.radioGroupGender);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);
        rbOther = findViewById(R.id.rbOther);
        tvShowDob = (TextView) findViewById(R.id.tvShowDoB);
        etPhoneNo = findViewById(R.id.etPhoneNo);
        tvErrorMsg = findViewById(R.id.tvErrorMsg);

        pDialog = new ProgressDialog(this);
        progressDialog = new ProgressDialog(this);
        userList = new ArrayList<>();

        sharedPreferences = getSharedPreferences(prefName,MODE_PRIVATE);
        authUser = sharedPreferences.getString("authenticatedUser", "Anonymous");
        downloadUsers(getApplicationContext(), authUser);

        civProfilePic.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                AlertDialog.Builder requestImgAlert = new AlertDialog.Builder(EditProfileActivity.this);
                requestImgAlert.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(EditProfileActivity.this,
                                new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},CODE_GALLERY_REQUEST);
                    }
                }).setNegativeButton("Take Photo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(EditProfileActivity.this,
                                new String[] {Manifest.permission.CAMERA},CODE_CAMERA_REQUEST);

                    }
                });

                requestImgAlert.setTitle("Pick Image From");
                AlertDialog alert = requestImgAlert.create();
                alert.show();
            }
        });

        tvShowDob.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        EditProfileActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                Log.d(TAG,"onDataSet: date" + dayOfMonth +"/"+month+"/"+year);

                String date =  dayOfMonth +" - "+getMonthInWord(month)+" - "+year;
                tvShowDob.setText(date);
            }
        };

        etDisplayName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String tempString = etDisplayName.getText().toString();
                if(!hasFocus){
                    etDisplayName.setText(etDisplayName.getText().toString());

                }else{
                    etDisplayName.setText("");
                }
            }
        });

        etPhoneNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String tempString = etPhoneNo.getText().toString();
                if(!hasFocus){
                    etPhoneNo.clearFocus();
                    etPhoneNo.setText(etPhoneNo.getText().toString());
                }else{
                    etPhoneNo.setText("");
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Checking the request code of our request
        if(requestCode == CODE_GALLERY_REQUEST) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent,"Select Image"),CODE_GALLERY_REQUEST);
            }else{
                Toast.makeText(getApplicationContext(),"You don't have permission to access gallery",Toast.LENGTH_LONG).show();
            }
            return;
        }else if(requestCode == CODE_CAMERA_REQUEST){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,CODE_CAMERA_REQUEST);
            }else{
                Toast.makeText(getApplicationContext(),"You don't have permission to access camera",Toast.LENGTH_LONG).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case CODE_GALLERY_REQUEST:
                if(resultCode == RESULT_OK && data != null){
                    filePath = data.getData();
                    try{
                        InputStream inputStream = getContentResolver().openInputStream(filePath);
                        bitmap = BitmapFactory.decodeStream(inputStream);
                        civProfilePic.setImageBitmap(bitmap);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
                break;
            case CODE_CAMERA_REQUEST:
                if(resultCode == RESULT_OK && data != null){
                    bitmap = (Bitmap) data.getExtras().get("data");
                    civProfilePic.setImageBitmap(bitmap);
                }
                break;
        }
    }

    private String imageToString(Bitmap bitmap){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        byte[] imageBytes = outputStream.toByteArray();

        String encodedImage = android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT);
        return encodedImage;
    }

    public void downloadUsers(Context context, String username){
        // Instantiate the RequestQueue
        queue = Volley.newRequestQueue(context);
        String url = GET_URL + "?Username=" + username;

        if (!pDialog.isShowing())
            pDialog.setMessage("Getting Personal Information...");
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
                                String displayName = usersResponse.getString("DisplayName");
                                String gender = usersResponse.getString("Gender");
                                String dob = usersResponse.getString("DoB");
                                String profilePic = usersResponse.getString("ProfilePic");
                                String phoneNo = usersResponse.getString("PhoneNo");

                                Users user = new Users(displayName,gender,dob,profilePic,phoneNo);
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

    public void saveProfileInfo(View view){
        if(!validateDisplayName()| !validatePhoneNo() | !validatePhoneNo() | !validateDoB()){
            return;
        }
        saveUserInformation();
    }

    private void saveUserInformation(){
        Users user = new Users();
        user.setUsername(authUser);
        user.setDisplayName(etDisplayName.getText().toString());

        int gender = choiceGender.getCheckedRadioButtonId();
        if(gender == R.id.rbMale){
            user.setGender("M");
        }else if(gender == R.id.rbFemale){
            user.setGender("F");
        }else{
            user.setGender("O");
        }

        user.setDob(tvShowDob.getText().toString());
        user.setPhoneNo(etPhoneNo.getText().toString());

        if(bitmap != null){
            user.setProfilePic(imageToString(bitmap));
        }else{
            Bitmap noImg = BitmapFactory.decodeResource(getResources(),R.drawable.testimg);
            user.setProfilePic(imageToString(noImg));
        }

        try {
            makeServiceCall(getApplicationContext(), "https://crocodilian-trade.000webhostapp.com/UpdateUserInfo.php",user);
        }catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

//        AlertDialog.Builder requestImgAlert = new AlertDialog.Builder(EditProfileActivity.this);
//        requestImgAlert.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });
//        requestImgAlert.setTitle("Pick Image From");
//        requestImgAlert.setMessage("Username:" + user.getUsername() + "\nDisplay Name:" + user.getDisplayName()
//                +"\nGender:"+user.getGender()+"\nDoB:"+user.getDob()+"\nPhone No:"+user.getPhoneNo());
//        AlertDialog alert = requestImgAlert.create();
//        alert.show();
    }

    public void makeServiceCall(Context context, String url, final Users users) {
        //mPostCommentResponse.requestStarted();
        RequestQueue queue = Volley.newRequestQueue(context);

        if (!progressDialog.isShowing())
            progressDialog.setMessage("Updating personal information...");
        progressDialog.show();

        try {
            StringRequest postRequest = new StringRequest(
                    Request.Method.POST,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
//                            JSONObject jsonObject;
//                            try {
//                                jsonObject = new JSONObject(response);
//                                int success = jsonObject.getInt("success");
//                                String message = jsonObject.getString("message");
//                                if (success==0) {
//                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
//                                }else{
//                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
//                                    finish();
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Error. " + error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();

                    params.put("Username", users.getUsername());
                    params.put("DisplayName", users.getDisplayName());
                    params.put("Gender", users.getGender());
                    params.put("DoB", users.getDob());
                    params.put("ProfilePic",users.getProfilePic());
                    params.put("PhoneNo",users.getPhoneNo());
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/x-www-form-urlencoded");
                    return params;
                }
            };
            queue.add(postRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setInformation(){
        etDisplayName.setText(userList.get(0).getDisplayName());

        if(userList.get(0).getGender().equals("M")){
            rbMale.setChecked(true);
        }else if(userList.get(0).getGender().equals("F")){
            rbFemale.setChecked(true);
        }else{
            rbOther.setChecked(true);
        }

        tvShowDob.setText(userList.get(0).getDob());

        if(userList.get(0).getPhoneNo() == null || userList.get(0).getPhoneNo().trim().equals("")){
            etPhoneNo.setHint("0123456789");
        }else{
            etPhoneNo.setText(userList.get(0).getPhoneNo());
        }

        byte[] decodedStringImg = Base64.decode(userList.get(0).getProfilePic(),Base64.DEFAULT);
        Bitmap myBitmap = BitmapFactory.decodeByteArray(decodedStringImg, 0, decodedStringImg.length);
        if(myBitmap != null){
            civProfilePic.setImageBitmap(myBitmap);
        }else{
            civProfilePic.setImageResource(R.drawable.testimg);
        }
    }

    private String getMonthInWord(int month){
        String monthInWord = "";

        switch (month){
            case 1:
                monthInWord = "JAN";
                break;
            case 2:
                monthInWord = "FEB";
                break;
            case 3:
                monthInWord = "MAR";
                break;
            case 4:
                monthInWord = "APR";
                break;
            case 5:
                monthInWord = "MAY";
                break;
            case 6:
                monthInWord = "JUN";
                break;
            case 7:
                monthInWord = "JUL";
                break;
            case 8:
                monthInWord = "AUG";
                break;
            case 9:
                monthInWord = "SEP";
                break;
            case 10:
                monthInWord = "OCT";
                break;
            case 11:
                monthInWord = "NOV";
                break;
            case 12:
                monthInWord = "DEC";
                break;
        }
        return monthInWord;
    }

    private boolean validateDisplayName(){
        String displayNameInput = null;
        if(etDisplayName.getText().toString().trim() != null){
            displayNameInput = etDisplayName.getText().toString().trim();
        }

        if(displayNameInput.isEmpty()){
            tvErrorMsg.setText("Display name can't be empty");
            return false;
        }else{
            tvErrorMsg.setText("");
            return true;
        }
    }

    private boolean validatePhoneNo(){
        String phoneNoInput = null;
        if(etPhoneNo.getText().toString().trim() != null){
            phoneNoInput = etPhoneNo.getText().toString().trim();
        }

        if(phoneNoInput.isEmpty()){
            tvErrorMsg.setText("Phone number can't be empty");
            return false;
        }else{
            if(phoneNoInput.length() >= 10 && phoneNoInput.length() <= 11){
                for(int i=0;i<phoneNoInput.length();i++){
                    if(!Character.isDigit(phoneNoInput.charAt(i))){
                        tvErrorMsg.setText("Phone number must contain digit only");
                        return false;
                    }
                }
                tvErrorMsg.setText("");
                return true;
            }else{
                tvErrorMsg.setText("Length of phone number must be between 11 or 12 included '-'.");
                return false;
            }
        }
    }

    private boolean validateGender(){
        int gender = choiceGender.getCheckedRadioButtonId();
        if(gender == R.id.rbMale || gender == R.id.rbFemale || gender == R.id.rbOther){
            tvErrorMsg.setText("");
            return true;
        }else{
            tvErrorMsg.setText("Please select one of the gender.");
            return false;
        }
    }

    private boolean validateDoB(){
        String dobInput = null;
        if(tvShowDob.getText().toString().trim() != null){
            dobInput = tvShowDob.getText().toString().trim();
        }

        if(dobInput.isEmpty()){
            tvErrorMsg.setText("Date of Birth can't be empty");
            return false;
        }else{
            tvErrorMsg.setText("");
            return true;
        }
    }
}
