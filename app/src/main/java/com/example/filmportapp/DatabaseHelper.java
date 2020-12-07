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

    private static final String COL_1 = "Email";
    private static final String COL_2 = "Username";
    private static final String COL_3 = "Password";
    private static final String COL_4 = "Adventures";
    private static final String COL_5 = "Points";
    private SQLiteDatabase db;

    public DatabaseHelper(Context context) {
        super(context, "Login.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;

        String createTableUsers = "CREATE TABLE " + TABLE_NAME + "(" +
                COL_1 + " Text NOT NULL," +
                COL_2 + " Text NOT NULL," +
                COL_3 + " Text NOT NULL," +
                COL_4 + " Integer NOT NULL," +
                COL_5 + " Integer NOT NULL)" + ";";

        //Log.d("DBText","createTable: "+createTableUsers);
        db.execSQL(createTableUsers);

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

    private void addQuestion(Question question){
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
            do {
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

    public boolean addRecord (String email,String username,String password, int adventures, int points){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, email);
        contentValues.put(COL_2, username);
        contentValues.put(COL_3, password);
        contentValues.put(COL_4, adventures);
        contentValues.put(COL_5, points);
        long ins = db.insert(TABLE_NAME,null,contentValues);
        if (ins==0) return false;
        else return true;
    }

    public boolean checkUsers (String username, String password){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from " + TABLE_NAME
                + " where " + COL_2 + " = " + "'" +username + "'" +
                " and " +  COL_3 + " = " + "'" +password+"'" +" ; ";

        Log.d("query",query);
        Cursor c = db.rawQuery(query,null);
        int rowsCount= c.getCount();
        c.close();
        if (rowsCount>0) return true;
        else return false;
    }


    public boolean checkUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from " + TABLE_NAME
                + " where " + COL_2 + " = " + "'" +username + "'" +" ; ";

        Log.d("query",query);
        Cursor c = db.rawQuery(query,null);
        int rowsCount= c.getCount();
        c.close();
        if (rowsCount>0) return true;
        else return false;
    }

    public void addPoints(String username, int adventures, int points) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_2 + "=" + "\"" + username + "\"", null);
        cursor.moveToFirst();
        adventures += cursor.getInt(cursor.getColumnIndex(COL_4)); // add previously existing adventures to the update
        points += cursor.getInt(cursor.getColumnIndex(COL_5)); // same with points
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, username);
        contentValues.put(COL_4, adventures);
        contentValues.put(COL_5, points);
        db.update(TABLE_NAME, contentValues, " Username = " + "\"" + username + "\"", null); // update the table
    }

    public Cursor viewData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }
}
