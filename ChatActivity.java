package com.example.abhinav.chatapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    String fromEmail,toEmail,msg;
    Button btnSend;
    EditText etMail,etMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        btnSend = findViewById(R.id.buttonSend);
        etMail = findViewById(R.id.et_email);
        etMsg = findViewById(R.id.et_msg);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.icon);
      //  getSupportActionBar().setDisplayUseLogoEnabled(true);

        FirebaseMessaging.getInstance().subscribeToTopic("Hello");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // getting user email
            fromEmail = user.getEmail();
            Toast.makeText(getApplicationContext(), "WELCOME: "+fromEmail, Toast.LENGTH_LONG).show();
        }

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toEmail = etMail.getText().toString();
                msg = etMsg.getText().toString();



                // sending from to and message to server
                String url = getString(R.string.messageurl);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

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
                        params.put("from_email", fromEmail);
                        params.put("to_email", toEmail);
                        params.put("message", msg );
                        return params;
                    }
                };
                MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_refresh:
                startActivity(new Intent(ChatActivity.this, UserProfileActivity.class));
                break;
            case R.id.action_settings:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ChatActivity.this,MainActivity.class));
                break;
            default:
                break;
        }
        return true;
    }
}
