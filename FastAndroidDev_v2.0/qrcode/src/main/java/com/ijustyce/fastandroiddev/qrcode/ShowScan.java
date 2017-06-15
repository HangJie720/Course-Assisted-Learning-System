package com.ijustyce.fastandroiddev.qrcode;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ShowScan extends Activity {

    private String name, phone,work , email, note, honeNum;
    private final static String SCAN_NAME = "N" , SCAN_EMAIL = "EMAIL", SCAN_PHONE = "TEL";
    private final static String SCAN_HOME = "HOME", SCAN_CELL = "CELL", SCAN_WORK = "WORK";
    private final static String SCAN_NOTE = "NOTE", SCAN_FN = "FN";
    private boolean isContact = false;
    private String res;
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_scan);

        Bundle bundle = getIntent().getExtras();
        if(bundle == null){
            this.finish();
            return ;
        }

        res = bundle.getString("res");
        if(res == null || res.equals("")){
            this.finish();
        }

        if(res.startsWith("BEGIN:VCARD") || res.startsWith("MECARD")){

            isContact = true;
            doRes(res);
        }else{
            TextView info = (TextView) findViewById(R.id.show_scan_info);
            info.setVisibility(View.VISIBLE);
            info.setText(res);
        }
    }

    private void doRes(String res){

        String mecard = "MECARD:";
        String vcard = "BEGIN:VCARD";
        if(res.contains("MECARD:")){
            doMecard(res.replace(mecard, ""));
        }else{
            doVcard(res.replace(vcard, ""));
        }
        showContacts();
    }

    private void showContacts(){

        RelativeLayout contacts = (RelativeLayout) findViewById(R.id.show_scan_contacts);
        contacts.setVisibility(View.VISIBLE);

        Button add = (Button) findViewById(R.id.show_scan_add);
        add.setText(R.string.add_contacts);

        TextView tmp = (TextView) findViewById(R.id.show_scan_name);
        tmp.setText(tmp.getText() + name);

        tmp = (TextView) findViewById(R.id.show_scan_phone);
        tmp.setText(tmp.getText() + phone);

        tmp = (TextView) findViewById(R.id.show_scan_work);
        tmp.setText(tmp.getText() + work);

        tmp = (TextView) findViewById(R.id.show_scan_home);
        tmp.setText(tmp.getText() + honeNum);

        tmp = (TextView) findViewById(R.id.show_scan_email);
        tmp.setText(tmp.getText() + email);

        tmp = (TextView) findViewById(R.id.show_scan_note);
        tmp.setText(tmp.getText() + note);
    }

    private void doMecard(String res){

        res = res.substring(0 , res.length() - 1);
        String[] values = res.split(";");
        for (String tmp : values){
            String[] value = tmp.split(":");
            if (value.length > 1){
                doParam(value);
            }
        }
    }

    private void doParam(String[] value){

        switch (value[0]){

            case SCAN_NAME:
                name = value[1];
                break;

            case SCAN_PHONE:
                phone = value[1];
                break;

            case SCAN_EMAIL:
                email = value[1];
                break;

            case SCAN_NOTE:
                note = value[1];
                break;

            case SCAN_PHONE + ";" + SCAN_CELL:
                phone = value[1];
                break;

            case SCAN_PHONE +  ";" +  SCAN_WORK:
                work = value[1];
                break;

            case SCAN_PHONE +  ";" +  SCAN_HOME:
                honeNum = value[1];
                break;

            case SCAN_HOME:
                honeNum = value[1];
                break;

            case SCAN_FN:
                name = value[1];
                break;

            default:
                break;
        }
    }

    private void doVcard(String res){

        res = res.replace("\nEND:VCARD", "");
        String[] values = res.split("\n");
        for (String tmp : values){
            String[] value = tmp.split(":");
            if (value.length > 1){
                doParam(value);
            }
        }
    }

	@Override
	public void onResume() {

        super.onResume();
    }

    public void viewClick(View view) {

        int id = view.getId();
        if (R.id.show_scan_back == id){
            finish();
        }else if(R.id.show_scan_add == id){
            if(isContact) {
                addContacts();
            }else{
                systemShare(this, res);
            }
        }
    }

    /**
     * open system share dialog
     *
     * @param context mContext
     * @param text    text to share
     * @return true if success or false
     */
    public static void systemShare(Context context, String text) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        //  intent.putExtra(Intent.EXTRA_SUBJECT, text);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setType("text/plain");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, "请选择应用"));
    }

    private void addContacts(){

        Intent insertIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
        insertIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
        insertIntent.putExtra(ContactsContract.Intents.Insert.NAME, name == null ? "" : name);
        insertIntent.putExtra(ContactsContract.Intents.Insert.NOTES, note == null ? "" : note);
        insertIntent.putExtra(ContactsContract.Intents.Insert.PHONE, phone == null ? "" : phone);
        insertIntent.putExtra(ContactsContract.Intents.Insert.SECONDARY_PHONE, work == null ? "" : work);
        insertIntent.putExtra(ContactsContract.Intents.Insert.TERTIARY_PHONE, honeNum == null ? "" : honeNum);
        insertIntent.putExtra(ContactsContract.Intents.Insert.EMAIL, email == null ? "" : email);

        if (insertIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(insertIntent);
        } else {
            Toast.makeText(getBaseContext(), "没有通讯录软件", Toast.LENGTH_SHORT);
        }
    }
}