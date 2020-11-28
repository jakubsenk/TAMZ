package com.jakubsenk.yata;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NewTodoActivity extends AppCompatActivity
{
    EditText date_time_in;
    final Calendar calendar = Calendar.getInstance();
    boolean timeSet = false;
    boolean dateSet = false;

    TodoItem editItem = null;
    int editID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_todo);
        date_time_in = findViewById(R.id.todoDeadline);
        date_time_in.setInputType(InputType.TYPE_NULL);

        editID = getIntent().getIntExtra("id", -1);
        if (editID != -1)
        {
            DBHelper db = new DBHelper(this);
            editItem = db.getItem(editID);

            ((EditText) findViewById(R.id.todoTitle)).setText(editItem.Title);
            ((EditText) findViewById(R.id.todoDescription)).setText(editItem.Description);
            RadioGroup radios = findViewById(R.id.radioGroup);
            switch (editItem.PriorityId)
            {
                case 0:
                    radios.check(R.id.radioNone);
                    break;
                case 1:
                    radios.check(R.id.radioLow);
                    break;
                case 2:
                    radios.check(R.id.radioNormal);
                    break;
                case 3:
                    radios.check(R.id.radioHigh);
                    break;
            }
            if (editItem.Deadline != null)
            {
                calendar.setTime(editItem.Deadline);
                SimpleDateFormat simpleDateFormat;
                if (calendar.get(Calendar.HOUR_OF_DAY) == 23 && calendar.get(Calendar.MINUTE) == 59)
                {
                    dateSet = true;
                    simpleDateFormat = new SimpleDateFormat("yy-MM-dd"); // Is java really that stupid that you cant retreive locale date format?
                }
                else
                {
                    dateSet = true;
                    timeSet = true;
                    simpleDateFormat = new SimpleDateFormat("yy-MM-dd HH:mm"); // Is java really that stupid that you cant retreive locale date format?
                    ((Button) findViewById(R.id.setTimeButton)).setText("Edit time");
                }
                date_time_in.setText(simpleDateFormat.format(calendar.getTime()));
            }
            ((EditText) findViewById(R.id.subtasks)).setText(String.join("\n", editItem.Subtasks));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.new_todo_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.addButton)
        {
            String title = ((EditText) findViewById(R.id.todoTitle)).getText().toString();
            if (title.equals(""))
            {
                alertDialog("Title is required", "Please type in task title.");
                return super.onOptionsItemSelected(item);
            }
            String description = ((EditText) findViewById(R.id.todoDescription)).getText().toString();
            RadioGroup radios = findViewById(R.id.radioGroup);
            RadioButton priority = findViewById(radios.getCheckedRadioButtonId());
            int priorityId = 0;
            if (priority.getText().equals("High"))
            {
                priorityId = 3;
            }
            else if (priority.getText().equals("Normal"))
            {
                priorityId = 2;
            }
            else if (priority.getText().equals("Low"))
            {
                priorityId = 1;
            }
            Date deadline = null;
            if (dateSet && timeSet)
            {
                deadline = calendar.getTime();
            }
            else if (dateSet)
            {
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                deadline = calendar.getTime();
            }
            String subtasks = ((EditText) findViewById(R.id.subtasks)).getText().toString();
            String[] subtasksParsed = new String[]{};
            if (!subtasks.equals(""))
            {
                subtasksParsed = subtasks.split("\n");
            }

            boolean[] dones = new boolean[subtasksParsed.length];
            for (int i = 0; i < subtasksParsed.length; i++)
            {
                dones[i] = false;
            }

            TodoItem newTodo = new TodoItem(-1, title, description, priorityId, deadline, subtasksParsed, dones, false);
            if (editItem == null)
            {
                createNewTodo(newTodo);
            }
            else
            {
                newTodo.ID = editID;
                updateTodo(newTodo);
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private void updateTodo(TodoItem todo)
    {
        TodoProvider.UpdateTodo(this, todo);
        finish();
    }

    private void createNewTodo(TodoItem todo)
    {
        TodoProvider.CreateTodo(this, todo);
        finish();
    }

    public void showDateTimeDialog(final View date_time_in)
    {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
            {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(timeSet ? "yy-MM-dd HH:mm" : "yy-MM-dd"); // Is java really that stupid that you cant retreive locale date format?

                dateSet = true;
                if (date_time_in instanceof Button)
                {
                    setTimeClick(view);
                    return;
                }
                ((EditText) date_time_in).setText(simpleDateFormat.format(calendar.getTime()));
            }
        };

        DatePickerDialog dialog = new DatePickerDialog(NewTodoActivity.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        if (dateSet)
        {
            dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Clear", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    NewTodoActivity.this.date_time_in.setText("");
                    dateSet = false;
                    timeSet = false;
                    ((Button) findViewById(R.id.setTimeButton)).setText("Specify time");
                }
            });
        }
        dialog.show();
    }

    public void setTimeClick(View view)
    {
        if (!dateSet)
        {
            showDateTimeDialog(view);
            return;
        }
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener()
        {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute)
            {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd HH:mm"); // Is java really that stupid that you cant retreive locale date format?

                date_time_in.setText(simpleDateFormat.format(calendar.getTime()));
                timeSet = true;
                ((Button) findViewById(R.id.setTimeButton)).setText("Edit time");
            }
        };

        TimePickerDialog dialog = new TimePickerDialog(NewTodoActivity.this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        if (timeSet)
        {
            dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Clear", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd"); // Is java really that stupid that you cant retreive locale date format?

                    date_time_in.setText(simpleDateFormat.format(calendar.getTime()));
                    timeSet = false;
                    ((Button) findViewById(R.id.setTimeButton)).setText("Specify time");
                }
            });
        }
        dialog.show();
    }

    private void alertDialog(String title, String message)
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(message);
        dialog.setTitle(title);
        dialog.setPositiveButton("OK", null);
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }
}