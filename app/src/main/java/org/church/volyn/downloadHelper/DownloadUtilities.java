package org.church.volyn.downloadHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by user on 11.12.2014.
 */
public class DownloadUtilities {
    public static String readResponse(InputStream data) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(data));
        StringBuilder sb = new StringBuilder();

        try {
            String line = reader.readLine();
            while (line != null) {
                sb.append(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            throw e;
        } finally {
            reader.close();
        }
        return sb.toString();
    }
}
