package com.eze.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;

import com.eze.dtos.UpdateRequest;
import com.eze.helper.NotifRequestListener;
import com.eze.model.Request;
import com.eze.retrofit.APIClient;
import com.eze.retrofit.RequestClient;
import com.eze.worker.UpdatePendingRequestWorker;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.eze.worker.UpdatePendingRequestWorker.NOTIFICATION_CODE;
import static com.eze.worker.UpdatePendingRequestWorker.REQUEST_ID;
import static com.eze.worker.UpdatePendingRequestWorker.REQUEST_STATUS;

public class NotificationRequestReceiver extends BroadcastReceiver {

    private static final String TAG = "NotificationRequestRece";

    public static final String NOTIFICATION_REQUEST_ACTION = "com.eze.notificationRequestAction";
    public static final String ACCESS_TOKEN_FOR_RECEIVER = "com.eze.accessTokenForReceiver";

    private RequestClient requestClient;
    private Context context;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public NotificationRequestReceiver() {
        requestClient = APIClient.getClient().create(RequestClient.class);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        if(NOTIFICATION_REQUEST_ACTION.equals(intent.getAction())){
            String requestId = intent.getStringExtra(REQUEST_ID);
            String requestStatus = intent.getStringExtra(REQUEST_STATUS);
            String accessToken = intent.getStringExtra(ACCESS_TOKEN_FOR_RECEIVER);
            int notificationCode = intent.getIntExtra(NOTIFICATION_CODE, 0);
            Log.d(TAG, "Updating request");
            updateRequest(accessToken, requestId, requestStatus);
            NotificationManagerCompat manager = NotificationManagerCompat.from(context);
            manager.cancel(notificationCode+5000);
            UpdatePendingRequestWorker.notificationRequest.remove(notificationCode);
        }
    }

    public void updateRequest(String accessToken, String requestId, String status){
        String token = "Bearer " + accessToken;
        Call<Void> updateCall = requestClient.updateRequest(token, requestId, new UpdateRequest(status));
        updateCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(context, "Update error. Please try again", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(context, "Update success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
