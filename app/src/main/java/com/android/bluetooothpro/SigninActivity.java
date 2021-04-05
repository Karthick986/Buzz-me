package com.android.bluetooothpro;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SigninActivity extends AppCompatActivity {

    TextInputEditText email, password;
    Button button, logtoregbtn;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;
    TextView forgottxt;
    private Button signInButton;
    GoogleSignInClient mgoogleSignInClient;
    private FirebaseAuth mAuth;
    private int RC_SIGn_In = 14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        progressDialog = new ProgressDialog(SigninActivity.this);
        progressDialog.setTitle("Validating your detials");
        progressDialog.setMessage("Please wait...");

        forgottxt = findViewById(R.id.forPasstxt);
        email = findViewById(R.id.emailLog);
        password = findViewById(R.id.passLog);
        button = findViewById(R.id.signinBtn);
        logtoregbtn = findViewById(R.id.logtoreg);

        signInButton = findViewById(R.id.signGooglebtn);
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();

        mgoogleSignInClient = GoogleSignIn.getClient(this,gso);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(email.getText().toString()) || TextUtils.isEmpty(password.getText().toString())) {
                    Snackbar.make(v, "Enter all details!", Snackbar.LENGTH_SHORT).show();
                }
                else {

                    progressDialog.show();

                    firebaseAuth.signInWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim())
                            .addOnCompleteListener(SigninActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        progressDialog.dismiss();
                                        startActivity(new Intent(SigninActivity.this, MainActivity.class));
                                        finish();
                                        // Sign in success, update UI with the signed-in user's information

                                    } else {
                                        // If sign in fails, display a message to the user.

                                        progressDialog.dismiss();
                                        Toast.makeText(SigninActivity.this, "Authentication failed!",
                                                Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }
            }
        });

        logtoregbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SigninActivity.this, SignupActivity.class));
                finish();
            }
        });

        forgottxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.setTitle("Recovering");
                progressDialog.setMessage("Please wait...");
                forgotshow();
            }
        });
    }

    private  void signIn(){
        Intent SignInIntent = mgoogleSignInClient.getSignInIntent();
        startActivityForResult(SignInIntent,RC_SIGn_In);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGn_In)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSigInResults(task);
        }
    }

    private void handleSigInResults(Task<GoogleSignInAccount> completeTask){
        try {
            GoogleSignInAccount acc = completeTask.getResult(ApiException.class);
            FireBaseGoogleOuth(acc);

        }catch (ApiException e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    private void FireBaseGoogleOuth(final GoogleSignInAccount account){

        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {

                    HashMap<String, Object> userMap = new HashMap<>();

                    userMap.put("uname", account.getGivenName());
                    userMap.put("uemail", account.getEmail());

                    databaseReference.child(firebaseAuth.getCurrentUser().getUid()).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            progressDialog.dismiss();
                            startActivity(new Intent(SigninActivity.this, MainActivity.class));
                            finish();
                        }
                    });
                }else {
                    progressDialog.dismiss();
                    Toast.makeText(SigninActivity.this, "Failed...! Try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void forgotshow() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");

        LinearLayout linearLayout = new LinearLayout(this);
        final EditText emailText = new EditText(this);
        emailText.setHint("Enter email");
        emailText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailText.setMinEms(15);

        linearLayout.addView(emailText);
        linearLayout.setPadding(10, 10, 10,10);

        builder.setView(linearLayout);

        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String recoveremail = emailText.getText().toString();

                if (recoveremail.isEmpty()) {
                    progressDialog.dismiss();
                    Toast.makeText(SigninActivity.this, "Please enter your email",
                            Toast.LENGTH_SHORT).show();
                } else {
                    beginRecovery(recoveremail);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                progressDialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void beginRecovery(String foremail) {

        progressDialog.show();

        firebaseAuth.sendPasswordResetEmail(foremail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(SigninActivity.this, "Recovery link is sent to your registered email, Please check!",
                                    Toast.LENGTH_LONG).show();
                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(SigninActivity.this, "Failed...! Try again",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(SigninActivity.this, "Error Occurred!" +e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });

    }

//    public void checkConnection() {
//        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(
//                Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
//
//        if (null == activeNetwork) {
//
//            progressDialog.dismiss();
//            Toast.makeText(Loginpage.this, "No Internet Connection!",
//                    Toast.LENGTH_LONG).show();
//        }
//    }
}

