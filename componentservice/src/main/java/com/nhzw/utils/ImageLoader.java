package com.nhzw.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.nhzw.component.lib.service.GlideApp;
import com.nhzw.rx.RxUtils;
import com.nhzw.service.AppService;

import java.io.File;
import java.math.BigDecimal;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ww on 2017/9/19.
 */

public class ImageLoader {

    public static void loadImage(String url, ImageView imageView) {
        if (imageView != null) {
            GlideApp.with(imageView.getContext()).load(url).transition(DrawableTransitionOptions.withCrossFade()).centerCrop().into(imageView);
        }
    }

    /**
     * 描述: 加载圆形图片.<br/><br/>
     * 作者：由姚宇编辑于2017/9/21.<br/><br/>
     */
    public static void loadCircleImage(String url, ImageView imageView) {
        if (imageView == null) {
            return;
        }
        RequestOptions options2 = new RequestOptions()
                .centerCrop()
//                .placeholder(R.drawable.ic_launcher)//预加载图片
//                .error(R.drawable.ic_launcher)//加载失败显示图片
                .priority(Priority.HIGH)//优先级
                .diskCacheStrategy(DiskCacheStrategy.NONE)//缓存策略
                .transform(new GlideCircleTransform());//转化为圆形
        Glide.with(imageView.getContext()).load(url).apply(options2).into(imageView);
    }

    /**
     * 取消未发起的图片请求
     *
     * @param imageView
     */
    public static void clear(ImageView imageView) {
        if (imageView == null) return;
        Context context = AppService.instance.getAppContext();
        Glide.get(context).getRequestManagerRetriever().get(context).clear(imageView);
    }

    public static Completable clearCache(Context context) {
        clearMemCache(context);
        return RxUtils.wrap(() -> {
            Glide.get(context).clearDiskCache();
        });
    }

    /**
     * 清除内存缓存
     *
     * @param ctx
     */
    public static void clearMemCache(Context ctx) {
        Observable.just(0)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        Glide.get(ctx).clearMemory();
                    }
                });
    }

    /**
     * 清除本地缓存
     *
     * @param ctx
     */
    public static void clearFileCache(Context ctx) {
        Observable.just(0).observeOn(Schedulers.io())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        Glide.get(ctx).clearDiskCache();
                    }
                });
    }

    // 获取Glide磁盘缓存大小
    public static String getCacheSize() {
        try {
            return getFormatSize(getFolderSize(GlideApp.getPhotoCacheDir(AppService.instance.getAppContext())));
        } catch (Exception e) {
            e.printStackTrace();
            return "获取失败";
        }
    }

    // 获取指定文件夹内所有文件大小的和
    private static long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (File aFileList : fileList) {
                if (aFileList.isDirectory()) {
                    size = size + getFolderSize(aFileList);
                } else {
                    size = size + aFileList.length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    // 格式化单位
    private static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "Byte";
        }
        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }
        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }
        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }


}
