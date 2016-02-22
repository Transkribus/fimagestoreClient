import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.dea.fimgstoreclient.FimgStoreGetClient;
import org.dea.fimgstoreclient.beans.FimgStoreImg;
import org.dea.fimgstoreclient.beans.ImgType;
import org.dea.fimgstoreclient.utils.FimgStoreUriBuilder;

public class FimgStoreGetClientTest {

	public static void main(String[] args) {
		final String testImgKey = "DYQLMRPHLXBKCQFRXBMKXRTF";
		FimgStoreGetClient fisc = new FimgStoreGetClient("dbis-thure.uibk.ac.at", "fimagestore");

		FimgStoreImg result = null;
		File download = null;

		try {
			final URI uri = (new FimgStoreUriBuilder("https", "dbis-thure.uibk.ac.at", null, "/fimagestore")).getFileUri(testImgKey);
			System.out.println("UriBuilder test: " + uri.toString());
			download = fisc.saveFile(uri, "/tmp/"); //TODO get stream here rather than having client save stuff
			result = fisc.getImg(testImgKey, ImgType.view);
		} catch (IllegalArgumentException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// image metadata testing:
		try {
			System.out.println(fisc.getFileMd(testImgKey));
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(result.toString());
		System.out.println(download.getAbsolutePath());		
	}
}
