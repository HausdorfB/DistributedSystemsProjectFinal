package com.example.filmportapp;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.filmportapp.utils.Constants;

import org.eclipse.paho.android.service.MqttAndroidClient;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class QuestionPage extends AppCompatActivity {
    public static final String EXTRA_SCORE = "userFinalScore";
    private static final long COUNTDOWN = 20000;
    private TextView question;
    private TextView questionNumber;
    private TextView timer;
    private TextView currentScore;
    private RadioGroup radioGroup;
    private RadioButton btn1;
    private RadioButton btn2;
    private RadioButton btn3;
    private Button nextPage;

    private ColorStateList colorStateList;
    private ColorStateList colorStateListTimer;

    private CountDownTimer countDownTimer;
    private long timeLeft;


    private List<Question> questionList;
    private int totalQuestions;
    private int Counter = 0;
    private Question currentQuestion;
    private int score;
    private Boolean answered;

    MqttAndroidClient client;


    String connType = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionpage);





        //get intent parameters
        Intent intent = getIntent();
        connType = intent.getStringExtra("CONN_TYPE");
        System.out.println("CONN TYPE: " + connType);

        question = findViewById(R.id.question_Text);
        questionNumber = findViewById(R.id.question_number_Text_View);
        timer = findViewById(R.id.timer);
        radioGroup = findViewById(R.id.radio_group);
        btn1 = findViewById(R.id.radio_button1);
        btn2 = findViewById(R.id.radio_button2);
        btn3 = findViewById(R.id.radio_button3);
        nextPage = findViewById(R.id.nextpage_btn);
        currentScore = findViewById(R.id.score);

        colorStateList = btn1.getTextColors();
        colorStateListTimer = timer.getTextColors();

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        questionList = dbHelper.getAllQuestions();
        totalQuestions = questionList.size();
        Collections.shuffle(questionList);

        showNextQuestion();
            
        nextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!answered){ //check if a button is checked
                    if (btn1.isChecked() || btn2.isChecked() || btn3.isChecked()) {
                        checkCorrect();
                    } else { //if no button is clicked, send message
                        Toast.makeText(getApplicationContext(),"Choose an answer before Submitting", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    showNextQuestion();
                }
            }
        });
    }

    private void showNextQuestion(){
        btn1.setTextColor(colorStateList);
        btn2.setTextColor(colorStateList);
        btn3.setTextColor(colorStateList);
        radioGroup.clearCheck();

        if (Counter < totalQuestions) {
            currentQuestion = questionList.get(Counter);
            //using questions from SQL Database, fill each question in relation to the counter value
            question.setText(currentQuestion.getQuestion());
            btn1.setText(currentQuestion.getOption1());
            btn2.setText(currentQuestion.getOption2());
            btn3.setText(currentQuestion.getOption3());
    
            Counter++;
            questionNumber.setText("Question #: " + Counter + "/" + totalQuestions); // initialize question counter
            answered = false;
            nextPage.setText("Confirm");

            timeLeft = COUNTDOWN;
            startCountdownTimer();
        } else {
            finishQuiz();

        }
    }
    //count down by 1 second and update timer text view
    private void startCountdownTimer(){
        countDownTimer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) { //on tick method increments the timer based on the devices internal clock
                timeLeft = millisUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish() {
                timeLeft = 0; //re-initialize the timer for next question
                updateTimer();
                checkCorrect();
            }
        }.start();
    }

    private void updateTimer(){
        //initialize second and minute variables
        int minute = (int) (timeLeft/1000)/60;
        int second = (int) (timeLeft/1000)%60;
        // use the Locale function to display the timer in a proper order
        String time = String.format(Locale.getDefault(), "%02d:%02d", minute, second);

        timer.setText(time);
        //switch the timer color to red once a certain time hits
        if (timeLeft < 10000) {
            timer.setTextColor(Color.RED);
        } else {
            timer.setTextColor(colorStateListTimer);
        }
    }

    private void checkCorrect(){
        answered = true;

        countDownTimer.cancel();
        RadioButton btnSelected = findViewById(radioGroup.getCheckedRadioButtonId());
        //recieve the correct index of radio button clicked
        int ans = radioGroup.indexOfChild(btnSelected) + 1;
        System.out.println("Button selected: " + ans);
        if (ans == currentQuestion.getAnswer()) { //if correct, increment the score variable
            score++;
            currentScore.setText("Score: " + score);
            Constants.KEY_MYSCORE = score;
        }
        
        showSolution();
    }

    private void showSolution(){ 
        //initialize the buttons as a red color for consistency
        btn1.setTextColor(Color.RED);
        btn2.setTextColor(Color.RED);
        btn3.setTextColor(Color.RED);

        switch (currentQuestion.getAnswer()) { //display the answer and change button color accordingly 
            case 1:
                btn1.setTextColor(Color.GREEN);
                question.setText("Answer 1 is Correct!");
                break;
            case 2:
                btn2.setTextColor(Color.GREEN);
                question.setText("Answer 2 is Correct!");
                break;
            case 3:
                btn3.setTextColor(Color.GREEN);
                question.setText("Answer 3 is Correct!");
                break;
        }
        if (Counter < totalQuestions){ //decide whether to show the next question or to finish the quiz
            nextPage.setText("Next Question");
        } else {
            nextPage.setText("Finish Quiz");
        }
    }

    private void finishQuiz(){
        //pass the connection type and score to the next activity
        Intent intent = new Intent(this,QuizDoneActivity.class);
        intent.putExtra("CONN_TYPE", connType);
        intent.putExtra("SCORE", score);
       // intent.putExtra("USER", )
        startActivity(intent);

       // Intent intent = new Intent(this,LeaderboardActivity.class);
      //startActivity(intent);
    }
    //cancel countdown timer so it doesnt run in background
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }




}
