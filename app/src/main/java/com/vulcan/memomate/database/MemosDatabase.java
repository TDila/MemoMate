package com.vulcan.memomate.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.vulcan.memomate.dao.MemoDao;
import com.vulcan.memomate.entities.Memo;

@Database(entities = Memo.class, version = 1,exportSchema = false)
public abstract class MemosDatabase extends RoomDatabase {
    private static MemosDatabase memosDatabase = null;
    public static synchronized MemosDatabase getDatabase(Context context){
        if(memosDatabase == null){
            memosDatabase = Room.databaseBuilder(
                    context,
                    MemosDatabase.class,
                    "memos_db"
            ).build();
        }
        return memosDatabase;
    }

    public abstract MemoDao memoDao();
}
