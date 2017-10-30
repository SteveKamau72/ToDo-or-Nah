package stevekamau.todo.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.text.TextUtils;

import java.net.URL;
import java.util.List;

/**
 * Created by steve on 1/13/17.
 */

public class IntentUtils {

    /**
     * Open app page at Google Play. If Play Store application isn't available on the device
     * then web browser will be opened
     *
     * @param context Application context
     */

    public static Intent openPlayStore(Context context) {
        return openPlayStore(context, true);
    }

    /**
     * Open app page at Google Play
     *
     * @param context       Application context
     * @param openInBrowser Should we try to open application page in web browser
     *                      if Play Store app not found on device
     */
    public static Intent openPlayStore(Context context, boolean openInBrowser) {
        String appPackageName = context.getPackageName();
        Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
        if (isIntentAvailable(context, marketIntent)) {
            return marketIntent;
        }
        if (openInBrowser) {
            return openLink("https://play.google.com/store/apps/details?id=" + appPackageName);
        }
        return marketIntent;
    }

    /**
     * Open a browser window to the URL specified.
     *
     * @param url Target url
     */
    public static Intent openLink(String url) {
        // if protocol isn't defined use http by default
        if (!TextUtils.isEmpty(url) && !url.contains("://")) {
            url = "http://" + url;
        }

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        return intent;
    }

    /**
     * @see #openLink(String)
     */
    public static Intent openLink(URL url) {
        return openLink(url.toString());
    }

    /**
     * Send email message
     *
     * @param to      Receiver email
     * @param subject Message subject
     * @param text    Message body
     * @see #sendEmail(String to, String, String)
     */

    /**
     * @see #sendEmail(String, String, String)
     */
    public static Intent sendEmail(String to, String subject, String text) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", to, null));
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        return intent;
    }

    public static Intent shareContent(String shareMessage, String subject) {

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        return shareIntent;
    }

    /**
     * Calls the entered phone number. Valid telephone numbers as defined in the IETF RFC 3966 are accepted.
     * Valid examples include the following:
     * tel:2125551212
     * tel: (212) 555 1212
     * <p/>
     * Note: This requires your application to request the following permission in your manifest:
     * <code>&lt;uses-permission android:name="android.permission.CALL_PHONE"/&gt;</code>
     *
     * @param phoneNumber Phone number
     */
    public static Intent callPhone(String phoneNumber) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        return intent;
    }

    /**
     * Send SMS message using built-in app
     *
     * @param context Application context
     * @param to      Receiver phone number
     * @param message Text to send
     */
    public static Intent sendSms(Context context, String to, String message) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(context);
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + to));
            intent.putExtra("sms_body", message);
            if (defaultSmsPackageName != null) {
                intent.setPackage(defaultSmsPackageName);
            }
            return intent;
        } else {
            Uri smsUri = Uri.parse("tel:" + to);
            Intent intent = new Intent(Intent.ACTION_VIEW, smsUri);
            intent.putExtra("address", to);
            intent.putExtra("sms_body", message);
            intent.setType("vnd.android-dir/mms-sms");
            return intent;
        }
    }

    /**
     * Pick contact from phone book
     */
    public static Intent pickContact() {
        return pickContact(null);
    }

    /**
     * Pick contact from phone book
     *
     * @param scope You can restrict selection by passing required content type. Examples:
     *              <p/>
     *              <code><pre>
     *                                                                                                                                                // Select only from users with emails
     *                                                                                                                                                IntentUtils.pickContact(ContactsContract.CommonDataKinds.Email.CONTENT_TYPE);
     *              <p/>
     *                                                                                                                                                // Select only from users with phone numbers on pre Eclair devices
     *                                                                                                                                                IntentUtils.pickContact(Contacts.Phones.CONTENT_TYPE);
     *              <p/>
     *                                                                                                                                                // Select only from users with phone numbers on devices with Eclair and higher
     *                                                                                                                                                IntentUtils.pickContact(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
     *                                                                                                                                                </pre></code>
     */
    public static Intent pickContact(String scope) {
        Intent intent;
        if (isSupportsContactsV2()) {
            intent = new Intent(Intent.ACTION_PICK, Uri.parse("content://com.android.contacts/contacts"));
        } else {
            intent = new Intent(Intent.ACTION_PICK, Contacts.People.CONTENT_URI);
        }

        if (!TextUtils.isEmpty(scope)) {
            intent.setType(scope);
        }
        return intent;
    }

    /**
     * Pick contact only from contacts with telephone numbers
     */
    public static Intent pickContactWithPhone() {
        Intent intent;
        if (isSupportsContactsV2()) {
            intent = pickContact(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        } else { // pre Eclair, use old contacts API
            intent = pickContact(Contacts.Phones.CONTENT_TYPE);
        }
        return intent;
    }

    /**
     * Dials (but does not actually initiate the call) the number given.
     * Telephone number normalization described for {@link #callPhone(String)} applies to dial as well.
     *
     * @param phoneNumber Phone number
     */
    public static Intent dialPhone(String phoneNumber) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        return intent;
    }

    /**
     * Check that in the system exists application which can handle this intent
     *
     * @param context Application context
     * @param intent  Checked intent
     * @return true if intent consumer exists, false otherwise
     */
    public static boolean isIntentAvailable(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private static boolean isSupportsContactsV2() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR;
    }


}
