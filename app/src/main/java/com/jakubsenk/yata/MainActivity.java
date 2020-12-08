package com.jakubsenk.yata;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private float x1;
    static final int MIN_DISTANCE = 250;

    ListView todos;

    List<TodoItem> items;
    TodoListItemAdapter adapter;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        items = TodoProvider.GetTodos(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new TodoListItemAdapter(this, items);
        todos = findViewById(R.id.list);
        todos.setAdapter(adapter);

        todos.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        float x2 = event.getX();
                        float deltaX = x2 - x1;

                        if (Math.abs(deltaX) > MIN_DISTANCE)
                        {
                            TodoItem item;
                            try
                            {
                                item = adapter.getItem(todos.pointToPosition(Math.round(event.getX()), Math.round(event.getY())));
                            }
                            catch (IndexOutOfBoundsException ex)
                            {
                                return false;
                            }
                            if (x2 < x1)
                            {
                                item.Done = !item.Done;
                                TodoProvider.UpdateTodo(getApplicationContext(), item);
                                items = TodoProvider.GetTodos(getApplicationContext());
                                adapter.clear();
                                adapter.addAll(items);
                            }
                            else
                            {
                                alertDialog("Delete todo", "Are you sure you want to delete this todo?", item);
                            }
                        }
                        else if (x1 == x2)
                        {
                            Intent i = new Intent(getApplicationContext(), TaskDetail.class);
                            i.putExtra("id", adapter.getItem(todos.pointToPosition(Math.round(event.getX()), Math.round(event.getY()))).ID);
                            startActivityForResult(i, 1);
                        }
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.mybutton)
        {
            Intent i = new Intent(this, NewTodoActivity.class);
            startActivityForResult(i, 1);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
        {
            items = TodoProvider.GetTodos(this);
            adapter.clear();
            adapter.addAll(items);
        }
    }

    private void alertDialog(String title, String message, final TodoItem item)
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(message);
        dialog.setTitle(title);
        dialog.setPositiveButton("YES", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                TodoProvider.DeleteTodo(getApplicationContext(), item.ID);
                items = TodoProvider.GetTodos(getApplicationContext());
                adapter.clear();
                adapter.addAll(items);
            }
        });
        dialog.setNegativeButton("NO", null);
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }


}
