package com.eze.ui.fragments;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.eze.R;
import com.eze.dtos.RequestDto;
import com.eze.dtos.UpdateRequest;
import com.eze.helper.Helper;
import com.eze.helper.ReqRecViewAdapter;
import com.eze.model.Account;
import com.eze.model.Item;
import com.eze.model.Request;
import com.eze.retrofit.APIClient;
import com.eze.retrofit.RequestClient;
import com.eze.room.viewmodel.RequestViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PendingRequestFragment extends Fragment {

    private static final String TAG = "PendingRequestFragment";
    private String accountId = "";
    private String accessToken = "";

    private RequestViewModel requestViewModel;
    private RecyclerView recyclerView;
    private FloatingActionButton fab_rejectAll, fab_acceptAll;
    private RequestClient requestClient;
    private ReqRecViewAdapter adapter;

    private Context context;

    public PendingRequestFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pending_request, container, false);
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(requireView());

        Account account = requestViewModel.getLatestAccount();
        if(account != null){
            accountId = account.getId();
            accessToken = account.getAccessToken();
        }

        getPendingRequest(accountId, accessToken);

        requestViewModel.getAllPendingRequest().observe(getViewLifecycleOwner(), new Observer<List<Request>>() {
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

        fab_acceptAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogForMultipleRequest("Accepted").show();
            }
        });

        fab_rejectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogForMultipleRequest("Rejected").show();
            }
        });
    }

    private void getPendingRequest(String accountId, String accessToken) {
        String bearerToken = "Bearer " + accessToken;
        Call<ArrayList<RequestDto>> call = requestClient.getPendingRequest(bearerToken, accountId);
        call.enqueue(new Callback<ArrayList<RequestDto>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<ArrayList<RequestDto>> call, Response<ArrayList<RequestDto>> response) {
                Log.d(TAG, "Start reading response");
                if(!response.isSuccessful()){
                    Log.d(TAG, "Code: " + response.code());
                    return;
                }

                Log.d(TAG, "Response body fetched");
                List<RequestDto> pendingRequests = response.body();

                assert pendingRequests != null;

                if(pendingRequests.isEmpty()){
                    Log.d(TAG, "Response is empty");
                    return;
                }

                for (RequestDto requestDto : pendingRequests) {
                    Log.d(TAG, "onResponse: Iterating requestDto received");
                    String itemIds = Helper.combineItemIds(requestDto.getItems(), false);

                    for (Item item : requestDto.getItems()) {
                        requestViewModel.insertItem(item);
                        Log.d(TAG, "Adding item in database");
                    }

                    Request request = new Request(requestDto.getId(),
                            itemIds,
                            requestDto.getItems().size(),
                            requestDto.getCreatedDate(),
                            requestDto.getStudentName(),
                            requestDto.getProfessorName(),
                            requestDto.getCode(),
                            requestDto.getStatus());

                    requestViewModel.insertRequest(request);
                    Log.d(TAG, "Adding request in database");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<RequestDto>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void init(View view) {

        requestClient = APIClient.getClient().create(RequestClient.class);

        requestViewModel = new ViewModelProvider(this).get(RequestViewModel.class);

        recyclerView = view.findViewById(R.id.recyclerView_pending_request);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        adapter = new ReqRecViewAdapter();
        recyclerView.setAdapter(adapter);

        fab_acceptAll = view.findViewById(R.id.fragment_fab_acceptAll);
        fab_rejectAll = view.findViewById(R.id.fragment_fab_rejectAll);

        context = view.getContext();

        adapter.setListener(new ReqRecViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RequestDto requestDto, String status) {
                if ("Pending".equals(status)) {
                    Bundle bundle = new Bundle();
                    bundle.putString(RequestDetailsFragment.REQUEST_ID, requestDto.getId());

                    final NavController navController = Navigation.findNavController(view);
                    navController.navigate(R.id.action_pendingRequestFragment_to_requestDetailsFragment, bundle);
                    return;
                }
                alertDialogForSingleRequest(requestDto, status).create().show();
                requestClient.updateRequest("Bearer " + accessToken, requestDto.getId(), new UpdateRequest(status));
            }
        });
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

    private AlertDialog.Builder alertDialogForSingleRequest(RequestDto requestDto, String status) {
        Request request = new Request(requestDto.getId(),
                Helper.combineItemIds(requestDto.getItems(), false),
                requestDto.getItems().size(),
                requestDto.getCreatedDate(),
                requestDto.getStudentName(),
                requestDto.getProfessorName(),
                requestDto.getCode(),
                requestDto.getStatus());

        String title = "";

        if(status.equals("Accepted")){
            title = "Accept";
        }else{
            title = "Reject";
        }

        return new AlertDialog.Builder(getContext()).setTitle(title + " Request")
                .setMessage("Do you want to " + title.toLowerCase() + " this request?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.setStatus(status);
                        requestViewModel.updateRequest(request);
                        updateRequestInServer(request.getId(), new UpdateRequest(status), true);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
    }

    private AlertDialog alertDialogForMultipleRequest(String status){
        String title = "";
        if(status.equals("Accepted")){
            title = "Accept";
        }else{
            title = "Reject";
        }

        return new AlertDialog.Builder(getContext()).setTitle(title + " all")
                .setMessage("Do you want to " + title + " all request")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UpdateRequest updateRequest = new UpdateRequest(status);
                        List<Request> requests = requestViewModel.getAllPendingRequestForMassUpdate();

                        for (Request request : requests) {
                            updateRequestInServer(request.getId(), updateRequest, false);
                            request.setStatus(status);
                            requestViewModel.updateRequest(request);
                        }
                        if(requestViewModel.getAllPendingRequestForMassUpdate().isEmpty()){
                            Toast.makeText(context, "Updated all request successfully", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(context, "Update unsuccessful", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
    }

    private void updateRequestInServer(String requestId, UpdateRequest updateRequest, boolean isSingleRequest){
        String accessToken = "Bearer " + requestViewModel.getLatestAccount().getAccessToken();

        Call<Void> call = requestClient.updateRequest(accessToken, requestId, updateRequest);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(!response.isSuccessful()){
                    Log.d(TAG, "Code: "+ response.code());
                    if(isSingleRequest){
                        Toast.makeText(context, "Update was not successful", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
                if(isSingleRequest){
                    Toast.makeText(context, "Update success", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d(TAG, t.getMessage());
                if(isSingleRequest){
                    Toast.makeText(context, "Error has occurred while updating the request", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getContext());
        notificationManagerCompat.cancelAll();
    }
}