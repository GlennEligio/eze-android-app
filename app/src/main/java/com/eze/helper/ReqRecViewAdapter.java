package com.eze.helper;

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
import com.eze.dtos.RequestDto;

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
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_request_list_item, parent, false);
        return new ViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RequestDto requestDto = getItem(position);

        holder.txt_request_date.setText(Helper.convertOffSetDateTimeToString(requestDto.getCreatedDate()));
        holder.txt_request_items.setText(Helper.combineItemNames(requestDto.getItems(), true));
        if(!requestDto.getStatus().equals("Pending")){
            holder.img_reject.setVisibility(View.GONE);
            holder.img_accept.setVisibility(View.GONE);
        }
    }

    @Override
    protected RequestDto getItem(int position) {
        return super.getItem(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private final TextView txt_request_items;
        private final TextView txt_request_date;
        private final ImageView img_accept;
        private final ImageView img_reject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_request_items = itemView.findViewById(R.id.txt_item_names);
            txt_request_date = itemView.findViewById(R.id.txt_request_date);

            img_accept = itemView.findViewById(R.id.img_accept_request);
            img_reject = itemView.findViewById(R.id.img_reject_request);

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
                        listener.onItemClick(getItem(position), "Accepted");
                    }
                }
            });

            img_reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(listener != null && position != RecyclerView.NO_POSITION){
                        listener.onItemClick(getItem(position), "Rejected");
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
