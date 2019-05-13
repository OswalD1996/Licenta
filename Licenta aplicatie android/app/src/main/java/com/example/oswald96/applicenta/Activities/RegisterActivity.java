package com.example.oswald96.applicenta.Activities;

import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.oswald96.applicenta.R;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;
import cz.msebera.android.httpclient.util.EntityUtils;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        if (Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        final EditText text_username = (EditText) findViewById(R.id.editTextUser2);
        final EditText text_password = (EditText) findViewById(R.id.editTextPass2);
        Button btnRegister = (Button) findViewById(R.id.buttonFinRegister);
            btnRegister.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                public void onClick(View view) {
                    HttpClient httpClient = HttpClientBuilder.create().build(); //Use this instead
                    HttpPost request = new HttpPost("http://exactonly.ro:13000/user_data");
                    Map< String, Object > jsonValues = new HashMap< String, Object >();
                    jsonValues.put("Username", text_username.getText().toString());
                    jsonValues.put("Password", text_password.getText().toString());

                    JSONObject json = new JSONObject(jsonValues);
                    StringEntity entity = new StringEntity(json.toString(), "UTF8");
                    entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    request.setEntity(entity);
                    try {
                        HttpResponse response = httpClient.execute(request);
                        HttpEntity respEntity = response.getEntity();
                        if (respEntity != null) {
                            // EntityUtils to get the response content
                            String content =  EntityUtils.toString(respEntity);
                            JSONObject rezultatfinal = new JSONObject(content);
                            content = rezultatfinal.getString("message").toString();
                            Toast.makeText(RegisterActivity.this, content, Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) { e.printStackTrace(); } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
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
