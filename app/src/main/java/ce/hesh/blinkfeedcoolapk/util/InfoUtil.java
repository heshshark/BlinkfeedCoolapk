package ce.hesh.blinkfeedcoolapk.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Resources;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import org.aisen.android.common.context.GlobalContext;
import org.aisen.android.common.utils.DateUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import ce.hesh.blinkfeedcoolapk.R;

/**
 * Created by Hesh on 2016/12/5.
 */

public class InfoUtil {

    private static String DeviceID = null;
    private static String Cookies = null;

    public static String getDeviceID() {
        if (DeviceID != null)
            return DeviceID;
        String str = "9774d56d682e549c";
        File file = new File(GlobalContext.getInstance().getFilesDir(), "INSTALLATION");
        try {
            if (!file.exists()) {
                FileOutputStream fileOutputStream;
                String str2 = null;
                str = Settings.Secure.getString(GlobalContext.getInstance().getContentResolver(), "android_id");
                if (!(str == null || "9774d56d682e549c".equals(str))) {
                    str2 = UUID.nameUUIDFromBytes(str.getBytes("utf8")).toString();
                }
                if (str2 == null) {
                    str = ((TelephonyManager) GlobalContext.getInstance().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                    if (str != null) {
                        str = UUID.nameUUIDFromBytes(str.getBytes("utf8")).toString();
                        if (str == null) {
                            str = UUID.randomUUID().toString();
                        }
                        fileOutputStream = new FileOutputStream(file);
                        fileOutputStream.write(str.getBytes());
                        fileOutputStream.close();
                    }
                }
                str = str2;
                if (str == null) {
                    str = UUID.randomUUID().toString();
                }
                fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(str.getBytes());
                fileOutputStream.close();
            }
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            byte[] bArr = new byte[((int) randomAccessFile.length())];
            randomAccessFile.readFully(bArr);
            randomAccessFile.close();
            DeviceID = new String(bArr);
            return DeviceID;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveCoockie(Context context, String cookie) {
        File file = new File(context.getFilesDir(), "Cookie");
        if (file.exists()) {
            file.delete();
        }
        try {
            if (!file.exists()) {
                cookie = cookie.replaceAll("\\s+", ";");
                FileOutputStream fileOutputStream;
                fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(cookie.getBytes());
                fileOutputStream.close();
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }


    public static String getCookie() {
        if (Cookies != null)
            return Cookies;
        File file = new File(GlobalContext.getInstance().getFilesDir(), "Cookie");
        if (file.exists()) {
            try {
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
                byte[] bArr = new byte[((int) randomAccessFile.length())];
                randomAccessFile.readFully(bArr);
                randomAccessFile.close();
                Cookies = new String(bArr);
                return Cookies;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String convDate(String time) {
        try {
            time = time + "000";
            Context context = GlobalContext.getInstance();
            Resources res = context.getResources();

            StringBuffer buffer = new StringBuffer();

            Calendar createCal = Calendar.getInstance();

            if (time.length() == 13) {
                try {
                    createCal.setTimeInMillis(Long.parseLong(time));
                } catch (Exception e) {
                    createCal.setTimeInMillis(Date.parse(time));
                }
            } else {
                createCal.setTimeInMillis(Date.parse(time));
            }

            Calendar currentcal = Calendar.getInstance();
            currentcal.setTimeInMillis(System.currentTimeMillis());

            long diffTime = (currentcal.getTimeInMillis() - createCal.getTimeInMillis()) / 1000;

            // 同一月
            if (currentcal.get(Calendar.MONTH) == createCal.get(Calendar.MONTH)) {
                // 同一天
                if (currentcal.get(Calendar.DAY_OF_MONTH) == createCal.get(Calendar.DAY_OF_MONTH)) {
                    if (diffTime < 3600 && diffTime >= 60) {
                        buffer.append((diffTime / 60) + res.getString(R.string.msg_few_minutes_ago));
                    } else if (diffTime < 60) {
                        buffer.append(res.getString(R.string.msg_now));
                    } else {
                        buffer.append(res.getString(R.string.msg_today)).append(" ").append(DateUtils.formatDate(createCal.getTimeInMillis(), "HH:mm"));
                    }
                }
                // 前一天
                else if (currentcal.get(Calendar.DAY_OF_MONTH) - createCal.get(Calendar.DAY_OF_MONTH) == 1) {
                    buffer.append(res.getString(R.string.msg_yesterday)).append(" ").append(DateUtils.formatDate(createCal.getTimeInMillis(), "HH:mm"));
                }
            }

            if (buffer.length() == 0) {
                buffer.append(DateUtils.formatDate(createCal.getTimeInMillis(), "MM-dd HH:mm"));
            }

            String timeStr = buffer.toString();
            if (currentcal.get(Calendar.YEAR) != createCal.get(Calendar.YEAR)) {
                timeStr = createCal.get(Calendar.YEAR) + " " + timeStr;
            }

            return timeStr;
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return time;
    }

    public static void copyToClipboard(String text) {
        // 得到剪贴板管理器
        try {
            ClipboardManager cmb = (ClipboardManager) GlobalContext.getInstance().getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setPrimaryClip(ClipData.newPlainText(null, text.trim()));
        } catch (Exception e) {
        }
    }
}
