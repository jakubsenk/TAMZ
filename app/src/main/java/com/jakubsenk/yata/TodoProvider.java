package com.jakubsenk.yata;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class TodoProvider
{
    public static List<TodoItem> GetTodos(Context ctx)
    {
        DBHelper db = new DBHelper(ctx);
        return db.getItemList();
//        ArrayList<TodoItem> todos = new ArrayList<>();
//
//        todos.add(new TodoItem(1,"Title 1 Which is verry verry long and when I say long I mean really long", R.drawable.high_priority));
//        todos.add(new TodoItem(2,"Title 2", R.drawable.medium_priority));
//        todos.add(new TodoItem(3,"Title 3", R.drawable.low_priority, new String[]{"Ein", "Zwai", "Drai"}));
//        return todos;
    }

    public static void CreateTodo(Context ctx, TodoItem item)
    {
        DBHelper db = new DBHelper(ctx);
        db.insertItem(item);
    }
}
