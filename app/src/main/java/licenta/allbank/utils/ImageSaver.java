package licenta.allbank.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.IOException;
import java.io.OutputStream;

import licenta.allbank.utils.messages.ImageMessage;
import licenta.allbank.utils.messages.MessageDisplay;

public class ImageSaver {

    /**
     * Function that saves an image in Directory pictures
     * @param context     Context object where the function was called from
     * @param bitmapImage Bitmap image that is being saved
     * @param imageName   Name of the new image
     */
    public static void saveImage(Context context, Bitmap bitmapImage, String imageName) {
        OutputStream fileOutputStream = null;
        boolean error = false;
        try {
            ContentResolver resolver = context.getContentResolver();
            ContentValues contentValues = new ContentValues();

            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, imageName);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

            Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            fileOutputStream = resolver.openOutputStream(uri);

            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
            error = true;
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (error) {
            MessageDisplay.showLongMessage(context, ImageMessage.ERROR_IMAGE_NOT_SAVED);
        } else {
            MessageDisplay.showLongMessage(context, ImageMessage.IMAGE_SAVED);
        }
    }
}
