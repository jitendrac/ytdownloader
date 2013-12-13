/*
 * code adapted from download manager project by 
 * Hiroshi Matsunaga (matsuhiro): 
 * https://github.com/matsuhiro/AndroidDownloadManger
 * (released "unlicensed").
 */

package dentex.youtube.downloader.dm;

public interface DownloadTaskListener {

    public void updateProcess(DownloadTask task);

    public void finishDownload(DownloadTask task);

    public void preDownload(DownloadTask task);

    public void queuedTask(DownloadTask task);

    public void pausedDownload(DownloadTask task);

    public void resumedDownload(DownloadTask task);

    public void deletedDownload(DownloadTask task);

    public void errorDownload(DownloadTask task, Throwable error);
}
