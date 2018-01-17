package com.example.abhinav.chatapp;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail,etPass;
    Button btnSignin;
    private String email,pass;
    private FirebaseAuth mAuth;
    RelativeLayout relativeLayout;
    TextView textView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etEmail = findViewById(R.id.etSigninEmail);
        etPass = findViewById(R.id.etSigninPass);
        progressBar = findViewById(R.id.progressBar3);
        btnSignin = findViewById(R.id.buttonSignIN);
        View view = getLayoutInflater().inflate(R.layout.custom_toasst, null);
        textView = view.findViewById(R.id.textToast);
        progressBar.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                email = etEmail.getText().toString();
                pass = etPass.getText().toString();
                progressBar.setVisibility(View.VISIBLE);

                // Firebase authentication for signing in
                mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            progressBar.setVisibility(View.INVISIBLE);
                            final Toast toast = new Toast(getApplicationContext());
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            LayoutInflater inflator = getLayoutInflater();
                            View display = inflator.inflate (R.layout.custom_toasst, (ViewGroup) findViewById(R.id.relative));
                            toast.setView(display);


                            FirebaseAuth auth = FirebaseAuth.getInstance();
                            FirebaseUser user = auth.getCurrentUser();

                            if(!(user.isEmailVerified()))
                            {
                            user.sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("Email Sent", "Email sent.");
                                                Toast.makeText(getApplicationContext(),"Verify Email, Before Log In",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                            }
                            else
                            {
                                toast.show();
                                Toast.makeText(getApplicationContext(),"Login SuccessFull",
                                        Toast.LENGTH_LONG).show();

                                startActivity(new Intent(LoginActivity.this,ChatActivity.class));
                            }

                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"Login Fail"+task.getException(),
                                    Toast.LENGTH_LONG).show();
                            Log.e("error","failed"+task.getException());
                        }
                    }
                });
            }
        });
    }
}
