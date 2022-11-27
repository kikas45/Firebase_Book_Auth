package com.example.firebasebookapp_authen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.firebasebookapp_authen.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;


    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        // the progressbar
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);


        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        binding.registerAccount.setOnClickListener(v -> validDateData());


    }
    private String name = "", email = "", password = "";

    private void validDateData() {

        name = binding.nameOfAccountForregister.getText().toString().trim();
        email = binding.enterEmailForRegister.getText().toString().trim();
        password = binding.passwordForAccountRegister.getText().toString().trim();
        String cPassword = binding.passwordComfirmRegister2.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getApplicationContext(), "Enter your Name", Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getApplicationContext(), "Invalid Email Format", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter a Valid Password", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(cPassword)) {
            Toast.makeText(getApplicationContext(), "Confirm Password", Toast.LENGTH_SHORT).show();
        }else if (!password.equals(cPassword)){
            Toast.makeText(getApplicationContext(), "Password does not match", Toast.LENGTH_SHORT).show();
        }else {
            /// all data validated, then create account
            createAccount();
        }
    }

    private void createAccount() {
        //shwo progress bar
        progressDialog.setMessage("Creating Account... ");
        progressDialog.show();

        // creer in firebase
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // progressDialog.dismiss();

                upDateuserInfo();

            }
        }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                progressDialog.dismiss();

                // Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void upDateuserInfo() {
        progressDialog.setMessage("Saving User Info....");

        /// timeStamp

        long timeStamp = System.currentTimeMillis();


        /// since user is registered, we can get the user ID
        String uId = firebaseAuth.getUid();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uId", uId);
        hashMap.put("email", email);
        hashMap.put("name", name);
        hashMap.put("profileImage", ""); // later we do it
        hashMap.put("userType", "user");  // this user can be admin or user, we will set admin manually in firebase
        hashMap.put("timestamp", timeStamp);

        DatabaseReference  ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uId).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Account Created...", Toast.LENGTH_SHORT).show();

                    /// since user account is created, we naviagte
                    startActivity(new Intent(getApplicationContext(), DashBord_User_Activity.class));
                    finish();
                }else {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Failed to Created Account... ", Toast.LENGTH_SHORT).show();

                }

            }
        });


              /*  .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();

                        Toast.makeText(getApplicationContext(), "Account Created...", Toast.LENGTH_SHORT).show();

                        /// since user account is created, we naviagte
                        startActivity(new Intent(getApplicationContext(), DashBord_User_Activity.class));
                        finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Failed to Created Account... ", Toast.LENGTH_SHORT).show();

                    }
                });*/

    }
}