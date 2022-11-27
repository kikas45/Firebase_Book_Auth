package com.example.firebasebookapp_authen;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.firebasebookapp_authen.databinding.ActivityPdfEditBinding;

public class PDFEditActivity extends AppCompatActivity {
ActivityPdfEditBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfEditBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        
        binding.editBookCategory.setOnClickListener(v->createMyuser());


    }

    private void createMyuser() {
    }
}