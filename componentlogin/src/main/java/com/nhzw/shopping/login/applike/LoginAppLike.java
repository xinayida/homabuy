package com.nhzw.shopping.login.applike;

import com.nhzw.component.lib.applicationlike.IApplicationLike;
import com.nhzw.component.lib.service.ServiceManager;
import com.nhzw.service.LoginService;
import com.nhzw.shopping.login.LoginServiceImpl;

/**
 * Created by ww on 2017/9/12.
 */

public class LoginAppLike implements IApplicationLike {
    ServiceManager serviceManager = ServiceManager.getInstance();

    @Override
    public void onCreate() {
        serviceManager.addService(LoginService.class, new LoginServiceImpl());
    }

    @Override
    public void onStop() {
        serviceManager.removeService(LoginService.class);
    }
}
