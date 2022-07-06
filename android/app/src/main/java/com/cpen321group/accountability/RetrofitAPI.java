package com.cpen321group.accountability;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.http.DELETE;
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

    @POST("{accountantId}")
    Call<String> postReview(@Path("accountantId") String id,
                            @Query("date") Date date,
                            @Query("content") String content,
                            @Query("title") String title,
                            @Query("rating") int rate);
    // Goals API
    @GET("{userId}")
    Call<ArrayList<JsonObject>> getAllGoals(@Path("userId") String id);

    @POST("{userId}")
    Call<JsonObject> postGoal(@Path("userId") String id,
                              @Query("title") String title,
                              @Query("target") int targetCents,
                              @Query("current") int currentCents,
                              @Query("deadline") String date);

    @DELETE("{userId}")
    void deleteGoals(@Path("userId") String id);

    @GET("{userId}/{goalId}")
    Call<ArrayList<JsonObject>> getSpecificGoal(@Path("userId") String id,
                                                @Path("goalId") String goalId);

    @PUT("{userId}/{goalId}")
    Call<JsonObject> updateSpecificGoal(@Path("userId") String id,
                              @Path("goalId") String goalId,
                              @Query("title") String title,
                              @Query("target") int targetCents,
                              @Query("current") int currentCents,
                              @Query("deadline") String date);

    @DELETE("{userId}/{goalId}")
    void deleteSpecificGoals(@Path("userId") String id,
                             @Path("goalId") String goalId);

}
