package com.eze.worker;

import android.content.Context;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.eze.dtos.AccountWithTokens;
import com.eze.dtos.RefreshRequest;
import com.eze.helper.Helper;
import com.eze.model.Account;
import com.eze.retrofit.APIClient;
import com.eze.retrofit.UserClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.GregorianCalendar;
import java.util.concurrent.CountDownLatch;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.eze.ui.ProfessorMainActivity.SERIALIZED_ACCOUNT_FROM_LOCAL_DB;
import static com.eze.ui.ProfessorMainActivity.SERIALIZED_ACCOUNT_FROM_SERVER;

public class RefreshJWTWorker extends Worker {

    private static final String TAG = "RefreshJWTWorker";

    private UserClient userClient;

    public RefreshJWTWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "Fetching Local account");
        Data inputData = getInputData();
        String localAccountJson = inputData.getString(SERIALIZED_ACCOUNT_FROM_LOCAL_DB);

        Gson gson = new Gson();
        Type type = new TypeToken<Account>(){}.getType();

        userClient = APIClient.getClient().create(UserClient.class);
        Log.d(TAG, "Fetching Account with new JWT");
        Account account = getAccountWithFreshJWT(gson.fromJson(localAccountJson, type));

        if(account != null){
            Data outputData = new Data.Builder()
                    .putString(SERIALIZED_ACCOUNT_FROM_SERVER, gson.toJson(account))
                    .build();
            Log.d(TAG, "Sending Account from server json");
            setProgressAsync(outputData);

            SystemClock.sleep(1000);
       }

        return Result.success();
    }

    public Account getAccountWithFreshJWT(Account accountFromLocalDb){
        final Account[] account = new Account[1];
        Call<AccountWithTokens> refreshCall = userClient.refreshToken(new RefreshRequest(accountFromLocalDb.getAccessToken(), accountFromLocalDb.getRefreshToken()));
        CountDownLatch refreshCountDown = new CountDownLatch(1);
        refreshCall.enqueue(new Callback<AccountWithTokens>() {
            @Override
            public void onResponse(Call<AccountWithTokens> call, Response<AccountWithTokens> response) {
                if(!response.isSuccessful()){
                    Log.d(TAG, "Code: " + response.code());
                    refreshCountDown.countDown();
                    return;
                }

                AccountWithTokens accountWithTokens = response.body();
                if(accountWithTokens != null){
                    account[0] = Helper.asAccount(accountWithTokens);
                    refreshCountDown.countDown();
                }
            }

            @Override
            public void onFailure(Call<AccountWithTokens> call, Throwable t) {
                Log.d(TAG, t.getMessage());
                refreshCountDown.countDown();
            }
        });

        try {
            refreshCountDown.await();
            return account[0];
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }
}
