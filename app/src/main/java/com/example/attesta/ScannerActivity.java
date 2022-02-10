package com.example.attesta;

import static android.Manifest.permission.CAMERA;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.util.List;


public class ScannerActivity extends AppCompatActivity {

    private ImageView captureIV;
    private TextView resultTV;
    private Button snapBtn,detectBtn,clipboardBtn;
    private Bitmap imageBitmap;
    static final int REQUEST_IMAGE_CAPTURE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_activity);
        captureIV=findViewById(R.id.idIVCaptureImage);
        resultTV = findViewById(R.id.idTVDetectedText);
        snapBtn = findViewById(R.id.idBtnSnap);
        detectBtn = findViewById(R.id.idBtnDetect);
        clipboardBtn = findViewById(R.id.idBtnClip);
        detectBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v){
                detectText();
            }
        });
        clipboardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data=String.valueOf(resultTV.getText());
                ClipboardManager clipboard;
                clipboard=(ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("text", data);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(ScannerActivity.this,"Successfully copied...",Toast.LENGTH_SHORT).show();
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, @NonNull  int[] grantResults) {
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
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_IMAGE_CAPTURE && resultCode ==RESULT_OK){
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
            public void onSuccess(@NonNull  Text texts) {
                List<Text.TextBlock> blocks=texts.getTextBlocks();
                //resultTV.setText(Integer.toString(blocks.size()));
                for(int i=0;i<blocks.size();i++)
                {
                    List<Text.Line> lines=blocks.get(i).getLines();
                        for(int j=0;j<lines.size();j++)
                        {
                            if(i==2) {
                                resultTV.setText(lines.get(j).getText());
                            }
                        }

                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ScannerActivity.this,"Fail to detect text from image.."+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }
}