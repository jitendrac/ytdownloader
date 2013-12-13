/*
 * code adapted from download manager project by 
 * Hiroshi Matsunaga (matsuhiro): 
 * https://github.com/matsuhiro/AndroidDownloadManger
 * (released "unlicensed").
 */

package dentex.youtube.downloader.dm;

public class OtherHttpErrorException extends DownloadException {

    private static final long serialVersionUID = 1L;

    public OtherHttpErrorException(String message) {
        super(message);
    }

    public OtherHttpErrorException(String message, String extra) {
        super(message, extra);
    }
}
