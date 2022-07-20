package com.cpen321group.accountability.mainscreen.profile;

import static com.facebook.FacebookSdk.getApplicationContext;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.cpen321group.accountability.VariablesSpace;
import com.cpen321group.accountability.databinding.FragmentProfileBinding;
import com.cpen321group.accountability.welcome.WelcomeActivity;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private TextView profileName;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        final TextView textView = binding.textNotifications;
//        notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        profileName = binding.profileName;
        profileName.setText(VariablesSpace.userName);
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
                Intent settingsIntent = new Intent(getActivity(), WelcomeActivity.class);
                startActivity(settingsIntent);
            }
        });

        Button subscription_info = binding.yourSubscriptionButton;
        subscription_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent subscriptionIntent;
                if (VariablesSpace.is_subscribed != true){
                    subscriptionIntent = new Intent(getActivity(), SubscriptionActivity.class);
                } else {
                    subscriptionIntent = new Intent(getActivity(), SubscriptionOKActivity.class);
                }
                startActivity(subscriptionIntent);

            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
}