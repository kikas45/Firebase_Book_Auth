package com.example.firebasebookapp_authen.Adapters;

import static com.example.firebasebookapp_authen.Filters.Constants.MAX_BYTES_PDF;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.firebasebookapp_authen.Models.ModelPdfClass;
import com.example.firebasebookapp_authen.R;
import com.example.firebasebookapp_authen.databinding.ItemRowUserPdfBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class Adapter_PDF_User extends RecyclerView.Adapter<Adapter_PDF_User.ViewHolder> {


    ItemRowUserPdfBinding binding;

    private static final String TAG = "PDF_ADAPTER";

    private Context context;
    private ArrayList<ModelPdfClass> arrayList;

    public Adapter_PDF_User(Context context, ArrayList<ModelPdfClass> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public Adapter_PDF_User.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemRowUserPdfBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_PDF_User.ViewHolder holder, int position) {


        ModelPdfClass model = arrayList.get(holder.getAdapterPosition());
        String title = model.getTitle();
        String description = model.getDescription();
        String url = model.getUrl();
        long timestamp = model.getTimestamp();

        /// we need to convert time to required Format

        String formatDate = MyApplication.formatTimestamp(timestamp);

        holder.book_title.setText(title);
        holder.book_description.setText(description);
        holder.date_category.setText(formatDate);


        /// load further categories of Book such as : sieze, url etc


//        loadCategory(model, holder);
//        load_pdf_url(model, holder);
      loadCate_Sieze(model, holder);



        loadGlideImage(model, holder);


    }

    private void loadGlideImage(ModelPdfClass model, ViewHolder holder) {

        String url = model.getUrl();

        Glide.with(context).load(url).addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                holder.progressBar.setVisibility(View.VISIBLE);
                return false;

            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                holder.progressBar.setVisibility(View.INVISIBLE);

                return false;
            }
        }).fitCenter().placeholder(R.drawable.ic_3d_rotation_24).into(holder.bookImagePdf);
    }

    private void loadCate_Sieze(ModelPdfClass model, ViewHolder holder) {


        try {

            String pdfUrl = model.getUrl();
            StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
            ref.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                @SuppressLint({"DefaultLocale", "SetTextI18n"})
                @Override
                public void onSuccess(StorageMetadata storageMetadata) {

                    double byes = storageMetadata.getSizeBytes();

                    /// convert byes to KB , MB

                    double kb = byes / 1024;
                    double mb = kb / 1024;

                    if (mb >= 1) {
                        holder.book_sieze.setText(String.format("%.2f", mb) + "  MB");
                    } else if (kb >= 1) {
                        holder.book_sieze.setText(String.format("%.2f", kb) + "  KB");
                    } else {
                        holder.book_sieze.setText(String.format("%.2f", byes) + "  bytes");
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(context.getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });

        }catch (Exception e){
            Toast.makeText(context.getApplicationContext(), "Unable to simplify Url", Toast.LENGTH_SHORT).show();
        }

    }

    private void load_pdf_url(ModelPdfClass model, ViewHolder holder) {

        String pdf_url = model.getUrl();
        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(pdf_url);
        reference.getBytes(MAX_BYTES_PDF).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {


                //     Glide.with(context.getApplicationContext()).load(model.getUrl()).fitCenter().placeholder(R.drawable.ic_3d_rotation_24).into(holder.bookImagePdf);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private void loadCategory(ModelPdfClass model, ViewHolder holder) {

        String categoryId = model.getCategoryID();
        DatabaseReference refs = FirebaseDatabase.getInstance().getReference("Categories");
        refs.child(categoryId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String category = "" + snapshot.child("category").getValue();

                holder.book_category.setText(category);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView bookImagePdf, image_more;
        ProgressBar progressBar;
        TextView book_title, book_description, book_sieze, date_category, book_category;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            bookImagePdf = binding.bookImagePdf;
            image_more = binding.imageMore;
            progressBar = binding.progressBarPdfItem;
            book_title = binding.bookTitle;
            book_description = binding.bookDescription;
            book_sieze = binding.bookSieze;
            date_category = binding.dateCategory;
            book_category = binding.bookCategory;

        }
    }
}
