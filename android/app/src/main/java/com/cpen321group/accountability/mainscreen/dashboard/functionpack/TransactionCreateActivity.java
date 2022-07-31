package com.cpen321group.accountability.mainscreen.dashboard.functionpack;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cpen321group.accountability.R;
import com.cpen321group.accountability.RetrofitAPI;
import com.cpen321group.accountability.FrontendConstants;
import com.cpen321group.accountability.welcome.RegisterSettingActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TransactionCreateActivity extends AppCompatActivity {
    private String transactionName = "";
    private String transactionCategory = "";
    private int transactionAmount;
    public static int year = 0;
    public static int month = 0;
    public static int day = 0;
    private String date;
    private static final int REQUEST_CODE = 100;
    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    protected static Uri tempUri;
    private TextView ocr_view;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_create);
        Button capture_button = findViewById(R.id.recipeButton);
        ocr_view = findViewById(R.id.ocr_view);
        Button createTransaction = findViewById(R.id.transactionCreateButton);
        AutoCompleteTextView autoText = findViewById(R.id.transactionCategoryText);

        String[] items = {"daily necessities", "food/drinks", "transportation", "housing", "education", "bills", "others"};
        ArrayAdapter<String> itemAdapter = new ArrayAdapter<>(TransactionCreateActivity.this, R.layout.list_item, items);
        autoText.setAdapter(itemAdapter);
        autoText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                transactionCategory = (String)adapterView.getItemAtPosition(i);
            }
        });

        createTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextInputEditText transactionNameEditText = (TextInputEditText) findViewById(R.id.transactionNameInput);
                transactionName = transactionNameEditText.getText().toString();

                TextInputEditText transactionAmountEditText = (TextInputEditText) findViewById(R.id.transactionAmountPriceInput);
                String TransactionAmountText = transactionAmountEditText.getText().toString();

                date = year + "/" + month + "/" + day;
                Log.d("Date:", "" + date);

                if(!date.equals("0/0/0") && !transactionName.equals("") && !transactionCategory.equals("") && !TransactionAmountText.equals("")) {
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
        year = 0;
        month = 0;
        day = 0;

        // if in test mode, no permission guarantee dialog
        if(FrontendConstants.is_test!=1 && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.CAMERA
            },REQUEST_CODE);
        }

        capture_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(TransactionCreateActivity.this);
                showChoosePicDialog();
            }
        });
    }

    protected void showChoosePicDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Picture");
        String[] items = { "From Gallery", "Camera" };
        builder.setNegativeButton("Cancel", null);
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case CHOOSE_PICTURE:
                        Intent openAlbumIntent = new Intent(
                                Intent.ACTION_GET_CONTENT);
                        openAlbumIntent.setType("image/*");
                        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
                        break;
                    case TAKE_PICTURE:
                        File file = new File(getExternalCacheDir(), "image.jpg");
                        try {
                            if(file.exists()) {
                                file.delete();
                            }
                            file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if(Build.VERSION.SDK_INT >= 24) {
                            tempUri = FileProvider.getUriForFile(TransactionCreateActivity.this, "com.cpen321.provider", file);
                        } else {
                            tempUri = Uri.fromFile(file);
                        }
                        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
                        startActivityForResult(takePhotoIntent, TAKE_PICTURE);
                        break;
                    default:
                        Log.d("Select dialog: ", "Selected nothing");
                        // DO NOTHING
                        break;
                }
            }
        });
        builder.create().show();
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
        }else if(requestCode == TAKE_PICTURE){
            if(resultCode == RESULT_OK){
                CropImage.activity(tempUri)
                        .start(this);
            }
        }else if(requestCode == CHOOSE_PICTURE && resultCode == RESULT_OK){
            assert data != null;
            CropImage.activity(data.getData())
                    .start(this);
        }
    }

    private void getTextFromImage(Bitmap bitmap){
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        recognizer.process(image)
                .addOnSuccessListener(new OnSuccessListener<Text>() {
                    @Override
                    public void onSuccess(Text visionText) {
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
                .baseUrl(FrontendConstants.baseURL + "/transactions/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<JsonObject> call = retrofitAPI.postTransaction(FrontendConstants.userID, this.transactionName, this.transactionCategory, this.date, this.transactionAmount, false, "null");

        Log.d("API url:", FrontendConstants.baseURL + "/transactions/"+ FrontendConstants.userID+"/");
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