package com.cpen321group.accountability.welcome.register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.WindowCompat;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cpen321group.accountability.HomeScreenActivity;
import com.cpen321group.accountability.R;
import com.cpen321group.accountability.RetrofitAPI;
import com.cpen321group.accountability.FrontendConstants;
import com.cpen321group.accountability.welcome.WelcomeActivity;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterSettingActivity extends AppCompatActivity {
    private ImageView avatar;
    private MyProfile myProfile_1;
    private String userId;
    private String text;
    private EditText emailText;
    private EditText ageText;
    private EditText professionText;
    private int GoogleOn = 0;
    private GoogleSignInAccount account;
    private Bitmap bitmap = null;
    private String av;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        DynamicColors.applyToActivitiesIfAvailable(this.getApplication());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_setting);
        if (FrontendConstants.is_darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        if(GoogleSignIn.getLastSignedInAccount(RegisterSettingActivity.this)!=null){
            GoogleOn = 1;
            account = GoogleSignIn.getLastSignedInAccount(RegisterSettingActivity.this);
        }

        //set visible
        TextInputLayout email = findViewById(R.id.emailField);
        ImageView image = findViewById(R.id.imageView5);
        emailText = findViewById(R.id.email_text);
        if(GoogleSignIn.getLastSignedInAccount(this)!=null){
            email.setVisibility(View.INVISIBLE);
            image.setVisibility(View.INVISIBLE);
        }else{
            email.setVisibility(View.VISIBLE);
            image.setVisibility(View.VISIBLE);
        }

        ageText = findViewById(R.id.title_text);
        professionText = findViewById(R.id.profession_text);

        //change avatar
        Button changeButton = findViewById(R.id.btn_change);
        avatar = findViewById(R.id.iv_personal_icon);
        if(GoogleOn == 1){
            GoogleSignInAccount account= GoogleSignIn.getLastSignedInAccount(this);
            if (account != null && account.getPhotoUrl() != null) {
                avatar.setImageURI(account.getPhotoUrl());
                Uri uri = account.getPhotoUrl();
                ContentResolver cr = this.getContentResolver();
                try {
                    bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                } catch (FileNotFoundException e) {
                    Log.e("Exception", e.getMessage(),e);
                }
            }
        }
        //changeButton.setVisibility(View.INVISIBLE);
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePic();
            }
        });


        //drop down menu
        AutoCompleteTextView autoText = findViewById(R.id.select_text);

        String[] items = {"User", "Accountant"};
        ArrayAdapter<String> itemAdapter = new ArrayAdapter<>(RegisterSettingActivity.this, R.layout.list_item, items);
        autoText.setAdapter(itemAdapter);
        autoText.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                text = (String)parent.getItemAtPosition(position);
            }
        });



        //register button
        Button sign_out = findViewById(R.id.button_register);

        sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    createProfile();
                    checkForCreate();
                }catch(Exception e){
                    Log.d("register",e.toString());
                }
            }
        });
    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void checkForCreate(){
        if((!myProfile_1.getEmail().equals(""))&&(text!=null)) {
            if (!isEmailValid(myProfile_1.getEmail())){
                Log.d("address",myProfile_1.getEmail());
                Toast.makeText(getApplicationContext(),"Email address is not valid",Toast.LENGTH_LONG).show();
            }else {
                if(myProfile_1.getAge()>0 && myProfile_1.getProfession().length()<20) {
                    try {
                        postAccount();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else if(myProfile_1.getAge()<=0){
                    Toast.makeText(getApplicationContext(),"Age is not valid",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Profession is not valid",Toast.LENGTH_LONG).show();
                }
            }
        }else{
            Toast.makeText(RegisterSettingActivity.this,"Some necessary information missing!",Toast.LENGTH_LONG).show();
        }
    }

    //choose picture
    private void choosePic() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Avatar");
        String[] select_item = {"Choose from local"};
        builder.setNegativeButton("cancel", null);
        builder.setItems(select_item, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
                openAlbumIntent.setType("image/*");
                startActivityForResult(openAlbumIntent, 0);
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri uri2 = data.getData();
            ContentResolver cr = this.getContentResolver();
            try {
                bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri2));
                avatar.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                Log.e("Exception", e.getMessage(),e);
            }
        }
    }

    private void postAccount() throws IOException {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FrontendConstants.baseURL + "")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        if(bitmap != null){
            av = bitmapToString(Bitmap.createScaledBitmap(bitmap, 100, 100, false));
        }else{
            av = " ";
        }

        JsonObject json = new JsonObject();
        json.addProperty("avatar",av);

        Call<JsonObject> call = retrofitAPI.createAccount(myProfile_1.getFirstname(),
                myProfile_1.getLastname(),
                myProfile_1.getEmail(),
                myProfile_1.getAge(),
                myProfile_1.getProfession(), myProfile_1.getAccountant(),
                myProfile_1.getAccountId(),
                json);

        call.enqueue(new Callback< JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("Message",response.toString());
                if(response.body().get("profile")!=null) {
                    Intent settingsIntent = new Intent(getApplicationContext(), HomeScreenActivity.class);
                    startActivity(settingsIntent);
                }else{
                    Toast.makeText(getApplicationContext(),"Check the information and try again",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Failed to register, Check the Internet",Toast.LENGTH_LONG).show();
                if (GoogleOn == 1) {
                    GoogleSignInClient account = GoogleSignIn.getClient(getApplicationContext(), GoogleSignInOptions.DEFAULT_SIGN_IN);
                    signOut(account);
                    Log.d("Profile", "Google sign out successfully!");
                }
                if (Profile.getCurrentProfile() != null) {
                    LoginManager.getInstance().logOut();
                    Log.d("Profile", "Facebook sign out successfully!");
                }
                Intent Intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                startActivity(Intent);
                Log.d("Message",t.toString());
            }
        });
    }

    //Google sign out
    private void signOut(GoogleSignInClient mGoogleSignInClient) {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(RegisterSettingActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }

    private void createProfile(){
        myProfile_1 = new MyProfile("","","",20,"student",false,"");
        if(GoogleOn == 1){
            myProfile_1.setFirstname(account.getGivenName());
            myProfile_1.setLastname(account.getFamilyName());
            myProfile_1.setEmail(account.getEmail());
            if(!ageText.getEditableText().toString().equals("")) {
                myProfile_1.setAge(Integer.parseInt(ageText.getEditableText().toString().trim()));
            }
            if(!professionText.getEditableText().toString().equals("")) {
                myProfile_1.setProfession(professionText.getEditableText().toString().trim());
            }
            userId = account.getId()+"go";
            if(text!=null) {
                myProfile_1.setAccountant((text.equals("Accountant")));
            }
            myProfile_1.setAccountId(userId);
            Log.d("userId",userId);
        }else{
            Profile profile = Profile.getCurrentProfile();
            myProfile_1.setFirstname(profile.getFirstName());
            myProfile_1.setLastname(profile.getLastName());
            myProfile_1.setEmail(emailText.getEditableText().toString().trim());
            if(!ageText.getEditableText().toString().equals("")) {
                myProfile_1.setAge(Integer.parseInt(ageText.getEditableText().toString().trim()));
            }
            if(!professionText.getEditableText().toString().equals("")) {
                myProfile_1.setProfession(professionText.getEditableText().toString().trim());
            }
            if(text!=null) {
                myProfile_1.setAccountant((text.equals("Accountant")));
            }
            userId = profile.getId()+"fb";
            myProfile_1.setAccountId(userId);
            Log.d("userId",userId);
        }
    }

    public String bitmapToString(Bitmap bitmap){
        String string=null;
        ByteArrayOutputStream bStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,bStream);
        byte[]bytes=bStream.toByteArray();
        string= Base64.encodeToString(bytes,Base64.DEFAULT);
        return string;
    }

}