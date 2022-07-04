package com.cpen321group.accountability;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Query;
import retrofit2.http.POST;

public interface RetrofitAPI {
    @POST("/accounts")

    //on below line we are creating a method to post our data.
    Call<String> createAccount(@Query("firstname") String fn,
                        @Query("lastname") String ln,
                        @Query("email") String email,
                        @Query("age") int age,
                        @Query("profession") String prfn,
                        @Query("isAccountant") boolean iA,
                        @Query("accountId") String id);
}
