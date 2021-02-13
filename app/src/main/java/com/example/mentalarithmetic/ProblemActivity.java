package com.example.mentalarithmetic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.os.CountDownTimer;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class ProblemActivity extends AppCompatActivity {

    //グローバル変数として、左項、右項、解答を格納する変数を用意しておく
    int leftNum = 0;
    int rightNum = 0;
    int correctAnswer = 0;
    //グローバル変数として正解数を格納する変数を用意しておく
    int correctCount = 0;
    //グローバル変数として誤答数を格納する変数を用意しておく
    int incorrectCount = 0;
    //出題数を変数として保持しておく変数を用意しておく
    int problemCount = 0;

    //プリファレンスから設定を引き出す関数を用意しておく
    /*プリファレンスから設定を引き出す関数を用意しておく*/
    public List<Integer> getIntArray(String PrefKey, List<Integer> def){
        SharedPreferences tmpSp = getSharedPreferences("setting", Context.MODE_PRIVATE);
        String stringItem = tmpSp.getString(PrefKey,"");
        if(stringItem != null && stringItem.length() != 0){
            List<Integer> ret = new ArrayList<Integer>();
            for(String item: stringItem.split(",")){
                ret.add(Integer.parseInt(item));
            }
            return ret;
        }else{
            return def;
        }
    }
    //グローバル変数として、桁数のリストを用意しておく。
    List<Integer> digits;
    //グローバル変数として、四則の組み合わせを保持しておく。
    List<String> opeList = new ArrayList<String>();
    //グローバル変数として、制限時間を保持しておく。
    int timeLimit = 0;
    int restTime;

    //結果画面に切り替える関数
    private void toResult() {
        Intent intent = new Intent(ProblemActivity.this,ResultActivity.class);
        intent.putExtra("correctCount",correctCount);
        intent.putExtra("incorrectCount",incorrectCount);
        intent.putExtra("time",timeLimit-restTime);
        startActivity(intent);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem);

        /*プリファレンスから値を取得する*/
        SharedPreferences sp = getSharedPreferences("setting", MODE_PRIVATE);
        digits = getIntArray("digits",new ArrayList<Integer>());
        List<Integer> checkList = getIntArray("checkList",new ArrayList<Integer>());
        timeLimit = sp.getInt("timeLimit",60);
        restTime = timeLimit;

        //カウントダウン処理
        final TextView timeCount = (TextView) findViewById(R.id.timeCount);
        final Handler handler = new Handler(Looper.getMainLooper());
        TimerTask countDown = new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> {
                    timeCount.setText("残り時間："+String.valueOf(restTime));
                });
                if(restTime == 0){
                    toResult();
                }
                restTime = restTime - 1;
            }
        };
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(countDown,0,1000);

        if (checkList.get(0)==1){
            opeList.add("+");
        }
        if (checkList.get(1)==1){
            opeList.add("-");
        }
        if (checkList.get(2)==1){
            opeList.add("×");
        }
        if (checkList.get(3)==1){
            opeList.add("÷");
        }

        createProblem();

        //Doneキーが押されたら回答が提出される処理
        final EditText editTextAnswer = (EditText) findViewById(R.id.editTextAnswer);
        editTextAnswer.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    judgeSubmit();
                    handled = true;
                }
                return handled; // このメソッド中でアクションを消化したら true を返す。
            }
        });
    }

    //問題を出題する処理
    public void createProblem(){
        String ope = opeList.get(new Random().nextInt(opeList.size()));
        TextView textViewOpe = (TextView) findViewById(R.id.textViewOpe);
        textViewOpe.setText(ope);
        switch (ope){
            case "+":
                createPlusProblem(digits.get(0),digits.get(1));
                break;
            case "-":
                createSubProblem(digits.get(2), digits.get(3));
                break;
            case "×":
                createByProblem(digits.get(4), digits.get(5));
                break;
            case "÷":
                createDivProblem(digits.get(6));
                break;
        }
        TextView textViewLeftNum = (TextView) findViewById(R.id.textViewLeftNum);
        TextView textViewRightNum = (TextView) findViewById(R.id.textViewRightNum);
        textViewLeftNum.setText(String.valueOf(leftNum));
        textViewRightNum.setText(String.valueOf(rightNum));
        problemCount = problemCount + 1;
    }

    private void createPlusProblem(int leftDigit,int rightDigit){
        Random random = new Random();
        int leftUp = (int)Math.pow(10,leftDigit)-1;
        int leftBottom = (int)Math.pow(10,leftDigit-1);
        int rightUp = (int)Math.pow(10,rightDigit)-1;
        int rightBottom = (int)Math.pow(10,rightDigit-1);
        leftNum = random.nextInt(leftUp-leftBottom)+leftBottom+1;
        rightNum = random.nextInt(rightUp-rightBottom)+rightBottom+1;
        correctAnswer = leftNum + rightNum;
    }

    private void createSubProblem(int leftDigit,int rightDigit){
        Random random = new Random();
        int leftUp = (int)Math.pow(10,leftDigit)-1;
        int leftBottom = (int)Math.pow(10,leftDigit-1);

        leftNum = random.nextInt(leftUp-leftBottom)+leftBottom+1;
        int rihgtUp = (int)Math.pow(10,rightDigit)-1>leftNum-1? (int)Math.pow(10,rightDigit)-1:leftNum-1;
        int rightBottom = (int)Math.pow(10,rightDigit-1);
        rightNum = random.nextInt(rihgtUp-rightBottom)+rightBottom+1;
        correctAnswer = leftNum - rightNum;
    }

    private void createByProblem(int leftDigit,int rightDigit){
        Random random = new Random();
        int leftUp = (int)Math.pow(10,leftDigit)-1;
        int leftBottom = (int)Math.pow(10,leftDigit-1);
        int rightUp = (int)Math.pow(10,rightDigit)-1;
        int rightBottom = (int)Math.pow(10,rightDigit-1);
        leftNum = random.nextInt(leftUp-leftBottom)+leftBottom+1;
        rightNum = random.nextInt(rightUp-rightBottom)+rightBottom+1;
        correctAnswer = leftNum * rightNum;
    }

    private void createDivProblem(int leftDigit){
        Random random = new Random();
        int up = (int)Math.pow(10,leftDigit)-1;
        int bottom = (int)Math.pow(10,leftDigit-1);
        leftNum = random.nextInt(up-bottom)+bottom+1;
        ArrayList<Integer> divisors = new ArrayList();
        for (int i=1; i <= Math.pow(leftNum,0.5); i++){
            if(leftNum%i==0){
                divisors.add(i);
                divisors.add((int)(leftNum/i));
            }
        }
        Collections.sort(divisors);
        int len = divisors.size();
        rightNum = divisors.get(random.nextInt(len));
        correctAnswer = leftNum / rightNum;
    }


    //提出内容を判定する処理
    public void judgeSubmit(){
        //提出内容を取得
        EditText editTextAnswer = (EditText) findViewById(R.id.editTextAnswer);
        int submittedAnswer = Integer.parseInt(editTextAnswer.getText().toString());

        //提出内容が正しいかを確認
        //提出内容が正しければ、正答数カウント増加。次の問題を表示する。そうでなければ回答欄をクリアにしてやり直し。
        editTextAnswer.setText("");
        if(submittedAnswer == correctAnswer){
            correctCount = correctCount + 1;
            createProblem();
        }else {
            incorrectCount = incorrectCount + 1;
        }
    }

    public void suspension(View view){
        toResult();
    }

}
