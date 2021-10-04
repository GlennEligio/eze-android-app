package com.eze.helper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eze.R;
import com.eze.model.Item;

import java.util.List;

public class ItemRecViewAdapter extends RecyclerView.Adapter<ItemRecViewAdapter.ViewHolder>{

    private List<Item> itemContents;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemContents.get(position);
        holder.txt_itemName.setText(item.getName());
        holder.txt_itemDescription.setText(item.getDescription());
        holder.txt_itemNumber.setText(String.valueOf(position + 1));
    }

    @Override
    public int getItemCount() {
        return itemContents.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final TextView txt_itemName;
        private final TextView txt_itemDescription;
        private final TextView txt_itemNumber;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_itemNumber = itemView.findViewById(R.id.txt_item_number);
            txt_itemName = itemView.findViewById(R.id.txt_item_name);
            txt_itemDescription = itemView.findViewById(R.id.txt_item_description);
        }
    }

    public void setItemContents(List<Item> itemContents) {
        this.itemContents = itemContents;
    }
}
