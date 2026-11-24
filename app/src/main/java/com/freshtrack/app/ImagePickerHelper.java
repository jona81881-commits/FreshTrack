package com.freshtrack.app;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class ImagePickerHelper {

    private static final String TAG = "ImagePickerHelper";
    private static final String IMAGE_DIR = "food_images";

    public static String saveImageToInternalStorage(Context context, Uri sourceUri) {
        try {
            File dir = new File(context.getFilesDir(), IMAGE_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fileName = "food_" + System.currentTimeMillis() + ".jpg";
            File destinationFile = new File(dir, fileName);

            InputStream in = context.getContentResolver().openInputStream(sourceUri);
            if (in == null) return null;

            OutputStream out = new FileOutputStream(destinationFile);
            byte[] buffer = new byte[4096];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            out.flush();
            out.close();
            in.close();

            Log.d(TAG, "Saved food image successfully: " + destinationFile.getAbsolutePath());
            return destinationFile.getAbsolutePath();
        } catch (Exception e) {
            Log.e(TAG, "Failed saving food image: " + e.getMessage(), e);
            return null;
        }
    }

    public static boolean deleteImage(String path) {
        if (path == null || path.isEmpty()) return false;
        try {
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                return file.delete();
            }
        } catch (Exception ignored) {
        }
        return false;
    }
}
