package com.cpen321group.accountability;

import com.cpen321group.accountability.welcome.MyProfile;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;

import java.lang.reflect.Array;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.POST;

public interface RetrofitAPI {
    @POST("/accounts")
    Call<String> createAccount(@Query("firstname") String fn,
                               @Query("lastname") String ln,
                               @Query("email") String email,
                               @Query("age") int age,
                               @Query("profession") String prfn,
                               @Query("isAccountant") boolean iA,
                               @Query("accountId") String id);

    @GET("{id}")
    Call<JsonObject> getAccount(@Path("id") String id);

    @GET("accountants")
    Call<ArrayList<JsonObject>> getAccountant();

    @POST("{conversationId}")
    Call<String> postMessage(@Path("conversationId") String id,
                             @Query("sender") String sender,
                             @Query("text") String content);

    @GET("{conversationId}")
    Call<ArrayList<JsonObject>> getAllMessage(@Path("conversationId") String id);

    @GET("conversation")
    Call<JsonObject> getRoomId(@Query("account1Id") String id1,
                               @Query("account2Id") String id2);

    @POST("conversation")
    Call<String> postRoomId(@Query("account1Id") String id1,
                               @Query("account2Id") String id2);

    @GET("{accountId}")
    Call<ArrayList<JsonObject>> getAllUsers(@Path("accountId") String id);

    @PUT("{conversationId}")
    Call<String> updateFinished(@Path("conversationId") String id,
                                @Query("isFinished") boolean bool);


}
