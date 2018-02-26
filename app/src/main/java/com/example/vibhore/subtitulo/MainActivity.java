package com.example.vibhore.subtitulo;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vibhore.subtitulo.data.ProductContract;
import com.example.vibhore.subtitulo.data.SignUpDbHelper;

import static com.example.vibhore.subtitulo.R.styleable.View;
public class MainActivity extends AppCompatActivity {

    EditText mUsername,mPassword;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsername=(EditText)findViewById(R.id.edit_email);
        mPassword=(EditText)findViewById(R.id.edit_password);
        textView=(TextView)findViewById(R.id.button8);
        String udata="Need an account? Sign Up";
        SpannableString content = new SpannableString(udata);
        content.setSpan(new UnderlineSpan(), 0, udata.length(), 0);
        textView.setText(content);
    }

    public void login(View view){
        String username=mUsername.getText().toString();
        int password=Integer.parseInt(mPassword.getText().toString());

        SignUpDbHelper mDbHelper=new SignUpDbHelper(this);
        SQLiteDatabase db=mDbHelper.getReadableDatabase();

        String projection[]={
                ProductContract.SignUpEntry.COLUMN_SIGNUP_PASSWORD,
                ProductContract.SignUpEntry.COLUMN_SIGNUP_NAME,
                ProductContract.SignUpEntry.COLUMN_SIGNUP_MAIL,
                ProductContract.SignUpEntry.COLUMN_SIGNUP_ADDRESS,
                ProductContract.SignUpEntry.COLUMN_SIGNUP_PHONE,
                ProductContract.SignUpEntry.COLUMN_SIGNUP_USERNAME
        };

        Cursor cursor=db.query(
                ProductContract.SignUpEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        try{
            int passwordColumnIndex=cursor.getColumnIndex(ProductContract.SignUpEntry.COLUMN_SIGNUP_PASSWORD);
            int nameColumnIndex=cursor.getColumnIndex(ProductContract.SignUpEntry.COLUMN_SIGNUP_NAME);
            int mailColumnIndex=cursor.getColumnIndex(ProductContract.SignUpEntry.COLUMN_SIGNUP_MAIL);
            int addressColumnIndex=cursor.getColumnIndex(ProductContract.SignUpEntry.COLUMN_SIGNUP_ADDRESS);
            int phoneColumnIndex=cursor.getColumnIndex(ProductContract.SignUpEntry.COLUMN_SIGNUP_PHONE);
            int usernameColumnIndex=cursor.getColumnIndex(ProductContract.SignUpEntry.COLUMN_SIGNUP_USERNAME);
            int test=0;
            while (cursor.moveToNext()){
                String usern=cursor.getString(usernameColumnIndex);
                int pass=cursor.getInt(passwordColumnIndex);

                if(usern.equalsIgnoreCase(username)&&pass==password){
                    test=1;
                    break;
                }
            }
            if(test==1){
                //Toast.makeText(this,"THe user exists", Toast.LENGTH_LONG).show();
                Intent i=new Intent(MainActivity.this,Pic.class);
                startActivity(i);
            }else {
                Toast.makeText(this,"Enter a valid username and password", Toast.LENGTH_LONG).show();
                mUsername.setText("");
                mPassword.setText("");
            }
        }finally {
            cursor.close();
        }
    }

    public void signup(View view){
        Intent i=new Intent(MainActivity.this,SignUp.class);
        startActivity(i);
    }
}
