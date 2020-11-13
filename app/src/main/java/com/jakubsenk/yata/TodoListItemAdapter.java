package com.jakubsenk.yata;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TodoListItemAdapter extends ArrayAdapter<TodoItem>
{

    private final Activity context;
    private final TodoItem[] items;

    public TodoListItemAdapter(Activity context, TodoItem[] items)
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
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

        titleText.setText(items[position].Title);
        imageView.setImageResource(items[position].PriorityId);
        if (items[position].Subtasks != null)
        {
            for (int i = 0; i < items[position].Subtasks.length; i++)
            {
                TextView textView = new TextView(getContext());
                textView.setText(items[position].Subtasks[i]);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins((int)(10 * context.getResources().getDisplayMetrics().density), 0, 0, 0);
                textView.setLayoutParams(lp); // Me je z te javy fakt na zvraceni
                // textView.layout.marginLeft = 10 by asi bylo moc normalni, tak to radeji budem delat takhle retardovane
                ((LinearLayout) rowView.findViewById(R.id.todoItemLayout)).addView(textView);
            }
        }

        return rowView;
    }
}