package com.example.filmportapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "user";
    private static final String COL_2 = "Username";
    private SQLiteDatabase db;

    public DatabaseHelper(Context context) {
        super(context, "Login.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;

        //Create table for questionnaire results
        final String SQL_CREATE_QUESTIONS_TABLE = "CREATE TABLE " +
                QuizInfo.QuestionTable.TABLE_NAME + " ( " +
                QuizInfo.QuestionTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                QuizInfo.QuestionTable.COLUMN_QUESTION + " TEXT, " +
                QuizInfo.QuestionTable.COLUMN_OPTION1 + " TEXT, " +
                QuizInfo.QuestionTable.COLUMN_OPTION2 + " TEXT, " +
                QuizInfo.QuestionTable.COLUMN_OPTION3 + " TEXT, " +
                QuizInfo.QuestionTable.COLUMN_ANS + " INTEGER" + ")";

        db.execSQL(SQL_CREATE_QUESTIONS_TABLE);
        fillQuestionsTable();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + QuizInfo.QuestionTable.TABLE_NAME);
        onCreate(db);
    }

    private void fillQuestionsTable() {
        Question q1 = new Question("In The Matrix, does Neo take the blue pill or the red pill?", "Blue", "Red", "Keanu Reeves takes it, not Neo",  2);
        addQuestion(q1);
        Question q2 = new Question("What flavor of Pop Tarts does Buddy the Elf use in his spaghetti in Elf?", "Smores", "Chocolate", "He Doesn't use Poptarts", 2);
        addQuestion(q2);
        Question q3 = new Question("In what 1976 thriller does Robert De Niro famously say “You talkin’ to me?”", "Goodfellas", "Taxi Driver", "Raging Bull", 2);
        addQuestion(q3);
        Question q4 = new Question("What 1994 film revitalized John Travolta’s career?", "Grease", "Pulp Fiction", "Saturday Night Fever", 2);
        addQuestion(q4);
        Question q5 = new Question("What was Quentin Tarantino‘s first feature as writer/director?", "Desperado", "Pulp Fiction", "Reservoir Dogs", 3);
        addQuestion(q5);
    }

    private void addQuestion(Question question){ // Used to initialize the questions being used
        ContentValues cv = new ContentValues();
        cv.put(QuizInfo.QuestionTable.COLUMN_QUESTION, question.getQuestion());
        cv.put(QuizInfo.QuestionTable.COLUMN_OPTION1, question.getOption1());
        cv.put(QuizInfo.QuestionTable.COLUMN_OPTION2, question.getOption2());
        cv.put(QuizInfo.QuestionTable.COLUMN_OPTION3, question.getOption3());
        cv.put(QuizInfo.QuestionTable.COLUMN_ANS, question.getAnswer());
        db.insert(QuizInfo.QuestionTable.TABLE_NAME, null, cv);
    }

    public List<Question> getAllQuestions(){
        List<Question> questionList = new ArrayList<>();
        db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + QuizInfo.QuestionTable.TABLE_NAME, null);
        
        if (c.moveToFirst()) {
            do { // generic method of dynamically setting questions from database when method is called
                Question question = new Question();
                question.setQuestion(c.getString(c.getColumnIndex(QuizInfo.QuestionTable.COLUMN_QUESTION)));
                question.setOption1(c.getString(c.getColumnIndex(QuizInfo.QuestionTable.COLUMN_OPTION1)));
                question.setOption2(c.getString(c.getColumnIndex(QuizInfo.QuestionTable.COLUMN_OPTION2)));
                question.setOption3(c.getString(c.getColumnIndex(QuizInfo.QuestionTable.COLUMN_OPTION3)));
                question.setAnswer(c.getInt(c.getColumnIndex(QuizInfo.QuestionTable.COLUMN_ANS)));
                questionList.add(question);
            } while (c.moveToNext());
        }

        c.close();
        return questionList;
    }

    //for displaying database contents
    public Cursor viewData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }
}
