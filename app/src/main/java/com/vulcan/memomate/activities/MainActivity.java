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
import com.vulcan.memomate.listeners.MemosListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MemosListener {

    private static final Integer REQUEST_CODE_ADD_MEMO = 1;
    private static final Integer REQUEST_CODE_UPDATE_MEMO = 2;
    private static final Integer REQUEST_CODE_SHOW_MEMOS = 3;
    private int memoClickedPosition = -1;
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
                            getMemos(REQUEST_CODE_ADD_MEMO);
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
        memosAdapter = new MemosAdapter(memoList,this, this);
        memosRecyclerView.setAdapter(memosAdapter);

        getMemos(REQUEST_CODE_SHOW_MEMOS);
    }

    ActivityResultLauncher<Intent> memoClicked = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult activityResult) {
                    if(activityResult.getResultCode() == RESULT_OK && activityResult.getData() != null){
                        getMemos(REQUEST_CODE_UPDATE_MEMO);
                    }
                }
            });

    @Override
    public void onMemoClicked(Memo memo, int position) {
        memoClickedPosition = position;
        Intent intent = new Intent(MainActivity.this, CreateNewMemo.class);
        intent.putExtra("isViewOrUpdate",true);
        intent.putExtra("memo", memo);
        memoClicked.launch(intent);
    }

    private void initComponents() {
        memosRecyclerView = findViewById(R.id.notesViewRecyclerView);
    }

    private void getMemos(final int requestCode){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                List<Memo> memos = MemosDatabase.getDatabase(MainActivity.this).memoDao().getAllMemos();
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(requestCode == REQUEST_CODE_SHOW_MEMOS){
                            memoList.addAll(memos);
                            memosAdapter.notifyDataSetChanged();
                        }else if(requestCode == REQUEST_CODE_ADD_MEMO){
                            memoList.add(0, memos.get(0));
                            memosAdapter.notifyItemInserted(0);
                            memosRecyclerView.smoothScrollToPosition(0);
                        }else if(requestCode == REQUEST_CODE_UPDATE_MEMO){
                            memoList.remove(memoClickedPosition);
                            memoList.add(memoClickedPosition, memos.get(memoClickedPosition));
                            memosAdapter.notifyItemChanged(memoClickedPosition);
                        }
                    }
                });
            }
        });
        thread.start();
    }
}