# ============ GENERAL ============
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes Exceptions

# ============ RETROFIT + OKHTTP ============
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class retrofit2.** { *; }
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.**

# ============ GSON ============
-keep class com.google.gson.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep API request/response models
-keep class com.junaidjamshid.healthloop.data.remote.** { *; }

# ============ ROOM ============
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# ============ HILT ============
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# ============ COMPOSE ============
-dontwarn androidx.compose.**

# ============ VICO CHARTS ============
-keep class com.patrykandpatrick.vico.** { *; }

# ============ DOMAIN MODELS ============
-keep class com.junaidjamshid.healthloop.domain.model.** { *; }
-keep class com.junaidjamshid.healthloop.data.local.entity.** { *; }