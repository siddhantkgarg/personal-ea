package in.sidd.personal.ea;

import android.app.Notification;
import android.content.pm.ApplicationInfo;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;

public class NotificationCaptureService extends NotificationListenerService {
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Notification n = sbn.getNotification();
        if (n == null || n.extras == null) return;

        CharSequence titleCs = n.extras.getCharSequence(Notification.EXTRA_TITLE);
        CharSequence textCs = n.extras.getCharSequence(Notification.EXTRA_TEXT);
        String title = titleCs == null ? "" : titleCs.toString();
        String text = textCs == null ? "" : textCs.toString();

        if (TextUtils.isEmpty(title) && TextUtils.isEmpty(text)) return;

        EventStore.Event e = new EventStore.Event();
        e.packageName = sbn.getPackageName();
        e.appLabel = getAppLabel(sbn.getPackageName());
        e.title = title;
        e.text = text;
        e.timestamp = sbn.getPostTime();
        EventStore.addEvent(this, e);
    }

    private String getAppLabel(String packageName) {
        try {
            ApplicationInfo info = getPackageManager().getApplicationInfo(packageName, 0);
            CharSequence label = getPackageManager().getApplicationLabel(info);
            return label == null ? packageName : label.toString();
        } catch (Exception ignored) {
            return packageName;
        }
    }
}
