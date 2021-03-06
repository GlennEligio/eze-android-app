package com.eze.retrofit;

import com.eze.dtos.AccountDisplay;
import com.eze.dtos.AccountWithTokens;
import com.eze.dtos.CreateAccount;
import com.eze.dtos.LoginAccount;
import com.eze.dtos.RefreshRequest;
import com.eze.dtos.UpdateAccount;
import com.eze.model.Account;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserClient {

     @POST("api/account/Login")
     Call<AccountWithTokens> getLogin(@Body LoginAccount loginAccount);

     @POST("api/account")
     Call<Account> createAccount(@Body CreateAccount createAccount);

     @PUT("api/account/{id}")
     Call<Void> updateAccount(@Path("id") String id,
                              @Body UpdateAccount updateAccount);

     @POST("api/account/RefreshToken")
     Call<AccountWithTokens> refreshToken(@Body RefreshRequest refreshRequest);

     @GET("api/account/role/{role}")
     Call<AccountDisplay> getAccountByRole(@Path("role") String role);
}
