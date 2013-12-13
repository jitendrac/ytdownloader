/*
 * code adapted from download manager project by 
 * Hiroshi Matsunaga (matsuhiro): 
 * https://github.com/matsuhiro/AndroidDownloadManger
 * (released "unlicensed").
 */

package dentex.youtube.downloader.dm;

public class FileAlreadyExistException extends DownloadException {

    private static final long serialVersionUID = 1L;

    public FileAlreadyExistException(String message) {

        super(message);
    }

}
