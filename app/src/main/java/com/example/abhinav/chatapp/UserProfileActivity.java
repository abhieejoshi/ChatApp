package com.example.abhinav.chatapp;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.io.FileNotFoundException;
import java.io.InputStream;

import static android.app.Activity.RESULT_OK;

public class UserProfileActivity extends Fragment {

    EditText etEmail,etName;
    Button btnUpdate;
    ImageView ivProfilePic;
    String usrEmail,usrName;
    Uri usrImage;
    View rootView;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
        {
            usrImage = data.getData();
            final InputStream imageStream;
            try {
                imageStream = getActivity().getContentResolver().openInputStream(usrImage);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                ivProfilePic.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        else {
            Toast.makeText(getActivity(), "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.activity_user_profile, container, false);

        etEmail      = rootView.findViewById(R.id.edit_email);
        etName       = rootView.findViewById(R.id.edit_name);
        btnUpdate    = rootView.findViewById(R.id.btn_pdate);
        ivProfilePic = rootView.findViewById(R.id.imageView);
        //ivProfilePic.setImageResource(R.drawable.camera);

        ivProfilePic.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.camera));
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // getting user email
            usrEmail = user.getEmail();
            usrName = user.getDisplayName();
            usrImage = user.getPhotoUrl();

            etName.setText(usrName);
            etEmail.setText(usrEmail);
            final InputStream imageStream;
            try {
                if (usrImage != null) {
                    imageStream = getActivity().getContentResolver().openInputStream(usrImage);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    ivProfilePic.setImageDrawable(ContextCompat.getDrawable(getActivity(), android.R.drawable.ic_menu_camera));
                   // ivProfilePic.setImageBitmap(selectedImage);
                }
            }catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            etEmail.setEnabled(false);

        }


        ivProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);
            }
        });


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(etName.getText().toString())
                        .setPhotoUri(usrImage)
                        .build();

                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    Toast.makeText(getActivity(), "User Information Updated",
                                            Toast.LENGTH_LONG).show();
                                }
                                else
                                {
                                    Toast.makeText(getActivity(), task.getException().toString(),
                                            Toast.LENGTH_LONG).show();

                                }
                            }
                        });
            }
        });


        return rootView;
    }

}
