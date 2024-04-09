package com.example.mydwm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
//import android.content.Context;
import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
import android.graphics.Color;
//import android.graphics.drawable.BitmapDrawable;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
//import java.nio.charset.Charset;
//import java.nio.charset.StandardCharsets;

public class MainActivity extends Activity {
    private String binaryImageString;



    private  Bitmap bitmap;
    private ImageView imageView;
    private Button imageButton;
    private Button saveButton;

    private Button insertButton;
    private EditText editText;
    private String insertText;
    private String binaryText;
    private List<Integer> pixels;
    private List<Integer> reconstructedPixels;
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
                saveImage(bitmap);
            }
        });

        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (editText!=null && bitmap!=null) {
                    insertText = editText.getText().toString();
                    String binarySim = Integer.toBinaryString(insertText.charAt(0));
                    for (int i = 1; i < insertText.length(); i++) {
                        binarySim += Integer.toBinaryString(insertText.charAt(i));
                    }
                    binaryText = binarySim;
                    //обработка текста

                    // Get the width and height of the bitmap
                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();

                    // Create a binary string to store pixel values
                    StringBuilder binaryString = new StringBuilder();

                    // Encode each pixel into binary code
                    for (int y = 0; y < height; y++) {
                        for (int x = 0; x < width; x++) {
                            int pixel = bitmap.getPixel(x, y);
                            int red = Color.red(pixel);
                            int green = Color.green(pixel);
                            int blue = Color.blue(pixel);
                            int alpha = Color.alpha(pixel);

                            // Append each color component to the binary string
                            binaryString.append(String.format("%8s", Integer.toBinaryString(alpha)).replace(' ', '0'));
                            binaryString.append(String.format("%8s", Integer.toBinaryString(red)).replace(' ', '0'));
                            binaryString.append(String.format("%8s", Integer.toBinaryString(green)).replace(' ', '0'));
                            binaryString.append(String.format("%8s", Integer.toBinaryString(blue)).replace(' ', '0'));
                        }
                    }

                    // Create a new bitmap to store the decoded image
                    Bitmap decodedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

                    // Decode the binary string back into pixels
                    int index = 0;
                    for (int y = 0; y < height; y++) {
                        for (int x = 0; x < width; x++) {
                            // Extract each color component from the binary string
                            int alpha = Integer.parseInt(binaryString.substring(index, index + 8), 2);
                            index += 8;
                            int red = Integer.parseInt(binaryString.substring(index, index + 8), 2);
                            index += 8;
                            int green = Integer.parseInt(binaryString.substring(index, index + 8), 2);
                            index += 8;
                            int blue = Integer.parseInt(binaryString.substring(index, index + 8), 2);
                            index += 8;

                            // Set the pixel in the decoded bitmap
                            decodedBitmap.setPixel(x, y, Color.argb(alpha, red, green, blue));
                        }
                    }

                    // Display the decoded bitmap
                    //ImageView imageView = findViewById(R.id.imageView);
                    imageView.setImageBitmap(decodedBitmap);









                    /*int W = bitmap.getWidth();
                    int H = bitmap.getHeight();
                    int[][][] clr = new int[3][H][W];
                    StringBuilder binaryImageStringBuilder = new StringBuilder();
                    for (int x = 0; x < W; x++) {
                        for (int y = 0; y < H; y++) {
                            int pixelColor = bitmap.getPixel(x, y);
                            // Здесь вы можете добавить логику для преобразования цвета в бинарный код
                            // Например, использование масок для RGB
                            clr[0][y][x] = Color.red(pixelColor);
                            clr[1][y][x] = Color.green(pixelColor);
                            clr[2][y][x] = Color.blue(pixelColor);
                            // Преобразование каждого компонента цвета в двоичную систему
                            //binaryImageStringBuilder.append(Integer.toBinaryString(clr[0][y][x])).append(" ");
                            //binaryImageStringBuilder.append(Integer.toBinaryString(clr[1][y][x])).append(" ");
                            //binaryImageStringBuilder.append(Integer.toBinaryString(clr[2][y][x])).append(" ");
                            
                            //bitmap.setPixel(x, y, Color.argb(Color.alpha(pixelColor), clr[0][y][x], clr[1][y][x], clr[2][y][x]));
                        }
                    }
                    bitmap.setPixel(1, 1, Color.argb(255, clr[0][1][1], clr[1][1][1], clr[2][1][1]));*/
                    /*for (int x = 0; x < W; x++) {
                            for (int y = 0; y < H; y++) {
                                bitmap.setPixel(x, y, Color.rgb(clr[0][y][x], clr[1][y][x], clr[2][y][x]));
                            }
                    }*/
                    //imageView.setImageBitmap(bitmap);
                    //binaryImageString = binaryImageStringBuilder.toString();//стереть

                }
            }
        });
        extractButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*вернутьString textBuild = Character.toString((char) Integer.parseInt(binaryText.substring(0,7), 2));
                int tenCode = 0;
                for (int i=7; i<binaryText.length(); i+=7)
                {
                    String returnChar = Character.toString((char) Integer.parseInt(binaryText.substring(i,i+7), 2));////////
                    textBuild=textBuild + returnChar;//////////
                }
                extractText = textBuild;*/
                extractText=reconstructedPixels.toString();
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
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //imageView.setImageBitmap(bitmap);
            Glide.with(this).load(selectedImage).into(imageView);
            //стереть binaryImageString = convertToBinaryString(bitmap);
        }
    }

   /*стереть private String convertToBinaryString(Bitmap bitmap)
    {
        StringBuilder binaryImageStringBuilder = new StringBuilder();
        for (int x = 0; x < bitmap.getWidth(); x++) {
            for (int y = 0; y < bitmap.getHeight(); y++) {
                int pixelColor = bitmap.getPixel(x, y);
                // Здесь вы можете добавить логику для преобразования цвета в бинарный код
                // Например, использование масок для RGB
                int red = Color.red(pixelColor);
                int green = Color.green(pixelColor);
                int blue = Color.blue(pixelColor);

                // Преобразование каждого компонента цвета в двоичную систему
                binaryImageStringBuilder.append(Integer.toBinaryString(red)).append(" ");
                binaryImageStringBuilder.append(Integer.toBinaryString(green)).append(" ");
                binaryImageStringBuilder.append(Integer.toBinaryString(blue)).append(" ");
            }
        }

        return binaryImageStringBuilder.toString();
    }*/

    private void saveImage(Bitmap bitmap) {
        Uri outputFileUri = getOutputMediaFileUri();
        Intent intent = new Intent(android.content.Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(outputFileUri);
        sendBroadcast(intent);
        try
        {
            FileOutputStream fos = new FileOutputStream(outputFileUri.getPath());
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
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
