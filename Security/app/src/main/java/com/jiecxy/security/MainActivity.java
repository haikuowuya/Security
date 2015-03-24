package com.jiecxy.security;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.jiecxy.security.adapter.MainUIAdapter;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener
{
    private GridView gridView;

    private MainUIAdapter adapter ;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        sp = this.getSharedPreferences("config", Context.MODE_PRIVATE);
        gridView = (GridView) findViewById(R.id.gv_main);
        adapter = new MainUIAdapter(this);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id)
            {

                if(position == 0)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("设置");
                    builder.setMessage("请输入要理性的名称");

                    final EditText et = new EditText(MainActivity.this);
                    et.setHint("新名称");

                    builder.setView(et);
                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            String name = et.getText().toString();
                            if(name.equals(""))
                            {
                                Toast.makeText(MainActivity.this, "输入内容不能为空", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("lostName", name);
                                editor.commit();    //提交

                                TextView tv = (TextView) view.findViewById(R.id.tv_main_name);
                                tv.setText(name);
                                adapter.notifyDataSetChanged();  //通知activity动态刷新
                            }
                        }
                    });
                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            // TODO Auto-generated method stub

                        }
                    });
                    builder.create().show();
                }
                return false;
            }
        });
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        switch(position)
        {
            case 0 : //手机防盗
                break;

            case 1 : //通讯卫士
                break;

            case 2 : //软件管理
                break;

            case 3 : //流量管理
                break;

            case 4 : //任务管理
                break;

            case 5 : //手机杀毒
                break;

            case 6 : //系统优化
                break;

            case 7 : //高级工具
                break;

            case 8 : //设置中心
                break;

            default :
                break;
        }
    }

}
