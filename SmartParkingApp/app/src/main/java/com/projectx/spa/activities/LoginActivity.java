package com.projectx.spa.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;
import com.orhanobut.logger.Logger;
import com.projectx.spa.R;
import com.projectx.spa.helpers.Constants;
import com.projectx.spa.helpers.FbHelper;
import com.projectx.spa.helpers.UserSession;
import com.projectx.spa.interfaces.OnAuthListener;
import com.projectx.spa.interfaces.OnSnapshotListener;
import com.projectx.spa.models.User;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button logIn;
    private EditText emailEditText, passwordEditText;
    private TextView register, forgot;
    private ProgressBar progressBar;
    private UserSession userSession;
    private FbHelper fbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getSupportActionBar().setTitle("Login");
        setContentView(R.layout.activity_login);

        userSession = new UserSession(this);
        Intent intent = new Intent();
        if (userSession.isUserLoggedIn()) {
            intent.setClass(this, AdminHomeActivity.class);
            intent.putExtra(Constants.CALLING_ACTIVITY, getClass().getSimpleName());

            // Closing all the Activities from stack
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // Add new Flag to start new Activity
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
            finish();
            /*String id = userSession.getUserDetails().get(Constants.PREF_ID);
            FbHelper fbHelper = new FbHelper(getApplicationContext());
            DocumentReference doc = fbHelper.toDocumentReference(Constants.USERS + "/" + id);
            doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    user = documentSnapshot.toObject(User.class);

                    intent.putExtra("user", user);


                }
            });*/
        }

        logIn = findViewById(R.id.login);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        register = findViewById(R.id.reg);
        forgot = findViewById(R.id.forgot);

        fbHelper = new FbHelper(this);

        logIn.setOnClickListener(this);
        register.setOnClickListener(this);
        forgot.setOnClickListener(this);

        progressBar = findViewById(R.id.progressBar);
    }

    public void onClick(View v) {
        if (v.equals(register)) {
            startActivity(new Intent(this, RegisterActivity.class));
        } else if (v.equals(forgot)) {
            showForgotPasswordDialog(v);
        } else if (v.equals(logIn)) {
            userLogInMethod();
        }
    }

    private void userLogInMethod() {
        unHideProgressBar();

        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required.");
            hideProgressBar();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("password is required");
            hideProgressBar();
            return;
        }
        if (email.isEmpty() || !(Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
            emailEditText.setError("email is not proper");
            hideProgressBar();
            return;
        }
        if (password.length() < 6) {
            passwordEditText.setError("password must be at least 6 characters");
            hideProgressBar();
            return;
        }

        // authenticating data in firebase
        fbHelper.authenticateUser(email, password, new OnAuthListener() {
            @Override
            public void onSuccess(FirebaseUser firebaseUser) {
                Logger.d("SUCCESS");

                String id = firebaseUser.getUid();

                String documentReference = Constants.USERS + "/" + id;
                fbHelper.readDocumentFromFirestore(User.class, documentReference, new OnSnapshotListener() {
                    @Override
                    public <T> void onSuccess(T object) {
                        User user = (User) object;

                        Logger.d(user.toString());

                        userSession.createUserLoginSession(id, user.getName(), user.getEmail());
                        makeToast("Sign in successful");

                        Intent intent = new Intent(getApplicationContext(), AdminHomeActivity.class);

                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Logger.d(errorMessage);
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                hideProgressBar();
                makeToast("Error !!" + errorMessage);
                Logger.d("FAILURE: " + errorMessage);
            }
        });
    }

    private void showForgotPasswordDialog(View v) {
        EditText resetMail = new EditText(v.getContext());
        resetMail.setPadding(67, 0, 0, 30);
        resetMail.setHint("email");
        AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
        passwordResetDialog.setCancelable(false);
        passwordResetDialog.setTitle("Reset password?");
        passwordResetDialog.setMessage("Enter your email to receive reset link");
        passwordResetDialog.setView(resetMail);

        passwordResetDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //extract email
                String mail = resetMail.getText().toString();

                fbHelper.resetPassword(mail, new OnSnapshotListener() {
                    @Override
                    public <T> void onSuccess(T object) {
                        makeToast(object.toString());
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        makeToast(errorMessage);
                    }
                });
            }
        });
        passwordResetDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //goto login page
            }
        });
        passwordResetDialog.create().show();
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
        logIn.setVisibility(View.VISIBLE);
        forgot.setVisibility(View.VISIBLE);
        register.setVisibility(View.VISIBLE);
    }

    private void unHideProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        logIn.setVisibility(View.INVISIBLE);
        forgot.setVisibility(View.INVISIBLE);
        register.setVisibility(View.INVISIBLE);
    }

    private void makeToast(String toastMessage) {
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
    }
}