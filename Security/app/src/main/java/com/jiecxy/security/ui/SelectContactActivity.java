package com.jiecxy.security.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jiecxy.security.R;
import com.jiecxy.security.domain.ContactInfo;
import com.jiecxy.security.engine.ContactInfoService;

import java.util.List;

/**
 * Created by apple on 15-1-29.
 */

public class SelectContactActivity extends Activity
{
    private ListView lv;
    private List<ContactInfo> infos;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_contact);

        infos = new ContactInfoService(this).getContactInfos();

        lv = (ListView) findViewById(R.id.lv_select_contact);
        lv.setAdapter(new SelectContactAdapter());
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String number = infos.get(position).getPhone();
                Intent intent = new Intent();
                intent.putExtra("number", number);

                //把要返回的数据设置进去，便通过onActivityResult(int, int, Intent)拿到
                setResult(1, intent);

                finish();
            }
        });
    }

    //=================================================================================

    private class SelectContactAdapter extends BaseAdapter
    {

        @Override
        public int getCount()
        {
            return infos.size();
        }

        @Override
        public Object getItem(int position)
        {
            return infos.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ContactInfo info = infos.get(position);
            View view = null;

            if(convertView == null)
            {
                view = View.inflate(SelectContactActivity.this, R.layout.contact_item, null);
            }
            else
            {
                view = convertView;
            }
            ((TextView) view.findViewById(R.id.tv_contact_name)).setText(infos.get(position).getName());
            ((TextView) view.findViewById(R.id.tv_contact_number)).setText("  " + infos.get(position).getPhone());

            return view;
        }

    }

    private class ContactViews
    {
        TextView tv_name;
        TextView tv_number;
    }

}