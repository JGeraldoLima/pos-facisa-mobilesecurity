package jwt.security.jgeraldo.com.posfacisajwtauth.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;

import java.io.IOException;

import jwt.security.jgeraldo.com.posfacisajwtauth.R;
import jwt.security.jgeraldo.com.posfacisajwtauth.databinding.MainActivityBinding;
import jwt.security.jgeraldo.com.posfacisajwtauth.prefs.Prefs;
import jwt.security.jgeraldo.com.posfacisajwtauth.retrofit.JWTServerParser;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static Activity activity;
    private static MainActivityBinding binding;

    private GoogleApiClient googleApiClient;

    private static FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;
        binding = DataBindingUtil.setContentView(
            activity, R.layout.main_activity);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build();

        googleApiClient = new GoogleApiClient.Builder(this)
            .enableAutoManage(this, this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build();

        binding.btRandomQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getRandomQuote();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    setUserData(user);
                } else {
                    goLogInScreen();
                }
            }
        };

        firebaseAuth.addAuthStateListener(firebaseAuthListener);
        new GetAccessTokenTask().execute();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, connectionResult.getErrorMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (firebaseAuthListener != null) {
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }
    }

    private void setUserData(FirebaseUser user) {
        binding.nameTextView.setText(user.getDisplayName());
        binding.emailTextView.setText(user.getEmail());
        binding.idTextView.setText(user.getUid());
    }

    private void getRandomQuote() {
        new GetRandomQuoteTask().execute();
    }

    private void goLogInScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void logOut(View view) {
        firebaseAuth.signOut();

        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    goLogInScreen();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.not_close_session, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void revoke(View view) {
        firebaseAuth.signOut();

        Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    goLogInScreen();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.not_revoke, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private static class GetAccessTokenTask extends AsyncTask<Void, Void, String> {

        private ProgressDialog progressDialog;

        private final String progressMessage = activity.getString(R.string.getting_access_token);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(activity);
            progressDialog.setCancelable(true);
            progressDialog.setMessage(progressMessage);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                return JWTServerParser.getAccessToken(activity, firebaseAuth.getCurrentUser());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String token) {
            super.onPostExecute(token);

            try {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            } catch (Exception e) {
                Log.e("List", e.getMessage());
            }

            Prefs.setAccessToken(activity, token);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                Log.e("List", e.getMessage());
            }
        }

        @Override
        protected void onCancelled(String token) {
            super.onCancelled(token);
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                Log.e("List", e.getMessage());
            }
        }
    }

    private static class GetRandomQuoteTask extends AsyncTask<Void, Void, String> {

        private ProgressDialog progressDialog;

        private final String progressMessage = activity.getString(R.string.getting_random_quote);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(activity);
            progressDialog.setCancelable(true);
            progressDialog.setMessage(progressMessage);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String accessToken = Prefs.getAccessToken(activity);
                return JWTServerParser.getRandomQuote(accessToken);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String quote) {
            super.onPostExecute(quote);

            try {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            } catch (Exception e) {
                Log.e("List", e.getMessage());
            }

            binding.tvRandomQuote.setText(quote);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                Log.e("List", e.getMessage());
            }
        }

        @Override
        protected void onCancelled(String token) {
            super.onCancelled(token);
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                Log.e("List", e.getMessage());
            }
        }
    }
}
