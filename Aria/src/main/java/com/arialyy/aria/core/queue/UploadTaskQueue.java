/*
 * Copyright (C) 2016 AriaLyy(https://github.com/AriaLyy/Aria)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.arialyy.aria.core.queue;

import android.text.TextUtils;
import android.util.Log;
import com.arialyy.aria.core.queue.pool.ExecutePool;
import com.arialyy.aria.core.scheduler.UploadSchedulers;
import com.arialyy.aria.core.upload.UploadEntity;
import com.arialyy.aria.core.upload.UploadTask;
import com.arialyy.aria.core.upload.UploadTaskEntity;

/**
 * Created by Aria.Lao on 2017/2/27.
 * 上传任务队列
 */
public class UploadTaskQueue extends AbsTaskQueue<UploadTask, UploadTaskEntity, UploadEntity> {
  private static final String TAG = "UploadTaskQueue";
  private static volatile UploadTaskQueue INSTANCE = null;
  private static final Object LOCK = new Object();

  public static UploadTaskQueue getInstance() {
    if (INSTANCE == null) {
      synchronized (LOCK) {
        INSTANCE = new UploadTaskQueue();
      }
    }
    return INSTANCE;
  }

  private UploadTaskQueue() {
    mExecutePool = new ExecutePool<>(false);
  }

  @Override public void setMaxTaskNum(int newMaxNum) {

  }

  @Override public UploadTask createTask(String targetName, UploadTaskEntity entity) {
    UploadTask task = null;
    if (!TextUtils.isEmpty(targetName)) {
      task = (UploadTask) TaskFactory.getInstance()
          .createTask(targetName, entity, UploadSchedulers.getInstance());
      mCachePool.putTask(task);
    } else {
      Log.e(TAG, "target name 为 null是！！");
    }
    return task;
  }

  @Override public UploadTask getTask(UploadEntity entity) {
    return getTask(entity.getFilePath());
  }

  @Override public void removeTask(UploadEntity entity) {
    UploadTask task = mExecutePool.getTask(entity.getFilePath());
    if (task != null) {
      Log.d(TAG, "从执行池删除任务，删除" + (mExecutePool.removeTask(task) ? "成功" : "失败"));
    }
    task = mCachePool.getTask(entity.getFilePath());
    if (task != null) {
      Log.d(TAG, "从缓存池删除任务，删除" + (mCachePool.removeTask(task) ? "成功" : "失败"));
    }
  }
}
