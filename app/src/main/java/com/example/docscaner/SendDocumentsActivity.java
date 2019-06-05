package com.example.docscaner;

import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.AdapterView;
import android.support.v7.app.AppCompatActivity;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import android.widget.Spinner;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
public class SendDocumentsActivity extends AppCompatActivity implements OnItemSelectedListener{
    ArrayList<File> documents;
    ArrayList<byte[]> documentsData;
    String selectClass;
    HTTP Http = new HTTP();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_document);

        documents = (ArrayList<File>)getIntent().getSerializableExtra(MainActivity.EXTRA_DOCUMENTS);
        documentsData = (ArrayList<byte[]>)getIntent().getSerializableExtra(MainActivity.EXTRA_DOCUMENTSBYTES);
        System.out.println(documents);

        GridView gallery = findViewById(R.id.galleryGridView);
        gallery.setAdapter(new ImageAdapter(this, documents));

        final ArrayList<String> classes = new ArrayList<>();


        final Spinner spinner = findViewById(R.id.classesList);
        // Создаем адаптер ArrayAdapter с помощью массива строк и стандартной разметки элемета spinner
        final ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, classes);
        // Определяем разметку для использования при выборе элемента
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        // Применяем адаптер к элементу spinner

        spinner.setOnItemSelectedListener(this);
        Http.get("http://192.168.31.101:3010/api/getClasses", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String jsonData = response.body().string();
                System.out.println(jsonData);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(jsonData);
                    JSONArray jsonArray=jsonObject.getJSONArray("collation");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                        String name_class=jsonObject1.getString("name");
                        classes.add(name_class);
                    }
                    spinner.setAdapter(adapter);
                    spinner.setPrompt("Выберете класс документа");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //System.out.println(call);
            }
        });

    }


    public void send(View view) {

        System.out.println(selectClass);
        System.out.println(documents);
        for (int i = 0; i < documents.size(); i++){
            System.out.println(documents.get(i).toURI());
        }
        this.Http.post("http://192.168.31.101:3010/api/putDocument", documentsData, selectClass, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonData = response.body().string();
                JSONObject jsonObject = null;
                try{
                    jsonObject = new JSONObject(jsonData);
                    String n=jsonObject.getString("n");
                    System.out.println(n);

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        System.out.println("Something selected");
        // An item was selected. You can retrieve the selected item using
        System.out.println(parent.getSelectedItem().toString());
        selectClass = parent.getSelectedItem().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        System.out.println("Nothing selected");
    }

}