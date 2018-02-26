package com.example.vibhore.subtitulo;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import com.example.vibhore.subtitulo.data.ProductContract;
import com.example.vibhore.subtitulo.data.SignUpDbHelper;
/**
 * Created by vibhore on 23/2/18.
 */
public class SignUp extends AppCompatActivity {

    EditText mName,mEmail,mAddress,mPhone,mUsername,mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mName=(EditText)findViewById(R.id.edit_name);
        mAddress=(EditText)findViewById(R.id.edit_address);
        mEmail=(EditText)findViewById(R.id.edit_email);
        mPhone=(EditText)findViewById(R.id.edit_phone1);
        mUsername=(EditText)findViewById(R.id.edit_username);
        mPassword=(EditText)findViewById(R.id.edit_password);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu me) {
        getMenuInflater().inflate(R.menu.menu_editor, me);
        return true;
    }

    public void insertdetails(){
        String name=mName.getText().toString();
        String address=mAddress.getText().toString();
        String email=mEmail.getText().toString();
        String username=mUsername.getText().toString();
        int password=Integer.parseInt(mPassword.getText().toString());

        SignUpDbHelper mDbHelper=new SignUpDbHelper(this);
        SQLiteDatabase db=mDbHelper.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put(ProductContract.SignUpEntry.COLUMN_SIGNUP_NAME,name);
        values.put(ProductContract.SignUpEntry.COLUMN_SIGNUP_ADDRESS,address);
        values.put(ProductContract.SignUpEntry.COLUMN_SIGNUP_MAIL,email);
        values.put(ProductContract.SignUpEntry.COLUMN_SIGNUP_PHONE,Long.parseLong(mPhone.getText().toString()));
        values.put(ProductContract.SignUpEntry.COLUMN_SIGNUP_USERNAME,username);
        values.put(ProductContract.SignUpEntry.COLUMN_SIGNUP_PASSWORD,password);

        long row=db.insert(ProductContract.SignUpEntry.TABLE_NAME,null,values);

        if(row!=-1)
            Toast.makeText(this,"Successfully Signed up", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this,"Enter a unique username/or Fill all the details",Toast.LENGTH_LONG).show();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                insertdetails();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
