package com.general.files;


import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

public class Util {
    public static final String TAG = Util.class.getSimpleName();

    private static final String HASH_TYPE = "SHA-256";
    public static final int NUM_HASHED_BYTES = 9;
    public static final int NUM_BASE64_CHAR = 11;

    /**
     * get App Signatures
     */
    public static ArrayList<String> getAppSignatures(Context context) {
        ArrayList<String> appCodes = new ArrayList<>();

        try {
            // Get all package signatures for the current package
            String packageName = context.getPackageName();
            PackageManager packageManager = context.getPackageManager();
            Signature[] signatures = packageManager.getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES).signatures;

            // For each signature create a compatible hash
            for (Signature signature : signatures) {
                String hash = getHash(packageName, signature.toCharsString());
                if (hash != null) {
                    appCodes.add(String.format("%s", hash));
                }
                Log.d(TAG, String.format("이 값을 SMS 뒤에 써서 보내주면 됩니다 : %s", hash));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, "Unable to find package to obtain hash. : " + e.toString());
        }
        return appCodes;
    }

    private static String getHash(String packageName, String signature) {
        String appInfo = packageName + " " + signature;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(HASH_TYPE);
            // minSdkVersion이 19이상이면 체크 안해도 됨
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                messageDigest.update(appInfo.getBytes(StandardCharsets.UTF_8));
            }
            byte[] hashSignature = messageDigest.digest();

            // truncated into NUM_HASHED_BYTES
            hashSignature = Arrays.copyOfRange(hashSignature, 0, NUM_HASHED_BYTES);
            // encode into Base64
            String base64Hash = Base64.encodeToString(hashSignature, Base64.NO_PADDING | Base64.NO_WRAP);
            base64Hash = base64Hash.substring(0, NUM_BASE64_CHAR);

            Log.d(TAG, String.format("\nPackage : %s\nHash : %s", packageName, base64Hash));
            return base64Hash;
        } catch (NoSuchAlgorithmException e) {
            Log.d(TAG, "hash:NoSuchAlgorithm : " + e.toString());
        }
        return null;
    }
}