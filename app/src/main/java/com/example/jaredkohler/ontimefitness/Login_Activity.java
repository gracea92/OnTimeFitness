package com.example.jaredkohler.ontimefitness;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.EditText;

public class Login_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }


    public void login(View view){
        EditText username = (EditText) findViewById(R.id.editName);
        EditText password = (EditText)findViewById(R.id.editPassword);

        if(username.getText().toString().equals("admin") && password.getText().toString().equals("admin")){
            Intent intent = new Intent(this, Options_Activity.class);
            startActivity(intent);
        }else if(username.getText().toString().equals("admin")){
            Toast.makeText(getApplicationContext(), "Wrong Password", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(), "Wrong Username", Toast.LENGTH_SHORT).show();
        }
    }

    public void loadCreateUser(View view){
        Intent intent = new Intent(this, Create_Activity.class);
        startActivity(intent);
    }
}
