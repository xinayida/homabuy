# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/ww/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#kepler start
-keep class com.kepler.**{*;}
-dontwarn com.kepler.**
-keep class com.jingdong.jdma.**{*;}
-dontwarn com.jingdong.jdma.**
-keep class com.jingdong.crash.**{*;}
-dontwarn com.jingdong.crash.**
#kepler end

#vLayout start
-keepattributes InnerClasses
-keep class com.alibaba.android.vlayout.ExposeLinearLayoutManagerEx { *; }
-keep class android.support.v7.widget.RecyclerView$LayoutParams { *; }
-keep class android.support.v7.widget.RecyclerView$ViewHolder { *; }
-keep class android.support.v7.widget.ChildHelper { *; }
-keep class android.support.v7.widget.ChildHelper$Bucket { *; }
-keep class android.support.v7.widget.RecyclerView$LayoutManager { *; }
#vLayout end

-dontwarn java.lang.invoke.*
-dontwarn **$$Lambda$*

#glide start
-dontwarn com.bumptech.glide.**
-keep class com.bumptech.glide.** { *; }
#-keep class com.bumptech.glide.BitmapTypeRequest { *; }
#-keep class com.bumptech.glide.BitmapRequestBuilder { *; }
#-keep class com.bumptech.glide.DrawableTypeRequest { *; }
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
# for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule
#glide end


#wechat start
-keep class com.tencent.mm.opensdk.** {*;}
-keep class com.tencent.wxop.** {*;}
-keep class com.tencent.mm.sdk.** {*;}
#wechat end

#友盟
-keep class com.umeng.commonsdk.** {*;}
#友盟

#bugly
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}
#bugly

#butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}
-dontwarn butterknife.compiler.**
-keep class butterknife.compiler.** { *; }
#butterknife

-dontwarn com.google.**
-keep class com.google.** {*;}

-dontwarn okhttp3.**
-keep class okhttp3.**{ *; }

-dontwarn rx.**
-keep class rx.**{*;}
-dontwarn okio.**
-keep class okio.**{*;}

-dontwarn com.squareup.okhttp.**
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }

-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}

-keepattributes Signature
-keepattributes *Annotation*

-dontwarn com.squareup.javapoet.**
-keep class com.squareup.javapoet.** {*;}

-dontwarn com.mcxtzhang.commonadapter.BR
-keep class com.mcxtzhang.commonadapter.BR {*;}

-dontwarn android.arch.core.util.Function
-keep class android.arch.core.util.Function {*;}

-dontwarn android.databinding.DataBinderMapper
-dontwarn android.databinding.DataBindingComponent
-keep class android.databinding.DataBinderMapper {*;}
-keep class android.databinding.DataBindingComponent {*;}
-keep class com.nhzw.shopping.model.repository.** {*;}

-keep interface com.nhzw.base.** {*;}
-keep public class * extends com.nhzw.base.**
#自定义不需要混淆的文件
-keep @com.xinayida.lib.annotation.NotProguard class * {*;}
-keep class * {
    @com.xinayida.lib.annotation.NotProguard <fields>;
}
-keepclassmembers class * {
    @com.xinayida.lib.annotation.NotProguard <methods>;
}
