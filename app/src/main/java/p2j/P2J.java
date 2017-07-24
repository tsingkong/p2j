package p2j;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import p2j.file.access.FileList;
import p2j.file.access.ImageRendererProcessor;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.prashanth.project3.R;

import p2j.file.access.image.ImageRendererAction;
import p2j.ui.Rowelements;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class P2J extends Activity
        implements AdapterView.OnItemClickListener,
        View.OnClickListener,
        AdapterView.OnItemLongClickListener {
    private ListView directory_list;
    private FileList file_list;
    private String path = "", file_path = "";
    private TextView status;
    private ImageButton convert_btn;
    private ImageButton delete_btn;
    private ImageButton up_btn;
    private Handler handler = new Handler();
    private Runnable runnable;
    private String[] array_file_names;
    private boolean is_long_click = false;
    private String externalPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final int permission = ActivityCompat.checkSelfPermission((Activity) this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        initialize();
    }

    public void initialize() {
        directory_list = (ListView) findViewById(R.id.listView);
        status = (TextView) findViewById(R.id.textView);
        convert_btn = (ImageButton) findViewById(R.id.convert_btn);
        delete_btn = (ImageButton) findViewById(R.id.delete_btn);
        up_btn = (ImageButton) findViewById(R.id.up_btn);
        file_list = new FileList();
        path = Environment.getExternalStorageDirectory().getPath();
        ArrayList<String> arrayList = file_list.getFiles(path);
        array_file_names = file_list.toArray(arrayList);
        Rowelements rows = new Rowelements(P2J.this, R.layout.custom_row, array_file_names);
        directory_list.setAdapter(rows);
        directory_list.setOnItemClickListener(this);
        directory_list.setOnItemLongClickListener(this);
        externalPath = Environment.getExternalStorageDirectory().getPath();

        convert_btn.setOnClickListener(this);
        delete_btn.setOnClickListener(this);
        up_btn.setOnClickListener(this);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Toolbar title_bar = (Toolbar) findViewById(R.id.toolbar);
        title_bar.setTitle(R.string.app_name);
        title_bar.setTitleTextColor(Color.rgb(185, 211, 238));
        title_bar.setSubtitle("PDF to JPEG conveter");
        title_bar.setSubtitleTextColor(Color.rgb(185, 211, 238));


        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getRealSize(point);
        directory_list.setBottom(point.y - mAdView.getHeight());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        file_path = path + "/" + array_file_names[position];
        final AlertDialog.Builder alert_dialog = new AlertDialog.Builder(P2J.this);
        alert_dialog.setTitle("P2J");
        alert_dialog.setMessage("Do you want delete the file " + file_path + "?");
        alert_dialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                file_list.deleteFile(file_path);
                updatePath(path);
            }
        });
        alert_dialog.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alert_dialog.setIcon(R.drawable.p2j);
        alert_dialog.show();
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int id_ = parent.getId();
        switch (id_) {
            case R.id.listView: {
                view.setBackgroundColor(Color.argb(100, 57, 73, 171));
                if (parent == null) {
                    Log.d("PDF", "Parent is null");
                }

                ArrayList<String> list = file_list.getFiles(path + "/" + array_file_names[position]);
                if (list != null) {

                    path = path + "/" + array_file_names[position];
                    array_file_names = file_list.toArray(list);

                    Rowelements rows = new Rowelements(P2J.this, R.layout.custom_row, array_file_names);
                    directory_list.setAdapter(rows);
                } else {
                    file_path = path + "/" + array_file_names[position];
                    if (array_file_names[position].endsWith(".pdf")) {
                        status.setText(file_path);
                    } else if (array_file_names[position].endsWith(".jpg")) {
                        Intent intent_image = new Intent();
                        intent_image.setAction(Intent.ACTION_VIEW);

                        File file = new File(externalPath);

                        Uri uriPath = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", new File(file_path));
                        intent_image.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent_image.setDataAndType(uriPath, "image/*");
                        startActivity(intent_image);
                    }
                }
                break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        int selected = v.getId();
        switch (selected) {
            case R.id.convert_btn:
                if (!status.getText().equals("")) {
                    Toast.makeText(getApplicationContext(), "Converting...", Toast.LENGTH_LONG).show();
                    createFolderIfNotExists(file_path);
                    timerTask();
                    ImageRendererProcessor imageRendererProcessor = new ImageRendererProcessor(file_path, handler, runnable);
                    imageRendererProcessor.execute();
                } else {
                    Toast.makeText(getApplicationContext(), "Select a File", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.up_btn:
                String up_str = file_list.upDirection(path);
                Toast.makeText(getApplicationContext(), up_str, Toast.LENGTH_LONG).show();
                updatePath(up_str);
                path = up_str;
                status.setText(path);
                break;
        }

    }

    public void updatePath(String path_temp) {
        ArrayList<String> list = file_list.getFiles(path_temp);
        if (list != null) {
            array_file_names = file_list.toArray(list);
            directory_list.setAdapter(new Rowelements(P2J.this, R.layout.custom_row, array_file_names));
        }
    }

    public void timerTask() {
        runnable = new Runnable() {
            public void run() {
                updatePath(path);
                handler.postDelayed(this, 100);
            }
        };
        runnable.run();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacks(runnable);
        finish();
    }

    @NonNull
    private String createFolderIfNotExists(String src) {
        String folderPath = src.split(".pdf")[0];
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder.getAbsolutePath();
    }
}
