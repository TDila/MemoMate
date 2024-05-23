package com.vulcan.memomate.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.vulcan.memomate.entities.Memo;

import java.util.List;

@Dao
public interface MemoDao {
    @Query("SELECT * FROM memos ORDER BY id DESC")
    List<Memo> getAllMemos();
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMemo(Memo memo);
    @Delete
    void deleteMemo(Memo memo);
}
