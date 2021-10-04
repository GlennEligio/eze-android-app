package com.eze.ui;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.eze.R;
import com.eze.dtos.RequestDto;
import com.eze.helper.Helper;
import com.eze.helper.ReqRecViewAdapter;
import com.eze.model.Item;
import com.eze.model.Request;
import com.eze.retrofit.APIClient;
import com.eze.retrofit.ItemClient;
import com.eze.retrofit.RequestClient;
import com.eze.room.viewmodel.RequestViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestActivity extends AppCompatActivity {

    public static final String ACCESS_TOKEN = "com.example.extra_access_token";
    private static final String TAG = "RequestActivity";

    public static final String ACCOUNT_ID = "com.example.extra_account_id";

    private String accountId = "";
    private String accessToken = "";

    private RequestViewModel requestViewModel;
    private RecyclerView recyclerView;
    private FloatingActionButton fab_rejectAll, fab_acceptAll;
    private RequestClient requestClient;
    private ItemClient itemClient;
    private ReqRecViewAdapter adapter;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        init();

        getPendingRequest(accountId);

        requestViewModel.getAllRequests().observe(this, new Observer<List<Request>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
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


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void init(){
        Intent intent = getIntent();
        accountId = intent.getStringExtra(ACCOUNT_ID);
        accessToken = intent.getStringExtra(ACCESS_TOKEN);

        requestClient = APIClient.getClient().create(RequestClient.class);
        itemClient = APIClient.getClient().create(ItemClient.class);

        requestViewModel = new ViewModelProvider(this).get(RequestViewModel.class);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReqRecViewAdapter();
        recyclerView.setAdapter(adapter);

        fab_acceptAll = findViewById(R.id.fab_acceptAll);
        fab_rejectAll = findViewById(R.id.fab_rejectAll);
    }

    public void getPendingRequest(String accountId){
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

                if(!pendingRequests.isEmpty()){
                    Log.d(TAG, "Response is empty");
                }

                List<Request> requestToStore = new ArrayList<>();
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