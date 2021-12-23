package com.example.attesta;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.Manifest.permission;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.TextRecognizerOptions;

import static android.Manifest.permission.CAMERA;


public class ScannerActitivity extends AppCompatActivity {

    private ImageView captureIV;
    private TextView resultTV;
    private Button snapBtn,detectBtn;
    private Bitmap imageBitmap;
    static final int REQUEST_IMAGE_CAPTURE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_actitivity);
        resultTV = findViewById(R.id.idTVDetectedText);
        snapBtn = findViewById(R.id.idBtnSnap);
        detectBtn = findViewById(R.id.idBtnDetect);

        detectBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v){
                detectText();
            }
        });
        snapBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v){
                if(checkPermissions()){
                    captureImage();
                }
                else
                {
                    requestPermission();
                }
            }
        });
    }
    private boolean checkPermissions(){
        int cameraPermission = ContextCompat.checkSelfPermission(getApplicationContext(),CAMERA);
        return cameraPermission== PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(){
        int PERMISSION_CODE=200;
        ActivityCompat.requestPermissions(this,new String[]{CAMERA},PERMISSION_CODE);
    }

    private void captureImage(){
        Intent takePicture=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePicture.resolveActivity(getPackageManager())!=null){
            startActivityForResult(takePicture,REQUEST_IMAGE_CAPTURE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull  String[] permissions, @NonNull  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0){
            boolean cameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if(cameraPermission){
                Toast.makeText(this,"Permission Granted...",Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this,"Permission denied",Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_IMAGE_CAPTURE && requestCode==RESULT_OK){
            Bundle extras= data.getExtras();
            imageBitmap=(Bitmap) extras.get("data");
            captureIV.setImageBitmap(imageBitmap);
        }
    }

    private void detectText(){
        InputImage image=InputImage.fromBitmap(imageBitmap,0);
        TextRecognizer recognizer= TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        Task<Text> result =recognizer.process(image).addOnSuccessListener(new OnSuccessListener<Text>() {
            @Override
            public void onSuccess(@NonNull  Text text) {
                StringBuilder result= new StringBuilder();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @org.jetbrains.annotations.NotNull Exception e) {
                Toast.makeText(ScannerActitivit.this)
            }
        })

    }
}