package com.vulcan.memomate.listeners;

import com.vulcan.memomate.entities.Memo;

public interface MemosListener {
    void onMemoClicked(Memo memo, int position);
}
