package com.example.to_do_list_application;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddEditTaskActivity extends AppCompatActivity {

    private TextInputEditText editTitle, editDescription;
    private Spinner spinnerPriority;
    private Button buttonSaveTask;
    private FirebaseFirestore db;
    private String taskId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task); // Ensure this is the correct layout

        // Initialize Views
        editTitle = findViewById(R.id.editTextTaskTitle);
        editDescription = findViewById(R.id.editTextTaskDescription);
        spinnerPriority = findViewById(R.id.spinnerPriority);
        buttonSaveTask = findViewById(R.id.buttonSaveTask);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Setup Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.priority_levels, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(adapter);

        // Check if editing an existing task
        Intent intent = getIntent();
        if(intent.hasExtra("task_id")){
            taskId = intent.getStringExtra("task_id");
            loadTask(taskId);
        }

        // Save Button Click
        buttonSaveTask.setOnClickListener(v -> saveTask());
    }

    private void loadTask(String id){
        db.collection("tasks").document(id)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()){
                        Task task = documentSnapshot.toObject(Task.class);
                        if(task != null){
                            editTitle.setText(task.getTitle());
                            editDescription.setText(task.getDescription());
                            int spinnerPosition = ((ArrayAdapter)spinnerPriority.getAdapter()).getPosition(task.getPriority());
                            spinnerPriority.setSelection(spinnerPosition);
                        }
                    } else {
                        Toast.makeText(this, "Task not found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading task: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void saveTask(){
        String title = editTitle.getText().toString().trim();
        String description = editDescription.getText().toString().trim();
        String priority = spinnerPriority.getSelectedItem().toString();

        if(title.isEmpty()){
            editTitle.setError("Title is required");
            editTitle.requestFocus();
            return;
        }

        long timestamp = System.currentTimeMillis(); // Current time in milliseconds

        Map<String, Object> taskMap = new HashMap<>();
        taskMap.put("title", title);
        taskMap.put("description", description);
        taskMap.put("priority", priority);
        taskMap.put("timestamp", timestamp); // Include timestamp

        if(taskId == null){
            // New Task
            db.collection("tasks")
                    .add(taskMap)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Task added", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error adding task: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Update Task
            db.collection("tasks").document(taskId)
                    .set(taskMap)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Task updated", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error updating task: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
