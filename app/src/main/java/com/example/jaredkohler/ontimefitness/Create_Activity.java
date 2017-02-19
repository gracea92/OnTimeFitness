package com.example.jaredkohler.ontimefitness;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Create_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body);
    }

    public void createUser(View view){
        EditText username = (EditText) findViewById(R.id.editUsername);
        EditText password = (EditText) findViewById(R.id.editPassword);

        if(username.getText().toString().equals("UserName") || username.getText().toString().equals(getString(R.string.username).toString()) || username.getText().toString() == null){
            Toast.makeText(getApplicationContext(), "Username is already taken, please use a different one.", Toast.LENGTH_SHORT).show();
        }else{
            Intent intent = new Intent(this, Login_Activity.class);
            startActivity(intent);
        }
    }
}
