package com.cpen321group.accountability;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
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
                               @Query("accountId") String id,
                               @Body JsonObject json);

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
    Call<JsonObject> postReview(@Path("accountantId") String id,
                            @Query("authorId") String userid,
                            @Query("date") Date date,
                            @Query("content") String content,
                            @Query("title") String title,
                            @Query("rating") int rate);
    // Goals APIs
    @GET("{userId}")
    Call<ArrayList<JsonObject>> getAllGoals(@Path("userId") String id);

    @POST("{userId}")
    Call<JsonObject> postGoal(@Path("userId") String id,
                              @Query("title") String title,
                              @Query("target") int targetCents,
                              @Query("current") int currentCents,
                              @Query("deadline") String date);

    @DELETE("{userId}")
    Call<JsonObject> deleteGoals(@Path("userId") String id);

    @GET("{userId}/{goalId}")
    Call<ArrayList<JsonObject>> getSpecificGoal(@Path("userId") String id,
                                                @Path("goalId") String goalId);

    @PUT("{userId}/{goalId}")
    Call<JsonObject> updateSpecificGoal(@Path("userId") String id,
                                        @Path("goalId") String goalId,
                                        @Query("current") int currentCents);

    @DELETE("{userId}/{goalId}")
    Call<ResponseBody> deleteSpecificGoals(@Path("userId") String id,
                                           @Path("goalId") String goalId);

    // Transaction APIs
    @GET("{userId}")
    Call<ArrayList<JsonObject>> getAllTransactions(@Path("userId") String id);

    @POST("{userId}")
    Call<JsonObject> postTransaction(@Path("userId") String id,
                                     @Query("title") String title,
                                     @Query("category") String category,
                                     @Query("date") String date,
                                     @Query("amount") int cents,
                                     @Query("isIncome") boolean isIncome,
                                     @Body JsonObject json);

    @DELETE("{userId}")
    Call<ArrayList<JsonObject>> deleteAllTransactions(@Path("userId") String id);

    @GET("{userId}/{transactionId}")
    Call<JsonObject> getSpecificTransaction(@Path("userId") String id,
                                            @Path("transactionId") String transactionId);

    @PUT("{userId}/{transactionId}")
    Call<JsonObject> updateSpecificTransaction(@Path("userId") String id,
                                               @Path("transactionId") String transactionId,
                                               @Query("title") String title,
                                               @Query("category") String category,
                                               @Query("date") String date,
                                               @Query("amount") int cents,
                                               @Query("isIncome") boolean isIncome,
                                               @Query("receipt") String receiptURL);

    @DELETE("{userId}/{transactionId}")
    Call<ResponseBody> deleteSpecificTransaction(@Path("userId") String id,
                                               @Path("transactionId") String transactionId);


    // Search API
    @GET("search/accountants")
    Call<ArrayList<JsonObject>> findAccountant(@Query("firstname") String str);

    // Reports APIs
    @GET("{userId}")
    Call<ArrayList<JsonObject>> getAllReports(@Path("userId") String id);

    @POST("{userId}")
    Call<JsonObject> postReport(@Path("userId") String id,
                                @Query("monthYear") String date);

    @PUT("{userId}")
    Call<ArrayList<JsonObject>> updateReport(@Path("userId") String id,
                                             @Query("accountantId") String accountantId);

    @DELETE("{userId}")
    Call<ResponseBody> deleteAllReports(@Path("userId") String id);

    @GET("{userId}/{reportId}")
    Call<JsonObject> getSpecificReport(@Path("userId") String id,
                                       @Path("reportId") String reportId);

    @PUT("{userId}/{reportId}")
    Call<JsonObject> updateSpecificReport(@Path("userId") String id,
                                          @Path("reportId") String reportId,
                                          @Query("recommendations") String recommendations);

    @DELETE("{userId}/{reportId}")
    Call<ResponseBody> deleteSpecificReport(@Path("userId") String id,
                                          @Path("reportId") String reportId);

    // stripe subscription
    @POST("/subscription/{accountId}")
    Call<JsonObject> startSubscription(@Path("accountId") String id,
                                       @Query("subscriptionDate") String subscriptionDate,
                                       @Query("expiryDate") String expiryDate);

    @PUT("/subscription/{accountId}")
    Call<JsonObject> updateSubscription(@Path("accountId") String id,
                                        @Query("expiryDate") String expiryDate);

    @PUT("accounts/{accountId}")
    Call<String> updateAvatar(@Path("accountId") String id,
                              @Body JsonObject json);

    @GET("search/transactions/{accountId}")
    Call<ArrayList<JsonObject>> findTransaction(@Path("accountId") String id,
                                                @Query("title") String str);
}
