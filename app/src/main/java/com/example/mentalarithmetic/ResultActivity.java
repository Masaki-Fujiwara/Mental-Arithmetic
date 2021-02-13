package com.example.mentalarithmetic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();
        int correctCount = intent.getIntExtra("correctCount",0);
        int incorrectCount = intent.getIntExtra("incorrectCount",0);
        int timeCount = intent.getIntExtra("time",0);
        TextView viewCorrectCount = (TextView) findViewById(R.id.textViewCorrectCount);
        TextView viewIncorrectCount = (TextView) findViewById(R.id.textViewIncorrectCount);
        TextView viewTimeCount = (TextView) findViewById(R.id.textViewTimeCount);
        viewCorrectCount.setText(String.valueOf(correctCount));
        viewIncorrectCount.setText(String.valueOf(incorrectCount));
        viewTimeCount.setText(String.valueOf(timeCount));
    }

    public void toProblem(View view){
        Intent mainIntent = new Intent(ResultActivity.this,ProblemActivity.class);
        startActivity(mainIntent);
    }

    public void toMain(View view){
        Intent mainIntent = new Intent(ResultActivity.this,MainActivity.class);
        startActivity(mainIntent);
    }
}
