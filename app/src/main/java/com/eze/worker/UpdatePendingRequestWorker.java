package com.eze.worker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.eze.dtos.AccountWithTokens;
import com.eze.dtos.LoginAccount;
import com.eze.dtos.RequestDto;
import com.eze.helper.Helper;
import com.eze.model.Account;
import com.eze.retrofit.APIClient;
import com.eze.retrofit.RequestClient;
import com.eze.retrofit.UserClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.eze.ui.ProfessorMainActivity.SERIALIZED_ACCOUNT_FROM_LOCAL_DB;
import static com.eze.ui.ProfessorMainActivity.SERIALIZED_ACCOUNT_FROM_SERVER;
import static com.eze.ui.ProfessorMainActivity.SERIALIZED_SERVER_REQUEST_LIST;

public class UpdatePendingRequestWorker extends Worker {

    private static final String TAG = "UpdateRequestWorker";
    public static final String REQUEST_STATUS = "com.eze.requestStatus";
    public static final String REQUEST_ID = "com.eze.notifRequestId";
    public static Map<Integer, String> notificationRequest = new HashMap<>();
    public static final String NOTIFICATION_CODE = "com.eze.notificationCode";
    public static final String NOTIFICATION_WORK_REQUEST_NAME = "com.eze.notificationWorkRequestId";

    public RequestClient requestClient;
    public UserClient userClient;
    public String accountUsername, accountPassword;
    private final Context context;
    private final CountDownLatch countDownLatch;

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
        String localAccountJson = inputData.getString(SERIALIZED_ACCOUNT_FROM_LOCAL_DB);

        Gson gson = new Gson();
        Type type = new TypeToken<Account>() {
        }.getType();

        Account localAccount = gson.fromJson(localAccountJson, type);

        if (localAccount == null) {
            return Result.failure();
        }

        requestClient = APIClient.getClient().create(RequestClient.class);
        userClient = APIClient.getClient().create(UserClient.class);

        Account account = getAccountWithRefreshedJWT(localAccount.getUsername(), localAccount.getPassword());


        if (account == null) {
            return Result.failure();
        }

        List<RequestDto> serverRequests = getPendingRequest(account.getId(), account.getAccessToken());
        Log.d(TAG, "Server request number: " + serverRequests.size());

        Data outputData = new Data.Builder()
                .putString(SERIALIZED_SERVER_REQUEST_LIST, gson.toJson(serverRequests))
                .putString(SERIALIZED_ACCOUNT_FROM_SERVER, gson.toJson(account))
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

    private Account getAccountWithRefreshedJWT(String username, String password){
        final Account[] account = new Account[1];
        CountDownLatch countDownLatchForLogin = new CountDownLatch(1);
        Call<AccountWithTokens> loginCall = userClient.getLogin(new LoginAccount(username, password));
        loginCall.enqueue(new Callback<AccountWithTokens>() {
            @Override
            public void onResponse(Call<AccountWithTokens> call, Response<AccountWithTokens> response) {
                if(!response.isSuccessful()){
                    if(404 == response.code()) {
                        Toast.makeText(context, "Account not in system", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }

                AccountWithTokens accountWithTokens = response.body();
                if(accountWithTokens != null){
                    account[0] = Helper.asAccount(accountWithTokens);
                    countDownLatchForLogin.countDown();
                }
            }

            @Override
            public void onFailure(Call<AccountWithTokens> call, Throwable t) {
                Log.d(TAG, t.getMessage());
                countDownLatchForLogin.countDown();
            }
        });

        try {
            countDownLatchForLogin.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return account[0];
    }
}
