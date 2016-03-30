package com.tywors.lolvideos.activitys;

import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tywors.lolvideos.Main_Principal;
import com.tywors.lolvideos.R;
import com.tywors.lolvideos.data.GetUniqueID;
import com.tywors.lolvideos.utils.CustomMultiPartEntity;
import com.tywors.lolvideos.utils.MultipartUtility;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
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
public class Uploading_intent_activity extends AppCompatActivity {

    private ProgressBar pgrs_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_received_intentfilter);
        Intent url = getIntent();


        pgrs_bar = (ProgressBar)findViewById(R.id.progressBar_intent_upload);

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("video/")) {
                handleSendVideo(intent); // Handle single image being sent
            }else{
                Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_LONG).show();
            }
        }else {
            // Handle other intents, such as being started from the home screen
            Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_LONG).show();
        }
    }

    void handleSendVideo(Intent intent) {
        String filePath = null;
        Uri _uri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (_uri != null && "content".equals(_uri.getScheme())) {
            Cursor cursor = this.getContentResolver().query(_uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
            cursor.moveToFirst();
            filePath = cursor.getString(0);
            cursor.close();
        } else {
            filePath = _uri.getPath();
        }

        new UploadVideo().execute(filePath);

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


    ////////////////////////////////////////
    ///////////////upload////////////////////////
    /////////////////////////////
    private class UploadVideo extends AsyncTask<String, Integer, String> {

        long totalSize;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext httpContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost("http://tywors.com/apps/lolvideos/andro_upload_video.php?id_device="+GetUniqueID.getDeviceId(getApplicationContext()));

            try {
                CustomMultiPartEntity multipartContent = new CustomMultiPartEntity(new CustomMultiPartEntity.ProgressListener() {
                    @Override
                    public void transferred(long num) {
                        publishProgress((int) ((num / (float) totalSize) * 100));
                    }
                });

                // We use FileBody to transfer an image
                multipartContent.addPart("uploadedfile", new FileBody(new File(params[0])));
                totalSize = multipartContent.getContentLength();

                // Send it
                httpPost.setEntity(multipartContent);
                HttpResponse response = httpClient.execute(httpPost, httpContext);
                String serverResponse = EntityUtils.toString(response.getEntity());

                //ResponseFactory rp = new ResponseFactory(serverResponse);
                return "w";
            } catch (Exception e) {
                System.out.println(e);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            pgrs_bar.setProgress((int) (progress[0]));
        }

        @Override
        protected void onPostExecute(String ui) {
            //Intent i = new Intent(Uploading_intent_activity.this, Main_Principal.class);
            //i.putExtra("tipo", 4);
            //startActivity(i);
            _Toast(getApplicationContext().getResources().getString(R.string.info_upload_finish));
            finish();
        }
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
}