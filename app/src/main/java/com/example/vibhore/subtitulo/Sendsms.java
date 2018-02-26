package com.example.vibhore.subtitulo;

import android.app.PendingIntent;
import android.telephony.SmsManager;
import android.Manifest;
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


import static android.app.Activity.RESULT_OK;

/**
 * Created by Souptik Kar on 24-Feb-18.
 */

public class Sendsms extends AppCompatActivity{
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =3 ;
    String email[]=new String[3] ;
    int flag=0;
    String phone[]=new String[3] ;
    String name[]=new String[3] ;
    Button takePictureButton;
    ImageView imageView;
    EditText edit_name ;
    Uri file;
    TextView etext ;
    Bitmap thePic;
    EditText emailAddress;
    EditText phoneNumber;
    EditText fullName;
    int count=0;
    Intent intent1=new Intent(ContactsContract.Intents.Insert.ACTION);
    //Intent intent3=new Intent(getApplicationContext(),Sendsms.class);
    //PendingIntent pi=PendingIntent.getActivity(getApplicationContext(), 0, intent3,0);
    private VisionServiceClient client ;
    final int pic_crop=2;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms);

        if(client==null)
        {
            client=new VisionServiceRestClient(getString(R.string.sub_key),getString(R.string.sub_apiroot));

        }

        takePictureButton = (Button) findViewById(R.id.button_image);
        imageView=(ImageView)findViewById(R.id.imageview);
        etext=(TextView) findViewById(R.id.eText);
        edit_name=(EditText)findViewById(R.id.edit_name);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            takePictureButton.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                takePictureButton.setEnabled(true);
            }
        }
        else if(requestCode == MY_PERMISSIONS_REQUEST_SEND_SMS){
            Log.e("Result","Coming here");
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("Result","Coming here");
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phone[count], null,"hello", null,null);
                Toast.makeText(getApplicationContext(), "SMS sent.",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                return;
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
            new DoRequest().execute();
        }
        catch(Exception e)
        {
            etext.setText("Error encountered");
        }

    }

    private String process() throws VisionServiceException,IOException {
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

    private class DoRequest extends AsyncTask<String,String,String> {
        private Exception e=null ;

        public DoRequest(){

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
           /* String email=null;
            int flag=0;
            String phone=null;
            String name=null;*/
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
                    name[count]=tokens[1] +" " +tokens[2];
                }
                else
                {
                    Log.e("result","Coming here");
                    name[count]=tokens[0] +" " +tokens[1];
                }


                for(int i=0;i<tokens.length;i++){
                    for(int j=0;j<tokens[i].length();j++){
                        if(tokens[i].charAt(j)=='@'){
                            email[count]=tokens[i];
                            break;
                        }
                        if((tokens[i].matches("[+]?([0-9|-])+") && tokens[i].length()>=8)){

                            phone[count]=tokens[i];
                            break;

                        }
                    }
                }
                Log.e("result",name[count] + " " + phone[count] + " " + email[count]);

            }
            intent1.setType(ContactsContract.RawContacts.CONTENT_TYPE);
            //emailAddress.setText(email);
            //phoneNumber.setText(phone);
            //fullName.setText(name);
            String message=edit_name.getText().toString();
            Intent intent=new Intent(getApplicationContext(),Pic.class);
            PendingIntent pi=PendingIntent.getActivity(getApplicationContext(),0,intent,0);
            SmsManager sms=SmsManager.getDefault();
            sms.sendTextMessage(phone[count],null,message,pi,null);
            intent1.putExtra(ContactsContract.Intents.Insert.EMAIL,email[count]);
            intent1.putExtra(ContactsContract.Intents.Insert.PHONE,phone[count]);
            intent1.putExtra(ContactsContract.Intents.Insert.NAME,name[count]);
            startActivity(intent1);
            //sendSMSMessage();
            count++;


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
    /*protected void sendSMSMessage() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                Log.e("Result","Coming here");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
    }*/

    /*public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(""+phone, null,"hello"+name, null,null);
                    Toast.makeText(getApplicationContext(), "SMS sent.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

    }*/

}
