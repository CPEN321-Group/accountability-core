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
    Call<JsonObject> createAccount(@Query("firstname") String fn,
                               @Query("lastname") String ln,
                               @Query("email") String email,
                               @Query("age") int age,
                               @Query("profession") String prfn,
                               @Query("isAccountant") boolean iA,
                               @Query("accountId") String id,
                               @Body JsonObject json);

    @POST("/accounts")
    Call<JsonObject> createGoogleAccount(@Query("firstname") String fn,
                                   @Query("lastname") String ln,
                                   @Query("email") String email,
                                   @Query("age") int age,
                                   @Query("profession") String prfn,
                                   @Query("isAccountant") boolean iA,
                                   @Query("accountId") String id,
                                   @Query("token") String token,
                                   @Body JsonObject json);

    @GET("{id}")
    Call<JsonObject> findAccount(@Path("id") String id);

    @GET("{id}")
    Call<JsonObject> checkAuth(@Path("id") String id,
                               @Query("token") String token);

    @GET("accountants")
    Call<ArrayList<JsonObject>> findAccountants();

    @POST("{conversationId}")
    Call<String> createMessage(@Path("conversationId") String id,
                             @Query("sender") String sender,
                             @Query("text") String content);

    @GET("{conversationId}")
    Call<ArrayList<JsonObject>> findMessages(@Path("conversationId") String id);

    @GET("conversation")
    Call<JsonObject> findConversation(@Query("account1Id") String id1,
                               @Query("account2Id") String id2);

    @POST("conversation")
    Call<JsonObject> createConversation(@Query("account1Id") String id1,
                               @Query("account2Id") String id2);

    @GET("{accountId}")
    Call<ArrayList<JsonObject>> findConversationsInAccount(@Path("accountId") String id);

    @PUT("{conversationId}")
    Call<JsonObject> updateFinished(@Path("conversationId") String id,
                                @Query("isFinished") boolean bool);

    @POST("{accountantId}")
    Call<JsonObject> createReview(@Path("accountantId") String id,
                            @Query("authorId") String userid,
                            @Query("date") Date date,
                            @Query("content") String content,
                            @Query("title") String title,
                            @Query("rating") int rate);
    // Goals APIs
    @GET("{userId}")
    Call<ArrayList<JsonObject>> findGoals(@Path("userId") String id);

    @POST("{userId}")
    Call<JsonObject> createGoal(@Path("userId") String id,
                              @Query("title") String title,
                              @Query("target") int targetCents,
                              @Query("current") int currentCents,
                              @Query("deadline") String date);

    @DELETE("{userId}")
    Call<JsonObject> deleteGoals(@Path("userId") String id);

    @GET("{userId}/{goalId}")
    Call<ArrayList<JsonObject>> findGoal(@Path("userId") String id,
                                                @Path("goalId") String goalId);

    @PUT("{userId}/{goalId}")
    Call<JsonObject> updateGoal(@Path("userId") String id,
                                        @Path("goalId") String goalId,
                                        @Query("current") int currentCents);

    @DELETE("{userId}/{goalId}")
    Call<ResponseBody> deleteGoal(@Path("userId") String id,
                                           @Path("goalId") String goalId);

    // Transaction APIs
    @GET("{userId}")
    Call<ArrayList<JsonObject>> findTransactions(@Path("userId") String id);

    @POST("{userId}")
    Call<JsonObject> createTransaction(@Path("userId") String id,
                                     @Query("title") String title,
                                     @Query("category") String category,
                                     @Query("date") String date,
                                     @Query("amount") int cents,
                                     @Query("isIncome") boolean isIncome,
                                     @Body JsonObject json);

    @DELETE("{userId}")
    Call<ArrayList<JsonObject>> deleteTransactions(@Path("userId") String id);

    @GET("{userId}/{transactionId}")
    Call<JsonObject> findTransaction(@Path("userId") String id,
                                     @Path("transactionId") String transactionId);

    @PUT("{userId}/{transactionId}")
    Call<JsonObject> updateTransaction(@Path("userId") String id,
                                               @Path("transactionId") String transactionId,
                                               @Query("title") String title,
                                               @Query("category") String category,
                                               @Query("date") String date,
                                               @Query("amount") int cents,
                                               @Query("isIncome") boolean isIncome,
                                               @Query("receipt") String receiptURL);

    @DELETE("{userId}/{transactionId}")
    Call<ResponseBody> deleteTransaction(@Path("userId") String id,
                                               @Path("transactionId") String transactionId);


    // Search API
    @GET("search/accountants")
    Call<ArrayList<JsonObject>> searchAccountants(@Query("firstname") String str);

    // Reports APIs
    @GET("{userId}")
    Call<ArrayList<JsonObject>> findReports(@Path("userId") String id);

    @POST("{userId}")
    Call<JsonObject> createReport(@Path("userId") String id,
                                @Query("monthYear") String date);

    @PUT("{userId}")
    Call<ArrayList<JsonObject>> updateAccountant(@Path("userId") String id,
                                             @Query("accountantId") String accountantId);

    @DELETE("{userId}")
    Call<ResponseBody> deleteReports(@Path("userId") String id);

    @DELETE("{userId}")
    Call<ResponseBody> deleteReportByDate(@Path("userId") String id,
                                    @Query("monthYear") String date);

    @GET("{userId}/{reportId}")
    Call<JsonObject> findReport(@Path("userId") String id,
                                       @Path("reportId") String reportId);

    @PUT("{userId}/{reportId}")
    Call<JsonObject> updateRecommendations(@Path("userId") String id,
                                          @Path("reportId") String reportId,
                                          @Query("recommendations") String recommendations);

    @DELETE("{userId}/{reportId}")
    Call<ResponseBody> deleteReport(@Path("userId") String id,
                                          @Path("reportId") String reportId);

    // stripe subscription
    @POST("/subscription/{accountId}")
    Call<JsonObject> createSubscription(@Path("accountId") String id,
                                       @Query("subscriptionDate") String subscriptionDate,
                                       @Query("expiryDate") String expiryDate);

    @PUT("/subscription/{accountId}")
    Call<JsonObject> updateSubscription(@Path("accountId") String id,
                                        @Query("expiryDate") String expiryDate);

    @PUT("accounts/{accountId}")
    Call<String> updateProfile(@Path("accountId") String id,
                              @Body JsonObject json);

    @GET("search/transactions/{accountId}")
    Call<ArrayList<JsonObject>> searchTransactions(@Path("accountId") String id,
                                                @Query("title") String str);
}
