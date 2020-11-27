package com.jakubsenk.yata;

import android.content.Context;

import java.util.List;

public class TodoProvider
{
    public static List<TodoItem> GetTodos(Context ctx)
    {
        DBHelper db = new DBHelper(ctx);
        return db.getItemList();
    }

    public static void CreateTodo(Context ctx, TodoItem item)
    {
        DBHelper db = new DBHelper(ctx);
        db.insertItem(item);
    }
}
