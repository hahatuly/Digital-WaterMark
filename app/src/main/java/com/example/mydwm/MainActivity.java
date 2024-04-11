package com.example.mydwm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends Activity {
    private  Bitmap bitmap;
    private ImageView imageView;
    private Button imageButton;
    private Button saveButton;

    private EditText keyText;
    private Button keyButton;
    private String binaryKey;
    private char[] binaryKeyArray;


    private EditText editText;
    private Button insertButton;
    private Bitmap modifiedBitmap;

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

        keyButton = findViewById(R.id.keyButton);
        keyText = findViewById(R.id.keyText);

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
            public void onClick(View v)
            {
                saveImage(modifiedBitmap);
            }
        });
        keyButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (keyText != null) {
                    String key = keyText.getText().toString();
                    binaryKey = Integer.toBinaryString(key.charAt(0));
                    for (int i = 1; i < key.length(); i++) {
                        binaryKey += Integer.toBinaryString(key.charAt(i));
                    }
                    binaryKeyArray = binaryKey.toCharArray();
                }
            }
        });
        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (editText!=null && bitmap!=null && keyText!=null) {
                    String insertText = editText.getText().toString();
                    String binaryText = Integer.toBinaryString(insertText.charAt(0));
                    for (int i = 1; i < insertText.length(); i++) {
                        binaryText += Integer.toBinaryString(insertText.charAt(i));
                    }
                    char[] binaryTextArray = binaryText.toCharArray();
                    //обработка текста

                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    // Create a new bitmap to store the modified image
                    modifiedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    int iKey = 0;
                    int iText =0;
                    for (int y = 0; y < height; y++)
                    {
                        for (int x = 0; x < width; x++)
                        {
                            int pixel = bitmap.getPixel(x, y);
                            int red = Color.red(pixel);
                            int green = Color.green(pixel);
                            int blue = Color.blue(pixel);
                            int alpha = Color.alpha(pixel);if (((y==1 || y % 3 == 0) && (y!=height-1)) && ((x==1 || x % 3 == 0) && (x!=width-1)))
                            {//добавить пробел между ключами и между отермарками мб 7 нулей
                                if (binaryKeyArray[iKey]=='1')
                                {
                                    if (binaryTextArray[iText]=='1')
                                    {
                                        if (red > green && red > blue) {
                                            if (red <= 245) {
                                                red += 10; // 10 умножить на бит вотермарки
                                            } else red -= 10;
                                        }
                                        if (green > red && green > blue) {
                                            if (green <= 245) {
                                                green += 10;
                                            } else green -= 10;
                                        }
                                        if (blue >= red && blue >= green) {
                                            if (blue <= 245) {
                                                blue += 10;
                                            } else blue -= 10;
                                        }
                                    }
                                    iText++;
                                    if (iText == binaryText.length())
                                    {
                                        iText=0;
                                    }
                                }
                                iKey++;
                                if (iKey == binaryKey.length())
                                {
                                    iKey=0;
                                }
                            }
                            int modifiedColor = Color.argb(alpha, red, green, blue);
                            modifiedBitmap.setPixel(x, y, modifiedColor);
                        }
                    }
                    imageView.setImageBitmap(modifiedBitmap);
                }
            }
        });
        extractButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (extractText != null) {
                String textBuild = Character.toString((char) Integer.parseInt(extractText.substring(0,7), 2));
                for (int i=7; i<extractText.length(); i+=7)
                {
                    String returnChar = Character.toString((char) Integer.parseInt(extractText.substring(i,i+7), 2));
                    textBuild+=returnChar;
                }
                textView.setText(textBuild);
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
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            imageView.setImageBitmap(bitmap);
        }
    }

    private void saveImage(Bitmap modifiedBitmap) {
        Uri outputFileUri = getOutputMediaFileUri();
        Intent intent = new Intent(android.content.Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(outputFileUri);
        sendBroadcast(intent);
        try
        {
            FileOutputStream fos = new FileOutputStream(outputFileUri.getPath());
            modifiedBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            Toast.makeText(this, "Image saved successfully", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
        }
    }

    private Uri getOutputMediaFileUri() {
        String timeStamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
            String imageFileName = "PNG_" + timeStamp + "_";
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "DigitalWatermarksImages");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + imageFileName + ".png");
        Uri outputFileUri = Uri.fromFile(mediaFile);
        return outputFileUri;
    }
}
