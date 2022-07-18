package com.cpen321group.accountability.mainScreen.dashboard.functionpack;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cpen321group.accountability.R;
import com.cpen321group.accountability.RetrofitAPI;
import com.cpen321group.accountability.VariableStoration;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageActivity;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TransactionCreateActivity extends AppCompatActivity {
    private String transactionName;
    private String transactionCategory;
    private int transactionAmount;
    public static int year;
    public static int month;
    public static int day;
    private String date;
    private static final int REQUEST_CODE = 100;
    private Button capture_button;
    private TextView ocr_view;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_create);
        capture_button = findViewById(R.id.recipeButton);
        ocr_view = findViewById(R.id.ocr_view);
        Button createTransaction = findViewById(R.id.transactionCreateButton);
        createTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextInputEditText transactionNameEditText = (TextInputEditText) findViewById(R.id.transactionNameInput);
                transactionName = transactionNameEditText.getText().toString();

                TextInputEditText transactionCategoryEditText = (TextInputEditText) findViewById(R.id.transactionCategoryInput);
                transactionCategory = transactionCategoryEditText.getText().toString();

                TextInputEditText transactionAmountEditText = (TextInputEditText) findViewById(R.id.transactionAmountPriceInput);
                String TransactionAmountText = transactionAmountEditText.getText().toString();

                date = year + "/" + month + "/" + day;
                Log.d("Date:", "" + date);

                if(!date.equals("") && !transactionName.equals("") && !TransactionAmountText.equals("")) {
                    transactionAmount = (int)Math.round((Double.parseDouble(TransactionAmountText)*100));
                    try {
                        createTransaction();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Handler handler2 = new Handler();
                    handler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent TransactionSetIntent = new Intent(TransactionCreateActivity.this, TransactionSetActivity.class);
                            startActivity(TransactionSetIntent);
                        }
                    },2000);
                }else{
                    Toast.makeText(TransactionCreateActivity.this,"Some necessary information missing!",Toast.LENGTH_LONG).show();
                }
            }
        });

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.CAMERA
            },REQUEST_CODE);
        }

        capture_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(TransactionCreateActivity.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                Uri resultUri = result.getUri();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),resultUri);
                    getTextFromImage(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void getTextFromImage(Bitmap bitmap){
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        Task<Text> result =
                recognizer.process(image)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text visionText) {
                                /*List<Text.TextBlock> textBlock= visionText.getTextBlocks();
                                StringBuilder stringBuilder = new StringBuilder();
                                for(int i = 0;i< textBlock.size();i++){
                                    Text.TextBlock text = textBlock.get(i);
                                    stringBuilder.append(text.getText());
                                    stringBuilder.append("\n");
                                }
                                ocr_view.setText(stringBuilder.toString());*/
                                ocr_view.setText(visionText.getText());
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(TransactionCreateActivity.this,"Try Again!",Toast.LENGTH_LONG).show();
                                    }
                                });
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerTransactionFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void createTransaction() throws IOException{
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://20.239.52.70:8000/transactions/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<JsonObject> call = retrofitAPI.postTransaction(VariableStoration.userID, this.transactionName, this.transactionCategory, this.date, this.transactionAmount, false, "null");

        Log.d("API url:", "http://20.239.52.70:8000/transactions/"+VariableStoration.userID+"/");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Toast.makeText(getApplicationContext(),"You have successfully added your new transaction",Toast.LENGTH_LONG).show();
                Log.d("Message",response.toString());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Failed to add your new transaction, try again",Toast.LENGTH_LONG).show();
                Log.d("Message","error");
            }
        });
    }
}