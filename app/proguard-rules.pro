# Keep line numbers for debugging
-keepattributes SourceFile,LineNumberTable,*Annotation*,Signature,InnerClasses,EnclosingMethod

# Keep Kotlin Serialization classes and serializers
-keep @kotlinx.serialization.Serializable class * { *; }
-keepclassmembers class * implements kotlinx.serialization.KSerializer {
    public static ** INSTANCE;
}
-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
    @kotlinx.serialization.Serializer *;
}

# Keep Navigation Screen routes
-keep class com.nisr.sauservices.ui.Screen** { *; }
-keepclassmembers class com.nisr.sauservices.ui.Screen** { *; }
