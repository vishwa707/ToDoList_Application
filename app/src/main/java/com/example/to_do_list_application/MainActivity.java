package com.example.to_do_list_application;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_ADD_EDIT = 1;

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private FirebaseFirestore db;
    private CollectionReference tasksRef;
    private FloatingActionButton fabAddTask;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Task> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Ensure this is the correct layout

        // Initialize Views
        recyclerView = findViewById(R.id.recyclerViewTasks);
        fabAddTask = findViewById(R.id.fabAddTask);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        tasksRef = db.collection("tasks");

        // Initialize Task List
        taskList = new ArrayList<>();

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(taskList, this, tasksRef);
        recyclerView.setAdapter(taskAdapter);

        // Add Task Button Click
        fabAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_EDIT);
        });

        // Swipe to Refresh
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadTasks();
            swipeRefreshLayout.setRefreshing(false);
        });

        // Load Tasks
        loadTasks();
    }

    private void loadTasks(){
        tasksRef.orderBy("priority", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if(e != null){
                        Toast.makeText(MainActivity.this, "Error loading tasks: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    taskList.clear();
                    if(snapshots != null){
                        for(com.google.firebase.firestore.DocumentSnapshot doc : snapshots.getDocuments()){
                            Task task = doc.toObject(Task.class);
                            if(task != null){
                                task.setId(doc.getId());
                                taskList.add(task);
                            }
                        }
                        taskAdapter.updateTasks(taskList);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_ADD_EDIT && resultCode == RESULT_OK){
            Toast.makeText(this, "Task Saved", Toast.LENGTH_SHORT).show();
        }
    }
}
