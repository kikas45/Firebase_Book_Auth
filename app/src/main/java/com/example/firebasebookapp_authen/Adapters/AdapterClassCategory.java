package com.example.firebasebookapp_authen.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebasebookapp_authen.Filters.FilterCategory;
import com.example.firebasebookapp_authen.Models.ModelCategoryClass;
import com.example.firebasebookapp_authen.PDFEditActivity;
import com.example.firebasebookapp_authen.PDF_List_Activity;
import com.example.firebasebookapp_authen.databinding.ItemRowUserBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AdapterClassCategory extends RecyclerView.Adapter<AdapterClassCategory.ViewHolder> implements Filterable {

    private Context context;
    public ArrayList<ModelCategoryClass> classArrayList, filterList;

    /// binding
    private ItemRowUserBinding binding;

    private DatabaseReference myRef;

    private FilterCategory filter;

    public AdapterClassCategory(Context context, ArrayList<ModelCategoryClass> classArrayList) {
        this.context = context;
        this.classArrayList = classArrayList;
        this.filterList = classArrayList;
    }

    @NonNull
    @Override
    public AdapterClassCategory.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemRowUserBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);  /// use parent.getContext() instead of contex if you wish to add onclick listner here
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterClassCategory.ViewHolder holder, int position) {

        ModelCategoryClass model = classArrayList.get(holder.getAdapterPosition());


        String category = model.getCategory();
        String id = model.getId();
        String uId = model.getuId();
        String timeStamp = model.getTimestamp();

        holder.categoryTitle.setText(category);

        // handle click category

        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                deleteDataCategory(model, holder);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(view.getContext(), PDF_List_Activity.class);
                intent.putExtra("category", category);
                view.getContext().startActivity(intent);



            }
        });


    }


    private void deleteDataCategory(ModelCategoryClass model, ViewHolder holder) {


        PopupMenu popupMenu = new PopupMenu(context.getApplicationContext(), holder.btn_delete);
        popupMenu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                return false;
            }
        });


        popupMenu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                String id = model.getId();

                Toast.makeText(context.getApplicationContext(), "" + id, Toast.LENGTH_SHORT).show();

                myRef = FirebaseDatabase.getInstance().getReference("Categories");

                myRef.child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context.getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


                return false;
            }
        });


        popupMenu.show();


    }


    @Override
    public int getItemCount() {
        return classArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new FilterCategory(filterList, this);
        }
        return filter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryTitle;
        ImageButton btn_delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTitle = binding.itemTitle;
            btn_delete = binding.itemImageButton;
        }
    }


}
