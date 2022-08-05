package com.cpen321group.accountability.mainscreen.profile;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.cpen321group.accountability.FrontendConstants;
import com.cpen321group.accountability.RetrofitAPI;
import com.cpen321group.accountability.databinding.FragmentProfileBinding;
import com.cpen321group.accountability.welcome.WelcomeActivity;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private TextView profileName;
    private ImageView avatar;
    private Bitmap bitmap = null;
    private String av = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        final TextView textView = binding.textNotifications;
//        notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        profileName = binding.profileName;
        profileName.setText(FrontendConstants.userName);
        Button sign_out = binding.signoutbutton;
        sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(GoogleSignIn.getClient(getApplicationContext(), GoogleSignInOptions.DEFAULT_SIGN_IN) != null){
                    GoogleSignInClient account = GoogleSignIn.getClient(getApplicationContext(), GoogleSignInOptions.DEFAULT_SIGN_IN);
                    signOut(account);
                    Log.d("Profile","Google sign out successfully!");
                }
                if(Profile.getCurrentProfile() != null){
                    LoginManager.getInstance().logOut();
                    Log.d("Profile","Facebook sign out successfully!");
                }

                SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("APP", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("is_logged_in", 0);
                editor.commit();

                Intent settingsIntent = new Intent(getActivity(), WelcomeActivity.class);
                startActivity(settingsIntent);
            }
        });

        avatar = binding.avatar;
        if(FrontendConstants.avatar != null) {
            avatar.setImageBitmap(stringToBitmap(FrontendConstants.avatar));
        }

        Button subscription_info = binding.yourSubscriptionButton;
        subscription_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent subscriptionIntent;
                if (!FrontendConstants.is_subscribed){
                    subscriptionIntent = new Intent(getActivity(), SubscriptionActivity.class);
                } else {
                    subscriptionIntent = new Intent(getActivity(), SubscriptionOKActivity.class);
                }
                startActivity(subscriptionIntent);

            }
        });

        Button changeButton = binding.changeAvatarButton;
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePic();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    //choose picture
    private void choosePic() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Set Avatar");
        String[] select_item = {"Choose from local"};
        builder.setNegativeButton("cancel", null);
        builder.setItems(select_item, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent openAlbumIntent  = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                openAlbumIntent.setType("image/*");
                startActivityForResult(openAlbumIntent, 0);
            }
        });
        builder.create().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri2 = data.getData();
            ContentResolver cr = getActivity().getContentResolver();
            try {
                bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri2));
                updateAvatar(bitmap);
            } catch (FileNotFoundException e) {
                Log.e("Exception", e.getMessage(),e);
            }
        }
    }

    private void updateAvatar(Bitmap bitmap){
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

        Call<JsonObject> call = retrofitAPI.updateProfile(FrontendConstants.userID,json);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.code()==200){
                    Toast.makeText(getApplicationContext(),"Change Successfully",Toast.LENGTH_LONG).show();
                    if(bitmap != null){
                        av = bitmapToString(Bitmap.createScaledBitmap(bitmap, 100, 100, false));
                        avatar.setImageBitmap(bitmap);
                        FrontendConstants.avatar = av;
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Try again, and the size of picture may too large!",Toast.LENGTH_LONG).show();
                }
                Log.d("Message",response.toString());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Change Failed",Toast.LENGTH_LONG).show();
                Log.d("Message",t.toString());
            }
        });
    }

    private void signOut(GoogleSignInClient mGoogleSignInClient) {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }

    public Bitmap stringToBitmap(String string) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
                    bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
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