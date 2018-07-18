package com.example.mikhno_ua.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    
    List<String> params = new ArrayList<String>();
    List<String> columns = new ArrayList<String>();
                    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //params.add("ido");
        params.add("ob");
        //params.add("ad");
        //params.add("idr");
        params.add("go");
        //params.add("sh");
        //params.add("dol");

        columns.add("№");
        columns.add("Объект");
        //columns.add("Адрес");
        columns.add("Год осмотра");

        new ParseTask(this).execute();
    }

    public static String LOG_TAG = "my_log";

    private class ParseTask extends AsyncTask<Void, Void, String> {

        private final Context context;

        public ParseTask (Context context) {
            super();
            this.context = context;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";
        TableLayout tableLayout = (TableLayout) findViewById(R.id.tableLayout);

        @Override
        protected String doInBackground(Void... params) {
            // получаем данные с внешнего ресурса
            try {
                URL url = new URL("https://arm.loesk.ru/android/main.php");

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
            // выводим целиком полученную json-строку
            Log.d(LOG_TAG, strJson);

            JSONObject dataJsonObj = null;
            String secondName = "";

            try {

                TableRow th = new TableRow(context);
                th.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT));

                for (String column:columns
                        ) {
                    addTextViewInTable(column,th);
                }

                tableLayout.addView(th);

                dataJsonObj = new JSONObject(strJson);
                JSONArray inspects = dataJsonObj.getJSONArray("inspects");

                for (int i = 0; i < inspects.length(); i++) {
                    JSONObject inspect = inspects.getJSONObject(i);

                    final int ido = inspect.getInt("ido");
                    //final String ido = inspect.getString("ido");
                    final String ad = inspect.getString("ad");

                    TableRow tableRow = new TableRow(context);
                    tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                            TableLayout.LayoutParams.WRAP_CONTENT));

                    addTextViewInTable(String.valueOf(i+1),tableRow);

                    for (String param:params
                         ) {
                        addTextViewInTable(inspect.getString(param),tableRow);
                    }

                    View.OnClickListener onClickTr = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            //Toast.makeText(context, ad, Toast.LENGTH_LONG).show();
                            //Toast.makeText(context, String.valueOf(ido), Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(context, objectInfo.class);
                            intent.putExtra("ido",ido);
                            startActivity(intent);
                        }
                    };

                    tableRow.setOnClickListener(onClickTr);
                    tableLayout.addView(tableRow);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void addTextViewInTable(String text, TableRow row) {
            TextView textView = new TextView(context);
            textView.setText(text);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            textView.setBackground(context.getResources().getDrawable(R.drawable.cell_shape));
            textView.setPadding(10,10,10,10);
            row.addView(textView);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
