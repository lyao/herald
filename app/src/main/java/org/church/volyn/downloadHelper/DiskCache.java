package org.church.volyn.downloadHelper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

/**
 * Created by user on 17.02.2015.
 */
public class DiskCache {


    private static File filesDir;
    private static DiskCache sFileCache;
    private static Context context;

    public static DiskCache getInstance(Context context) {
        if (sFileCache == null) {
            sFileCache = new DiskCache(context);
        }
        return sFileCache;
    }

    private DiskCache(Context context) {
        //Find the dir to save cached images
//        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
//            filesDir = new File(android.os.Environment.getExternalStorageDirectory(), "TTImages_cache");
//        else
//            filesDir = context.getCacheDir();
        this.context = context;
        filesDir = context.getFilesDir();
//        if (!filesDir.exists())
//            filesDir.mkdirs();
    }

    public static File getFile(String imgUrl) {
        String filename = null;
        File f = null;
        filename = getFileName(imgUrl);
        if (filename == null) return null;
        f = new File(getFilesDir(), filename);
        return f;
    }

    public static File getFilesDir() {
        return filesDir;
    }

    public static String getFileName(String imgUrl) {

        if ((imgUrl == null) || (imgUrl.isEmpty())) return null;
        String urlPrefix;
        if (imgUrl.indexOf(".jpg") > 0)
            urlPrefix = imgUrl.substring(0, imgUrl.indexOf(".jpg"));
        else if (imgUrl.indexOf(".jpeg") > 0)
            urlPrefix = imgUrl.substring(0, imgUrl.indexOf(".jpeg"));
        else if (imgUrl.indexOf(".png") > 0)
            urlPrefix = imgUrl.substring(0, imgUrl.indexOf(".png"));
        else
            urlPrefix = imgUrl.substring(0, imgUrl.indexOf(".gif"));
        StringBuffer fileName = new StringBuffer(urlPrefix);
        fileName.append(".png");

        return fileName.toString().replace("http://", "").replace("/", "^");
    }

    public static String getFilePath(String imgUrl) {
        return getFilesDir().toString() + "/" + getFileName(imgUrl);
    }

    public Bitmap getBitmap(String imgUrl) {
        File f = this.getFile(imgUrl);
        if (f == null) return null;
        Bitmap bitmap = decodeFile(f);
        return bitmap;
    }


    public void saveBitmap(String url, Bitmap bitmap) {
//        File imagefile = getFile(newsItem.getImageUrl());
        File imagefile = getFile(url);
        if (imagefile == null) return;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imagefile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveJsonToFile(String filename, String jsonString) {

        FileOutputStream outputStream;

        try {
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(jsonString.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


//        try {
//            FileWriter fileWriter = new FileWriter(new File(getFilesDir(), fileName));
//            fileWriter.write(jsonString);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public String readJsonFromFile(String fileName) {

        File file = new File(getFilesDir(), fileName);
        FileInputStream stream = null;


        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
        }
        return text.toString();

    }

    private Bitmap decodeFile(File f) {
        try {
            //decode image size
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, bitmapOptions);

            //Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 150;
            int width_tmp = bitmapOptions.outWidth, height_tmp = bitmapOptions.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    public boolean bitmapIsSaved(String url) {
        File file = getFile(url);
        if (file == null)
            return false;
        if (!file.exists())
            return false;
        return true;
    }

    public void clear() {
        File[] files = filesDir.listFiles();
        if (files == null)
            return;
        for (File f : files)
            f.delete();
    }

    public long sizeOfFile(String url) {
        File file = getFile(url);
        if (file == null)
            return 0;
        else
            return file.length();
    }


}
