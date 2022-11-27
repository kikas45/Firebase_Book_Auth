package com.example.firebasebookapp_authen;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.firebasebookapp_authen.Models.ModelCategoryClass;
import com.example.firebasebookapp_authen.databinding.ActivityPdfactivityBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class PDFActivity extends AppCompatActivity {
    private ActivityPdfactivityBinding binding;

    private FirebaseAuth firebaseAuth;
    private static final String TAG = "ADD_PDF_TAG";
    private static final int PDF_PICK_CODE = 1000;
    private String title = "", description = "", categoery = "";
    // let pcik pdf
    Uri pdfUri;

    /// arraylist of PDF categories
    ArrayList<ModelCategoryClass> classArrayList;
    /// database
    private DatabaseReference myRef;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfactivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // the firebase Authen
        firebaseAuth = FirebaseAuth.getInstance();
        loginPdfCategories();

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


        binding.attAchBtn.setOnClickListener(v -> pdfIntent());

        binding.editBookCategory.setOnClickListener(v -> cateGoryPickDialog());

        binding.btnUpload.setOnClickListener(v -> valiDateData());


    }



    private void valiDateData() {

        Log.d(TAG, "valiDateData: validating data....");

        title = binding.editBookTitle.getText().toString().trim();
        description = binding.editBookDescription.getText().toString().trim();
        categoery = binding.editBookCategory.getText().toString().trim();

        // STEP: 1
        // validate the data

        if (TextUtils.isEmpty(title)) {
            Toast.makeText(getApplicationContext(), "Enter Title", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(description)) {
            Toast.makeText(getApplicationContext(), "Enter Description", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(categoery)) {
            Toast.makeText(getApplicationContext(), "Pick Categoery", Toast.LENGTH_SHORT).show();
        } else if (pdfUri == null) {
            Toast.makeText(getApplicationContext(), "Pick PDF", Toast.LENGTH_SHORT).show();
        } else {
            /// all data validated, let now pick item
            upLoadPDFtoStorage();

        }

    }

    private void upLoadPDFtoStorage() {
        // STEP 2 : upload Files to Firebase Storage
        Log.d(TAG, "upLoadPDFtoStorage: uplading data to storage...");
        progressDialog.setMessage("Uploading PDF");
        progressDialog.show();

        long timesatamp = System.currentTimeMillis();

        /// Path of PDF in Storage
        String filePathAndName = "Books/" + timesatamp;
        /// Storage of Reference

        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
        storageReference.putFile(pdfUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "onSuccess: PDF upload Successful");
                Log.d(TAG, "onSuccess: getting PDF url");

                // getting the PDF URL
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();

                while (!uriTask.isSuccessful());  // i dont know why , but it stop the stuffs from crashing just the way it is
                String upLoadedPDFurl = "" + uriTask.getResult();

                // now Upload to Firebase db;
                upLoadPDFtoFirebaseDB(upLoadedPDFurl, timesatamp);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Log.d(TAG, "onFailure: PDf Uplaod Failed due to " + e.getMessage());
                Toast.makeText(getApplicationContext(), "PDF Failed to Upload due to " + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void upLoadPDFtoFirebaseDB(String upLoadedPDFurl, long timesatamp) {

        // STEP 3 : upload PDF url into Firebase DB
        Log.d(TAG, "upLoadPDFtoFirebaseDB: uploadint ino firebase DB");

        progressDialog.setMessage("Uploading PDF Info");
        String uId = firebaseAuth.getUid();

        //set Up data to UpLoad

        HashMap<String , Object> hashMap = new HashMap<>();
        hashMap.put("uid", uId);
        hashMap.put("id", timesatamp);
        hashMap.put("title", ""+ title);
        hashMap.put("description", ""+ description);
        hashMap.put("category", ""+ categoery);
        hashMap.put("url", ""+ upLoadedPDFurl);
        hashMap.put("timestamp", timesatamp);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Books");
        reference.child(""+timesatamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                progressDialog.dismiss();
                Log.d(TAG, "onSuccess: Upload Successfully...");
                Toast.makeText(getApplicationContext(), " Upload Successfully...", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Log.d(TAG, "onFailure: Failed to Upload due to " + e.getMessage());
                Toast.makeText(getApplicationContext(), "Failed to Upload due to " + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        /// db reference


    }

    private void loginPdfCategories() {
        Log.d(TAG, "loginPdfCategories: loding pdf categories...");


        // imit Category
        classArrayList = new ArrayList<>();

        myRef = FirebaseDatabase.getInstance().getReference("Categories");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {


                if (datasnapshot.exists()) {
                    classArrayList.clear();

                    for (DataSnapshot ds : datasnapshot.getChildren()) {
                        ModelCategoryClass message = new ModelCategoryClass();

                        message.setCategory(Objects.requireNonNull(ds.child("category")
                                .getValue()).toString());  // note if we get a category type physics
                        /// we get back physics in our upload take in
                        classArrayList.add(message);

                    }

                } else {
                    Toast.makeText(getApplicationContext(), "data not found", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void cateGoryPickDialog() {
        Log.d(TAG, "cateGoryPickDialog: showing category picking dialog");

        // string for getting arraylist
        String[] listArray = new String[classArrayList.size()];

        for (int i = 0; i < classArrayList.size(); i++) {
            listArray[i] = classArrayList.get(i).getCategory();
        }
        /// alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Category")
                .setItems(listArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {

                        String name_of_category = listArray[which];  // e.g, we get a category type physics
                        binding.editBookCategory.setText(name_of_category);
                        Log.d(TAG, "onClick: Selected Category" + name_of_category);

                    }
                }).show();

    }

    private void pdfIntent() {

        Log.d(TAG, "pdfIntent: starting pdf pick intent");

        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PDF_PICK_CODE);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PDF_PICK_CODE && data!=null){
            pdfUri = data.getData();
            String  name_text = pdfUri.toString();

            Toast.makeText(getApplicationContext(), ""+name_text, Toast.LENGTH_SHORT).show();


        }else {
            Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
        }
    }


}