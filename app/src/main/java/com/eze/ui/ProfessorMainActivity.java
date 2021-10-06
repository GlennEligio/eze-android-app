package com.eze.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.eze.R;
import com.eze.dtos.RefreshRequest;
import com.eze.dtos.RequestDto;
import com.eze.helper.Helper;
import com.eze.model.Account;
import com.eze.model.Item;
import com.eze.model.Request;
import com.eze.receiver.NotificationRequestReceiver;
import com.eze.retrofit.APIClient;
import com.eze.retrofit.RequestClient;
import com.eze.retrofit.UserClient;
import com.eze.room.viewmodel.AccountViewModel;
import com.eze.room.viewmodel.RequestViewModel;
import com.eze.worker.RefreshJWTWorker;
import com.eze.worker.UpdatePendingRequestWorker;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.eze.EZEApp.REQUEST_NOTIFICATION_CHANNEL;
import static com.eze.EZEApp.REQUEST_NOTIFICATION_GROUP;
import static com.eze.receiver.NotificationRequestReceiver.ACCESS_TOKEN_FOR_RECEIVER;
import static com.eze.receiver.NotificationRequestReceiver.NOTIFICATION_REQUEST_ACTION;
import static com.eze.ui.fragments.SettingsFragment.ENABLE_NOTIFICATION;
import static com.eze.ui.fragments.SettingsFragment.EZE_SETTING_PREFERENCES;

public class ProfessorMainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    private static final String TAG = "ProfessorMainActivity";
    public static final String SERIALIZED_LOCAL_REQUEST_LIST = "com.eze.serializedLocalList";
    public static final String SERIALIZED_SERVER_REQUEST_LIST = "com.eze.serializedServerList";
    public static final String SERIALIZED_ACCOUNT_FROM_SERVER = "com.eze.serializedAccountFromServer";
    public static final String SERIALIZED_ACCOUNT_FROM_LOCAL_DB = "com.eze.serializedAccountFromLocalDb";
    public static final String ACCOUNT_ID_FOR_WORKER = "com.eze.accountIdForWorker";
    public static final String ACCESS_TOKEN_FOR_WORKER = "com.eze.accessTokenForWorker";

    private RequestViewModel requestViewModel;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView textTitle, txt_account_role, txt_account_name;

    public static final String REQUEST_STATUS = "com.eze.requestStatus";
    public static final String REQUEST_ID = "com.eze.notifRequestId";
    public static Map<Integer, String> notificationRequest = new HashMap<>();
    public static final String NOTIFICATION_CODE = "com.eze.notificationCode";
    public static final String NOTIFICATION_WORK_REQUEST_NAME = "com.eze.notificationWorkRequestId";
    public static final String REFRESH_JWT_WORK_REQUEST_NAME = "com.eze.refreshJwtWorkRequestName";


    private ImageView img_refresh, imgProfile, img_menu;

    public static NotificationRequestReceiver receiver;

    private Account account;

    public RequestClient requestClient;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor_main);
        Log.d(TAG, "Main Activity created");

        init();

        setUpNavigationView();

        populateViews();

        setUpWorkerForNotificationRequest();

    }

    private void populateViews() {
        account = requestViewModel.getLatestAccount();
        if(requestViewModel != null){
            if(account != null){
                txt_account_role.setText(account.getRole());
                txt_account_name.setText(account.getName());
                imgProfile.setImageResource(R.mipmap.prof_default_image);
            }
        }

        img_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPendingRequest(account.getId(), account.getAccessToken());
                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(ProfessorMainActivity.this);
                notificationManagerCompat.cancelAll();
            }
        });

        //TODO: Use Glide library to populate the image profile
    }

    private void setUpWorkerForNotificationRequest() {
        if(sharedPreferences.getBoolean(ENABLE_NOTIFICATION, false)) {
            //Work Manager
            requestViewModel = new ViewModelProvider(ProfessorMainActivity.this).get(RequestViewModel.class);
            Gson gson = new Gson();

            Data data = new Data.Builder()
                    .putString(SERIALIZED_LOCAL_REQUEST_LIST, gson.toJson(requestViewModel.getAllPendingRequestForMassUpdate()))
                    .putString(ACCESS_TOKEN_FOR_WORKER, account.getAccessToken())
                    .putString(ACCOUNT_ID_FOR_WORKER, account.getId())
                    .build();

            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();

            OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(UpdatePendingRequestWorker.class)
                    .setConstraints(constraints)
                    .setInputData(data)
                    .build();

            PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(UpdatePendingRequestWorker.class, 15, TimeUnit.MINUTES)
                    .setConstraints(constraints)
                    .setInputData(data)
                    .build();

            WorkManager.getInstance(this).enqueueUniquePeriodicWork(NOTIFICATION_WORK_REQUEST_NAME, ExistingPeriodicWorkPolicy.REPLACE, periodicWorkRequest);

            WorkManager.getInstance(this).getWorkInfoByIdLiveData(periodicWorkRequest.getId()).observe(this, new Observer<WorkInfo>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onChanged(WorkInfo workInfo) {
                    Log.d(TAG, "WorkInfo is finished");
                    Data progress = workInfo.getProgress();
                    String serverRequestsjson = progress.getString(SERIALIZED_SERVER_REQUEST_LIST);
                    String serverAccountJson = progress.getString(SERIALIZED_ACCOUNT_FROM_SERVER);

                    if(serverAccountJson != null){
                        Type type = new TypeToken<Account>(){}.getType();
                        Account serverAccount = gson.fromJson(serverAccountJson, type);
                        requestViewModel.insertAccount(serverAccount);
                    }

                    if(serverRequestsjson != null){
                        Log.d(TAG, "Request from server is fetched");
                        Type type = new TypeToken<List<RequestDto>>(){}.getType();
                        List<RequestDto> serverRequestsDto = gson.fromJson(serverRequestsjson, type);
                        List<Request> localRequest = requestViewModel.getAllPendingRequestForMassUpdate();
                        List<RequestDto> localRequestDto = new ArrayList<>();
                        for (Request request:localRequest) {
                            RequestDto requestDto = new RequestDto(request.getId(),
                                    getItemsFromString(request.getItemIds()),
                                    request.getCreatedDate(),
                                    request.getStudentName(),
                                    request.getProfessorName(),
                                    request.getCode(),
                                    request.getStatus());
                            localRequestDto.add(requestDto);
                        }

                        List<RequestDto> newRequestDto = getNewRequest(localRequestDto, serverRequestsDto);
                        if(newRequestDto != null){
                            Log.d(TAG, "Notification Request: " + newRequestDto.size());
                            for (RequestDto requestDto:newRequestDto) {
                                if(!notificationRequest.containsValue(requestDto.getId())){
                                    Integer notificationCode;
                                    do {
                                        notificationCode = Helper.getRandomNumberInRange(3, 1000);
                                        if(!notificationRequest.containsKey(notificationCode)){
                                            notificationRequest.put(notificationCode, requestDto.getId());
                                            sendNotification(requestDto, notificationCode);
                                        }
                                    }while (!notificationRequest.containsKey(notificationCode));
                                }
                            }
                        }
                    }
                }
            });
        }else{
            WorkManager.getInstance(this).cancelUniqueWork(NOTIFICATION_WORK_REQUEST_NAME);
        }
    }

    private void setUpNavigationView() {
        navigationView.setItemIconTintList(null);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_prof);

        NavController navController = navHostFragment.getNavController();

        NavigationUI.setupWithNavController(navigationView, navController);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                textTitle.setText(destination.getLabel());
                if(destination.getId() == R.id.pendingRequestFragment){
                    img_refresh.setVisibility(View.VISIBLE);
                    return;
                }
                img_refresh.setVisibility(View.GONE);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void init(){
        img_menu = findViewById(R.id.imageMenu_prof);
        img_refresh = findViewById(R.id.img_refresh_prof);
        drawerLayout = findViewById(R.id.drawerLayout_prof);
        imgProfile = drawerLayout.findViewById(R.id.img_profile_navView);
        textTitle = findViewById(R.id.textTitle_prof);

        navigationView = findViewById(R.id.navigationView_prof);

        View view = navigationView.getHeaderView(0);

        txt_account_name = view.findViewById(R.id.txt_account_name_navView);
        txt_account_role = view.findViewById(R.id.txt_account_role_navView);
        imgProfile = view.findViewById(R.id.img_profile_navView);

        img_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        requestViewModel = new ViewModelProvider(this).get(RequestViewModel.class);

        sharedPreferences = this.getSharedPreferences(EZE_SETTING_PREFERENCES, Context.MODE_PRIVATE);

        requestClient = APIClient.getClient().create(RequestClient.class);
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

    public List<Item> getItemsFromString(String combinedItemIds){
        String[] itemIds = combinedItemIds.split(",");
        List<Item> items = new ArrayList<>();
        for (String itemId : itemIds) {
            Item item = requestViewModel.getItem(itemId);
            items.add(item);
        }
        return items;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<RequestDto> getNewRequest(List<RequestDto> localRequest, List<RequestDto> newRequest){
        Log.d(TAG, "Getting Unique request only in Server request");
        if(newRequest == null && localRequest == null){
            Log.d(TAG, "newRequest and localRequest is null");
            return null;
        }

        for (RequestDto requestDto : localRequest) {
            newRequest.removeIf(requestDto1 -> requestDto1.getId().equals(requestDto.getId()));
        }
        return newRequest;
    }

    public void sendNotification(RequestDto requestDto, Integer notificationCode){

        //Intent to go to ProfessorMainActivity
        Intent activityIntent = new Intent(this, ProfessorMainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                notificationCode+10000,
                activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        //Intent for rejecting Notification Request
        Intent rejectIntent = new Intent(this, NotificationRequestReceiver.class);
        rejectIntent.putExtra(REQUEST_ID, requestDto.getId());
        rejectIntent.putExtra(REQUEST_STATUS, "Rejected");
        rejectIntent.putExtra(NOTIFICATION_CODE, notificationCode);
        rejectIntent.putExtra(ACCESS_TOKEN_FOR_RECEIVER, account.getAccessToken());
        rejectIntent.setAction(NOTIFICATION_REQUEST_ACTION);
        PendingIntent actionRejectIntent = PendingIntent.getBroadcast(this,
                -notificationCode,
                rejectIntent,
                PendingIntent.FLAG_ONE_SHOT);

        //Intent for rejecting Notification Request
        Intent acceptIntent = new Intent(this, NotificationRequestReceiver.class);
        acceptIntent.putExtra(REQUEST_ID, requestDto.getId());
        acceptIntent.putExtra(REQUEST_STATUS, "Accepted");
        acceptIntent.putExtra(NOTIFICATION_CODE, notificationCode);
        acceptIntent.putExtra(ACCESS_TOKEN_FOR_RECEIVER, account.getAccessToken());
        acceptIntent.setAction(NOTIFICATION_REQUEST_ACTION);
        PendingIntent actionAcceptIntent = PendingIntent.getBroadcast(this,
                notificationCode,
                acceptIntent,
                PendingIntent.FLAG_ONE_SHOT);

        //Intent for accepting all request
        Intent acceptAllIntent = new Intent(this, NotificationRequestReceiver.class);
        acceptAllIntent.putExtra(REQUEST_STATUS, "Accept all");
        acceptAllIntent.putExtra(NOTIFICATION_CODE, notificationCode);
        acceptAllIntent.setAction(NOTIFICATION_REQUEST_ACTION);
        PendingIntent actionAcceptAllIntent = PendingIntent.getBroadcast(this,
                1,
                acceptAllIntent,
                0);

        //Intent for rejecting all request
        Intent rejectAllIntent = new Intent(this, NotificationRequestReceiver.class);
        rejectAllIntent.putExtra(REQUEST_STATUS, "Reject all");
        rejectAllIntent.putExtra(NOTIFICATION_CODE, notificationCode);
        rejectAllIntent.setAction(NOTIFICATION_REQUEST_ACTION);
        PendingIntent actionRejectAllIntent = PendingIntent.getBroadcast(this,
                2,
                acceptAllIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Notification incomingRequestNotif = new NotificationCompat.Builder(this, REQUEST_NOTIFICATION_CHANNEL)
                .setContentText(requestDto.getStudentName())
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(Helper.combineItemNames(requestDto.getItems(), true))
                .setSubText("Incoming Request")
                .setContentIntent(contentIntent)
                .setOnlyAlertOnce(true)
                .setOngoing(false)
                .setAutoCancel(true)
                .setGroup(REQUEST_NOTIFICATION_GROUP)
                .addAction(R.drawable.ic_accept, "Accept", actionAcceptIntent)
                .addAction(R.drawable.ic_reject, "Decline", actionRejectIntent)
                .build();

        Log.d(TAG, "Sending notification for single request");
        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(notificationCode+5000, incomingRequestNotif);

        if(notificationRequest.size() > 3){
            Log.d(TAG, "Sending notification summary for the requests");
            NotificationCompat.Builder summaryRequestNotif = new NotificationCompat.Builder(this, REQUEST_NOTIFICATION_CHANNEL)
                    .setContentTitle(notificationRequest.size() + "Incoming Requests")
                    .setContentIntent(contentIntent)
                    .setGroup(REQUEST_NOTIFICATION_GROUP)
                    .setStyle(new NotificationCompat.InboxStyle()
                            .setBigContentTitle(notificationRequest.size() + " new Request")
                            .setSummaryText(notificationRequest.size() + " Incoming Requests"))
                    .setGroupSummary(true)
                    .setOngoing(false)
                    .setAutoCancel(true)
                    .addAction(R.drawable.ic_accept, "Accept", actionAcceptAllIntent)
                    .addAction(R.drawable.ic_reject, "Decline", actionRejectAllIntent)
                    .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
                    .setSmallIcon(R.drawable.ic_notification);

            Log.d(TAG, "sendIncomingRequestNotification: SummaryNotificationLaunched");
            manager.notify(1000, summaryRequestNotif.build());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Account account = requestViewModel.getLatestAccount();
        Gson gson = new Gson();

        Data data = new Data.Builder()
                .putString(SERIALIZED_ACCOUNT_FROM_LOCAL_DB, gson.toJson(account))
                .build();

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(RefreshJWTWorker.class, 1, TimeUnit.HOURS)
                .setInputData(data)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(REFRESH_JWT_WORK_REQUEST_NAME, ExistingPeriodicWorkPolicy.REPLACE, periodicWorkRequest);

        WorkManager.getInstance(this).getWorkInfosForUniqueWorkLiveData(REFRESH_JWT_WORK_REQUEST_NAME).observe(this, new Observer<List<WorkInfo>>() {
            @Override
            public void onChanged(List<WorkInfo> workInfos) {
                if(workInfos != null){
                    WorkInfo workInfo = workInfos.get(0);
                    if(workInfo != null){
                        Data outputData = workInfo.getProgress();

                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WorkManager.getInstance(this).cancelUniqueWork(REFRESH_JWT_WORK_REQUEST_NAME);
    }
}