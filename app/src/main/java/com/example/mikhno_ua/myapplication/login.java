package com.example.mikhno_ua.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class login extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        final Context context = this;

        Button btnLogin = (Button) findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                TextView textViewMess = (TextView) findViewById(R.id.mess);
                textViewMess.setText("");

                EditText editTextLogin = (EditText) findViewById(R.id.login);
                EditText editTextPass = (EditText) findViewById(R.id.pass);

                String login = editTextLogin.getText().toString();
                String pass = editTextPass.getText().toString();

                try {
                    MessageDigest md = MessageDigest.getInstance("MD5");

                    md.update(pass.getBytes());
                    byte byteData[] = md.digest();

                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < byteData.length; i++) {
                        sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
                    }

                    //Toast.makeText(context, sb.toString(), Toast.LENGTH_LONG).show();
                    TextView textView2 = (TextView) findViewById(R.id.textView2);
                    textView2.setText(sb.toString());
                    pass = sb.toString();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                //Toast.makeText(context, login.getText()+"\n"+pass.getText(), Toast.LENGTH_LONG).show();

                new ParseTask(context, login, pass).execute();
            }
        });
    }

    private class ParseTask extends AsyncTask<Void, Void, String> {

        private final Context context;
        private final String login;
        private final String pass;

        public ParseTask(Context context, String login, String pass) {
            super();
            this.context = context;
            this.login = login;
            this.pass = pass;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";
        TableLayout tableLayout = (TableLayout) findViewById(R.id.tableLayout);

        @Override
        protected String doInBackground(Void... params) {
            // получаем данные с внешнего ресурса
            try {
                URL url = new URL("https://arm.loesk.ru/android/login.php?login=" + URLEncoder.encode(login,"UTF-8") + "&pass=" + pass);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);

            Log.d("AUTH", strJson);

            JSONObject dataJsonObj = null;
            String secondName = "";

            try {
                dataJsonObj = new JSONObject(strJson);
                int authorizationStatus = dataJsonObj.getInt("success");
                if (authorizationStatus == 0) {
                    TextView mess = (TextView) findViewById(R.id.mess);
                    mess.setText(dataJsonObj.getString("message"));
                } else if (authorizationStatus == 1) {
                    Intent intent = new Intent(context, MainActivity.class);
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
