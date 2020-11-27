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
    public static final String ITEM_COLUMN_SubtasksDone = "subtasksDone";
    public static final String ITEM_COLUMN_Done = "done";

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE items (id INTEGER PRIMARY KEY, title TEXT, description TEXT, priority INTEGER, deadline DATETIME, subtasks TEXT, subtasksDone TEXT, done INTEGER)");
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
        contentValues.put(ITEM_COLUMN_Subtasks, item.Subtasks != null && item.Subtasks.length > 0 ? String.join("\n", item.Subtasks) : null);
        String[] dones = null;
        if (item.Subtasks != null)
        {
            dones = new String[item.Subtasks.length];
            for (int i = 0; i < item.Subtasks.length; i++)
            {
                dones[i] = "0";
            }
        }
        contentValues.put(ITEM_COLUMN_SubtasksDone, dones != null && dones.length > 0 ? String.join(";", dones) : null);
        contentValues.put(ITEM_COLUMN_Done, item.Done);
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
        contentValues.put(ITEM_COLUMN_Deadline, item.Deadline != null ? dateFormat.format(item.Deadline) : null);
        contentValues.put(ITEM_COLUMN_Subtasks, item.Subtasks != null && item.Subtasks.length > 0 ? String.join("\n", item.Subtasks) : null);
        String[] dones = null;
        if (item.SubtasksDone != null)
        {
            dones = new String[item.SubtasksDone.length];
            for (int i = 0; i < item.SubtasksDone.length; i++)
            {
                dones[i] = item.SubtasksDone[i] ? "1" : "0";
            }
        }
        contentValues.put(ITEM_COLUMN_SubtasksDone, dones != null && dones.length > 0 ? String.join(";", dones) : null);
        contentValues.put(ITEM_COLUMN_Done, item.Done);
        return db.update("items", contentValues, "id=" + item.ID, null) > 0;
    }

    public ArrayList<TodoItem> getItemList()
    {
        ArrayList<TodoItem> arrayList = new ArrayList<TodoItem>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from items order by done, priority desc, CASE WHEN deadline IS NULL THEN 1 ELSE 0 END, deadline", null);
        res.moveToFirst();

        while (!res.isAfterLast())
        {
            String title = res.getString(res.getColumnIndex(ITEM_COLUMN_Title));
            String description = res.getString(res.getColumnIndex(ITEM_COLUMN_Description));
            int priority = res.getInt(res.getColumnIndex(ITEM_COLUMN_Priority));
            String deadlineString = res.getString(res.getColumnIndex(ITEM_COLUMN_Deadline));
            String subtasks = res.getString(res.getColumnIndex(ITEM_COLUMN_Subtasks));
            String subtasksDone = res.getString(res.getColumnIndex(ITEM_COLUMN_SubtasksDone));
            boolean done = res.getInt(res.getColumnIndex(ITEM_COLUMN_Done)) == 1;
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
            String[] subtasksArr = subtasks != null ? subtasks.split("\n") : new String[]{};
            String[] subtasksArrDone = subtasksDone != null ? subtasksDone.split(";") : new String[]{};
            boolean[] dones = new boolean[subtasksArr.length];
            for (int i = 0; i < subtasksArr.length; i++)
            {
                dones[i] = subtasksArrDone[i].equals("1");
            }
            arrayList.add(new TodoItem(id, title, description, priority, deadline, subtasksArr, dones, done));
            res.moveToNext();
        }

        return arrayList;
    }

    public TodoItem getItem(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from items where id=" + id, null);
        res.moveToFirst();

        if (!res.isAfterLast())
        {
            String title = res.getString(res.getColumnIndex(ITEM_COLUMN_Title));
            String description = res.getString(res.getColumnIndex(ITEM_COLUMN_Description));
            int priority = res.getInt(res.getColumnIndex(ITEM_COLUMN_Priority));
            String deadlineString = res.getString(res.getColumnIndex(ITEM_COLUMN_Deadline));
            String subtasks = res.getString(res.getColumnIndex(ITEM_COLUMN_Subtasks));
            String subtasksDone = res.getString(res.getColumnIndex(ITEM_COLUMN_SubtasksDone));
            boolean done = res.getInt(res.getColumnIndex(ITEM_COLUMN_Done)) == 1;
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
            String[] subtasksArr = subtasks != null ? subtasks.split("\n") : new String[]{};
            String[] subtasksArrDone = subtasksDone != null ? subtasksDone.split(";") : new String[]{};
            boolean[] dones = new boolean[subtasksArr.length];
            for (int i = 0; i < subtasksArr.length; i++)
            {
                dones[i] = subtasksArrDone[i].equals("1");
            }
            return new TodoItem(id, title, description, priority, deadline, subtasksArr, dones, done);
        }
        return null;
    }

    public int removeAll()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("items", "1", null);
    }

    public void markSubtaskDone(TodoItem item, String subtask)
    {
        for (int i = 0; i < item.Subtasks.length; i++)
        {
            if (item.Subtasks[i].equals(subtask))
            {
                item.SubtasksDone[i] = true;
                break;
            }
        }
        updateItem(item);
    }

    public void markSubtaskUndone(TodoItem item, String subtask)
    {
        for (int i = 0; i < item.Subtasks.length; i++)
        {
            if (item.Subtasks[i].equals(subtask))
            {
                item.SubtasksDone[i] = false;
                break;
            }
        }
        updateItem(item);
    }
}
