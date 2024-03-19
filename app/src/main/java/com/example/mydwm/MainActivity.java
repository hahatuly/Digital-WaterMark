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
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;



public class MainActivity extends Activity {
    private static final int SELECT_PICTURE = 0;
    private Button button;
    private ImageView imageView;
    private Button save_button;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);
        imageView = findViewById(R.id.imageView);
        save_button = findViewById(R.id.save_button);

        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                saveImage(bitmap);
                //Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                //startActivityForResult(intent, 2);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        if (requestCode == 1 && resultCode == RESULT_OK && data != null)
        {
            Uri selectedImage = data.getData();
            Glide.with(this).load(selectedImage).into(imageView);
        }

    }

    private void saveImage(Bitmap bitmap)
    {
        Uri outputFileUri = getOutputMediaFileUri();
        Intent intent = new Intent(android.content.Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(outputFileUri);
        sendBroadcast(intent);
    }

    private Uri getOutputMediaFileUri()
    {
        String timeStamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");

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
