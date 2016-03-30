package com.tywors.lolvideos.activitys;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tywors.lolvideos.data.GetUniqueID;
import com.tywors.lolvideos.utils.MultipartUtility;
import com.tywors.lolvideos.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Lenovo on 03/12/2015.
 */
public class CreateUser_Activity extends AppCompatActivity {

    private static final int STATIC_IMAGE_SELECT = 10004;
    private ImageView imageView_profile;
    private Button bt_send;
    private EditText ed_nick;
    private EditText ed_email;
    private String nick;
    private String email;

    private String URL_ = "http://tywors.com/apps/lolvideos/";

    private Bitmap selectedBitmap;
    private boolean b_image = false;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        imageView_profile = (ImageView)findViewById(R.id.img_create_profile);
        ed_nick = (EditText)findViewById(R.id.ed_create_user_nick);
        ed_email = (EditText)findViewById(R.id.ed_create_user_email);
        bt_send = (Button)findViewById(R.id.bt_create_user);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.icon_default_profile_big);
        imageView_profile.setImageBitmap(getCircleBitmap(largeIcon));



        imageView_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                photoPickerIntent.putExtra("crop", "true");
                photoPickerIntent.putExtra("aspectX", 1);
                photoPickerIntent.putExtra("aspectY", 1);
                photoPickerIntent.putExtra("outputX", 280);
                photoPickerIntent.putExtra("outputY", 280);
                photoPickerIntent.putExtra("scaleUpIfNeeded", true);
                photoPickerIntent.putExtra("scale", true);
                startActivityForResult(photoPickerIntent, STATIC_IMAGE_SELECT);
            }
        });

        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean b_nick = false;
                boolean b_email = false;

                if(ed_nick.getText().toString().length()<4){
                    _Toast(getApplicationContext().getResources().getString(R.string.reg_error_4));
                    b_nick = false;
                }else{b_nick=true;}

                if(ed_email.getText().toString().length()>4 & emailValidator(ed_email.getText().toString().trim())){
                    b_email = true;
                }else{
                    b_email=false;
                    _Toast(getApplicationContext().getResources().getString(R.string.reg_error_5));
                }

                if(!b_image){
                    _Toast(getApplicationContext().getResources().getString(R.string.reg_error_6));
                }


                if(b_nick & b_email & b_image){
                    nick = ed_nick.getText().toString();
                    email = ed_email.getText().toString();

                    dialog = new Dialog(CreateUser_Activity.this,R.style.ThemeDialogCustom);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                    //you can move the dialog, so that is not centered
                    // dialog.getWindow().getAttributes().y = 50; //50 should be based on density

                    dialog.setContentView(R.layout.layout_loading);
                    dialog.setCancelable(true);
                    //dialog.setOnCancelListener(cancelListener);
                    dialog.show();
                    new RegisterUser().execute(selectedBitmap);
                }
            }
        });

    }

    public boolean emailValidator(String email)
    {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public void _Toast(String sms) {
        Toast toast2 = Toast.makeText(getApplicationContext(), sms, Toast.LENGTH_LONG);
        View view2 = toast2.getView();
        view2.setBackgroundResource(R.drawable.style_toast);
        TextView text2 = (TextView) view2.findViewById(android.R.id.message);
        text2.setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getApplicationContext().getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getApplicationContext().getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getApplicationContext().getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getApplicationContext().getResources().getDisplayMetrics()));
        text2.setTypeface(Typeface.DEFAULT_BOLD);
        text2.setTextColor(Color.WHITE);
        toast2.show();
    }

    public static String getRealFilePath(final Context context, final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if
                ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {

            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Video.VideoColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {

                    int index = cursor.getColumnIndex( MediaStore.Video.VideoColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case STATIC_IMAGE_SELECT:
                if (resultCode == RESULT_OK) {
                    /**if (imageReturnedIntent!=null) {
                        Bundle extras = imageReturnedIntent.getExtras();
                        selectedBitmap = (Bitmap)extras.get("data");
                        imageView_profile.setImageBitmap(getCircleBitmap(selectedBitmap));
                        b_image = true;
                        SaveImageProfile(selectedBitmap);
                    }else{
                        b_image = false;
                    }*/
                    try{
                        Bundle extras = imageReturnedIntent.getExtras();
                        selectedBitmap = (Bitmap)extras.get("data");
                        imageView_profile.setImageBitmap(getCircleBitmap(selectedBitmap));
                        b_image = true;
                        SaveImageProfile(selectedBitmap);
                    }catch (Exception e){
                        Uri selectedImageURI = imageReturnedIntent.getData();
                        InputStream input = null;
                        try {
                            input = getApplicationContext().getContentResolver().openInputStream(selectedImageURI);
                            Bitmap d = BitmapFactory.decodeStream(input , null, null);
                            imageView_profile.setImageBitmap(getCircleBitmap(d));
                            b_image = true;
                            SaveImageProfile(d);
                        } catch (FileNotFoundException w) {
                            w.printStackTrace();
                        }

                    }




                }
        }
    }
//URL_+"andro_register_user.php?id_device="+id_device+"&nick="+nick+"&email="+email

    private void SaveImageProfile(Bitmap btm){
        String path = Environment.getExternalStorageDirectory().toString();
        OutputStream fOutputStream = null;
        File myDir = new File(path + "/LoLvideos");
        myDir.mkdirs();
        File file = new File(path + "/LoLvideos/", "profile.jpg");
        try {
            fOutputStream = new FileOutputStream(file);

            btm.compress(Bitmap.CompressFormat.JPEG, 100, fOutputStream);
            fOutputStream.flush();
            fOutputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    class RegisterUser extends AsyncTask<Bitmap,Void,String> {

        private String line;

        public RegisterUser(){
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Bitmap... params) {
            MultipartUtility multipart = null;
            List<String> response = null;
            try {
                multipart = new MultipartUtility(URL_+"andro_register_user.php", "UTF-8");
                //multipart.addHeaderField("User-Agent", "CodeJava");
                //multipart.addHeaderField("Test-Header", "Header-Value");

                multipart.addFormField("id_device", GetUniqueID.getDeviceId(getApplicationContext()));
                multipart.addFormField("nick", nick);
                multipart.addFormField("email", email);

                String path = Environment.getExternalStorageDirectory().toString();

                    File f = new File(path + "/LoLvideos/", "profile.jpg");
                    multipart.addFilePart("uploadedfile", f);

                //multipart.addFilePart("fileUpload", uploadFile2);

                response = multipart.finish();

            } catch (IOException e) {
             e.printStackTrace();
            }
            return response.get(0);
        }

        @Override
        protected void onPostExecute(String result) {
             //_Toast(result+"ww");
            dialog.dismiss();
            if(result.equals("0")){
                _Toast(getApplicationContext().getResources().getString(R.string.reg_error_0));
            }else if(result.equals("1")){
                _Toast(getApplicationContext().getResources().getString(R.string.reg_error_1));
            }else if(result.equals("2")){
                _Toast(getApplicationContext().getResources().getString(R.string.reg_error_2));
            }else if(result.equals("3")){
                _Toast(getApplicationContext().getResources().getString(R.string.reg_error_3));
            }else if(result.equals("10")) {
                _Toast(getApplicationContext().getResources().getString(R.string.reg_error_10));
                finish();
            }
        }

    }
















    private Bitmap getCircleBitmap(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int radius = Math.min(h / 2 - 4, w / 2 - 4);
        Bitmap output = Bitmap.createBitmap(w + 8, h + 8, Bitmap.Config.ARGB_8888);

        Paint p = new Paint();
        p.setAntiAlias(true);

        Canvas c = new Canvas(output);
        c.drawARGB(0, 0, 0, 0);
        p.setStyle(Paint.Style.FILL);

        c.drawCircle((w / 2) + 4, (h / 2) + 4, radius, p);

        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        c.drawBitmap(bitmap, 4, 4, p);
        p.setXfermode(null);
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.rgb(255, 153, 0));
        p.setStrokeWidth(5);
        c.drawCircle((w / 2) + 4, (h / 2) + 4, radius, p);

        return output;
    }

}