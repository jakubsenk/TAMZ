package com.jakubsenk.yata;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity
{
    ListView todos;

    List<TodoItem> items;
    TodoListItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        items = TodoProvider.GetTodos(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new TodoListItemAdapter(this, items);
        todos = findViewById(R.id.list);
        todos.setAdapter(adapter);


        todos.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), "Click!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // R.menu.mymenu is a reference to an xml file named mymenu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
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
}
