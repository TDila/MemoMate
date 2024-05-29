package com.vulcan.memomate.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.vulcan.memomate.R;
import com.vulcan.memomate.adapters.MemosAdapter;
import com.vulcan.memomate.database.MemosDatabase;
import com.vulcan.memomate.entities.Memo;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final Integer REQUEST_CODE_ADD_MEMO = 1;
    private RecyclerView memosRecyclerView;
    private List<Memo> memoList;
    private MemosAdapter memosAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult activityResult) {
                        if(activityResult.getResultCode() == RESULT_OK){
                            getMemos();
                        }
                    }
                });

        findViewById(R.id.addNoteBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateNewMemo.class);
                activityResultLauncher.launch(intent);
            }
        });

        memosRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        memoList = new ArrayList<>();
        memosAdapter = new MemosAdapter(memoList,this);
        memosRecyclerView.setAdapter(memosAdapter);

        getMemos();
    }

    private void initComponents() {
        memosRecyclerView = findViewById(R.id.notesViewRecyclerView);
    }

    private void getMemos(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                List<Memo> memos = MemosDatabase.getDatabase(MainActivity.this).memoDao().getAllMemos();
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(memoList.size() == 0){
                            memoList.addAll(memos);
                            memosAdapter.notifyDataSetChanged();
                        }else{
                            memoList.add(0,memos.get(0));
                            memosAdapter.notifyItemInserted(0);
                        }
                        memosRecyclerView.smoothScrollToPosition(0);
                    }
                });
            }
        });
        thread.start();
    }
}