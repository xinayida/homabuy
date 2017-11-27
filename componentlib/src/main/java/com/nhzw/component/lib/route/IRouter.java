package com.nhzw.component.lib.route;

import android.content.Context;
import android.os.Bundle;

/**
 * Created by ww on 2017/9/12.
 */

public interface IRouter {
    void registerRouter(String route, Class activityClass);

    void unregisterRouter(String route);

    boolean open(Context context, String route);

    boolean open(Context context, String route, Bundle bundle);
}
