package com.example.vibhore.subtitulo;

import android.Manifest;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Region;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.LanguageCodes;
import com.microsoft.projectoxford.vision.contract.Line;
import com.microsoft.projectoxford.vision.contract.OCR;
import com.microsoft.projectoxford.vision.contract.Word;
import com.microsoft.projectoxford.vision.rest.VisionServiceException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by vibhore on 24/2/18.
 */

public class Pic extends AppCompatActivity{
    Button takePictureButton;
    ImageView imageView;
    Uri file;
    TextView etext ;
    Bitmap thePic;
    EditText emailAddress;
    EditText phoneNumber;
    EditText fullName;
    Intent intent1=new Intent(ContactsContract.Intents.Insert.ACTION);

    private VisionServiceClient client ;
    final int pic_crop=2;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pic);

        if(client==null)
        {
            client=new VisionServiceRestClient(getString(R.string.sub_key),getString(R.string.sub_apiroot));

        }

        takePictureButton = (Button) findViewById(R.id.button_image);
        imageView=(ImageView)findViewById(R.id.imageview);
        etext=(TextView) findViewById(R.id.eText);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            takePictureButton.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }
    }
    public void move(View view){
        Intent intent3=new Intent(Pic.this,Sendsms.class);
        startActivity(intent3);
    }
    public void movetomail(View view){
        Intent intent4=new Intent(Pic.this,Email.class);
        startActivity(intent4);
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                takePictureButton.setEnabled(true);
            }
        }
    }
    public void takePicture(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = Uri.fromFile(getOutputMediaFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, file);

        startActivityForResult(intent,100);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode==100) {
                //file=data.getData();
                performcrop();}
                else if(requestCode==pic_crop) {
                Bundle extras=data.getExtras();
                thePic=extras.getParcelable("data");
                imageView.setImageBitmap(thePic);
                doRecognize();
            }

            }
        }

    public void doRecognize(){
        //etext.setText("Analyzing");
        Log.e("resul","res");
        try{
            new doRequest().execute();
        }
        catch(Exception e)
        {
            etext.setText("Error encountered");
        }

    }

    private String process() throws VisionServiceException,IOException{
        Gson gson=new Gson();
        Log.e("resul","res");
        ByteArrayOutputStream output=new ByteArrayOutputStream();
        thePic.compress(Bitmap.CompressFormat.JPEG,100,output);
        ByteArrayInputStream inputStream=new ByteArrayInputStream(output.toByteArray());

        OCR ocr=this.client.recognizeText(inputStream, LanguageCodes.AutoDetect,true);

        String result=gson.toJson(ocr);
        Log.d("result",result);
        return result;
    }

    private class doRequest extends AsyncTask<String,String,String>{
        private Exception e=null ;

        public doRequest(){

        }

        @Override
        protected String doInBackground(String... args) {
            try {
                return process();
            } catch (Exception e) {
                this.e = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String data)
        {
            super.onPostExecute(data);
            String email=null;
            int flag=0;
            String phone=null;
            String name=null;
            if(e!=null){
                etext.setText("Error:"+e.getMessage());
                this.e=null ;

            }
            else
            {
                Gson gson=new Gson();
                OCR r=gson.fromJson(data,OCR.class);
                String result="";
                for(com.microsoft.projectoxford.vision.contract.Region reg:r.regions){
                    for(Line line: reg.lines){
                        for(Word word: line.words){
                            result+=word.text+ " ";
                        }
                        result+=" ";
                    }
                    result+=" ";
                }
                //etext.setText(result);
                Log.e("result:",result);
                String delims = "[ ,:]+";
                String[] tokens = result.split(delims);
                for (int i = 0; i < tokens.length; i++)
                    System.out.println(tokens[i]);
                if(tokens[0].equals("Dr.")||tokens[0].equals("Mr.")||tokens[0].equals("Mrs.")||tokens[0].equals("Ms."))
                {
                    Log.e("result","Coming here1");
                    name=tokens[1] +" " +tokens[2];
                }
                 else
                {
                    Log.e("result","Coming here");
                    name=tokens[0] +" " +tokens[1];
                }


                for(int i=0;i<tokens.length;i++){
                    for(int j=0;j<tokens[i].length();j++){
                        if(tokens[i].charAt(j)=='@'){
                            email=tokens[i];
                            break;
                        }
                        if((tokens[i].matches("[+]?([0-9|-])+") && tokens[i].length()>=8)){

                            phone=tokens[i];
                            break;

                        }
                    }
                }
                Log.e("result",name + " " + phone + " " + email);

            }
            intent1.setType(ContactsContract.RawContacts.CONTENT_TYPE);
            //emailAddress.setText(email);
            //phoneNumber.setText(phone);
            //fullName.setText(name);

            intent1.putExtra(ContactsContract.Intents.Insert.EMAIL,email);
            intent1.putExtra(ContactsContract.Intents.Insert.PHONE,phone);
            intent1.putExtra(ContactsContract.Intents.Insert.NAME,name);
            startActivity(intent1);



        }

    }

    private static File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo");

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");
    }
    //crop image
    private void performcrop(){
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(file, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            cropIntent.putExtra("return-data", true);
           startActivityForResult(cropIntent,pic_crop);
            //imageView.setImageURI(file);
        }
        catch(ActivityNotFoundException anfe){
            //display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

}
