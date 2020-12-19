package com.autoReadOtp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.text.TextUtils;

import com.general.functions.Utils;


/**
 * Created by swarajpal on 13-12-2015.
 * BroadcastReceiver OtpReader for receiving and processing the SMS messages.
 */
public class OtpReader extends BroadcastReceiver {

    /**
     * Constant TAG for logging key.
     */
    private static final String TAG = "OtpReader";

    /**
     * The bound OTP Listener that will be trigerred on receiving message.
     */
    private static OTPListener otpListener;

    /**
     * The Sender number string.
     */
    private static String receiverString;


    /**
     * Binds the sender string and listener for callback.
     *
     * @param listener
     * @param sender
     */


    public static void bind(OTPListener listener, String sender) {
        otpListener = listener;
        receiverString = sender;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final Bundle bundle = intent.getExtras();
        if (bundle == null) {
            Utils.printLog(TAG, "BroadcastReceiver failed, no intent data to process.");
            return;
        }

           /* if (bundle != null) {

                final Object[] pdusArr = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusArr.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusArr[i]);
                    String senderNum = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();
                    Log.i(TAG, "senderNum: " + senderNum + " message: " + message);

                    if ((!TextUtils.isEmpty(receiverString) && senderNum.contains(receiverString)) || (!TextUtils.isEmpty(numberString) && senderNum.contains(numberString))) { //If message received is from required number.
                        //If bound a listener interface, callback the overriden method.
                        if (otpListener != null) {
                            otpListener.otpReceived(message);
                        }
                    }

                }
            }*/

        Utils.printLog(TAG, "SMS_RECEIVED");

        String smsOriginatingAddress, smsDisplayMessage;

        // You have to CHOOSE which code snippet to use NEW (KitKat+), or legacy
        // Please comment out the for{} you don't want to use.

        // API level 19 (KitKat 4.4) getMessagesFromIntent
        for (SmsMessage message : Telephony.Sms.Intents.
                getMessagesFromIntent(intent)) {
            Utils.printLog(TAG, "KitKat or newer");
            if (message == null) {
                Utils.printLog(TAG, "SMS message is null -- ABORT");
                break;
            }
            smsOriginatingAddress = message.getDisplayOriginatingAddress();
            smsOriginatingAddress = message.getOriginatingAddress();
            //see getMessageBody();
            smsDisplayMessage = message.getDisplayMessageBody();

            if ((!TextUtils.isEmpty(receiverString) && smsDisplayMessage.contains(receiverString))) { //If message received is from required number.
                //If bound a listener interface, callback the overriden method.
                if (otpListener != null) {

                    if (Utils.checkText(receiverString)) {
                        otpListener.otpReceived(smsDisplayMessage.replace(receiverString, ""));
                    } else {
                        otpListener.otpReceived(smsDisplayMessage);
                    }

                }
            }
        }

        // Processing SMS messages the OLD way, before KitKat,
        // this WILL work on KitKat or newer Android
        // PDU is a “protocol data unit”, which is the industry
        // format for an SMS message
        Object[] data = (Object[]) bundle.get("pdus");
        for (Object pdu : data) {
            Utils.printLog(TAG, "legacy SMS implementation (before KitKat)");
            SmsMessage message = SmsMessage.createFromPdu((byte[]) pdu);
            if (message == null) {
                Utils.printLog(TAG, "SMS message is null -- ABORT");
                break;
            }
            smsOriginatingAddress = message.getDisplayOriginatingAddress();
            smsOriginatingAddress = message.getOriginatingAddress();
            // see getMessageBody();
            smsDisplayMessage = message.getDisplayMessageBody();
            if ((!TextUtils.isEmpty(receiverString) && doesSmsStartWith(smsDisplayMessage, receiverString))) { //If message received is from required number.
                //If bound a listener interface, callback the overriden method.
                if (otpListener != null) {
                    otpListener.otpReceived(smsDisplayMessage);
                }
            }

        } // onReceive method
    }

    private static boolean doesSmsStartWith(String smsMessage, String txt) {
        return smsMessage.trim().toLowerCase().startsWith(txt.trim().toLowerCase());
    }


    /**
     * Unbinds the sender string and listener for callback.
     */
    public static void unbind() {
        otpListener = null;
        receiverString = null;
    }
}
