package com.example.firebasebookapp_authen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.example.firebasebookapp_authen.databinding.ActivitySplashBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Splash_Activity extends AppCompatActivity {

    Handler handler;
    private FirebaseUser firebaseUser;
    private DatabaseReference ref;

    ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        handler = new Handler(Looper.getMainLooper());
;      firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


    }

    @Override
    protected void onStart() {
        super.onStart();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                checkUser();
            }
        }, 200);
    }

    private void checkUser() {

        if (firebaseUser == null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }else {
            ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // get user type
                    String userType = "" + snapshot.child("userType").getValue();
                    if (userType.equals("user")) {
                        startActivity(new Intent(getApplicationContext(), DashBord_User_Activity.class));
                        finish();

                    } else if (userType.equals("admin")) {
                        startActivity(new Intent(getApplicationContext(), DashBord_Admin_Activity.class));
                        finish();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

    }
}