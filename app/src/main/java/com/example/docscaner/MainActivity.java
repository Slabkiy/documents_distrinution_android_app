package com.example.docscaner;

import android.annotation.SuppressLint;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Button;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;

import android.os.StrictMode;
import android.content.Intent;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_DOCUMENTS = "com.example.docscaner.DOCUMENTS";
    public static final String EXTRA_DOCUMENTSBYTES = "com.example.docscaner.DOCUMENTSBYTES";
    Camera camera;
    FrameLayout frameLayout;
    ShowCamera showCamera;
    Button btnCount;
    TextView badgeNotification;
    ArrayList<File> documents = new ArrayList<>();
    ArrayList<byte[]> documentsData = new ArrayList<>();
    //Boolean Auth = (Boolean)getIntent().getSerializableExtra(Login.EXTRA_AUTH);
    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        frameLayout = findViewById(R.id.counter);
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        /*if (Auth) {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
        }*/
        System.out.println("_________________onCreated___________________");
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        btnCount = findViewById(R.id.btnCount);
        badgeNotification = findViewById(R.id.badge_notification);
        //badgeNotification.setText(0);
        //open the camera
        btnCount.setEnabled(false);


    }
    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File picture_file = getOutputMediaFile();
            System.out.println(picture_file);
            documents.add(picture_file);
            documentsData.add(data);
            badgeNotification.setText(String.valueOf(documents.size()));
            btnCount.setEnabled(true);
            if(picture_file == null){
                return;
            }else {
                try {
                    FileOutputStream fos = new FileOutputStream(picture_file);
                    fos.write(data);
                    fos.close();

                    camera.startPreview();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


        }
    };
    private File getOutputMediaFile(){
        String state = Environment.getExternalStorageState();
        if(!state.equals(Environment.MEDIA_MOUNTED)){
            return null;
        }else{
            File folder_qui = new File(Environment.getExternalStorageDirectory() + File.separator + "docScaner");
            if(!folder_qui.exists()){
                folder_qui.mkdirs();
            }
            String name = new Timestamp(new Date().getTime()) + ".jpg";
            File outputFile = new File(folder_qui, name);
            return outputFile;
        }
    }

    public void captureImage(View v){

        if(camera != null){
            camera.takePicture(null, null, mPictureCallback);
        }
    }

    public void openGallery(View v) {

    }
    public void sendImages(View v){
        Intent intent = new Intent(this, SendDocumentsActivity.class);
        System.out.println(documents);
        intent.putExtra(EXTRA_DOCUMENTS, documents);
        intent.putExtra(EXTRA_DOCUMENTSBYTES, documentsData);
        startActivity(intent);
    }

    @Override
    protected void onPause() {

        super.onPause();
        System.out.println("_______________________________ON PAUSE___________________");
    }
    @Override
    protected void onStart() {         // release the camera immediately on pause event
        super.onStart();
        System.out.println("_______________________________ON START___________________");
    }
    @Override
    protected void onStop() {       // release the camera immediately on pause event
        super.onStop();
        System.out.println("_______________________________ON STOP___________________");
    }
    @Override
    protected void onResume() {           // release the camera immediately on pause event
        super.onResume();
        releaseCamera();
        System.out.println("_______________________________ON RESUME___________________");
    }

    private void releaseCamera(){
        camera = Camera.open();
        showCamera = new ShowCamera(this, camera);
        frameLayout.addView(showCamera);
    }
}

class UploadService {

    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    public void uploadImage(File image, String imageName) throws IOException {

        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", imageName, RequestBody.create(MEDIA_TYPE_PNG, image))
                .build();

        Request request = new Request.Builder().url("http://localhost:3010/api/putDocument")
                .post(requestBody).build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }

    }

}