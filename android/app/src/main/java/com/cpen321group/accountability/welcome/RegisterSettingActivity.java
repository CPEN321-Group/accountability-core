package com.cpen321group.accountability.welcome;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.google.android.gms.common.util.CollectionUtils.listOf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.core.motion.utils.Utils;
import androidx.core.view.WindowCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;

import com.cpen321group.accountability.MainActivity;
import com.cpen321group.accountability.R;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterSettingActivity extends AppCompatActivity {
    private TextInputLayout inputText;
    private AutoCompleteTextView autoText;
    protected static final int CHOOSE_PICTURE = 0;
    private static final int CROP_SMALL_PICTURE = 1;
    protected static Uri tempUri;
    private ImageView avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        DynamicColors.applyToActivitiesIfAvailable(this.getApplication());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_setting);
        if (MainActivity.is_darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        //change avatar
        Button changeButton = findViewById(R.id.btn_change);
        avatar = findViewById(R.id.iv_personal_icon);
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePic();
            }
        });


        //drop down menu
        inputText = findViewById(R.id.menu);
        autoText = findViewById(R.id.select_text);

        String[] items = {"User", "Accountant"};
        ArrayAdapter<String> itemAdapter = new ArrayAdapter<>(RegisterSettingActivity.this, R.layout.list_item, items);
        autoText.setAdapter(itemAdapter);

        //register button
        Button sign_out = findViewById(R.id.button_register);
        sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GoogleSignIn.getClient(getApplicationContext(), GoogleSignInOptions.DEFAULT_SIGN_IN) != null) {
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
            if (data != null) {
                setImageToView(data);
            }
        }
    }

    protected void setImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            avatar.setImageBitmap(photo);
        }
    }
}


