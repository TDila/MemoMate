package com.vulcan.memomate.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.vulcan.memomate.R;
import com.vulcan.memomate.database.MemosDatabase;
import com.vulcan.memomate.entities.Memo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateNewMemo extends AppCompatActivity {
    private EditText memoTitle, memoSubTitle, memoContent;
    private TextView newMemoDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_memo);

        initComponents();

        findViewById(R.id.closeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.saveButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
            }
        });
    }

    private void initComponents(){
        memoTitle = findViewById(R.id.memoTitle);
        memoSubTitle = findViewById(R.id.memoSubTitle);
        memoContent = findViewById(R.id.memoContent);
        newMemoDateTime = findViewById(R.id.newMemoDateTime);

        newMemoDateTime.setText(
                new SimpleDateFormat("EEEE, dd-MM-yyyy HH:mm:ss", Locale.getDefault())
                        .format(new Date())
        );
    }

    private void saveNote(){
        if(memoTitle.getText().toString().trim().isEmpty()){
            Toast.makeText(this,"Memo Title can't be empty!",Toast.LENGTH_SHORT).show();
            return;
        }else if(memoContent.getText().toString().trim().isEmpty()){
            Toast.makeText(this,"Memo Content can't be empty!", Toast.LENGTH_SHORT).show();
            return;
        }else{
            Memo memo = new Memo();
            memo.setTitle(memoTitle.getText().toString());
            memo.setSubtitle(memoSubTitle.getText().toString());
            memo.setMemoContent(memoContent.getText().toString());
            memo.setDateTime(newMemoDateTime.getText().toString());

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    MemosDatabase.getDatabase(getApplicationContext()).memoDao().insertMemo(memo);
                    CreateNewMemo.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent();
                            setResult(RESULT_OK,intent);
                            finish();
                        }
                    });
                }
            });
            thread.start();
        }
    }
}