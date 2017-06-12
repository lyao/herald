package org.church.volyn.downloadHelper;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by user on 23.09.2015.
 */
public class MediaFtpClient {
    private FTPClient mFtpClient;

    public MediaFtpClient() {
        mFtpClient = new FTPClient();
    }

    public void connnectingwithFTP(String serverName, String userName, String pass) {
        boolean status = false;
        try {
//            mFtpClient = new FTPClient();
            mFtpClient.setConnectTimeout(10 * 1000);
            mFtpClient.connect(InetAddress.getByName(serverName));
            status = mFtpClient.login(userName, pass);
            if (FTPReply.isPositiveCompletion(mFtpClient.getReplyCode())) {
                mFtpClient.setFileType(FTP.ASCII_FILE_TYPE);
                mFtpClient.enterLocalPassiveMode();
//                FTPFile[] mFileArray = mFtpClient.listFiles();
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readFileFromFTP(String remoteFilePath) {
        StringBuilder total = new StringBuilder();
        try {
            InputStream inputStream = mFtpClient.retrieveFileStream(remoteFilePath);
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
            total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);}
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total.toString();
    }

}
