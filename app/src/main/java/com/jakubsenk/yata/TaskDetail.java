package com.jakubsenk.yata;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TaskDetail extends AppCompatActivity
{

    TodoItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        int id = getIntent().getIntExtra("id", -1);
        if (id == -1)
        {
            Toast.makeText(this, "Invalid todo.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        item = TodoProvider.GetTodo(this, id);
        TextView title = findViewById(R.id.titleDetail);
        title.setText(item.Title);
        TextView description = findViewById(R.id.descriptionDetail);
        description.setText(item.Description);
        if (item.Description == null || item.Description.equals(""))
        {
            description.setHeight(0);
        }
        TextView deadline = findViewById(R.id.deadlineDetail);
        TextView deadlineCount = findViewById(R.id.deadlineCountDetail);
        if (item.Deadline != null)
        {
            SimpleDateFormat format;
            Calendar cal = Calendar.getInstance();
            Calendar calNow = Calendar.getInstance();
            cal.setTime(item.Deadline);
            if (cal.get(Calendar.HOUR_OF_DAY) == 23 && cal.get(Calendar.MINUTE) == 59)
            {
                format = new SimpleDateFormat("yyyy-MM-dd");
            }
            else
            {
                format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            }
            deadline.setText(format.format(item.Deadline));
            deadlineCount.setText("");
            if (item.Deadline.before(new Date()))
            {
                deadline.setTextColor(Color.RED);
                long daysBetween = TimeUnit.DAYS.convert(new Date().getTime() - item.Deadline.getTime(), TimeUnit.MILLISECONDS);
                if (daysBetween == 0)
                {
                    long hoursBetween = TimeUnit.HOURS.convert(new Date().getTime() - item.Deadline.getTime(), TimeUnit.MILLISECONDS);
                    deadlineCount.setText("Task is delayed by " + hoursBetween + (hoursBetween == 1 ? " hour." : " hours."));
                }
                else
                {
                    deadlineCount.setText("Task is delayed by " + daysBetween + (daysBetween == 1 ? " day." : " days."));
                }
            }
            else if (cal.get(Calendar.DAY_OF_MONTH) == calNow.get(Calendar.DAY_OF_MONTH) && cal.get(Calendar.MONTH) == calNow.get(Calendar.MONTH) && cal.get(Calendar.YEAR) == calNow.get(Calendar.YEAR))
            {
                deadline.setTextColor(Color.rgb(255, 120, 0)); // No predefined orange? How low Java can even fall?
            }
        }
        else
        {
            deadline.setText("");
            deadline.setHeight(0);
            TextView deadlineText = findViewById(R.id.deadlineDetailLabel);
            deadlineText.setHeight(0);
            deadlineCount.setHeight(0);
        }
        ImageView imageView = findViewById(R.id.priorityDetailImg);
        TextView priority = findViewById(R.id.priorityDetail);
        switch (item.PriorityId)
        {
            case 0:
                imageView.setImageResource(0);
                // imageView.setMaxWidth(0);
                // You must be really shitting me...
                LayoutParams params = new LayoutParams(16,LayoutParams.WRAP_CONTENT);
                imageView.setLayoutParams(params);
                priority.setText("No priority");
                break;
            case 1:
                imageView.setImageResource(R.drawable.low_priority);
                priority.setText("Low priority");
                break;
            case 2:
                imageView.setImageResource(R.drawable.medium_priority);
                priority.setText("Medium priority");
                break;
            case 3:
                imageView.setImageResource(R.drawable.high_priority);
                priority.setText("High priority");
                break;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, item.Subtasks);
        ListView subtasksList = findViewById(R.id.subtasksDetail);
        subtasksList.setAdapter(adapter);

        // Cant retreive list items so i can set striked text? (Without creating custom adapter)

        subtasksList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                DBHelper db = new DBHelper(getApplicationContext());
                TextView row = (TextView) view;
                if (item.SubtasksDone[position])
                {
                    db.markSubtaskUndone(item, item.Subtasks[position]);
                    row.setPaintFlags(row.getPaintFlags() ^ Paint.STRIKE_THRU_TEXT_FLAG);
                }
                else
                {
                    db.markSubtaskDone(item, item.Subtasks[position]);
                    row.setPaintFlags(row.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.task_detail_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Intent intent;
        switch (item.getItemId())
        {
            case R.id.checkButton:
                this.item.Done = true;
                final MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.tada);
                mp.start();
                TodoProvider.UpdateTodo(this, this.item);
                finish();
                break;
            case R.id.editButton:
                intent = new Intent(this, NewTodoActivity.class);
                intent.putExtra("id", this.item.ID);
                startActivityForResult(intent, 1);
                finish();
                break;
            case R.id.deleteButton:
                alertDialog("Delete todo", "Are you sure you want to delete this todo?");
                break;
        }
        return true;
    }

    private void alertDialog(String title, String message)
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
                finish();
            }
        });
        dialog.setNegativeButton("NO", null);
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }
}