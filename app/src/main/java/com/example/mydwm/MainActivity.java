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


    private Button extractButton;
    private TextView textView;



    private String output;


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
                saveImage(bitmap);
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
                    Bitmap modifiedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    int iKey = 0;
                    int iText = 0;
                    boolean stop = false;
                    for (int y = 0; y < height; y++)
                    {
                        for (int x = 0; x < width; x++)
                        {
                            int pixel = bitmap.getPixel(x, y);
                            int red = Color.red(pixel);
                            int green = Color.green(pixel);
                            int blue = Color.blue(pixel);
                            int alpha = Color.alpha(pixel);
                            if ((y % 2 == 0) && (x % 2 == 0) && (x>((width/2)-100)) && (x<((width/2)+100)) && (y>((height/2)-100)) && (y<((height/2)+100)) && (!stop))
                            {
                                if (binaryKeyArray[iKey]=='1')
                                {
                                        if (binaryTextArray[iText]=='1')//дальше переделать
                                        {
                                            int pixel1 = bitmap.getPixel(x-1, y-1);
                                            int pixel2 = bitmap.getPixel(x, y-1);
                                            int pixel3 = bitmap.getPixel(x+1, y-1);
                                            int pixel4 = bitmap.getPixel(x+1, y);
                                            int pixel5 = bitmap.getPixel(x+1, y+1);
                                            int pixel6 = bitmap.getPixel(x, y+1);
                                            int pixel7 = bitmap.getPixel(x-1, y+1);
                                            int pixel8 = bitmap.getPixel(x-1, y);

                                            red = (Color.red(pixel1)+Color.red(pixel2)+Color.red(pixel3)+Color.red(pixel4)+Color.red(pixel5)+Color.red(pixel6)+Color.red(pixel7)+Color.red(pixel8))/8;
                                            green = (Color.green(pixel1)+Color.green(pixel2)+Color.green(pixel3)+Color.green(pixel4)+Color.green(pixel5)+Color.green(pixel6)+Color.green(pixel7)+Color.green(pixel8))/8;
                                            blue = (Color.blue(pixel1)+Color.blue(pixel2)+Color.blue(pixel3)+Color.blue(pixel4)+Color.blue(pixel5)+Color.blue(pixel6)+Color.blue(pixel7)+Color.blue(pixel8))/8;
                                            //output=String.valueOf(red) + ' ' + green + ' ' + blue;
                                            //output=String.valueOf(y)+' '+x;
                                            if (blue >= red && blue >= green) {
                                                if (blue <= 245) {
                                                    blue += 10;
                                                } else blue -= 10;
                                            }
                                            else if (green >= red) {
                                                if (green <= 245) {
                                                    green += 10;
                                                } else green -= 10;
                                            }
                                            else {
                                                if (red <= 245) {
                                                    red += 10;
                                                } else red -= 10;
                                            }
                                        }
                                        iText++;
                                        if (iText == binaryText.length())
                                        {
                                            iText=0;
                                            stop = true;
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
                    bitmap=modifiedBitmap;
                    imageView.setImageBitmap(bitmap);
                }
            }
        });
        extractButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                int iKey = 0;
                String extractText = "";
                for (int yStart = 0; yStart < height; yStart++)
                {
                    for (int xStart = 0; xStart < width; xStart++)
                    {
                        if ((yStart % 2 == 0) && (xStart % 2 == 0) && (xStart>((width/2)-100)) && (xStart<((width/2)+100)) && (yStart>((height/2)-100)) && (yStart<((height/2)+100)))
                        {
                            if (binaryKeyArray[iKey] == '1') {
                                int pixel = bitmap.getPixel(xStart, yStart);
                                int red = Color.red(pixel);
                                int green = Color.green(pixel);
                                int blue = Color.blue(pixel);
                                int alpha = Color.alpha(pixel);
                                int pixel1 = bitmap.getPixel(xStart - 1, yStart - 1);
                                int pixel2 = bitmap.getPixel(xStart, yStart - 1);
                                int pixel3 = bitmap.getPixel(xStart + 1, yStart - 1);
                                int pixel4 = bitmap.getPixel(xStart + 1, yStart);
                                int pixel5 = bitmap.getPixel(xStart + 1, yStart + 1);
                                int pixel6 = bitmap.getPixel(xStart, yStart + 1);
                                int pixel7 = bitmap.getPixel(xStart - 1, yStart + 1);
                                int pixel8 = bitmap.getPixel(xStart - 1, yStart);

                                int arithmeticMeanRed = (Color.red(pixel1)+Color.red(pixel2)+Color.red(pixel3)+Color.red(pixel4)+Color.red(pixel5)+Color.red(pixel6)+Color.red(pixel7)+Color.red(pixel8))/8;
                                int arithmeticMeanGreen = (Color.green(pixel1) + Color.green(pixel2) + Color.green(pixel3) + Color.green(pixel4) + Color.green(pixel5) + Color.green(pixel6) + Color.green(pixel7) + Color.green(pixel8)) / 8;
                                int arithmeticMeanBlue = (Color.blue(pixel1) + Color.blue(pixel2) + Color.blue(pixel3) + Color.blue(pixel4) + Color.blue(pixel5) + Color.blue(pixel6) + Color.blue(pixel7) + Color.blue(pixel8)) / 8;
                                //вставить
                                //if (xStart==286 && yStart==270) {
                                //    output = String.valueOf(red) + ' ' + green + ' ' + blue+' '+arithmeticMeanRed+' '+arithmeticMeanGreen+' '+arithmeticMeanBlue;
                                //}
                                    if (((Math.abs(red - arithmeticMeanRed) == 10) && green == arithmeticMeanGreen && blue == arithmeticMeanBlue) || ((Math.abs(green - arithmeticMeanGreen) == 10) && red == arithmeticMeanRed && blue == arithmeticMeanBlue) || ((Math.abs(blue - arithmeticMeanBlue) == 10) && red == arithmeticMeanRed && green == arithmeticMeanGreen)) {
                                        extractText += '1';
                                    } else {
                                        extractText += '0';
                                    }
                                /*if (red==arithmeticMeanRed && green == arithmeticMeanGreen && blue == arithmeticMeanBlue) {
                                    extractText += '1';
                                } else {
                                    extractText += '0';
                                }*/
                            }
                            iKey++;
                            if (iKey == binaryKey.length())
                            {
                                iKey = 0;

                            }
                        }
                    }
                }
                if (!extractText.isEmpty())
                {
                    String textBuild = "";
                    String oneBinarySim;
                    for (int i=0; i<extractText.length()-6; i+=7)
                    {
                        oneBinarySim = extractText.substring(i,i+7);
                        int cc10 = Integer.parseInt(oneBinarySim, 2);
                        if (cc10>=65 && cc10<=122)
                        {
                            String returnChar = Character.toString((char) cc10);
                            textBuild += returnChar;
                        }
                    }
                    if (!textBuild.isEmpty())
                    {
                        textView.setText(textBuild);
                    } else {textView.setText("Цифровой водяной знак не найден.");}
                    //textView.setText(textBuild);
                } else {textView.setText("Цифровой водяной знак не найден.");}
                //textView.setText(output);
            }
        });
    }
                /*for (int yStart = 1; yStart < height-1; yStart++) {
                    for (int xStart = 1; xStart < width-1; xStart++) {//переделать

                        int pixel = bitmap.getPixel(xStart, yStart);
                        int red = Color.red(pixel);
                        int green = Color.green(pixel);
                        int blue = Color.blue(pixel);

                        int pixel1 = bitmap.getPixel(xStart-1, yStart-1);
                        int pixel2 = bitmap.getPixel(xStart, yStart-1);
                        int pixel3 = bitmap.getPixel(xStart+1, yStart-1);
                        int pixel4 = bitmap.getPixel(xStart+1, yStart);
                        int pixel5 = bitmap.getPixel(xStart+1, yStart+1);
                        int pixel6 = bitmap.getPixel(xStart, yStart+1);
                        int pixel7 = bitmap.getPixel(xStart-1, yStart+1);
                        int pixel8 = bitmap.getPixel(xStart-1, yStart);

                        int arithmeticMeanRed = (Color.red(pixel1)+Color.red(pixel2)+Color.red(pixel3)+Color.red(pixel4)+Color.red(pixel5)+Color.red(pixel6)+Color.red(pixel7)+Color.red(pixel8))/8;
                        int arithmeticMeanGreen = (Color.green(pixel1)+Color.green(pixel2)+Color.green(pixel3)+Color.green(pixel4)+Color.green(pixel5)+Color.green(pixel6)+Color.green(pixel7)+Color.green(pixel8))/8;
                        int arithmeticMeanBlue = (Color.blue(pixel1)+Color.blue(pixel2)+Color.blue(pixel3)+Color.blue(pixel4)+Color.blue(pixel5)+Color.blue(pixel6)+Color.blue(pixel7)+Color.blue(pixel8))/8;

                        if ((Math.abs(red-arithmeticMeanRed)>40) || (Math.abs(green-arithmeticMeanGreen)>40) || (Math.abs(blue-arithmeticMeanBlue)>40))
                        {
                            y=yStart;
                            x=xStart;
                            break;
                        }
                    }
                    if (y!=0 && x!=0)
                    {
                        break;
                    }
                }

                while (y < height-1)
                {
                    while (x < width-1)
                    {
                            if (binaryKeyArray[iKey]=='1') //переделать
                            {
                                int pixel = bitmap.getPixel(x, y);
                                int red = Color.red(pixel);
                                int green = Color.green(pixel);
                                int blue = Color.blue(pixel);

                                int pixel1 = bitmap.getPixel(x-1, y-1);
                                int pixel2 = bitmap.getPixel(x, y-1);
                                int pixel3 = bitmap.getPixel(x+1, y-1);
                                int pixel4 = bitmap.getPixel(x+1, y);
                                int pixel5 = bitmap.getPixel(x+1, y+1);
                                int pixel6 = bitmap.getPixel(x, y+1);
                                int pixel7 = bitmap.getPixel(x-1, y+1);
                                int pixel8 = bitmap.getPixel(x-1, y);

                                int arithmeticMeanRed = (Color.red(pixel1)+Color.red(pixel2)+Color.red(pixel3)+Color.red(pixel4)+Color.red(pixel5)+Color.red(pixel6)+Color.red(pixel7)+Color.red(pixel8))/8;
                                int arithmeticMeanGreen = (Color.green(pixel1)+Color.green(pixel2)+Color.green(pixel3)+Color.green(pixel4)+Color.green(pixel5)+Color.green(pixel6)+Color.green(pixel7)+Color.green(pixel8))/8;
                                int arithmeticMeanBlue = (Color.blue(pixel1)+Color.blue(pixel2)+Color.blue(pixel3)+Color.blue(pixel4)+Color.blue(pixel5)+Color.blue(pixel6)+Color.blue(pixel7)+Color.blue(pixel8))/8;

                                if ((Math.abs(red-arithmeticMeanRed)>40) || (Math.abs(green-arithmeticMeanGreen)>40) || (Math.abs(blue-arithmeticMeanBlue)>40))
                                {
                                    extractText+='1';
                                } else { extractText+='0'; }
                            }
                            iKey++;
                            if (iKey == binaryKey.length())
                            {
                                iKey=0;
                            }
                        x+=3;
                    }
                    y+=3;
                }
                if (extractText != "")
                {
                    String textBuild = "";
                    for (int i=0; i<extractText.length()-6; i+=7)
                    {
                        String oneBinarySim = extractText.substring(i,i+7);
                        int cc10 = Integer.parseInt(oneBinarySim, 2);
                        if (cc10>=65 && cc10<=122)
                        {
                            String returnChar = Character.toString((char) cc10);
                            textBuild += returnChar;
                        } //else {textBuild+=' ';}
                    }
                        textView.setText(textBuild);
                } else {textView.setText("Цифровой водяной знак не найден.");}
            }
        });
    }*/

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
