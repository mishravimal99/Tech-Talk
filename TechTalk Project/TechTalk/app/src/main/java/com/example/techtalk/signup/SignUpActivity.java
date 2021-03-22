package com.example.techtalk.signup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.techtalk.Common.NodeNames;
import com.example.techtalk.R;
import com.example.techtalk.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    // Declaring variables
    private TextInputEditText etEmail, etName, etPassword, etConfirmPassword;
    private ImageView ivProfile;
    private View progressBar;

    private String email, name, password, confirmPassword;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private StorageReference fileStorage;
    // for path of profile pic
    private Uri localFileUri, serverFileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialising variables
        etEmail = findViewById(R.id.etEmail);
        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        ivProfile = findViewById(R.id.ivProfile);
        progressBar = findViewById(R.id.progressBar);

        fileStorage = FirebaseStorage.getInstance().getReference();


    }

    // For uploading profile image
    public void pickImage(View v){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 101);
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 102);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==101){
            if(resultCode==RESULT_OK){
                localFileUri = data.getData(); // path of image
                ivProfile.setImageURI(localFileUri);

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 102) {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 101);
            }
            else{
                Toast.makeText(this, R.string.permission_required, Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Update name and profile image
    public void updateNameAndPhoto(){
        String strFileName = firebaseUser.getUid() + ".jpg";
        // getting reference
        final StorageReference fileRef = fileStorage.child("images/"+ strFileName);

        progressBar.setVisibility(View.VISIBLE);

        // uploading file
        fileRef.putFile(localFileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                progressBar.setVisibility(View.GONE);
                if(task.isSuccessful()){
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            serverFileUri = uri;
                            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(etName.getText().toString().trim())
                                    .setPhotoUri(serverFileUri)
                                    .build();

                            firebaseUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        // get userID
                                        String userID = firebaseUser.getUid();
                                        // storing in database
                                        databaseReference = FirebaseDatabase.getInstance().getReference().child(NodeNames.USERS);
                                        HashMap<String, String> hashMap = new HashMap<>();
                                        hashMap.put(NodeNames.NAME, etName.getText().toString().trim());
                                        hashMap.put(NodeNames.EMAIL, etEmail.getText().toString().trim());
                                        hashMap.put(NodeNames.ONLINE, "true");
                                        hashMap.put(NodeNames.PHOTO, serverFileUri.getPath());

                                        databaseReference.child(userID).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                Toast.makeText(SignUpActivity.this, R.string.user_created_successfully, Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));

                                            }
                                        });


                                    }
                                    else{
                                        Toast.makeText(SignUpActivity.this, getString(R.string.failed_to_update_profile, task.getException()), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });

    }

    // Update names
    private void updateOnlyName(){

        progressBar.setVisibility(View.VISIBLE);

        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(etName.getText().toString().trim()).build();

        // updating details
        firebaseUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.GONE);

                if(task.isSuccessful()){
                    // get userID
                    String userID = firebaseUser.getUid();
                    // storing in database
                    databaseReference = FirebaseDatabase.getInstance().getReference().child(NodeNames.USERS);
                    HashMap<String, String> hashMap = new HashMap<>();

                    hashMap.put(NodeNames.NAME, etName.getText().toString().trim());
                    hashMap.put(NodeNames.EMAIL, etEmail.getText().toString().trim());
                    hashMap.put(NodeNames.ONLINE, "true");
                    hashMap.put(NodeNames.PHOTO, "");

                    progressBar.setVisibility(View.VISIBLE);

                    databaseReference.child(userID).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            progressBar.setVisibility(View.GONE);

                            Toast.makeText(SignUpActivity.this, R.string.user_created_successfully, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));

                        }
                    });


                }
                else{
                    Toast.makeText(SignUpActivity.this, getString(R.string.failed_to_update_profile, task.getException()), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // OnClickListener for sign up button
    public void btnSignUpClick(View v){
        email = etEmail.getText().toString().trim();
        name = etName.getText().toString().trim();
        password = etPassword.getText().toString().trim();
        confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validating the details
        if(email.equals("")) {
            etEmail.setError(getString(R.string.enter_email));
        }
        else if(name.equals("")) {
            etName.setError(getString(R.string.enter_name));
        }
        else if(password.equals("")) {
            etPassword.setError(getString(R.string.enter_password));
        }
        else if(confirmPassword.equals("")) {
            etConfirmPassword.setError(getString(R.string.confirm_password));
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etEmail.setError(getString(R.string.enter_valid_email));
        }
        else if(!password.equals(confirmPassword)) {
            etConfirmPassword.setError(getString(R.string.password_mismatch));
        }
        else {

            progressBar.setVisibility(View.VISIBLE);

            // Getting instance of FirebaseAuth
            final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

            // Create user
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){

                        progressBar.setVisibility(View.GONE);
                        // Fetching current user details upon successful login
                        firebaseUser = firebaseAuth.getCurrentUser();
                        if(localFileUri!=null){
                            updateNameAndPhoto();
                        }
                        else {
                            updateOnlyName();
                        }
                    }
                    else {
                        Toast.makeText(SignUpActivity.this, getString(R.string.signUp_failed, task.getException()) , Toast.LENGTH_SHORT).show();
                    }

                }
            });


        }
    }
}