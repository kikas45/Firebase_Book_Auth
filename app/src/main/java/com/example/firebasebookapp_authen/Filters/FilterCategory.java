package com.example.firebasebookapp_authen.Filters;

import android.annotation.SuppressLint;
import android.widget.Filter;

import com.example.firebasebookapp_authen.Adapters.AdapterClassCategory;
import com.example.firebasebookapp_authen.Models.ModelCategoryClass;

import java.util.ArrayList;

public class FilterCategory extends Filter {
    ArrayList<ModelCategoryClass> filterList;
    AdapterClassCategory adapterFilter;

    public FilterCategory(ArrayList<ModelCategoryClass> filterList, AdapterClassCategory adapterFilter) {
        this.filterList = filterList;
        this.adapterFilter = adapterFilter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraints) {
        FilterResults results = new FilterResults();
        // value shoyld not be null and empty
        if (constraints !=null && constraints.length()>0){
            // change to upper case or lower case
            constraints = constraints.toString().toUpperCase();
            ArrayList<ModelCategoryClass> filterModel = new ArrayList<>();
            for (int i = 0; i < filterList.size(); i++) {
                if (filterList.get(i).getCategory().toUpperCase().contains(constraints)){
                    filterModel.add(filterList.get(i));
                }

            }

            results.count = filterModel.size();
            results.values = filterModel;

        }else {
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        adapterFilter.classArrayList = (ArrayList<ModelCategoryClass>)filterResults.values;
        adapterFilter.notifyDataSetChanged();

    }
}
