package com.eze.ui.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eze.R;
import com.eze.dtos.UpdateRequest;
import com.eze.helper.Helper;
import com.eze.helper.ItemRecViewAdapter;
import com.eze.model.Account;
import com.eze.model.Item;
import com.eze.model.Request;
import com.eze.retrofit.APIClient;
import com.eze.retrofit.RequestClient;
import com.eze.room.viewmodel.RequestViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestDetailsFragment extends Fragment {

    private static final String TAG = "RequestDetailsFragment";
    public static final String REQUEST_ID = "com.example.eze.request.id";

    private TextView txt_request_id, txt_request_student, txt_request_date_fragment, txt_request_code;
    private Button btn_accept_request, btn_reject_request;
    private RecyclerView recyclerView_requestDetail;
    private ItemRecViewAdapter adapter;

    private RequestClient requestClient;

    private RequestViewModel requestViewModel;

    private Request request = null;

    private String accessToken = "";

    public RequestDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_request_details, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

        populateViews();

        btn_accept_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateRequest updateRequest = new UpdateRequest("Accepted");
                request.setStatus("Accepted");

                Account account = requestViewModel.getLatestAccount();
                if(account != null){
                    accessToken = "Bearer " + account.getAccessToken();
                }

                updateRequestToServer(view.getContext(), updateRequest, accessToken);
                requestViewModel.updateRequest(request);

                final NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_requestDetailsFragment_to_pendingRequestFragment);
            }
        });

        btn_accept_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateRequest updateRequest = new UpdateRequest("Rejected");
                request.setStatus("Rejected");

                Account account = requestViewModel.getLatestAccount();
                if(account != null){
                    accessToken = "Bearer " + account.getAccessToken();
                }

                updateRequestToServer(view.getContext(), updateRequest, accessToken);
                requestViewModel.updateRequest(request);


                final NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_requestDetailsFragment_to_pendingRequestFragment);
            }
        });
    }

    private void updateRequestToServer(Context context, UpdateRequest updateRequest, String token) {
        Call<Void> call = requestClient.updateRequest(token, request.id, updateRequest);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(context, "Error has occurred. Please try again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Error has occurred. Please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void populateViews() {
        Log.d(TAG, "Populating Views in RequestDetailsFragment");
        Bundle bundle = getArguments();
        String requestId = bundle.getString(REQUEST_ID);
        request = requestViewModel.getRequest(requestId);
        List<Item> itemsInRequest = getItemsFromString(request.getItemIds());

        txt_request_id.setText(request.getId());
        txt_request_student.setText(request.getStudentName());
        txt_request_code.setText(request.getCode());
        txt_request_date_fragment.setText(Helper.convertOffSetDateTimeToString(request.getCreatedDate()));
        adapter.setItemContents(itemsInRequest);

        recyclerView_requestDetail.setAdapter(adapter);

        if (!request.getStatus().equals("Pending")){
            btn_accept_request.setVisibility(View.GONE);
            btn_reject_request.setVisibility(View.GONE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void init(View view) {
        txt_request_id = view.findViewById(R.id.txt_request_id);
        txt_request_student = view.findViewById(R.id.txt_request_student);
        txt_request_code = view.findViewById(R.id.txt_request_code);
        txt_request_date_fragment = view.findViewById(R.id.txt_request_date_fragment);
        btn_accept_request = view.findViewById(R.id.btn_accept_request);
        btn_reject_request = view.findViewById(R.id.btn_reject_request);

        recyclerView_requestDetail = view.findViewById(R.id.recyclerView_request_details);
        recyclerView_requestDetail.setLayoutManager(new LinearLayoutManager(view.getContext()));
        adapter = new ItemRecViewAdapter();

        requestClient = APIClient.getClient().create(RequestClient.class);

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
}