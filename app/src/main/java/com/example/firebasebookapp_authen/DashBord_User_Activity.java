package com.example.firebasebookapp_authen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.firebasebookapp_authen.Adapters.AdapterClassCategory;
import com.example.firebasebookapp_authen.Models.ModelCategoryClass;
import com.example.firebasebookapp_authen.databinding.ActivityDashUserBordBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class DashBord_User_Activity extends AppCompatActivity {

    ActivityDashUserBordBinding binding;

    private    FirebaseAuth firebaseAuth;
    private   FirebaseUser firebaseUser;
    private DatabaseReference myRef;
    private ArrayList<ModelCategoryClass> arrayList;
    private AdapterClassCategory adapterClassCategory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashUserBordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        checkUser();
        loadCategory();



        binding.addPDFFloatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), PDFActivity.class));
            }
        });


        binding.searcheditt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // called when the text is typed of

                try {
                    adapterClassCategory.getFilter().filter(s);
                }catch (Exception e){

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        binding.imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firebaseAuth.signOut();
                checkUser();
            }
        });




        binding.addCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CategoryActivity.class));

            }
        });




    }



    private void loadCategory() {

        // imit Category
        arrayList = new ArrayList<>();
        //   ref = FirebaseDatabase.getInstance().getReference("Categories");

        myRef = FirebaseDatabase.getInstance().getReference("Categories");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {

                if (datasnapshot.exists()){
                    arrayList.clear();


                    for (DataSnapshot ds:datasnapshot.getChildren() ){
                        ModelCategoryClass mModel = new ModelCategoryClass();

                        mModel.setCategory(ds.child("category").getValue().toString()); // e.g we just get a simple name like Physics as category

                        arrayList.add(mModel);


                    }


                    adapterClassCategory = new AdapterClassCategory(getApplicationContext(), arrayList);
                    binding.recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    binding.recyclerView.setAdapter(adapterClassCategory);
                    adapterClassCategory.notifyDataSetChanged();
                }else {
                    Toast.makeText(getApplicationContext(), "data not found", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });



    }

    private void checkUser() {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }else {
            String email = firebaseUser.getEmail();
            binding.adminEmail.setText(email);
        }

    }
}