package com.android.bluetooothpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {

    TextInputEditText email, password, fnameedit;
    Button button, regtologbtn;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        progressDialog = new ProgressDialog(SignupActivity.this);
        progressDialog.setTitle("Creating your account");
        progressDialog.setMessage("Please wait...");

        email = findViewById(R.id.emailReg);
        password = findViewById(R.id.passReg);
        fnameedit = findViewById(R.id.fnameReg);
        button = findViewById(R.id.signupBtn);
        regtologbtn = findViewById(R.id.regtolog);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(fnameedit.getText().toString())
                        || TextUtils.isEmpty(email.getText().toString()) || TextUtils.isEmpty(password.getText().toString())) {
                    Snackbar.make(v, "Enter all details!", Snackbar.LENGTH_SHORT).show();
                } else if (password.getText().toString().length() < 6) {
                    Snackbar.make(v, "Keep password at least 6 characters!", Snackbar.LENGTH_SHORT).show();
                } else {
                    progressDialog.show();
                    firebaseAuth.createUserWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim())
                            .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        HashMap<String, Object> userMap = new HashMap<>();

                                        userMap.put("uname", fnameedit.getText().toString());
                                        userMap.put("uemail", email.getText().toString());

                                        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                progressDialog.dismiss();
                                                startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                                finish();
                                            }
                                        });
                                        // Sign in success, update UI with the signed-in user's information

                                    } else {
                                        // If sign in fails, display a message to the user.

                                        progressDialog.dismiss();
                                        Toast.makeText(SignupActivity.this, "Authentication failed!",
                                                Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }
            }
        });

        regtologbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, SigninActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if (FirebaseAuth.getInstance().getCurrentUser() != null || account != null) {
            startActivity(new Intent(SignupActivity.this, MainActivity.class));
            finish();
        }
    }
}