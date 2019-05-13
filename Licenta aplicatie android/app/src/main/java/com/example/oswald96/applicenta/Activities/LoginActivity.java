package com.example.oswald96.applicenta.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.oswald96.applicenta.R;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        final EditText text_username = (EditText) findViewById(R.id.editTextUser);
        final EditText text_password = (EditText) findViewById(R.id.editTextPass);
        Button btnLogin = (Button) findViewById(R.id.buttonLogin);
        Button btnRegister = (Button) findViewById(R.id.buttonFinRegister);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
                if(isInternetWorking()==false)
                {
                    Toast.makeText(LoginActivity.this, "You are not connected to the internet", Toast.LENGTH_SHORT).show();
                }
                else {
                    String rezultat = new String();
                    URL url = null;
                    if (text_username.getText().toString().equals("") || text_password.getText().toString().equals("")) {
                        Toast.makeText(LoginActivity.this, "Username or Password are initial", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        try {
                            url = new URL("http://exactonly.ro:13000/user_data/Login/" + text_username.getText().toString() + "/" + text_password.getText().toString());
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        HttpURLConnection urlConnection = null;
                        try {
                            urlConnection = (HttpURLConnection) url.openConnection();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            InputStream in = null;
                            try {
                                in = new BufferedInputStream(urlConnection.getInputStream());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            rezultat = readStream(in);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            urlConnection.disconnect();
                        }
                        JSONObject rezultatfinal = null;
                        try {
                            rezultatfinal = new JSONObject(rezultat);
                            rezultat = rezultatfinal.getString("message").toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //Toast.makeText(LoginActivity.this, rezultat, Toast.LENGTH_SHORT).show();
                        if (rezultat.equals("You are now logged in"))
                        {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra( "PreviousActivity", "Main");
                            intent.putExtra("Username", text_username.getText().toString());
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this, "Username or password wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (isInternetWorking() == false) {
                    Toast.makeText(LoginActivity.this, "You are not connected to the internet", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
    public boolean isInternetWorking() {
        boolean success = false;
        try {
            URL url = new URL("https://google.com");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.connect();
            success = connection.getResponseCode() == 200;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return success;
    }
    private String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while(i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }
}
