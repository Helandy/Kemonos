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

############################################
# Base / Debuggable stacktraces
############################################

# Чтобы крэши читались (можно убрать когда всё стабильно)
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Kotlin metadata (часто полезно для reflection/DI)
-keep class kotlin.Metadata { *; }

############################################
# Retrofit / OkHttp
############################################

-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**

# Retrofit uses generic signatures
-keepattributes Signature
-keepattributes InnerClasses,EnclosingMethod
-keepattributes RuntimeVisibleAnnotations,RuntimeVisibleParameterAnnotations,AnnotationDefault

############################################
# Gson (SerializedName)
############################################

-keepattributes *Annotation*

# Сохранить поля, помеченные @SerializedName
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Если используешь @Expose
-keepclassmembers class * {
    @com.google.gson.annotations.Expose <fields>;
}

# На всякий: не резать модели DTO (самый частый источник проблем после R8)
-keep class su.afk.kemonos.**.dto.** { *; }
-keep class su.afk.kemonos.common.data.** { *; }

############################################
# Coroutines
############################################

-dontwarn kotlinx.coroutines.**

############################################
# Hilt / Dagger
############################################

-dontwarn dagger.**
-dontwarn javax.inject.**

# Обычно этого достаточно, но если где-то ловишь DI-странности — раскомментируй:
# -keep class dagger.hilt.** { *; }
# -keep class javax.inject.** { *; }

############################################
# Compose
############################################

-dontwarn androidx.compose.**
-dontwarn kotlin.jvm.internal.**

############################################
# (Опционально) kotlinx.serialization
# Оставь, если где-то ещё используешь @Serializable и kotlinx-конвертер
############################################

#-keep class kotlinx.serialization.** { *; }
#-keepclassmembers class ** {
#    *** serializer(...);
#}
#-keepclassmembers class kotlinx.serialization.internal.** { *; }

############################################
# Если используешь Moshi/Adapters — не нужно (удали)
############################################

# (ничего)
