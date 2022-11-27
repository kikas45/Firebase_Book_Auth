package com.example.firebasebookapp_authen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.firebasebookapp_authen.Adapters.Adapter_PDF_User;
import com.example.firebasebookapp_authen.Models.ModelPdfClass;
import com.example.firebasebookapp_authen.databinding.ActivityPdfListBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PDF_List_Activity extends AppCompatActivity {

    private ActivityPdfListBinding binding;

    private ArrayList<ModelPdfClass> arrayList;
    private Adapter_PDF_User adapter;


    private String category, title;

    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfListBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);



        Intent intent = getIntent();

       category = intent.getStringExtra("category");
       binding.bookCategory.setText(category);


        binding.arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        loadData_For_RV();

    }

    private void loadData_For_RV() {

        // imit Category
        arrayList = new ArrayList<>();

        // Let us filter the category were book wil be equal to such category
        // that is we fecth from any part of the data base that has a category which equals the name we intent to fid

        String intent = getIntent().getStringExtra("category"); // e.g we can get category as Physics

        myRef = FirebaseDatabase.getInstance().getReference("Books");
        myRef.orderByChild("category").equalTo(intent).addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {

                if (datasnapshot.exists()){
                    arrayList.clear();


                    for (DataSnapshot ds:datasnapshot.getChildren() ){
                        ModelPdfClass message = new ModelPdfClass();

                        message.setTitle(ds.child("category").getValue().toString());
                        message.setUrl(ds.child("url").getValue().toString());
                        message.setDescription(ds.child("description").getValue().toString());

                        arrayList.add(message);


                    }


                    adapter = new Adapter_PDF_User(getApplicationContext(), arrayList);
                    binding.recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    binding.recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
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
}