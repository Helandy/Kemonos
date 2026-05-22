# Keep enough source info for useful crash stack traces.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Retrofit/Gson rely on generic signatures and runtime annotations.
-keepattributes Signature,InnerClasses,EnclosingMethod
-keepattributes RuntimeVisibleAnnotations,RuntimeVisibleParameterAnnotations,AnnotationDefault

# DTO payload fields parsed reflectively by Gson. Class retention is driven by
# Retrofit signatures and direct type references, so only field names need help.
-keepclassmembers class su.afk.kemonos.**.dto.** {
    <fields>;
}
