package com.jakubsenk.yata;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{
    ListView todos;

    TodoItem[] items = {
            new TodoItem("Title 1 Which is verry verry long and when I say long I mean really long", R.drawable.high_priority),
            new TodoItem("Title 2", R.drawable.medium_priority),
            new TodoItem("Title 3", R.drawable.low_priority, new String[]{"Ein", "Zwai", "Drai"}),
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TodoListItemAdapter adapter = new TodoListItemAdapter(this, items);
        todos = (ListView) findViewById(R.id.list);
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
}
