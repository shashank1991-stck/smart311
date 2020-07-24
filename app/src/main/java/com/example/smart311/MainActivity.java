package com.example.smart311;

import android.app.AlertDialog;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.smart311.MESSAGE";
    private TextView txvResult;

    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = "http://10.0.2.2:3002";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txvResult = (TextView) findViewById(R.id.txvResult);
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);
    }
    public void sendMessage(View view) {

        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        HashMap<String, String> map = new HashMap<>();

        map.put("text", message);


        Call<Void> call = retrofitInterface.addSpeech(map);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (response.code() == 200) {
                    Toast.makeText(MainActivity.this,
                            "Saved Successfully", Toast.LENGTH_LONG).show();

                    Call<List<SpeechReserved>> getSpeechCall = retrofitInterface.getSpeech();
                    getSpeechCall.enqueue(new Callback<List<SpeechReserved>>() {
                        @Override
                        public void onResponse(Call<List<SpeechReserved>> call, Response<List<SpeechReserved>> response) {

                            if (response.code() == 200) {
                                List<SpeechReserved> result = response.body();

                                StringBuilder sb = new StringBuilder();
                                for(SpeechReserved sp: result){
                                    sb.append(sp.getText() + "\n");
                                }

                                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);

                                builder1.setMessage(sb.toString());

                                builder1.show();


                            }
                            else{
                                Toast.makeText(MainActivity.this,
                                        "Response code "+response.code() + " , url " + call.request().url(),Toast.LENGTH_LONG).show();
                            }

                        }

                        @Override
                        public void onFailure(Call<List<SpeechReserved>> call, Throwable t) {
                            Toast.makeText(MainActivity.this, t.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else{
                    Toast.makeText(MainActivity.this,
                            "Response code"+response.code() + " , url " + call.request().url(),Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });



        //intent.putExtra(EXTRA_MESSAGE, message);
        //startActivity(intent);
    }

    public void getSpeechInput(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txvResult.setText(result.get(0));
                    HashMap<String, String> map = new HashMap<>();

                    map.put("text", result.get(0));


                    Call<Void> call = retrofitInterface.addSpeech(map);

                }
                break;
        }
    }
}