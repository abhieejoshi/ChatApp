package com.example.abhinav.chatapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    EditText etEmail,etPass,etOtp;
    Button btnSignup,btnSingin,btnOtp;
    private String email,pass;
    int otp,randomNumber;
    ProgressBar progressBar;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    int range = 9;  // to generate a single number with this range, by default its 0..9
    int length = 4;       // by default length is 4

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etEmail = findViewById(R.id.etSignupEmail);
        etPass = findViewById(R.id.etSignUpPass);
        btnSignup = findViewById(R.id.buttonSignUp);
        btnSingin = findViewById(R.id.btnLogin);
        etOtp = findViewById(R.id.et_otp);
        btnOtp = findViewById(R.id.btn_otp);
        progressBar = findViewById(R.id.progressBar2);
        etOtp.setVisibility(View.INVISIBLE);
        btnOtp.setVisibility(View.INVISIBLE);

        progressBar.setVisibility(View.GONE);
        btnSingin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
            }
        });
        btnSignup.setVisibility(View.INVISIBLE);

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                btnSignup.setVisibility(View.VISIBLE);
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = etEmail.getText().toString();
                pass = etPass.getText().toString();
//                otp = Integer.valueOf(etOtp.getText().toString());
                if(validation())
                {
                    // Sending email and token to our server

                    String url = getString(R.string.tokenurl);
                    SharedPreferences sharedPreferences = getApplicationContext()
                            .getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
                    progressBar.setVisibility(View.VISIBLE);
                    final String recentToken = sharedPreferences.getString(getString(R.string.FCM_TOKEN),"");
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(MainActivity.this, "An OTP has been sent", Toast.LENGTH_SHORT).show();
                                    etOtp.setVisibility(View.VISIBLE);
                                    btnOtp.setVisibility(View.VISIBLE);
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    })
                    {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("email", email);
                            params.put("fcm_token",recentToken );
                            return params;
                        }
                    };
                    MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

                    randomNumber = generateRandomNumber();
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt("OTP_PIN", randomNumber);
                    editor.commit();
                    //Toast.makeText(getApplicationContext(),randomNumber+"",Toast.LENGTH_LONG).show();

                    String url2 = getString(R.string.otp_url);
                    StringRequest stringRequest2 = new StringRequest(Request.Method.POST, url2,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
//                                    progressBar.setVisibility(View.GONE);
//                                    Toast.makeText(MainActivity.this, "An OTP has been sent"+response, Toast.LENGTH_SHORT).show();
//                                    etOtp.setVisibility(View.VISIBLE);
//                                    btnOtp.setVisibility(View.VISIBLE);

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    })
                    {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("user_email", email);
                            params.put("otp",randomNumber+"" );
                            return params;
                        }
                    };
                    MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest2);
                }
            }
        });

        btnOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                otp = Integer.valueOf(etOtp.getText().toString());
                if(otp == randomNumber) {

                    progressBar.setVisibility(View.VISIBLE);
                    // Using Firebase Authentication for Signing Up
                    mAuth.createUserWithEmailAndPassword(email, pass)
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        Toast.makeText(MainActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.INVISIBLE);
                                        startActivity(new Intent(MainActivity.this, LoginActivity.class));

                                    } else {
                                        Log.e("exception", "createUserWithEmail:failure", task.getException());
                                    }
                                }
                            });
                }
                else {
                    Toast.makeText(MainActivity.this, "OTP doesent matchl", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getToken() {
        //Generating token and storing it in shared preference

        String recentToken = FirebaseInstanceId.getInstance().getToken();
        SharedPreferences sharedPreferences = getApplicationContext()
                .getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.FCM_TOKEN),recentToken);
        editor.commit();
    }

    public boolean validation() {

        // Email and password validation
        boolean check = true;
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);

        if(!(matcher.matches()))
        {
            Toast.makeText(getApplicationContext(), "Incorrect Emmail ", Toast.LENGTH_LONG).show();
            etEmail.requestFocus();
            etEmail.setError("Wrong email");
            check = false;
        }

        if(pass.length()<6)
        {
            Toast.makeText(getApplicationContext(), "Password should be atleast 6 characters", Toast.LENGTH_LONG).show();
            etPass.requestFocus();
            etPass.setError("password must be of 6 characters");
            check = false;
        }
        return check;
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI acc
        // check share preference if token exists

        SharedPreferences sharedPreferences = getApplicationContext()
                .getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);

        final String recentToken = sharedPreferences.getString(getString(R.string.FCM_TOKEN),"");
        if(recentToken.equals(""))
        {
            getToken();
        }
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (!(currentUser == null)) {
            startActivity(new Intent(MainActivity.this, ChatActivity.class));
        }
    }

    public int generateRandomNumber()
    {
        int randomNumber;

        SecureRandom secureRandom = new SecureRandom();
        String s = "";
        for (int i = 0; i < length; i++) {
            int number = secureRandom.nextInt(range);
            if (number == 0 && i == 0) { // to prevent the Zero to be the first number as then it will reduce the length of generated pin to three or even more if the second or third number came as zeros
                i = -1;
                continue;
            }
            s = s + number;
        }
        randomNumber = Integer.parseInt(s);

        return randomNumber;
    }
}
