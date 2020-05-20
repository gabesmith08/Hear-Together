package com.example.heartogether.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.heartogether.FFTTest;
import com.example.heartogether.PitchActivityTest;
import com.example.heartogether.MainActivity;
import com.example.heartogether.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment {

    private final String TAG = "HomeFragment";
    public static final int RC_SIGN_IN = 123;

    private Button createBtn;
    private Button joinBtn;
    private Button wifiBtn;
    private Button signIn;
    private Button signOut;
    private MainActivity activity;
    private Button PitchBtn;
    private Button FFTBtn;


    private AuthUI.SignInIntentBuilder signInIntentBuilder;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedFragmentState) {
        super.onCreate(savedFragmentState);
        Log.d(TAG, "onCreate called");
        activity = (MainActivity) this.getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView called");
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        createBtn = v.findViewById(R.id.sesssion_create);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction()
                        .replace(R.id.fragmentLayout, new CreateSessionFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        joinBtn = v.findViewById(R.id.session_join);
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.WiFiPeerDiscovery();
                JoinSessionDialogFragment dialog = new JoinSessionDialogFragment();
                dialog.show(getFragmentManager(), "join");
            }
        });


        PitchBtn = v.findViewById(R.id.PitchBtn);
        final Intent pitchIntent = new Intent(HomeFragment.this.getActivity(), PitchActivityTest.class);
        PitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(pitchIntent);
            }
        });

        FFTBtn = v.findViewById(R.id.FFTBtn);
        final Intent FFTIntent = new Intent(HomeFragment.this.getActivity(), FFTTest.class);
        FFTBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(FFTIntent);
            }
        });


        signIn = v.findViewById(R.id.login);
        signIn.setOnClickListener(signInListener);
        signOut = v.findViewById(R.id.logout);
        signOut.setOnClickListener(signOutListener);
        createSignInIntent();
        setButtonVisibility(FirebaseAuth.getInstance().getCurrentUser() != null);
        return v;
    }

    private void setButtonVisibility(boolean loggedIn) {
        Log.d(TAG, "Setting button visibility");
        if (loggedIn) {
            signIn.setVisibility(View.GONE);
            signOut.setVisibility(View.VISIBLE);
        } else {
            signIn.setVisibility(View.VISIBLE);
            signOut.setVisibility(View.GONE);
        }
    }

    private View.OnClickListener signInListener = new View.OnClickListener() {
        public void onClick(View v) {
            login(v);
        }
    };

    private View.OnClickListener signOutListener = new View.OnClickListener() {
        public void onClick(View v) {
            logout(v);
        }
    };

    public void login(View v) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            startActivityForResult(
                    signInIntentBuilder.build(),
                    RC_SIGN_IN);
        }
    }

    public void logout(View v) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            auth.signOut();
            setButtonVisibility(FirebaseAuth.getInstance().getCurrentUser() != null);
        }
    }

    public void createSignInIntent() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.AnonymousBuilder().build());

        signInIntentBuilder = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false, true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            setButtonVisibility(FirebaseAuth.getInstance().getCurrentUser() != null);
            // Successfully signed in
            if (resultCode == RESULT_OK) {

            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    return;
                }
                Log.e(TAG, "Sign-in error: ", response.getError());
            }
        }
    }
}
