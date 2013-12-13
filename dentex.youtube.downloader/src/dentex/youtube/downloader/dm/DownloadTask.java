/*
 * code adapted from download manager project by 
 * Hiroshi Matsunaga (matsuhiro): 
 * https://github.com/matsuhiro/AndroidDownloadManger
 * (released "unlicensed").
 */

package dentex.youtube.downloader.dm;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.conn.ConnectTimeoutException;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import dentex.youtube.downloader.YTD;
import dentex.youtube.downloader.utils.Utils;

public class DownloadTask extends AsyncTask<String, Integer, Long> {

    public final static int TIME_OUT = 30000;

    private final static int BUFFER_SIZE = 1024 * 8;

    private static final String DEBUG_TAG = "DownloadTask";

    private static final boolean DEBUG = true;

    public static final String TEMP_SUFFIX = ".download";

    private HttpURLConnection mConnection = null;

    private File mFile;

    private File mTempFile;
    
    private String mAudioExt;
    
    private String mType;

    private String mUrlString;

    private String mPath;

    private URL mURL;

    private DownloadTaskListener mListener;

    private Context mContext;

    private long mDownloadSize;

    private long mPreviousFileSize;

    private long mTotalSize;

    private long mDownloadPercent;
    
    private long mNetworkSpeed;

    private long mPreviousTime;

    private long mTotalTime;

    private Throwable mError = null;

    private boolean mInterrupt = false;

	private long downloadId = -1;

	private boolean mCheckLink;

    private DownloadManager mDownloadManager;

    private final class ProgressReportingRandomAccessFile extends RandomAccessFile {
        private int progress = 0;

        public ProgressReportingRandomAccessFile(File file, String mode)
                throws FileNotFoundException {
            super(file, mode);
        }

        @Override
        public void write(byte[] buffer, int offset, int count) throws IOException {
            super.write(buffer, offset, count);
            progress += count;
            //mTotalSize = progress;
            publishProgress(progress);
        }
    }

    public DownloadTask(
    		Context context, 
    		DownloadManager dnMgr, 
    		long id, 
    		String url, 
    		String name, 
    		String path, 
    		String audioExt, 
    		String type,
    		DownloadTaskListener listener, 
    		boolean checkLink) throws MalformedURLException {
    	
        super();
        this.mUrlString = url;
        this.mListener = listener;
        this.mURL = new URL(url);
        this.mCheckLink = checkLink;
        
        if (TextUtils.isEmpty(name))
            name = new File(mURL.getFile()).getName();
        
        this.mAudioExt = audioExt;
        this.mType = type;
        
        this.mFile = new File(path, name);
        this.mTempFile = new File(path, name + TEMP_SUFFIX);
        
        this.mContext = context;
        this.downloadId = id;
        mPath = path;
        mDownloadManager = dnMgr;
    }

    public Context getContext() {
        return this.mContext;
    }

    public String getUrl() {
        return mUrlString;
    }

    public String getPath() {
        return this.mPath;
    }

    public URL getURL() {
        return mURL;
    }

    public String getFilePath() {
        return mFile.getAbsolutePath();
    }
    
    public boolean isInterrupt() {
        return mInterrupt;
    }

    public long getDownloadPercent() {
        return mDownloadPercent;
    }
    
    public long getDownloadId() {
    	return this.downloadId;
    }

    public long getDownloadSize() {
        return mDownloadSize + mPreviousFileSize;
    }

    public long getTotalSize() {
        return mTotalSize;
    }

    public long getDownloadSpeed() {
        return this.mNetworkSpeed;
    }

    public long getTotalTime() {
        return this.mTotalTime;
    }

    public DownloadTaskListener getListener() {
        return this.mListener;
    }
    
    public String getAbsolutePath() {
    	return this.mFile.getParent();
    }
    
    public String getFileName() {
    	return this.mFile.getName();
    }
    
    public String getType() {
    	return this.mType;
    }
    
    public String getAudioExt() {
    	return this.mAudioExt;
    }

    @Override
    protected void onPreExecute() {
        mPreviousTime = System.currentTimeMillis();
        if (mListener != null)
            mListener.preDownload(this);
    }

    @Override
    protected Long doInBackground(String... params) {

        long id = Thread.currentThread().getId();
        mDownloadManager.mThreads.put(id, 1);
        long result = -1;
        try {
            result = download();
        } catch (NetworkErrorException e) {
            mError = e;
        } catch (FileAlreadyExistException e) {
            mError = e;
        } catch (NoMemoryException e) {
            mError = e;
        } catch (IOException e) {
            mError = e;
        } catch (SpecifiedUrlIsNotFoundException e) {
            mError = e;
        } catch (OtherHttpErrorException e) {
            mError = e;
        } catch (InvalidYoutubeLinkException e) {
        	mError = e;
		} finally {
            if (mConnection != null) {
                mConnection.disconnect();
                mConnection = null;
            }
        }

        return result;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {

        if (progress.length > 1) {
            mTotalSize = progress[1];
            YTD.mTotalSizeMap.put(downloadId, mTotalSize);
            if (mTotalSize == -1) {
                if (mListener != null) {
                    mListener.errorDownload(this, mError);
                    return;
                }
            }
        }
        mTotalTime = System.currentTimeMillis() - mPreviousTime;
		   
        mDownloadSize = progress[0];
        YTD.mDownloadSizeMap.put(downloadId, mDownloadSize + mPreviousFileSize);
        
        if (mTotalSize == 0) {
            mDownloadPercent = -1;
        } else {
            mDownloadPercent = (mDownloadSize + mPreviousFileSize) * 100 / mTotalSize;
        }
        YTD.mDownloadPercentMap.put(downloadId, mDownloadPercent);
        
        mNetworkSpeed = mDownloadSize / mTotalTime;
        YTD.mNetworkSpeedMap.put(downloadId, mNetworkSpeed);
        if (mListener != null)
            mListener.updateProcess(this);
    }

    @Override
    protected void onPostExecute(Long result) {
        if (result == -1 || mInterrupt || mError != null) {
            if (DEBUG && mError != null) {
                Utils.logger("w", "Download failed. " + mError.getMessage(), DEBUG_TAG);
            }
            if (mListener != null) {
                mListener.errorDownload(this, mError);
            }
            mDownloadManager.notifyCompleted();
            return;
        }
        // finish download
        mTempFile.renameTo(mFile);
        if (mListener != null) {
            mListener.finishDownload(this);
        }
        mDownloadManager.notifyCompleted();
    }

    @Override
    public void onCancelled() {
        super.onCancelled();
        mInterrupt = true;
    }

    private long download() throws NetworkErrorException, IOException,
            SpecifiedUrlIsNotFoundException, FileAlreadyExistException, 
            NoMemoryException, OtherHttpErrorException, InvalidYoutubeLinkException {

        if (DEBUG) {
            Utils.logger("v", "totalSize: " + mTotalSize, DEBUG_TAG);
        }

        /*
         * check network
         */
        if (!NetworkUtils.isNetworkAvailable(mContext)) {
            throw new NetworkErrorException("Network blocked.");
        }

        /*
         * check expire time
         */
        if (mCheckLink) {
        	long exp = NetworkUtils.findLinkExpireTime(mUrlString);
        	Utils.logger("i", "link expires at: " + exp, DEBUG_TAG);
        	long ct = System.currentTimeMillis() / 1000;
        	Utils.logger("i", "current time is: " + ct, DEBUG_TAG);
        	
        	if (ct > exp - 120 /* 2 min as buffer */) {
        		throw new InvalidYoutubeLinkException("Youtube link expired.");
        	}
        }
        
        /*
         * check ip
         */
        if (mCheckLink) {
	        String linkIp = NetworkUtils.findLinkIp(mUrlString);
	        Utils.logger("i", "initial request IP: " + linkIp, DEBUG_TAG);
	        String actualExtIp = NetworkUtils.getExternalIpAddress();
	        Utils.logger("i", "current request IP: " + actualExtIp, DEBUG_TAG);
	        
			if (!linkIp.equals(actualExtIp) || actualExtIp == null) {
				throw new InvalidYoutubeLinkException("IP is different from initial request.");
			}
        }
        
		/*
         * check file length
         */
        String userAgent = "Mozilla/5.0 (X11; Linux i686; rv:10.0) Gecko/20100101 Firefox/10.0"; //NetworkUtils.getUserAgent(mContext);
        mConnection = (HttpURLConnection) mURL.openConnection();
        mConnection.setRequestMethod("GET");
        mConnection.setRequestProperty("User-Agent", userAgent);
        mConnection.setRequestProperty("Accept-Encoding", "identity");
        if (mTempFile.exists()) {
            mPreviousFileSize = mTempFile.length();
            mConnection.setRequestProperty("Range", "bytes=" + mPreviousFileSize + "-");
        }
        mConnection.connect();

        int responseCode = mConnection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
            throw new SpecifiedUrlIsNotFoundException("Not found: " + mUrlString);
        } else if (responseCode != HttpURLConnection.HTTP_OK
                && responseCode != HttpURLConnection.HTTP_PARTIAL) {
            String responseCodeString = Integer.toString(responseCode);
            throw new OtherHttpErrorException("http error code: " + responseCodeString, responseCodeString);
        }

        boolean isRangeDownload = false;
        int length = mConnection.getContentLength();
        if (responseCode == HttpURLConnection.HTTP_PARTIAL) {
            length += mPreviousFileSize;
            isRangeDownload = true;
        }

        if (mFile.exists() && length == mFile.length()) {
            if (DEBUG) {
                Utils.logger("w", "Output file already exists. Skipping download.", DEBUG_TAG);
            }
            throw new FileAlreadyExistException("Output file already exists. Skipping download.");
        }

        /*
         * check memory
         */
        String mFileStorage = StorageUtils.findStoragePath(mFile);
        long storageSpace = StorageUtils.getAvailableStorage(mFileStorage);
        
        if (DEBUG) {
            Utils.logger("v", "storageSpace:" + storageSpace + " total file size:" + length, DEBUG_TAG);
        }

        if (length - mPreviousFileSize > storageSpace) {
            throw new NoMemoryException("SD card no memory.");
        }

        RandomAccessFile outputStream = new ProgressReportingRandomAccessFile(mTempFile, "rw");

        InputStream inputStream = mConnection.getInputStream();

        publishProgress(0, length);
        
        int bytesCopied = copy(inputStream, outputStream, isRangeDownload);

        if ((mPreviousFileSize + bytesCopied) != mTotalSize && mTotalSize != -1 && !mInterrupt) {
            throw new IOException("Download incomplete: " + bytesCopied + " != " + mTotalSize);
        }

        if (DEBUG) {
            Utils.logger("d", "Download completed successfully.", DEBUG_TAG);
        }

        return bytesCopied;
    }

    private int copy(InputStream input, RandomAccessFile output, boolean isRangeDownload) 
    		throws IOException, NetworkErrorException {

        if (input == null || output == null) {
            return -1;
        }

        byte[] buffer = new byte[BUFFER_SIZE];

        BufferedInputStream in = new BufferedInputStream(input, BUFFER_SIZE);
        if (DEBUG) {
            Utils.logger("v", "length " + output.length(), DEBUG_TAG);
        }

        int count = 0, n = 0;
        long errorBlockTimePreviousTime = -1, expireTime = 0;

        try {

            if (isRangeDownload) {
                output.seek(output.length());
            }

            while (!mInterrupt) {
                n = in.read(buffer, 0, BUFFER_SIZE);
                if (n == -1) {
                    break;
                }
                output.write(buffer, 0, n);
                count += n;

                /*
                 * check network
                 */
                if (!NetworkUtils.isNetworkAvailable(mContext)) {
                    throw new NetworkErrorException("Network blocked.");
                }

                if (mNetworkSpeed == 0) {
                    if (errorBlockTimePreviousTime > 0) {
                        expireTime = System.currentTimeMillis() - errorBlockTimePreviousTime;
                        if (expireTime > TIME_OUT) {
                            throw new ConnectTimeoutException("connection time out.");
                        }
                    } else {
                        errorBlockTimePreviousTime = System.currentTimeMillis();
                    }
                } else {
                    expireTime = 0;
                    errorBlockTimePreviousTime = -1;
                }
            }
        } finally {
            mConnection.disconnect();
            mConnection = null;
            output.close();
            in.close();
            input.close();
        }
        return count;
    }

    public void cancel() {
        this.cancel(true);
        mInterrupt = true;
        Utils.logger("d", "cancel on id " + downloadId, DEBUG_TAG);
    }
}
