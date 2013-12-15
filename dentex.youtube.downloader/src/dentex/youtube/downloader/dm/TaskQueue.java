/*
 * code adapted from download manager project by 
 * Hiroshi Matsunaga (matsuhiro): 
 * https://github.com/matsuhiro/AndroidDownloadManger
 * (released "unlicensed").
 */

package dentex.youtube.downloader.dm;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.os.AsyncTask;
import dentex.youtube.downloader.utils.Utils;

public class TaskQueue<T extends AsyncTask<?, ?, ?>> {
    private static final String DEBUG_TAG = "TaskQueue";

    private final BlockingQueue<T> mQueue = new LinkedBlockingQueue<T>();

    private volatile int mCount = 0;

    private final int MAXIMUM_RUN_TASK_COUNT;

    public TaskQueue(int maximumCount) {
        MAXIMUM_RUN_TASK_COUNT = maximumCount;
    }

    public T getTask() {
        T task = null;
        final int runCount;
        synchronized (this) {
            runCount = mCount - mQueue.size();
        }
        Utils.logger("d", "get task, mCount : " + mCount, DEBUG_TAG);
        Utils.logger("d", "get task, runCount : " + runCount, DEBUG_TAG);
        if (runCount >= MAXIMUM_RUN_TASK_COUNT) {
            synchronized (this) {
                try {
                    Utils.logger("d", "get wait", DEBUG_TAG);
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                task = mQueue.take();
                Utils.logger("d", "taken task", DEBUG_TAG);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        }
        return task;
    }

    public synchronized void putTask(T task) {
        try {
            mQueue.put(task);
            mCount += 1;
            Utils.logger("d", "put task, mCount : " + mCount, DEBUG_TAG);
            notifyAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void removeTask(T task) {
        if (task == null) {
            return;
        }
        mQueue.remove(task);
        mCount -= 1;
        if (mCount < 0) {
            mCount = 0;
        }
        Utils.logger("d", "remove task, mCount : " + mCount, DEBUG_TAG);
        notifyAll();
    }

    public synchronized void notifyComplete() {
        mCount -= 1;
        if (mCount < 0) {
            mCount = 0;
        }
        Utils.logger("d", "completed task, mCount : " + mCount, DEBUG_TAG);
        notifyAll();
    }

    public void removeAll() {
        for (T item : mQueue) {
            mQueue.remove(item);
        }
    }

    public int getCount() {
        return mCount;
    }
}
