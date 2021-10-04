package com.eze.ui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eze.R;
import com.eze.dtos.RequestDto;
import com.eze.helper.ReqRecViewAdapter;
import com.eze.model.Item;
import com.eze.model.Request;
import com.eze.room.viewmodel.RequestViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class FinishedRequestFragment extends Fragment {

    private static final String TAG = "FinishedRequestFragment";

    private RecyclerView recyclerView;
    private FloatingActionButton fab_delete_all;
    private RequestViewModel requestViewModel;
    private ReqRecViewAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_finished_request, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

        requestViewModel.getAllFinishedRequest().observe(getViewLifecycleOwner(), new Observer<List<Request>>() {
            @Override
            public void onChanged(List<Request> requests) {
                Log.d(TAG, "onChanged called");
                List<RequestDto> requestDtos = new ArrayList<>();

                for (Request request : requests) {
                    List<Item> items = getItemsFromString(request.getItemIds());
                    RequestDto requestDto = new RequestDto(request.id,
                            items,
                            request.getCreatedDate(),
                            request.getStudentName(),
                            request.getProfessorName(),
                            request.getCode(),
                            request.getStatus());

                    requestDtos.add(requestDto);
                }

                adapter.submitList(requestDtos);
            }
        });

        fab_delete_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogForMultipleRequest().show();
            }
        });
    }

    private void init(View view) {
        fab_delete_all = view.findViewById(R.id.fab_delete_all);
        recyclerView = view.findViewById(R.id.recyclerView_finished_request);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        adapter = new ReqRecViewAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setListener(new ReqRecViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RequestDto requestDto, String status) {
                Log.d(TAG, requestDto.getId());
                Bundle bundle = new Bundle();
                bundle.putString(RequestDetailsFragment.REQUEST_ID, requestDto.getId());

                final NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_finishedRequestFragment_to_requestDetailsFragment, bundle);
            }
        });

        requestViewModel = new ViewModelProvider(this).get(RequestViewModel.class);
    }

    public List<Item> getItemsFromString(String combinedItemIds){
        String[] itemIds = combinedItemIds.split(",");
        List<Item> items = new ArrayList<>();
        for (String itemId : itemIds) {
            Item item = requestViewModel.getItem(itemId);
            items.add(item);
        }
        return items;
    }

    private AlertDialog alertDialogForMultipleRequest(){
        return new AlertDialog.Builder(getContext()).setTitle("Delete All")
                .setMessage("Do you want to delete all request")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestViewModel.deleteAllRequest();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
    }

}