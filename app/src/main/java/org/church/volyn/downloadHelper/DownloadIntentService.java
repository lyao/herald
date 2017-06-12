package org.church.volyn.downloadHelper;

import android.app.IntentService;
import android.content.Intent;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.church.volyn.parser.XmlPullNewsParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Admin on 26.03.2015.
 */
public class DownloadIntentService extends IntentService {
    private static final boolean USING_ASSETS = false;
    private static boolean FIRST_TIME = true;
    private static boolean isRunning = false;
    private static String md5;

    public DownloadIntentService() {
        super(DownloadIntentService.class.getName());
        setIntentRedelivery(true);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        isRunning = true;
        if (USING_ASSETS) {
//            InputStream data;
//            try {
//                if (FIRST_TIME) {
//                    data = getApplicationContext().getAssets().open("feed.xml");
//                    FIRST_TIME = false;
//                } else {
//                    data = getApplicationContext().getAssets().open("feed1.xml");
//                }
//                XmlPullNewsParser.parseFeedItem(getApplicationContext(), data);
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

        } else {

            HttpClient httpClient = RetryingHttpClient.getHttpClient();
            String path = "http://volyn.church.ua/feed/";
            HttpGet request = new HttpGet(path);

            HttpResponse response = null;
            try {
                response = httpClient.execute(request);
                InputStream data = response.getEntity().getContent();
//
//                md5 = getCheckSum(data);
//                Log.d("md5", md5);
//                XmlPullNewsParser.parseFeedItem(getApplicationContext(), data);
                String strInput = inputStreamToString(data);
               //TODO md5 = getMD5EncryptedString(strInput);
                XmlPullNewsParser.parseFeedItem(getApplicationContext(), strInput);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




    public String inputStreamToString(InputStream is1)
    {
        BufferedReader rd = new BufferedReader(new InputStreamReader(is1), 4096);
        String line;
        StringBuilder sb =  new StringBuilder();
        try {
            while ((line = rd.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            rd.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static String getMD5EncryptedString(String encTarget) {
        MessageDigest mdEnc = null;
        try {
            mdEnc = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Exception while encrypting to md5");
            e.printStackTrace();
        } // Encryption algorithm
        mdEnc.update(encTarget.getBytes(), 0, encTarget.length());
        String md5 = new BigInteger(1, mdEnc.digest()).toString(16);
        while (md5.length() < 32) {
            md5 = "0" + md5;
        }
        return md5;
    }

    public static String getCheckSum(InputStream is) {
        String checksum = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            //Using MessageDigest update() method to provide input
            byte[] buffer = new byte[8192];
            int numOfBytesRead;
            while ((numOfBytesRead = is.read(buffer)) > 0) {
                md.update(buffer, 0, numOfBytesRead);
            }
            byte[] hash = md.digest();
            checksum = new BigInteger(1, hash).toString(16); //don't use this, truncates leading zero
            while (checksum.length() < 32) {
                checksum = "0" + checksum;
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return checksum;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
    }

    public static boolean isRunning() {
        return isRunning;
    }
}
