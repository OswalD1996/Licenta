package com.example.oswald96.applicenta.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.oswald96.applicenta.StructureClasses.DataFromWAQI;
import com.example.oswald96.applicenta.R;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class GraficIndiciPoluareActivity extends AppCompatActivity {
    private DataFromWAQI[] dateCurente= new DataFromWAQI[10000];
    int counter = 0;
    String username;
    Spinner drp_listaoptiuni;
    List<String> listdrop = new ArrayList<String>();
    GraphView graph;
    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafic_indici_poluare);
        username = getIntent().getStringExtra("Username");
        graph = (GraphView) findViewById(R.id.graph);
        //initializare dropdown
        drp_listaoptiuni = (Spinner) findViewById(R.id.dropdown2);
        listdrop.add("IndexAQI");
        listdrop.add("PM10");
        listdrop.add("O3");
        listdrop.add("SO2");
        listdrop.add("CO");
        listdrop.add("NO2");
        listdrop.add("PM25");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(GraficIndiciPoluareActivity.this, android.R.layout.simple_spinner_item, listdrop);
        drp_listaoptiuni.setAdapter(dataAdapter);
        try {
            dateCurente[9999] = new DataFromWAQI("http://exactonly.ro:13000/quality_indexes/"+username, this);
            dateCurente[9999].getDataPerUserFromMyAPI("http://exactonly.ro:13000/quality_indexes/"+username);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Button btnMap = (Button) findViewById(R.id.btn_graph);
        btnMap.setOnClickListener(new View.OnClickListener() {
                                      public void onClick(View view){
                                          plotGrpah();
                                      }
                                  }
        );
    }

    public void plotGrpah()
    {
        counter = 0;
        ArrayList<DataFromWAQI> dataFromMyAPI = dateCurente[9999].datefinaleperUser;
        for(DataFromWAQI elements:dataFromMyAPI)
        {
            dateCurente[counter] = elements;
            counter++;
        }
        graph.removeAllSeries();
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {});

        /*Arrays.sort(dateCurente, new Comparator<DataFromWAQI>() {
            @Override
            public int compare(DataFromWAQI date1, DataFromWAQI date2) {
                return (date1.formatedDate()).compareTo(date2.formatedDate());
            }
        });*/

        DataPoint puncteGrafic[]= new DataPoint[counter];
        for(int j = 0; j < counter ;j++) {
            if (drp_listaoptiuni.getSelectedItem().toString() == "IndexAQI") {
                series.appendData(new DataPoint(dateCurente[j].formatedDate(), dateCurente[j].calculateaqi()), true, counter);
                series.setColor(Color.BLACK);
            }
            else {
                Method getterMethod = null;
                try {
                    getterMethod = dateCurente[j].getClass().getMethod("get" + drp_listaoptiuni.getSelectedItem().toString());
                    String pollutionValue = getterMethod.invoke(dateCurente[j]).toString();

                    series.appendData(new DataPoint(dateCurente[j].formatedDate(), Float.parseFloat(pollutionValue)), true, counter);
                    series.setColor(Color.BLACK);

                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

        graph.addSeries(series);
        graph.getViewport().setScalable(true);
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter()
        {
            @Override
            public String formatLabel(double value, boolean isValueX)
            {
                if(isValueX)
                {
                    return  sdf.format(new Date((long) value));
                }else
                {
                    return super.formatLabel(value, isValueX);
                }
            }
        });
        graph.getGridLabelRenderer().setNumHorizontalLabels(3);

    }
    public void onBackPressed() {
        Intent intent = new Intent(GraficIndiciPoluareActivity.this, MainActivity.class);
        intent.putExtra("Username", username.toString());
        startActivity(intent);
        finish();
    }
}
