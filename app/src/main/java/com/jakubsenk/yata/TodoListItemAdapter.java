package com.jakubsenk.yata;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TodoListItemAdapter extends ArrayAdapter<TodoItem>
{

    private final Activity context;
    private final List<TodoItem> items;

    public TodoListItemAdapter(Activity context, List<TodoItem> items)
    {
        super(context, R.layout.todo_list_item, items);
        // TODO Auto-generated constructor stub

        this.context = context;
        this.items = items;
    }

    public View getView(int position, View view, ViewGroup parent)
    {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.todo_list_item, null, true);

        TextView titleText = (TextView) rowView.findViewById(R.id.title);
        TextView descriptionText = (TextView) rowView.findViewById(R.id.listTodoDescription);
        TextView deadlineText = (TextView) rowView.findViewById(R.id.deadlineLabel);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

        final TodoItem item = items.get(position);
        titleText.setText(item.Title);
        descriptionText.setText(item.Description);// != null ? item.Description : ""); // well done java, another bullshit... proste musim to checknout jak chuj, nemuzu tam dat proste null
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
            deadlineText.setText(format.format(item.Deadline));
            if (item.Deadline.before(new Date()))
            {
                deadlineText.setTextColor(Color.RED);
            }
            else if (cal.get(Calendar.DAY_OF_MONTH) == calNow.get(Calendar.DAY_OF_MONTH) && cal.get(Calendar.MONTH) == calNow.get(Calendar.MONTH) && cal.get(Calendar.YEAR) == calNow.get(Calendar.YEAR))
            {
                deadlineText.setTextColor(Color.rgb(255, 120, 0)); // No predefined orange? How low Java can even fall?
            }
        }
        else
        {
            deadlineText.setText("");
        }
        switch (item.PriorityId)
        {
            case 0:
                imageView.setImageResource(0);
                break;
            case 1:
                imageView.setImageResource(R.drawable.low_priority);
                break;
            case 2:
                imageView.setImageResource(R.drawable.medium_priority);
                break;
            case 3:
                imageView.setImageResource(R.drawable.high_priority);
                break;
        }
        if (item.Subtasks != null)
        {
            for (int i = 0; i < item.Subtasks.length; i++)
            {
                TextView textView = new TextView(getContext());
                textView.setText(item.Subtasks[i]);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins((int) (13 * context.getResources().getDisplayMetrics().density), 0, 0, 0);
                textView.setLayoutParams(lp); // Me je z te javy fakt na zvraceni
                // textView.layout.marginLeft = 10 by asi bylo moc normalni, tak to radeji budem delat takhle retardovane
                if (item.SubtasksDone[i])
                {
                    textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
                ((LinearLayout) rowView.findViewById(R.id.todoItemLayout)).addView(textView);
            }
        }

        if (item.Done)
        {
            titleText.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            titleText.setTextColor(Color.GRAY);
        }

        float measureText = deadlineText.getPaint().measureText(deadlineText.getText().toString());
        deadlineText.setMinWidth(deadlineText.getPaddingLeft() + deadlineText.getPaddingRight() + (int) measureText);
        deadlineText.setMinimumWidth(deadlineText.getPaddingLeft() + deadlineText.getPaddingRight() + (int) measureText);
        deadlineText.setWidth(deadlineText.getPaddingLeft() + deadlineText.getPaddingRight() + (int) measureText);

        return rowView;
    }
}