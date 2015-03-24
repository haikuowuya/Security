package com.jiecxy.security.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jiecxy.security.R;
import com.jiecxy.security.ui.SetupGuide1.SetupGuide1Activity;
import com.jiecxy.security.utils.MD5Encoder;

/**
 * Created by apple on 15-1-28.
 */

public class LostProtectedActivity extends Activity implements View.OnClickListener
{
    private SharedPreferences sp;
    private Dialog dialog;
    private EditText password;
    private EditText confirmPassword;

    private TextView tv_protectedNumber;
    private Button   tv_protectedGuide;
    private CheckBox cb_isProtected;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //这里可以设置界面  但是会被dialog覆盖

        sp = getSharedPreferences("config", Context.MODE_PRIVATE);

        //判断是否已经设置密码
        if(isSetPassword())
        {
            showLoginDialog();
        }
        else
        {
            showFirstDialog();
        }
    }

    //登录
    private void showLoginDialog()
    {
        dialog = new Dialog(this, R.style.MyDialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        View view = View.inflate(this, R.layout.login_dialog, null);
        password = (EditText) view.findViewById(R.id.et_protected_password);
        Button yes = (Button) view.findViewById(R.id.bt_protected_login_yes);
        Button cancel = (Button) view.findViewById(R.id.bt_protected_login_no);
        yes.setOnClickListener(this);
        cancel.setOnClickListener(this);
        dialog.setContentView(view);
        dialog.show();
    }

    //第一次登录 设置密码
    private void showFirstDialog()
    {
        dialog = new Dialog(this, R.style.MyDialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        //dialog.setContentView(R.layout.first_dialog);
        View view = View.inflate(this, R.layout.first_dialog, null);        //这样来填充一个而已文件，比较方便
        password = (EditText) view.findViewById(R.id.et_protected_first_password);
        confirmPassword = (EditText) view.findViewById(R.id.et_protected_confirm_password);
        Button yes = (Button) view.findViewById(R.id.bt_protected_first_yes);
        Button cancel = (Button) view.findViewById(R.id.bt_protected_first_no);
        yes.setOnClickListener(this);
        cancel.setOnClickListener(this);
        dialog.setContentView(view);
        dialog.show();
    }

    private boolean isSetPassword()
    {
        String pwd = sp.getString("password", "");
        if(pwd.equals("") || pwd == null)
        {
            return false;
        }
        return true;
    }

    private boolean isSetupGuide()
    {
        return sp.getBoolean("setupGuide", false);
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.bt_protected_first_yes :
                String fp = password.getText().toString().trim();
                String cp = confirmPassword.getText().toString().trim();

                if(fp.equals("") || cp.equals(""))
                {
                    Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    if(fp.equals(cp))
                    {
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("password", MD5Encoder.encode(fp));   //MD5 加密
                        editor.commit();
                        dialog.dismiss();

                        if (!isSetupGuide())
                        {
                            finish();
                            Intent intent = new Intent(this, SetupGuide1Activity.class);
                            startActivity(intent);
                        }
                    }
                    else
                    {
                        Toast.makeText(this, "两次密码不相同", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                //dialog.dismiss();
                break;

            case R.id.bt_protected_first_no :
                dialog.dismiss();
                finish();
                break;

            case R.id.bt_protected_login_yes :
                String pwd = password.getText().toString().toString();
                if(pwd.equals(""))
                {
                    Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String str = sp.getString("password", "");
                    if(MD5Encoder.encode(pwd).equals(str))
                    {
                        if (isSetupGuide())
                        {
                            setContentView(R.layout.lost_protected);
                            tv_protectedNumber = (TextView) findViewById(R.id.tv_lost_protected_number);
                            tv_protectedGuide  = (Button) findViewById(R.id.tv_lost_protected_guide);
                            cb_isProtected     = (CheckBox) findViewById(R.id.cb_lost_protected_isProtected);

                            tv_protectedNumber.setText("手机安全号码为：" + sp.getString("number", ""));
                            tv_protectedGuide.setOnClickListener(this);

                            boolean isProtecting = sp.getBoolean("isProtected", false);
                            if(isProtecting)
                            {
                                cb_isProtected.setText("已经开启保护");
                                cb_isProtected.setChecked(true);
                            }

                            cb_isProtected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
                            {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                                {
                                    if(isChecked)
                                    {
                                        cb_isProtected.setText("已经开启保护");
                                        SharedPreferences.Editor editor = sp.edit();
                                        editor.putBoolean("isProtected", true);
                                        editor.commit();
                                    }
                                    else
                                    {
                                        cb_isProtected.setText("没有开启保护");
                                        SharedPreferences.Editor editor = sp.edit();
                                        editor.putBoolean("isProtected", false);
                                        editor.commit();
                                    }
                                }
                            });
                        }
                        else
                        {
                            finish();
                            Intent intent = new Intent(this, SetupGuide1Activity.class);
                            startActivity(intent);
                        }
                        dialog.dismiss();
                    }
                    else
                    {
                        Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.bt_protected_login_no :
                dialog.dismiss();
                finish();
                break;

            case R.id.tv_lost_protected_guide : //重新进入设置向导
                finish();
                Intent setupGuideIntent = new Intent(this, SetupGuide1Activity.class);
                startActivity(setupGuideIntent);
                break;

            default :
                break;
        }
    }

}
