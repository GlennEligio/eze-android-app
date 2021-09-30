package com.example.eze.helper;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eze.R;
import com.example.eze.dtos.RequestDto;
import com.example.eze.model.Item;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class ReqRecViewAdapter extends ListAdapter<RequestDto, ReqRecViewAdapter.ViewHolder> {

    public OnItemClickListener listener;

    public ReqRecViewAdapter() {
        super(DIFF_CALLBACK);
    }

    public static final DiffUtil.ItemCallback<RequestDto> DIFF_CALLBACK = new DiffUtil.ItemCallback<RequestDto>() {
        @Override
        public boolean areItemsTheSame(@NonNull RequestDto oldItem, @NonNull RequestDto newItem) {
            return Objects.equals(oldItem.id, newItem.id);
        }

        @Override
        public boolean areContentsTheSame(@NonNull RequestDto oldItem, @NonNull RequestDto newItem) {

            String oldItemIds = Helper.combineItemIds(oldItem.getItems(), false);
            String newItemIds = Helper.combineItemIds(newItem.getItems(), false);

            return oldItem.getCode().equals(newItem.getCode()) &&
                    oldItem.getCreatedDate().equals(newItem.createdDate) &&
                    oldItem.getItems().size() == newItem.getItems().size() &&
                    oldItemIds.equals(newItemIds) &&
                    oldItem.getProfessorName().equals(newItem.getProfessorName()) &&
                    oldItem.getStatus().equals(newItem.getStatus()) &&
                    oldItem.getStudentName().equals(newItem.getStudentName());
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item, parent, false);
        return new ViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RequestDto requestDto = getItem(position);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, LLL dd, yyyy - KK:mm:ss a");

        holder.txt_request_date.setText(requestDto.getCreatedDate().toLocalDateTime().format(formatter));
        holder.txt_request_items.setText(Helper.combineItemNames(requestDto.getItems(), true));
    }

    @Override
    protected RequestDto getItem(int position) {
        return super.getItem(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private final TextView txt_request_items;
        private final TextView txt_request_date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_request_items = itemView.findViewById(R.id.txt_item_names);
            txt_request_date = itemView.findViewById(R.id.txt_request_date);

            ImageView img_accept = itemView.findViewById(R.id.img_accept_request);
            ImageView img_reject = itemView.findViewById(R.id.img_reject_request);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(listener != null && position != RecyclerView.NO_POSITION){
                        listener.onItemClick(getItem(position), "Pending");
                    }
                }
            });

            img_accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(listener != null && position != RecyclerView.NO_POSITION){
                        listener.onItemClick(getItem(position), "Accept");
                    }
                }
            });

            img_reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(listener != null && position != RecyclerView.NO_POSITION){
                        listener.onItemClick(getItem(position), "Reject");
                    }
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(RequestDto requestDto, String status);
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
