package com.example.abhinav.chatapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * Created by abhinav on 11/1/18.
 */

public class ChatFragment extends Fragment {

    String fromEmail,toEmail,msg;
    Button btnSend;
    EditText etMail,etMsg;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View rootView = inflater.inflate(R.layout.chat_fragment, container, false);

        btnSend = rootView.findViewById(R.id.buttonSend);
        etMail = rootView.findViewById(R.id.et_email);
        etMsg = rootView.findViewById(R.id.et_msg);


        FirebaseMessaging.getInstance().subscribeToTopic("Hello");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // getting user email
            fromEmail = user.getEmail();
            Toast.makeText(getActivity(), "WELCOME: "+fromEmail, Toast.LENGTH_LONG).show();
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
                MySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
            }
        });


        return rootView;

    }
}
