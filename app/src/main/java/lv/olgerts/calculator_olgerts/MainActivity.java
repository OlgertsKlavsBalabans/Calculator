package lv.olgerts.calculator_olgerts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        final TextView txtInputField = findViewById(R.id.inputField);
        txtInputField.setText(getResources().getString(R.string.input_field_name));

        Button btnMS = findViewById(R.id.memorySave);
        btnMS.setText(getResources().getString(R.string.memory_save));

        Button btnMC = findViewById(R.id.memoryClear);
        btnMC.setText(getResources().getString(R.string.memory_clear));

        Button btnMR = findViewById(R.id.memoryRead);
        btnMR.setText(getResources().getString(R.string.memory_read));

        Button btnCalculate = findViewById(R.id.submitButton);
        btnCalculate.setText(getResources().getString(R.string.calculate));

        final TextView historyText = findViewById(R.id.historyText);
        historyText.setText(getResources().getString(R.string.history_text));


        btnMC.setOnClickListener(new View.OnClickListener() { //clear
            @Override
             public void onClick(View v) {
                SharedPreferences sp = getApplicationContext().getSharedPreferences("sp", 0);
                sp.edit().putString("savedNumber","").commit();
             }
        });

        btnMR.setOnClickListener(new View.OnClickListener() { //read
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getApplicationContext().getSharedPreferences("sp", 0);
                txtInputField.setText(txtInputField.getText()+sp.getString("savedNumber", ""));
            }
        });

        btnMS.setOnClickListener(new View.OnClickListener() { //save
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getApplicationContext().getSharedPreferences("sp", 0);
                sp.edit().putString("savedNumber",""+txtInputField.getText()).commit();
            }
        });

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            historyText.setText("");

                boolean eqationAccepted = false;
                while (true) {

                    String textInput = txtInputField.getText().toString();
                    Log.d("", "" + textInput );
                    String regex = "^([+]*[-]*\\d+(?:\\.\\d+)?[\\+\\-\\*\\/]{1}[-]*[+]*)+\\d+(?:\\.\\d+)?$";

                    if (textInput.matches(regex)) {
                        int devideSign = textInput.indexOf("/");
                        int timesSign = textInput.indexOf("*");
                        int plusSign = textInput.indexOf("+");
                        int minusSign = textInput.indexOf("-");
                        if (plusSign==0){
                            plusSign = textInput.indexOf("+",plusSign+1);
                        }
                        if (minusSign==0){
                            minusSign = textInput.indexOf("-",minusSign+1);
                        }


                        eqationAccepted = true;
                        String newNumber = "";
                        double[][] numbers = new double[2][2];
                        Log.d("", "Pirmais / " + devideSign);
                        Log.d("", "Pirmais * " + timesSign);
                        Log.d("", "Pirmais + " + plusSign);
                        Log.d("", "Pirmais - " + minusSign);
                        historyText.setText(historyText.getText()+"\n"+textInput);
                        if (((devideSign > timesSign) && (timesSign > 0)) || ((timesSign > 0) && (devideSign <= 0))) { //timesSign is first
                            numbers = findNumbersOnBothSides(textInput, timesSign);
                            newNumber = Double.toString(numbers[0][0] * numbers[1][0]) ;
                            if ((numbers[0][0] * numbers[1][0])>0){
                                newNumber ="+"+ newNumber;
                            }
                        } else if (((timesSign > devideSign) && (devideSign > 0)) || ((devideSign > 0) && (timesSign <= 0))) { // devideSign is first
                            numbers = findNumbersOnBothSides(textInput, devideSign);
                            if (numbers[1][0] == 0) {
                                newNumber = "42";
                            } else {
                                newNumber =Double.toString(numbers[0][0] / numbers[1][0]) ;
                            }
                            if ((numbers[0][0] / numbers[1][0])>0){
                                newNumber ="+"+ newNumber;
                            }
                        } else if (((plusSign > minusSign) && (minusSign > 0)) || ((minusSign > 0) && (plusSign <= 0))) { // Minus is first and there is no * or /
                            numbers = findNumbersOnBothSides(textInput, minusSign);
                            newNumber = Double.toString(numbers[0][0] - numbers[1][0]);
                            if ((numbers[0][0] - numbers[1][0])>0){
                                newNumber ="+"+ newNumber;
                            }
                        } else if (((minusSign > plusSign) && (plusSign > 0)) || ((plusSign > 0) && (minusSign <= 0))) { // Plus is first and there is no * or /
                            numbers = findNumbersOnBothSides(textInput, plusSign);
                            newNumber =Double.toString( numbers[0][0] + numbers[1][0]);
                            if ((numbers[0][0] + numbers[1][0])>0){
                                newNumber ="+"+ newNumber;
                            }
                        }

                        newNumber = String.format("%.2f",Double.parseDouble(newNumber));
                        if (Double.parseDouble(newNumber)>0){
                            newNumber = "+"+newNumber;
                        }
                        StringBuilder newText = new StringBuilder(textInput);
                        newText.replace((int)numbers[0][1], (int)numbers[1][1], newNumber);
                        txtInputField.setText(newText);
                    } else {
                        if (eqationAccepted == false) {
                            Toast toast = Toast.makeText(MainActivity.this, "Input valid equation!", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        break;
                    }
                }
            }
        });





    }
    private double[][] findNumbersOnBothSides(String inputText, int indexOfSign){
        double [][] numbers = new double[2][2];
        int i = 1;
        boolean endNumber = false;
        while (true) {
            i++;
            if (endNumber){
                break;
            }
            if (indexOfSign-i<0){
                break;
            }

            if ((!Character.isDigit(inputText.charAt(indexOfSign - i)))&&!(inputText.charAt(indexOfSign-i)=='-')&&!(inputText.charAt(indexOfSign-i)=='+')&&!(inputText.charAt(indexOfSign-i)=='.')){
                break;
            }
            if ((inputText.charAt(indexOfSign-i)=='-')||(inputText.charAt(indexOfSign-i)=='+')) {
                endNumber = true;
            }
        }
        double firstNumber  = Double.parseDouble(inputText.substring(indexOfSign -i +1,indexOfSign));
        numbers[0][1] = indexOfSign -i +1;
        Log.d("", "Pirmais skaitlis "+firstNumber);
        Log.d("", "Pirmais skaitlis index "+numbers[0][1]);
        i = 1;
        while (true) {
            i++;
            if (indexOfSign+i>=inputText.length()){
                break;
            }
            if ((!Character.isDigit(inputText.charAt(indexOfSign + i)))&&!(inputText.charAt(indexOfSign+i)=='.')){
                break;
            }
        }
        double secondNumber = Double.parseDouble(inputText.substring(indexOfSign +1,indexOfSign +i));
        numbers[1][1] = indexOfSign +i;
        Log.d("", "Otrais skaitlis "+secondNumber);
        Log.d("", "Otrais skaitlis index "+numbers[1][1]);


        numbers[0][0] = firstNumber;
        numbers[1][0] = secondNumber;
        return numbers;
    }


}
