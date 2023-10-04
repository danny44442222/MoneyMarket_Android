package com.buzzware.monymarket.classes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageCapture {

    final int REQUEST_IMAGE_CAPTURE = 1;

    AlertDialog alertDialog;

    String currentPhotoPath;

    private File createImageFile(Activity context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, new Date().toString(), null);

        return Uri.parse(path);
    }

    public void showImagePickerDialog(Activity context, ActivityResultLauncher<Intent> dispatchTakePictureLauncher, ActivityResultLauncher<Intent> galleryLauncher) {

        // setup the alert builder

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        builder.setTitle("Choose");

        // add a list

        String[] animals = {"Camera", "Gallery"};
        builder.setItems(animals, (dialog, which) -> {
            switch (which) {
                case 0:
                    dispatchTakePictureIntent(context, dispatchTakePictureLauncher);
                    break;
                case 1:
                    openGallery(galleryLauncher);
                    break;
            }
        });

        // create and show the alert dialog

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void openGallery(ActivityResultLauncher<Intent> galleryLauncher) {

        Intent intent = new Intent();

        intent.setType("image/*");

        intent.setAction(Intent.ACTION_GET_CONTENT);

        galleryLauncher.launch(intent);
    }

    private void dispatchTakePictureIntent(Activity context, ActivityResultLauncher<Intent> dispatchTakePictureLauncher) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile(context);
            } catch (IOException ex) {
                // Error occurred while creating the File

                showErrorAlert("Unable to capture image. Please check your storage permissions from settings.", context);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(context,
                        "com.buzzware.monymarket.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                dispatchTakePictureLauncher.launch(takePictureIntent);
            }
        }
    }


    public void showErrorAlert(String msg, Activity context) {

        if (alertDialog != null && alertDialog.isShowing())

            alertDialog.dismiss();

        alertDialog = new AlertDialog.Builder(context)
                .setMessage(msg)
                .setTitle("Alert")
                .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss())
                .create();

        alertDialog.show();
    }

    private void galleryAddPic(Activity context) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    public Uri getImageCapturedUri() {

        File f = new File(currentPhotoPath);

        Uri contentUri = Uri.fromFile(f);

        return contentUri;
    }
}
