package com.nhzw.rx;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import com.nhzw.service.AppService;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ww on 2017/10/24.
 */

public class RxUtils {
    public static Completable runOnUI(Action action) {
        return Completable.fromAction(action).subscribeOn(AndroidSchedulers.mainThread());
    }

    public static Completable runOnUIDelay(Action action, long time) {
        return Completable.fromAction(action).subscribeOn(AndroidSchedulers.mainThread()).delay(time, TimeUnit.MILLISECONDS);
    }

    public static void download(Context context, String url) {
        Uri uri = Uri.parse(url);
        Intent intentUri = new Intent(Intent.ACTION_VIEW, uri);
        if (isIntentAvailable(context, intentUri)) {
            try {
                AppService.instance.getCurActivity().startActivity(intentUri);
                //Open the specific App Info page:
//                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                intent.setData(Uri.parse("package:" + "com.android.providers.downloads"));
//                AppService.instance.getCurActivity().startActivity(intent);

            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                //Open the generic Apps page:
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                AppService.instance.getCurActivity().startActivity(intent);
            }
        }
    }

    private static boolean isIntentAvailable(Context context, Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    public static Completable wrap(Action action) {
        return Completable.fromAction(action).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
