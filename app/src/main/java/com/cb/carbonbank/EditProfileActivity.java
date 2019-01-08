package com.cb.carbonbank;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
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
import java.util.Date;
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
    private String errorMsgForAll;
    private TextView usedToStoreFirstLogin;
    Date date = Calendar.getInstance().getTime();
    CharSequence dt=android.text.format.DateFormat.format("dd/MM/yyyy  hh:mm:ss",date.getTime());

    private static final String TAG = "EditProfileAcitivity";
    private static final String TAG_UPDATE = "UpdateProfile";
//    private DatePickerDialog.OnDateSetListener mDateSetListener;
//
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

    private ConstraintLayout thisScreen;

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
        usedToStoreFirstLogin = findViewById(R.id.usedToStoreFirstLogin);

        pDialog = new ProgressDialog(this);
        progressDialog = new ProgressDialog(this);
        userList = new ArrayList<>();

        thisScreen = findViewById(R.id.thisScreen);

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
                        EditProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month+1;
                        String date = dayOfMonth + " - " + getMonthInWord(month) + " - " + year;
                        tvShowDob.setText(date);
                    }
                }, year,month,day);
                dialog.show();
            }
        });

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
                                String firstLogin = usersResponse.getString("FirstLogin");

                                Users user = new Users(displayName,gender,dob,profilePic,phoneNo,firstLogin);
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
        if(!validateDisplayName()| !validatePhoneNo() | !validateGender() | !validateDoB()){
            tvErrorMsg.setText("Click Here To View Error.");
            tvErrorMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder errorInputAlert = new AlertDialog.Builder(EditProfileActivity.this);
                    errorInputAlert.setMessage(errorMsgForAll).setCancelable(false)
                            .setPositiveButton("Understood", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    tvErrorMsg.setText("");
                                    errorMsgForAll = null;
                                    dialog.cancel();
                                }
                            });
                    errorInputAlert.setIcon(R.drawable.ic_error_blue_24dp);

                    AlertDialog alert = errorInputAlert.create();
                    alert.setTitle("Error");
                    alert.show();
                }
            });
            return;
        }
        errorMsgForAll = null;
        tvErrorMsg.setText("");
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
            BitmapDrawable getCurrentImage = ((BitmapDrawable) civProfilePic.getDrawable());
            Bitmap noImg = getCurrentImage.getBitmap();
            user.setProfilePic(imageToString(noImg));
        }

        try {
            makeServiceCall(getApplicationContext(), "https://crocodilian-trade.000webhostapp.com/UpdateUserInfo.php",user);
        }catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

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
                            if(usedToStoreFirstLogin.getText().toString().equals("T")){
                               notifcationCall();
                                CreditHistory ch=new CreditHistory(String.format("%s",dt),"First Login Reward",1000,1);
                                CarbonCreditActivity.creditList.push(ch);
                            }

                            Toast.makeText(getApplicationContext(),"Your Profile Has Been Successfully Updated",Toast.LENGTH_LONG).show();
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            finish();

                        }
                    },

                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Error:" + error.toString(), Toast.LENGTH_LONG).show();
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("Username", users.getUsername());
                    params.put("DisplayName", users.getDisplayName());
                    params.put("Gender", users.getGender());
                    params.put("DoB", users.getDob());
                    params.put("PhoneNo",users.getPhoneNo());
                    params.put("ProfilePic",users.getProfilePic());
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
            if (progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }

    private void notifcationCall(){
        int NOTIFICATION_ID = 234;
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String CHANNEL_ID="Carbon Credit Added";
        CharSequence name = "Congratulation";
        String Description = "1000 carbon credit has been added your account.";

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.BLUE);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(EditProfileActivity.this,CHANNEL_ID)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Carbon Bank Notification")
                .setAutoCancel(true)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),R.drawable.ic_launcher))
                .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400}) //VIBRATION
                .setLights(Color.BLUE,3000,3000) //LED LIGHT
                .setContentText("Thanks for completing your profile, 1000 carbon credit has been added your account.");

        final Intent intent = new Intent(this,HomeActivity.class);
        PendingIntent contentIntent =
                PendingIntent.getActivity(this, 0, intent, 0);
        notificationBuilder.setContentIntent(contentIntent);
        notificationManager.notify(1,notificationBuilder.build());
    }

    private void setInformation(){
        usedToStoreFirstLogin.setText(userList.get(0).getFirstLogin());
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
                monthInWord = "Jan";
                break;
            case 2:
                monthInWord = "Feb";
                break;
            case 3:
                monthInWord = "Mar";
                break;
            case 4:
                monthInWord = "Apr";
                break;
            case 5:
                monthInWord = "May";
                break;
            case 6:
                monthInWord = "Jun";
                break;
            case 7:
                monthInWord = "Jul";
                break;
            case 8:
                monthInWord = "Aug";
                break;
            case 9:
                monthInWord = "Sep";
                break;
            case 10:
                monthInWord = "Oct";
                break;
            case 11:
                monthInWord = "Nov";
                break;
            case 12:
                monthInWord = "Dec";
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
            if(errorMsgForAll != null){
                errorMsgForAll = errorMsgForAll + "\nDisplay name can't be empty";
            }else{
                errorMsgForAll = "Display name can't be empty";
            }
            return false;
        }else{
            return true;
        }
    }

    private boolean validatePhoneNo(){
        String phoneNoInput = null;
        if(etPhoneNo.getText().toString().trim() != null){
            phoneNoInput = etPhoneNo.getText().toString().trim();
        }

        if(phoneNoInput.isEmpty()){
            if(errorMsgForAll != null){
                errorMsgForAll = errorMsgForAll + "\nPhone number can't be empty";
            }else{
                errorMsgForAll = "Phone number can't be empty";
            }
            return false;
        }else{
            if(phoneNoInput.length() >= 10 && phoneNoInput.length() <= 11){
                for(int i=0;i<phoneNoInput.length();i++) {
                    if (!Character.isDigit(phoneNoInput.charAt(i))) {
                        if (errorMsgForAll != null) {
                            errorMsgForAll = errorMsgForAll + "\nPhone number must contain digit only";
                        } else {
                            errorMsgForAll = "Phone number must contain digit only";
                        }
                        return false;
                    }
                }
                return true;
            }else{
                if(errorMsgForAll != null){
                    errorMsgForAll = errorMsgForAll + "\nLength of phone number must be between 10 or 11";
                }else{
                    errorMsgForAll = "Length of phone number must be between 10 or 11";
                }
                return false;
            }
        }
    }

    private boolean validateGender(){
        int gender = choiceGender.getCheckedRadioButtonId();
        if(gender == R.id.rbMale || gender == R.id.rbFemale || gender == R.id.rbOther){
            return true;
        }else{
            if(errorMsgForAll != null){
                errorMsgForAll = errorMsgForAll + "\nPlease select one of the gender.";
            }else{
                errorMsgForAll = "Please select one of the gender.";
            }
            return false;
        }
    }

    private boolean validateDoB(){
        String dobInput = null;
        if(tvShowDob.getText().toString().trim() != null){
            dobInput = tvShowDob.getText().toString().trim();
        }

        if(dobInput.isEmpty()){
            if(errorMsgForAll != null){
                errorMsgForAll = errorMsgForAll + "\nDate of Birth can't be empty";
            }else{
                errorMsgForAll = "Date of Birth can't be empty";
            }
            return false;
        }else{
            return true;
        }
    }
}
