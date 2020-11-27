package com.jakubsenk.yata;

import java.util.Date;

public class TodoItem
{
    public int ID;
    public String Title;
    public String Description;
    public int PriorityId;
    public Date Deadline;
    public String[] Subtasks;
    public boolean[] SubtasksDone;
    public boolean Done;

    public TodoItem(int id)
    {
        this(id, null, null, 0, null, null, null, false);
    }

    public TodoItem(int id, String title)
    {
        this(id, title, null, 0, null, null, null, false);
    }

    public TodoItem(int id, String title, int priorityId)
    {
        this(id, title, null, priorityId, null, null, null, false);
    }

    public TodoItem(int id, String title, String description, int priorityId, Date deadline, String[] subtasks, boolean[] dones, boolean done)
    {
        ID = id;
        Title = title;
        Description = description;
        PriorityId = priorityId;
        Deadline = deadline;
        Subtasks = subtasks;
        SubtasksDone = dones;
        Done = done;
    }
}
