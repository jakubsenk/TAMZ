package com.jakubsenk.yata;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DBHelper extends SQLiteOpenHelper
{

    public static final String DATABASE_NAME = "TODOS.db";
    public static final String ITEM_COLUMN_Title = "title";
    public static final String ITEM_COLUMN_Description = "description";
    public static final String ITEM_COLUMN_Priority = "priority";
    public static final String ITEM_COLUMN_Deadline = "deadline";
    public static final String ITEM_COLUMN_Subtasks = "subtasks";

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE items (id INTEGER PRIMARY KEY, title TEXT, description TEXT, priority INTEGER, deadline DATETIME, subtasks TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS items");
        onCreate(db);
    }

    public boolean insertItem(TodoItem item)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ITEM_COLUMN_Title, item.Title);
        contentValues.put(ITEM_COLUMN_Description, item.Description);
        contentValues.put(ITEM_COLUMN_Priority, item.PriorityId);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        contentValues.put(ITEM_COLUMN_Deadline, item.Deadline != null ? dateFormat.format(item.Deadline) : null);
        contentValues.put(ITEM_COLUMN_Subtasks, item.Subtasks != null ? String.join("\n", item.Subtasks) : null);
        long insertedId = db.insert("items", null, contentValues);
        if (insertedId == -1) return false;
        return true;
    }

    public boolean deleteItem(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("items", "id=" + id, null) > 0;
    }

    public boolean updateItem(TodoItem item)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ITEM_COLUMN_Title, item.Title);
        contentValues.put(ITEM_COLUMN_Description, item.Description);
        contentValues.put(ITEM_COLUMN_Priority, item.PriorityId);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        contentValues.put(ITEM_COLUMN_Deadline, dateFormat.format(item.Deadline));
        contentValues.put(ITEM_COLUMN_Subtasks, String.join("\n", item.Subtasks));
        return db.update("items", contentValues, "id=" + item.ID, null) > 0;
    }

    public ArrayList<TodoItem> getItemList()
    {
        ArrayList<TodoItem> arrayList = new ArrayList<TodoItem>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from items", null);
        res.moveToFirst();

        while (!res.isAfterLast())
        {
            String title = res.getString(res.getColumnIndex(ITEM_COLUMN_Title));
            String description = res.getString(res.getColumnIndex(ITEM_COLUMN_Description));
            int priority = res.getInt(res.getColumnIndex(ITEM_COLUMN_Priority));
            String deadlineString = res.getString(res.getColumnIndex(ITEM_COLUMN_Deadline));
            String subtasks = res.getString(res.getColumnIndex(ITEM_COLUMN_Subtasks));
            int id = res.getInt(0);
            Date deadline = null;
            if (deadlineString != null)
            {
                try
                {
                    deadline = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(deadlineString);
                }
                catch (ParseException e)
                {
                }
            }
            arrayList.add(new TodoItem(id, title, description, priority, deadline, subtasks != null ? subtasks.split("\n") : null));
            res.moveToNext();
        }

        return arrayList;
    }

    public int removeAll()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("items", "1", null);
    }
}
