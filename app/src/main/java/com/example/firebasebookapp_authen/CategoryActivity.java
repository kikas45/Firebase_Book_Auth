package com.example.firebasebookapp_authen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.firebasebookapp_authen.databinding.ActivityCategoryBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class CategoryActivity extends AppCompatActivity {

    private ActivityCategoryBinding binding;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference ref;


    private ProgressDialog progressDialog;

    private  String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        firebaseAuth = FirebaseAuth.getInstance();
        // the progressbar
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);


        binding.summitButton.setOnClickListener(view1 -> validDateData());



    }

    private void validDateData() {

        category = binding.editTextCategory.getText().toString().trim();

        if (TextUtils.isEmpty(category)){
            Toast.makeText(getApplicationContext(), "Please Add Category...", Toast.LENGTH_SHORT).show();
        }else {
            addCategoryFireBase();
        }


    }

    private void addCategoryFireBase() {

        progressDialog.setMessage("Saving User Info....");
        progressDialog.show();

        long timeStamp = System.currentTimeMillis();

        HashMap<String , Object> hashMap = new HashMap<>();
        hashMap.put("id", timeStamp);
        hashMap.put("category", ""+ category);
        hashMap.put("timestamp", timeStamp);
        hashMap.put("uId", firebaseAuth.getUid());


        ref = FirebaseDatabase.getInstance().getReference("Categories");

        ref.child(""+timeStamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Category Added Successful", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();


            }
        });

    }
}