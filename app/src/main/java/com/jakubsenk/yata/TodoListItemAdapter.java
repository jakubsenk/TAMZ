package com.jakubsenk.yata;

import android.app.Activity;
import android.graphics.Color;
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
        TextView deadlineText = (TextView) rowView.findViewById(R.id.deadlineLabel);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

        final TodoItem item = items.get(position);
        titleText.setText(item.Title);
        if (item.Deadline != null)
        {
            SimpleDateFormat format;
            Calendar cal = Calendar.getInstance();
            Calendar calNow = Calendar.getInstance();
            cal.setTime(item.Deadline);
            if (cal.get(Calendar.HOUR_OF_DAY) == 0 && cal.get(Calendar.MINUTE) == 0)
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
                deadlineText.setTextColor(Color.rgb(255,120,0)); // No predefined orange? How low Java can even fall?
            }
        }
        else
        {
            deadlineText.setText("");
        }
        imageView.setImageResource(item.PriorityId);
        if (item.Subtasks != null)
        {
            for (int i = 0; i < item.Subtasks.length; i++)
            {
                TextView textView = new TextView(getContext());
                textView.setText(item.Subtasks[i]);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins((int) (10 * context.getResources().getDisplayMetrics().density), 0, 0, 0);
                textView.setLayoutParams(lp); // Me je z te javy fakt na zvraceni
                // textView.layout.marginLeft = 10 by asi bylo moc normalni, tak to radeji budem delat takhle retardovane
                ((LinearLayout) rowView.findViewById(R.id.todoItemLayout)).addView(textView);
            }
        }

        return rowView;
    }
}