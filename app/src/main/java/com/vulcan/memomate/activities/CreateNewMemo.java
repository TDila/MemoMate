package com.vulcan.memomate.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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
    private TextView newMemoDateTime,deleteMemoButton;
    private ImageView deleteButton;
    private Memo alreadyAvailableMemo;
    private AlertDialog dialogDeleteMemo;

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

        if (getIntent().getBooleanExtra("isViewOrUpdate", false)) {
            alreadyAvailableMemo = (Memo) getIntent().getSerializableExtra("memo");
            setViewOrUpdateMemo();
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteMemoDialog();
                }
            });
        }
    }

    private void initComponents(){
        memoTitle = findViewById(R.id.memoTitle);
        memoSubTitle = findViewById(R.id.memoSubTitle);
        memoContent = findViewById(R.id.memoContent);
        newMemoDateTime = findViewById(R.id.newMemoDateTime);
        deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setVisibility(View.GONE);

        newMemoDateTime.setText(
                new SimpleDateFormat("EEEE, dd-MM-yyyy HH:mm:ss", Locale.getDefault())
                        .format(new Date())
        );
    }

    public void setViewOrUpdateMemo(){
        memoTitle.setText(alreadyAvailableMemo.getTitle());
        memoSubTitle.setText(alreadyAvailableMemo.getSubtitle());
        memoContent.setText(alreadyAvailableMemo.getMemoContent());
        newMemoDateTime.setText(alreadyAvailableMemo.getDateTime());
    }

    private void showDeleteMemoDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateNewMemo.this);
        View view = LayoutInflater.from(CreateNewMemo.this)
                .inflate(R.layout.layout_delete_note, (ViewGroup) findViewById(R.id.deleteMemoLayout));
        builder.setView(view);
        dialogDeleteMemo = builder.create();
        if(dialogDeleteMemo.getWindow() != null){
            dialogDeleteMemo.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        view.findViewById(R.id.deleteMemoButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        MemosDatabase.getDatabase(CreateNewMemo.this).memoDao().deleteMemo(alreadyAvailableMemo);

                        CreateNewMemo.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent();
                                intent.putExtra("isNoteDeleted",true);
                                setResult(RESULT_OK,intent);
                                finish();
                            }
                        });
                    }
                });
                thread.start();
            }
        });

        view.findViewById(R.id.deleteCancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDeleteMemo.dismiss();
            }
        });

        dialogDeleteMemo.show();
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

            if(alreadyAvailableMemo != null){
                memo.setId(alreadyAvailableMemo.getId());
            }

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