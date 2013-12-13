
package dentex.youtube.downloader.dm;

public class InvalidYoutubeLinkException extends DownloadException {
	
	private static final long serialVersionUID = 1L;

    public InvalidYoutubeLinkException(String message) {

        super(message);
    }
}
