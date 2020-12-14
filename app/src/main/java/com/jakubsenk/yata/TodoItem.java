package com.jakubsenk.yata;

import java.io.Serializable;
import java.util.Date;


public class TodoItem implements Serializable
{
    public int ID;
    public String Title;
    public String Description;
    public int PriorityId;
    public Date Deadline;
    public String[] Subtasks;
    public boolean[] SubtasksDone;
    public boolean Done;

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
