# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
#-keep public class com.vito.ad.managers.AdManager { *; }
#-keep public class com.vito.ad.channels.** { *; }
#-keep public class com.vito.ad.base.entity.** { *; }
#-keep public class com.vito.ad.base.task.** { *; }
#-keep public class com.vito.ad.services.** { *; }
#-keep public class com.vito.ad.views.activitys.** { *; }
#-keep public class com.vito.ad.views.webview.** { *; }
#-keep public class com.vito.utils.** { *; }
#-keep public class com.vito.ad.base.interfaces.IPrepareCompleteCallBack { *; }
#-keep public class com.vito.ad.base.interfaces.IPullAppEventListenertener { *; }
#-keep public class com.zhouwei.** { *; }