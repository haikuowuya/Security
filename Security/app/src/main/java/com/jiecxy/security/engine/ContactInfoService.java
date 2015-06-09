package com.jiecxy.security.engine;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.jiecxy.security.domain.ContactInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by apple on 15-1-29.
 */

public class ContactInfoService
{
    private Context context;

    public ContactInfoService(Context context)
    {
        this.context = context;
    }

    public List<ContactInfo> getContactInfos()
    {
        List<ContactInfo> infos = new ArrayList<ContactInfo>();

        getContact(infos);

        /*

        //下面代码会出现 一个人名多个号码 只显示最后一个电话的情况

        ContentResolver contentResolver = context.getContentResolver();
        //在源码的AndroidManifest里面可以看到这些uri
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri dataUri = Uri.parse("content://com.android.contacts/data");
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        while(cursor.moveToNext())
        {
            info = new ContactInfo();
            String id = cursor.getString(cursor.getColumnIndex("_id"));
            String name = cursor.getString(cursor.getColumnIndex("display_name"));
            info.setName(name);

            //通过raw_contacts里面的id拿到data里面对应的数据
            Cursor dataCursor = contentResolver.query(dataUri, null, "raw_contact_id = ? ", new String[] {id}, null);
            while(dataCursor.moveToNext())
            {
                String type = dataCursor.getString(dataCursor.getColumnIndex("mimetype"));
                //根据类型，只要电话这种类型的数据
                if(type.equals("vnd.android.cursor.item/phone_v2"))
                {
                    String number = dataCursor.getString(dataCursor.getColumnIndex("data1"));//拿到数据
                    info.setPhone(number);
                }
            }
            dataCursor.close();

            infos.add(info);
            info = null;
        }
        */

        return infos;
    }

    private void getContact(List<ContactInfo> infos)
    {
        ContactInfo info = null;

        ContentResolver cr = context.getContentResolver();
        Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME},
                ContactsContract.CommonDataKinds.Phone.MIMETYPE + "=?", new String[]{ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE},
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        while (phone.moveToNext())
        {
            info = new ContactInfo();
            info.setName(phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
            info.setPhone(phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
            infos.add(info);
        }
        phone.close();

    }
}