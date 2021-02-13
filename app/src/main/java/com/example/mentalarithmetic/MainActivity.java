package com.example.mentalarithmetic;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    /*プリファレンスに保存されている設定を読み込む先の変数をグローバルで用意しておく*/
    SharedPreferences sp;
    int timeLimit;
    List<Integer> checkList;
    List<Integer> digits;

    /*プリファレンスから設定を引き出す関数を用意しておく*/
    public List<Integer> getIntArray(String PrefKey, List<Integer> def){
        SharedPreferences tmpSp = getSharedPreferences("setting", MODE_PRIVATE);
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

    /*プリファレンスに保存するための関数を用意しておく*/
    public void saveIntArray(String PrefKey,List<Integer> array){
        //arrayの中の数字をつなげて","で区切った上で、stringに返還して保存。
        StringBuffer buffer = new StringBuffer();
        String stringItem = null;
        for(int item: array){
            buffer.append(String.valueOf(item)+",");
        }
        if(buffer != null){
            String buf = buffer.toString();
            stringItem = buf.substring(0,buf.length()-1);

            SharedPreferences tmpSp = getSharedPreferences("setting",MODE_PRIVATE);
            SharedPreferences.Editor editor = tmpSp.edit();
            editor.putString(PrefKey,stringItem).commit();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = getSharedPreferences("setting",MODE_PRIVATE);
        List<Integer> defCheckList = new ArrayList<>(Arrays.asList(1,1,1,1));
        List<Integer> defDigits = new ArrayList<>(Arrays.asList(2,2,2,2,2,2,2));
        checkList = getIntArray("checkList",defCheckList);
        digits = getIntArray("digits",defDigits);
        timeLimit = sp.getInt("timeLimit",30);

        /*制限時間を反映*/
        EditText editTextTimeLimit = (EditText) findViewById(R.id.editTextTimeLimit);
        editTextTimeLimit.setText(String.valueOf(timeLimit));

        /*spinnerのセッティング*/
        //ポイント①「android.R.layout.simple_spinner_item」ではなく、自作のレイアウト「spinner_item.xml」を指定する
        //これによりスピナー内のtextSize等を個別に設定できるようになる。
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.spinner, getResources().getStringArray(R.array.inputDigit));

        //ポイント②「android.R.layout.simple_spinner_dropdown_item」ではなく、自作のレイアウト「spinner_dropdown_item.xml」を指定する
        //これによりスピナーを選択した際のドロップダウンリストのtextSize等を個別に設定出来るようになる。
        //（藤原が追記）あとついでにpreferenceからもってきた設定値をセット
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_items);

        Spinner input = (Spinner) findViewById(R.id.inputPlusDigitLeft);
        input.setAdapter(adapter);
        input.setSelection(digits.get(0)-1);
        input = (Spinner) findViewById(R.id.inputPlusDigitRight);
        input.setAdapter(adapter);
        input.setSelection(digits.get(1)-1);
        input = (Spinner) findViewById(R.id.inputSubDigitLeft);
        input.setAdapter(adapter);
        input.setSelection(digits.get(2)-1);
        input = (Spinner) findViewById(R.id.inputSubDigitRight);
        input.setAdapter(adapter);
        input.setSelection(digits.get(3)-1);
        input = (Spinner) findViewById(R.id.inputByDigitLeft);
        input.setAdapter(adapter);
        input.setSelection(digits.get(4)-1);
        input = (Spinner) findViewById(R.id.inputByDigitRight);
        input.setAdapter(adapter);
        input.setSelection(digits.get(5)-1);
        input = (Spinner) findViewById(R.id.inputDivDigitLeft);
        input.setAdapter(adapter);
        input.setSelection(digits.get(6)-1);

        //チェックボックスにcheckListのステータスを反映
        CheckBox checkBoxPlus = (CheckBox) findViewById(R.id.checkBoxPlus);
        CheckBox checkBoxSub = (CheckBox) findViewById(R.id.checkBoxSub);
        CheckBox checkBoxBy = (CheckBox) findViewById(R.id.checkBoxBy);
        CheckBox checkBoxDiv = (CheckBox) findViewById(R.id.checkBoxDiv);
        checkBoxPlus.setChecked(checkList.get(0)==1? true:false);
        checkBoxSub.setChecked(checkList.get(1)==1? true:false);
        checkBoxBy.setChecked(checkList.get(2)==1? true:false);
        checkBoxDiv.setChecked(checkList.get(3)==1? true:false);
    }

    public boolean start(View view){
        /*制限時間に関する処理*/
        //制限時間取得
        EditText editTextTimeLimit = (EditText) findViewById(R.id.editTextTimeLimit);
        int timeLimit = Integer.parseInt(editTextTimeLimit.getText().toString());
        //制限時間が0なら処理中断してポップアップ
        if (timeLimit == 0){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("制限時間は1秒以上で設定してください").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
            return false;
        }
        //制限時間の設定情報をpreferenceに保存
        sp.edit().putInt("timeLimit", timeLimit).commit();

        /*チェックボックスに関する処理*/
        //チェックボックス情報と桁数情報の引き渡し
        CheckBox checkBoxPlus = (CheckBox) findViewById(R.id.checkBoxPlus);
        CheckBox checkBoxSub = (CheckBox) findViewById(R.id.checkBoxSub);
        CheckBox checkBoxBy = (CheckBox) findViewById(R.id.checkBoxBy);
        CheckBox checkBoxDiv = (CheckBox) findViewById(R.id.checkBoxDiv);
        checkList.set(0,(checkBoxPlus.isChecked())? 1:0);
        checkList.set(1,(checkBoxSub.isChecked())? 1:0);
        checkList.set(2,(checkBoxBy.isChecked())? 1:0);
        checkList.set(3,(checkBoxDiv.isChecked())? 1:0);
        //一つもチェックが入っていない場合は、ポップアップ表示。
        if((checkList.get(0)+checkList.get(1)+checkList.get(2)+checkList.get(3))==0){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("四則のうち少なくとも一つは選択してください。").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    //System.exit(0);
                }
            });
            builder.show();
            return false;
        }
        saveIntArray("checkList",checkList);

        //桁数の引き渡し
        Spinner inputPlusDigitLeft = (Spinner) findViewById(R.id.inputPlusDigitLeft);
        Spinner inputPlusDigitRight = (Spinner) findViewById(R.id.inputPlusDigitRight);
        Spinner inputSubDigitLeft = (Spinner) findViewById(R.id.inputSubDigitLeft);
        Spinner inputSubDigitRight = (Spinner) findViewById(R.id.inputSubDigitRight);
        Spinner inputByDigitLeft = (Spinner) findViewById(R.id.inputByDigitLeft);
        Spinner inputByDigitRight = (Spinner) findViewById(R.id.inputByDigitRight);
        Spinner inputDivDigitLeft = (Spinner) findViewById(R.id.inputDivDigitLeft);
        digits.set(0,inputPlusDigitLeft.getSelectedItemPosition()+1);
        digits.set(1,inputPlusDigitRight.getSelectedItemPosition()+1);
        digits.set(2,inputSubDigitLeft.getSelectedItemPosition()+1);
        digits.set(3,inputSubDigitRight.getSelectedItemPosition()+1);
        digits.set(4,inputByDigitLeft.getSelectedItemPosition()+1);
        digits.set(5,inputByDigitRight.getSelectedItemPosition()+1);
        digits.set(6,inputDivDigitLeft.getSelectedItemPosition()+1);
        saveIntArray("digits",digits);

        Intent intent = new Intent(MainActivity.this, ProblemActivity.class);
        startActivity(intent);
        return true;
    }
}
