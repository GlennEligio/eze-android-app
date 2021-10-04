package com.eze.worker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.eze.dtos.RequestDto;
import com.eze.retrofit.APIClient;
import com.eze.retrofit.RequestClient;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.eze.ui.ProfessorMainActivity.ACCESS_TOKEN_FOR_WORKER;
import static com.eze.ui.ProfessorMainActivity.ACCOUNT_ID_FOR_WORKER;
import static com.eze.ui.ProfessorMainActivity.SERIALIZED_SERVER_REQUEST_LIST;

public class UpdatePendingRequestWorker extends Worker {

    private static final String TAG = "UpdateRequestWorker";
    public static final String REQUEST_STATUS = "com.eze.requestStatus";
    public static final String REQUEST_ID = "com.eze.notifRequestId";
    public static Map<Integer, String> notificationRequest = new HashMap<>();
    public static final String NOTIFICATION_CODE = "com.eze.notificationCode";
    public static final String NOTIFICATION_WORK_REQUEST_NAME = "com.eze.notificationWorkRequestId";

    public RequestClient requestClient;
    public String accessToken, accountId, serializedRequestList;
    private final Context context;
    private CountDownLatch countDownLatch;

    private SharedPreferences sharedPreferences;

    public UpdatePendingRequestWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = getApplicationContext();
        countDownLatch = new CountDownLatch(1);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "Work is starting to fetch request");
        Data inputData = getInputData();
        accessToken = inputData.getString(ACCESS_TOKEN_FOR_WORKER);
        accountId = inputData.getString(ACCOUNT_ID_FOR_WORKER);
        Log.d(TAG, "Access Token: " + accessToken + "\n"
                        + "Account Id: " + accountId);

//        serializedRequestList = inputData.getString(SERIALIZED_LOCAL_REQUEST_LIST);
//        Log.d(TAG, serializedRequestList);
//        sharedPreferences = context.getSharedPreferences(EZE_SETTING_PREFERENCES, Context.MODE_PRIVATE);

        Gson gson = new Gson();
//        Type type = new TypeToken<List<RequestDto>>(){}.getType();

//        List<RequestDto> localRequests = gson.fromJson(serializedRequestList, type);
//        Log.d(TAG, "Local request size " + localRequests.size());

        requestClient = APIClient.getClient().create(RequestClient.class);
        List<RequestDto> serverRequests = getPendingRequest(accountId, accessToken);
        Log.d(TAG, "Server request number: " + serverRequests.size());

        Data outputData = new Data.Builder()
                .putString(SERIALIZED_SERVER_REQUEST_LIST, gson.toJson(serverRequests))
                .build();

        setProgressAsync(outputData);

        SystemClock.sleep(1000);

        return Result.success();
    }

    private List<RequestDto> getPendingRequest(String accountId, String accessToken) {
        String bearerToken = "Bearer " + accessToken;
        CountDownLatch pendingRequestCountDownLatch = new CountDownLatch(1);
        List<RequestDto> incomingRequest = new ArrayList<>();

        Call<ArrayList<RequestDto>> call = requestClient.getPendingRequest(bearerToken, accountId);
        call.enqueue(new Callback<ArrayList<RequestDto>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<ArrayList<RequestDto>> call, Response<ArrayList<RequestDto>> response) {
                Log.d(TAG, "Start reading response from Worker");
                if(!response.isSuccessful()){
                    Log.d(TAG, "Code: " + response.code());
                    pendingRequestCountDownLatch.countDown();
                    return;
                }

                List<RequestDto> newRequestDto = response.body();
                incomingRequest.addAll(newRequestDto);
                pendingRequestCountDownLatch.countDown();
            }

            @Override
            public void onFailure(Call<ArrayList<RequestDto>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
                pendingRequestCountDownLatch.countDown();
            }
        });

        try {
            pendingRequestCountDownLatch.await();
            countDownLatch.countDown();
            return incomingRequest;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
