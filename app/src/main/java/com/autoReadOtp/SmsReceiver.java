package com.autoReadOtp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

public class SmsReceiver extends BroadcastReceiver {
    //interface
    private static OTPListener mListener;

    public static void bind(OTPListener listener) {
        mListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            if(extras != null) {
                Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

                if(status != null) {
                    switch (status.getStatusCode()) {
                        case CommonStatusCodes.SUCCESS:
                            // Get SMS message contents
                            String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                            //Pass the message text to interface
                            Log.d("SMSReceiver", message);

                            if (mListener != null  ) {
                                mListener.otpReceived(message);
                            }
                            break;
                        case CommonStatusCodes.TIMEOUT:
                            Log.d("SMSReceiver", "timed out (5 minutes)");
                            break;
                    }
                }
            }
        }
    }

    public static void bindListener(OTPListener listener) {
        mListener = listener;
    }
}
