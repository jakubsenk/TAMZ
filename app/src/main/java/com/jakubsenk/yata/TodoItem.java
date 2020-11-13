package com.jakubsenk.yata;

public class TodoItem
{
    public String Title;
    public String[] Subtasks;
    public int PriorityId;

    public TodoItem()
    {
        this(null, 0, null);
    }

    public TodoItem(String title)
    {
        this(title, 0, null);
    }

    public TodoItem(String title, int priorityId)
    {
        this(title, priorityId, null);
    }

    public TodoItem(String title, int priorityId, String[] subtasks)
    {
        Title = title;
        Subtasks = subtasks;
        PriorityId = priorityId;
    }
}
