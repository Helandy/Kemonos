# Keep enough source info for useful crash stack traces.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Retrofit/Gson rely on generic signatures and runtime annotations.
-keepattributes Signature,InnerClasses,EnclosingMethod
-keepattributes RuntimeVisibleAnnotations,RuntimeVisibleParameterAnnotations,AnnotationDefault

# Preserve Gson-annotated fields while still allowing class/field obfuscation.
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.Expose <fields>;
}

# DTO payload models parsed reflectively by Gson.
-keep class su.afk.kemonos.**.dto.** {
    <fields>;
}

-keep class su.afk.kemonos.common.data.** {
    <fields>;
}
