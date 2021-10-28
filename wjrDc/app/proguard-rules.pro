#指定代码的压缩级别
-optimizationpasses 5
 #包名不混合大小写
-dontusemixedcaseclassnames
#不忽略非公共的库类
-dontskipnonpubliclibraryclasses
 #优化/不优化输入的类文件
-dontoptimize
#预校验
-dontpreverify
#混淆时是否记录日志
-verbose
#混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
#忽略警告
-ignorewarnings
#保护注解
-keepattributes *Annotation*
#保护反射的正常调用
-keepattributes Signature

-keepattributes EnclosingMethod
 #不混淆哪些类

#   -keep public class * extends android.app.Fragment


#############################################
#
# Android开发中一些需要保留的公共部分
#
#############################################

# 保留我们使用的四大组件，自定义的Application等等这些类不被混淆
# 因为这些子类都有可能被外部调用
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View

# 保留support下的所有类及其内部类
-keep class android.support.** {*;}
# 保留我们自定义控件（继承自View）不被混淆
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
# 保留继承的
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
-keep public class * extends android.support.annotation.**



# OkHttp3
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *;}
-dontwarn okio.**
# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# RxJava RxAndroid
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}


# Gson
-dontwarn com.google.gson.**
-keep class com.google.gson.**{*;}
-keep interface com.google.gson.**{*;}


# 使用Gson时需要配置Gson的解析对象及变量都不混淆。不然Gson会找不到变量。
# 将下面替换成自己的实体类
-keep class com.example.bean.** { *; }

 #指定不混淆所有的JNI方法
-keepclasseswithmembernames class * {
    native <methods>;
}
# 保留枚举类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
 #不混淆Activity中参数类型为View的所有方法

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
#不混淆Parcelable和它的子类，还有Creator成员变量
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
 #不混淆Serializable接口

-keepnames class * implements java.io.Serializable

#不混淆Serializable接口的子类中指定的某些成员变量和方法

-keepclassmembers class * implements java.io.Serializable {
 static final long serialVersionUID;
 private static final java.io.ObjectStreamField[] serialPersistentFields;
 private void writeObject(java.io.ObjectOutputStream);
 private void readObject(java.io.ObjectInputStream);
 java.lang.Object writeReplace();
 java.lang.Object readResolve();

}

#不混淆R类里及其所有内部static类中的所有static变量字段

-keepclassmembers class **.R$* {
 public static<fields>;
}



 #apk包内所有class的内部结构

-dump class_files.txt

 #未混淆的类和成员

-printseeds seeds.txt

  #列出从apk中删除的代码

-printusage unused.txt

  #混淆前后的映射

-printmapping mapping.txt
#忽略某个类的警告
-dontwarn com.unionpay.**

#不混淆某个类和成员变量

-keep class com.unionpay.** { *; }
# 删除代码中Log相关的代码
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}
# 对于带有回调函数的onXXEvent、**On*Listener的，不能被混淆
-keepclassmembers class * {
    void *(**On*Event);
    void *(**On*Listener);
}



-dontwarn rx.**
-keep class rx.**{*;}
-keep class com.cashloans.cashcloud.models.* {*;}


-keep class uk.co.senab.photoview** { *; }
-keep interface uk.co.senab.photoview** { *; }

