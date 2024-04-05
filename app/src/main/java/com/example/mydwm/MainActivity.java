package com.example.mydwm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class MainActivity extends Activity {
    private ImageView imageView;
    private Button imageButton;
    private Button saveButton;

    private Button insertButton;
    private EditText editText;
    private String insertText;
    private String binaryText;
    private Button extractButton;
    private String extractText;
    private TextView textView;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageButton = findViewById(R.id.imageButton);
        imageView = findViewById(R.id.imageView);
        saveButton = findViewById(R.id.saveButton);

        insertButton = findViewById(R.id.insertButton);
        editText = findViewById(R.id.editText);
        extractButton = findViewById(R.id.extractButton);
        textView = findViewById(R.id.textView);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                saveImage(bitmap);
            }
        });

        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertText = editText.getText().toString();
                StringBuilder binaryBuild = new StringBuilder();
                for (int i=0; i<insertText.length(); i++)
                {
                    char c = insertText.charAt(i);
                    binaryBuild.append(Integer.toBinaryString(c));
                }
                binaryText = binaryBuild.toString();
                //обработка текста
            }
        });
        extractButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //extractText = binaryText;
                //int tenCode = Integer.parseInt(binaryText, 2);//////////
                //int charCode = Integer.parseInt(binaryText.substring(2), 2);
                //String str = new Character((char)charCode).toString();
                //StringBuilder textBuild = new StringBuilder();
                String textBuild = Character.toString((char) Integer.parseInt(binaryText.substring(0,7), 2));
                int tenCode = 0;
                for (int i=7; i<binaryText.length(); i+=7)
                {
                    //tenCode = Integer.parseInt(binaryText.substring(i,i+7), 2);
                    String returnChar = Character.toString((char) Integer.parseInt(binaryText.substring(i,i+7), 2));////////
                    textBuild=textBuild + returnChar;//////////
                }
                //extractText = Character.toString((char)tenCode);////////
                //extractText =String.valueOf(tenCode);
                extractText = textBuild;
                if (extractText != null) {
                    textView.setText(extractText);
                } else {
                    textView.setText("Цифровой водяной знак не найден.");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            Glide.with(this).load(selectedImage).into(imageView);
        }
    }

    private void saveImage(Bitmap bitmap) {
        Uri outputFileUri = getOutputMediaFileUri();
        Intent intent = new Intent(android.content.Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(outputFileUri);
        sendBroadcast(intent);
        try
        {
            FileOutputStream fos = new FileOutputStream(outputFileUri.getPath());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            Toast.makeText(this, "Image saved successfully", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
        }
    }

    private Uri getOutputMediaFileUri() {
        String timeStamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "DigitalWatermarksImages");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + imageFileName + ".jpg");
        Uri outputFileUri = Uri.fromFile(mediaFile);
        return outputFileUri;
    }
}
