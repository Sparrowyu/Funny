# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

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

#------------------------------------通用的------------------------------------
#指定代码的压缩级别，默认为5，一般不做修改
-optimizationpasses 5
#混淆时不使用大小写混合类名，混合后的类名为小写
-dontusemixedcaseclassnames
#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses
#不去忽略包可见的库类的成员
-dontskipnonpubliclibraryclassmembers
#不进行优化,建议使用此选项
-dontoptimize
#不进行预校验，Android不需要，可加快混淆速度
-dontpreverify
#混淆时记录日志(打印混淆的详细信息)
-verbose
# 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*,!code/allocation/variable
#优化时允许访问并修改有修饰符的类和类的成员
-allowaccessmodification
#将文件来源重命名为“SourceFile”字符串
-renamesourcefileattribute SourceFile
#保护注解
-keepattributes *Annotation*
-keep class * extends java.lang.annotation.Annotation {*;}
#泛型与反射
-keepattributes Signature
-keepattributes EnclosingMethod
#不混淆内部类
-keepattributes InnerClasses
#抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable
#忽略警告
-ignorewarnings

#保持哪些类不被混淆
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class * extends androidx.multidex.MultiDexApplication

#引用androidx包下的Fragment
-keep public class * extends androidx.fragment.app.Fragment

#如果引用了v4或者v7包
-dontwarn android.support.**

#保持native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

#保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

#在layout中写的onclick方法android:onclick="onClick"，不进行混淆
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

#表示不混淆任何一个View中的setXxx()和getXxx()方法，因为属性动画需要有相应的setter和getter的方法实现，混淆了就无法工作了
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, java.lang.Boolean);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

#保持Parcelable不被混淆
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

#保持Serializable不被混淆
-keepnames class * implements java.io.Serializable

#保持Serializable不被混淆并且enum类也不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#保持枚举enum类不被混淆
-keepclassmembers enum * {
  public static **[] values();
  public static ** valueOf(java.lang.String);
}

#不混淆资源类
-keepclassmembers class **.R$* {
    public static <fields>;
}

#移除Log类打印各个等级日志的代码，打正式包的时候可以做为禁log使用，这里可以作为禁止log打印的功能使用，另外的一种实现方案是通过BuildConfig.DEBUG的变量来控制
-assumenosideeffects class android.util.Log {
    public static *** v(...);
    public static *** i(...);
    public static *** d(...);
    public static *** w(...);
    public static *** e(...);
}

#------------------------------------java相关的-----------------------------------
-dontwarn java.awt.**
-keep class java.awt.** { *; }

-dontwarn javax.**
-keep class javax.** { *; }
#------------------------------------通用的end------------------------------------

#-----------------------------WebView(项目中没有可以忽略)--------------------------
#webView需要进行特殊处理
-keepclassmembers class fqcn.of.javascript.interface.for.Webview {
   public *;
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, jav.lang.String);
}

#------------------------------------项目相关的------------------------------------
-keep class com.sortinghat.funny.bean.** { *; }

#视频库
-keep class com.shuyu.gsyvideoplayer.video.** { *; }
-dontwarn com.shuyu.gsyvideoplayer.video.**
-keep class com.shuyu.gsyvideoplayer.video.base.** { *; }
-dontwarn com.shuyu.gsyvideoplayer.video.base.**
-keep class com.shuyu.gsyvideoplayer.utils.** { *; }
-dontwarn com.shuyu.gsyvideoplayer.utils.**
-keep class tv.danmaku.ijk.** { *; }
-dontwarn tv.danmaku.ijk.**

#阿里云存储
-keep class com.alibaba.sdk.android.oss.** { *; }
-dontwarn okio.**
-dontwarn org.apache.commons.codec.binary.**

#选择城市
-keep class com.lljjcoder.**{*;}

#友盟，秒验，短信验证码
-keep class com.mob.**{*;}
-keep class cn.smssdk.**{*;}
-dontwarn com.mob.**

# 秒验for SecVerify
# for CTCC
-keep class cn.com.chinatelecom.account.**{*;}
# for CUCC
-keep class com.sdk.**{*;}
# for CMCC
-keep class com.cmic.sso.sdk.**{*;}
-keep class com.unicom.xiaowo.account.shield.**{*;}

#友盟和四大厂商推送
-keep class org.android.agoo.xiaomi.MiPushBroadcastReceiver {*;}
-dontwarn com.xiaomi.push.**
-keep public class * extends android.app.Service
-dontwarn com.vivo.push.**
-keep class com.vivo.push.** {*;}
-keep class com.vivo.vms.** {*;}
-ignorewarnings
-keepattributes *Annotation*, Exceptions, InnerClasses, Signature, SourceFile, LineNumberTable
-keep class com.hianalytics.android.** {*;}
-keep class com.huawei.updatesdk.** {*;}
-keep class com.huawei.hms.** {*;}

#穿山甲
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod
-keep class com.bytedance.sdk.openadsdk.** {*;}
-keep public interface com.bytedance.sdk.openadsdk.downloadnew.** {*;}
-keep class com.pgl.sys.ces.* {*;}


# Demo工程里用到了AQuery库，因此需要添加下面的配置
# 请开发者根据自己实际情况给第三方库的添加相应的混淆设置
-dontwarn com.androidquery.**
-keep class com.androidquery.** { *;}

-dontwarn tv.danmaku.**
-keep class tv.danmaku.** { *;}

-dontwarn androidx.**

# 如果使用了微信OpenSDK，需要添加如下配置
-keep class com.tencent.mm.opensdk.** {
    *;
}

-keep class com.tencent.wxop.** {
    *;
}

-keep class com.tencent.mm.sdk.** {
    *;
}
-keep class org.chromium.** {*;}
-keep class org.chromium.** { *; }
-keep class aegon.chrome.** { *; }
-keep class com.kwai.**{ *; }
-dontwarn com.kwai.**
-dontwarn com.kwad.**
-dontwarn com.ksad.**
-dontwarn aegon.chrome.**


#oaid 不同的版本混淆代码不太一致，你注意你接入的oaid版本
 -dontwarn com.bun.**
 -keep class com.bun.** {*;}
 -keep class a.**{*;}
 -keep class XI.CA.XI.**{*;}
 -keep class XI.K0.XI.**{*;}
 -keep class XI.XI.K0.**{*;}
 -keep class XI.vs.K0.**{*;}
 -keep class XI.xo.XI.XI.**{*;}
 -keep class com.asus.msa.SupplementaryDID.**{*;}
 -keep class com.asus.msa.sdid.**{*;}
 -keep class com.huawei.hms.ads.identifier.**{*;}
 -keep class com.samsung.android.deviceidservice.**{*;}
 -keep class com.zui.opendeviceidlibrary.**{*;}
 -keep class org.json.**{*;}
 -keep public class com.netease.nis.sdkwrapper.Utils {public <methods>;}

 #聚合广告start
 -keep class com.bytedance.pangle.** {*;}
 -keep class com.bytedance.sdk.openadsdk.** { *; }
 -keep class com.bytedance.frameworks.** { *; }

 -keep class ms.bd.c.Pgl.**{*;}
 -keep class com.bytedance.mobsec.metasec.ml.**{*;}

 -keep class com.ss.android.**{*;}

 -keep class com.bytedance.embedapplog.** {*;}
 -keep class com.bytedance.embed_dr.** {*;}

 -keep class com.bykv.vk.** {*;}


 #聚合混淆
 -keep class bykvm*.**
 -keep class com.bytedance.msdk.adapter.**{ public *; }
 -keep class com.bytedance.msdk.api.** {
  public *;
 }
 -keep class com.bytedance.msdk.base.TTBaseAd{*;}
 -keep class com.bytedance.msdk.adapter.TTAbsAdLoaderAdapter{
     public *;
     protected <fields>;
 }

 # baidu sdk 不接入baidu sdk可以不引入
 -ignorewarnings
 -dontwarn com.baidu.mobads.sdk.api.**
 -keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
 }

 -keepclassmembers enum * {
     public static **[] values();
     public static ** valueOf(java.lang.String);
 }

 -keep class com.baidu.mobads.** { *; }
 -keep class com.style.widget.** {*;}
 -keep class com.component.** {*;}
 -keep class com.baidu.ad.magic.flute.** {*;}
 -keep class com.baidu.mobstat.forbes.** {*;}


 # Admob 不接入admob sdk可以不引入
 -keep class com.google.android.gms.ads.MobileAds {
  public *;
 }

 #sigmob  不接入sigmob sdk可以不引入
 -dontwarn android.support.v4.**
 -keep class android.support.v4.** { *; }
 -keep interface android.support.v4.** { *; }
 -keep public class * extends android.support.v4.**

 -keep class sun.misc.Unsafe { *; }
 -dontwarn com.sigmob.**
 -keep class com.sigmob.**.**{*;}
  #聚合广告end

#rxffmpeg
 -dontwarn io.microshow.rxffmpeg.**
 -keep class io.microshow.rxffmpeg.**{*;}

  #glide的webp
 -keep public class com.bumptech.glide.integration.webp.WebpImage { *; }
 -keep public class com.bumptech.glide.integration.webp.WebpFrame { *; }
 -keep public class com.bumptech.glide.integration.webp.WebpBitmapFactory { *; }


 -keep class androidx.recyclerview.widget.**{*;}
 -keep class androidx.viewpager2.widget.**{*;}