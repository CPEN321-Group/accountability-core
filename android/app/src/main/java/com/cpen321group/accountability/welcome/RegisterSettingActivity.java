package com.cpen321group.accountability.welcome;

import static com.google.android.gms.common.util.CollectionUtils.listOf;

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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cpen321group.accountability.R;
import com.cpen321group.accountability.RetrofitAPI;
import com.cpen321group.accountability.VariableStore;
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

import java.io.FileNotFoundException;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterSettingActivity extends AppCompatActivity {
    private AutoCompleteTextView autoText;
    private ImageView avatar;
    private String TAG = "register";
    private String server_url = VariableStore.baseURL + "/accounts";
    private MyProfile myProfile_1;
    private String userId;
    private String text;
    private EditText emailText;
    private EditText ageText;
    private EditText professionText;
    private int GoogleOn = 0;
    private GoogleSignInAccount account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        DynamicColors.applyToActivitiesIfAvailable(this.getApplication());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_setting);
        if (VariableStore.is_darkMode) {
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

        ageText = findViewById(R.id.age_text);
        professionText = findViewById(R.id.profession_text);

        //change avatar
        Button changeButton = findViewById(R.id.btn_change);
        avatar = findViewById(R.id.iv_personal_icon);
        if(GoogleOn == 1){
            GoogleSignInAccount account= GoogleSignIn.getLastSignedInAccount(this);
            if (account != null) {
                if (account.getPhotoUrl() != null) {
                    avatar.setImageURI(account.getPhotoUrl());
                }
            }
        }
        changeButton.setVisibility(View.INVISIBLE);
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePic();
            }
        });


        //drop down menu
        autoText = findViewById(R.id.select_text);

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
                createProfile();
                if((!myProfile_1.getEmail().equals(""))&&(text!=null)) {
                    try {
                        postAccount();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (GoogleOn == 1) {
                        GoogleSignInClient account = GoogleSignIn.getClient(getApplicationContext(), GoogleSignInOptions.DEFAULT_SIGN_IN);
                        signOut(account);
                        Log.d("Profile", "Google sign out successfully!");
                    }
                    if (Profile.getCurrentProfile() != null) {
                        LoginManager.getInstance().logOut();
                        Log.d("Profile", "Facebook sign out successfully!");
                    }
                    Intent settingsIntent = new Intent(RegisterSettingActivity.this, WelcomeActivity.class);
                    startActivity(settingsIntent);
                }else{
                    Toast.makeText(RegisterSettingActivity.this,"Some necessary information missing!",Toast.LENGTH_LONG).show();
                }
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
            Uri uri = data.getData();
            String img_url = uri.getPath();
            ContentResolver cr = this.getContentResolver();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                avatar.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                Log.e("Exception", e.getMessage(),e);
            }
        }
    }

    private void postAccount() throws IOException {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(VariableStore.baseURL + "")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<String> call = retrofitAPI.createAccount(myProfile_1.getFirstname(),
                myProfile_1.getLastname(),
                myProfile_1.getEmail(),
                myProfile_1.getAge(),
                myProfile_1.getProfession(), myProfile_1.getAccountant(),
                myProfile_1.getAccountId());

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("Message",response.toString());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("Message","error");
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
}