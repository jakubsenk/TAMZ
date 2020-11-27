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
                Intent i = new Intent(getApplicationContext(), TaskDetail.class);
                i.putExtra("id", items.get(position).ID);
                startActivityForResult(i, 1);
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
}
