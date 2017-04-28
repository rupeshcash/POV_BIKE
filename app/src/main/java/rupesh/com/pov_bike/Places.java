package rupesh.com.pov_bike;

/**
 * Created by rupesh kashyap on 4/15/2017.
 */

        import android.os.Environment;

import java.io.File;
import java.io.IOException;

public class Places {
    public static File getScreenshotFolder() {
        File path = new File(Environment.getExternalStorageDirectory(),
                "/Acrylic Paint/");
        path.mkdirs();

        return path;
    }

    public static File getCameraTempFolder() {
        File path = new File(Environment.getExternalStorageDirectory(),
                "/Acrylic Paint/Temp/");
        path.mkdirs();
        // this folder should not be scanned
        File noScanning = new File(path, ".nomedia");
        if (!noScanning.exists())
            try {
                noScanning.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        return path;
    }

    public static File getCameraTempFile() {
        return new File(getCameraTempFolder(), "temp.jpg");
    }
}