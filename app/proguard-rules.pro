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

# Keep Supabase Models and Data Classes
-keep class com.nisr.sauservices.data.model.** { *; }

# Keep Room entities and database classes
-keep class com.nisr.sauservices.data.local.** { *; }

# Keep Hilt / Dagger generated components
-keepattributes *Annotation*,EnclosingMethod,InnerClasses
-keep class androidx.hilt.navigation.compose.** { *; }
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.internal.GeneratedComponent { *; }
-allowaccessmodification

# Keep Razorpay SDK integration
-keep class com.razorpay.** { *; }
-dontwarn com.razorpay.**

# Keep OkHttp & Ktor networking
-dontwarn okhttp3.**
-dontwarn io.ktor.**
-keepattributes Signature

