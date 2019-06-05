
package com.example.docscaner;
import java.io.File;
import java.util.ArrayList;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
public class HTTP {
    OkHttpClient client = new OkHttpClient();
    public void get(String url, Callback callback){
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public void post(String url, ArrayList<byte[]> documents, String docClass, final Callback callback){
        MediaType MEDIA_TYPE_PNG;

        MultipartBody.Builder buildernew = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (byte[] image : documents) {
            MEDIA_TYPE_PNG = MediaType.parse("image/png");
            RequestBody imageBody = RequestBody.create(MEDIA_TYPE_PNG, image);
            buildernew.addFormDataPart(docClass, "test", imageBody);
        }
        MultipartBody requestBody = buildernew.build();
        System.out.println(requestBody);
        final Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = client.newCall(request).execute();
                    if (response.message().contentEquals("OK") && response.code() == 200) {
                        client.newCall(request).enqueue(callback);
                    }
                    System.out.println(response.body().string());
                } catch (Exception e) {

                }
            }
        });
        thread.start();
    }

}
