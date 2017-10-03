package stevekamau.todo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by steve on 9/29/17.
 */

public class ToDoDB extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "todoDB";
    public static final String TABLE_NAME = "todoTable";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_STATUS = "status";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    public ToDoDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TODO_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER,"
                + COLUMN_TITLE + " TEXT unique,"
                + COLUMN_CREATED_AT + " TEXT unique,"
                + COLUMN_STATUS + " TEXT" + ")";

        db.execSQL(CREATE_TODO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addTodo(String title, String created_at, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("created_at", created_at);
        contentValues.put("status", status);
        db.insertWithOnConflict(TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public ArrayList<ToDoItem> getToDoItems(String variant) {
        ArrayList<ToDoItem> toDoItemArrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = null;
        //filter todo items
        if (variant.equals("active")) {
            res = db.rawQuery("SELECT * from  " + TABLE_NAME + " WHERE " + COLUMN_STATUS + "= 'active'", null);
        } else {
            res = db.rawQuery("SELECT * from  " + TABLE_NAME + " WHERE " + COLUMN_STATUS + "= 'inactive'", null);
        }
        res.moveToFirst();
        while (!res.isAfterLast()) {
            ToDoItem todoModel = new ToDoItem();
            todoModel.setTitle(res.getString(res.getColumnIndex("title")));
            todoModel.setCreatedAt(res.getString(res.getColumnIndex("created_at")));
            todoModel.setStatus(res.getString(res.getColumnIndex("status")));
            toDoItemArrayList.add(0, todoModel);
            res.moveToNext();
        }
        res.close();
        return toDoItemArrayList;
    }

    public void setToDoAsDone(String status, String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("status", status);
        db.update(TABLE_NAME, cv, "title = ?", new String[]{title});
    }

}