package com.example.to_do_list_application;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> tasks;
    private Context context;
    private CollectionReference tasksRef; // Reference to Firestore tasks collection

    public TaskAdapter(List<Task> tasks, Context context, CollectionReference tasksRef){
        this.tasks = tasks;
        this.context = context;
        this.tasksRef = tasksRef;
    }

    public void updateTasks(List<Task> tasks){
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position){
        Task task = tasks.get(position);
        holder.textTitle.setText(task.getTitle());
        holder.textDescription.setText(task.getDescription());
        holder.textPriority.setText(task.getPriority());

        // Set priority color and indicator
        int priorityColor;
        switch(task.getPriority()){
            case "High":
                priorityColor = Color.RED;
                break;
            case "Medium":
                priorityColor = Color.parseColor("#FFA500"); // Orange
                break;
            case "Low":
                priorityColor = Color.GREEN;
                break;
            default:
                priorityColor = Color.GRAY;
        }
        holder.textPriority.setTextColor(priorityColor);
        holder.viewPriorityIndicator.setBackgroundColor(priorityColor);

        // Handle Item Clicks for Edit
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddEditTaskActivity.class);
            intent.putExtra("task_id", task.getId());
            ((Activity) context).startActivityForResult(intent, MainActivity.REQUEST_CODE_ADD_EDIT);
        });

        // Handle Long Clicks for Delete
        holder.itemView.setOnLongClickListener(v -> {
            // Show delete confirmation dialog
            new androidx.appcompat.app.AlertDialog.Builder(context)
                    .setTitle("Delete Task")
                    .setMessage("Are you sure you want to delete this task?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        if(task.getId() != null){
                            tasksRef.document(task.getId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(context, "Task deleted", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context, "Error deleting task: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        });
    }

    @Override
    public int getItemCount(){
        return tasks.size();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textDescription, textPriority;
        View viewPriorityIndicator;

        public TaskViewHolder(@NonNull View itemView){
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTaskTitle);
            textDescription = itemView.findViewById(R.id.textTaskDescription);
            textPriority = itemView.findViewById(R.id.textTaskPriority);
            viewPriorityIndicator = itemView.findViewById(R.id.viewPriorityIndicator);
        }
    }
}
