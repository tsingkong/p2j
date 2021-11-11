package p2j.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.prashanth.project3.R;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by prashanth on 7/17/15.
 */
public class Rowelements extends ArrayAdapter<String> {
    Context context;
    LayoutInflater inflater=null;
    String[] file_name_list;
    public Rowelements(Context context, int resource,String[] file_name_list) {
        super(context, resource);
        this.context=context;
        this.file_name_list=file_name_list;
    }

    @Override
    public int getCount() {
        return this.file_name_list.length;
    }

    /**********************************************************************************/
    @Override
    public String getItem(int position) {
        return null;
    }

    /**********************************************************************************/
    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(inflater==null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
                convertView = inflater.inflate(R.layout.custom_row, parent,false);
        }


        ImageView icon_view = (ImageView)convertView.findViewById(R.id.icon);
        TextView file_name_view = (TextView)convertView.findViewById(R.id.file_name);

        String file_name = file_name_list[position];
        file_name_view.setText(file_name);
        if(!(file_name.toLowerCase().endsWith(".pdf")||file_name.toLowerCase().endsWith(".jpg"))){
            icon_view.setImageResource(R.drawable.folder);
        }else if(file_name.toLowerCase().endsWith(".pdf")){
            icon_view.setImageResource(R.drawable.pdf_icon);
        }else if(file_name.toLowerCase().endsWith(".jpg")){
            icon_view.setImageResource(R.drawable.jpg);
        }
        return convertView;
    }
}
