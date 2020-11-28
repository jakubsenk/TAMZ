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

    public static TodoItem GetTodo(Context ctx, int id)
    {
        DBHelper db = new DBHelper(ctx);
        return db.getItem(id);
    }

    public static void CreateTodo(Context ctx, TodoItem item)
    {
        DBHelper db = new DBHelper(ctx);
        db.insertItem(item);
    }

    public static void UpdateTodo(Context ctx, TodoItem item)
    {
        DBHelper db = new DBHelper(ctx);
        db.updateItem(item);
    }

    public static void DeleteTodo(Context ctx, int id)
    {
        DBHelper db = new DBHelper(ctx);
        db.deleteItem(id);
    }
}
