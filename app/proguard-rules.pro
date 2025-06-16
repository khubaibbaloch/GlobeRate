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

# Retrofit (keep data models and service interfaces)
-keep class com.globerate.app.data.model.** { *; }
-keep interface com.globerate.app.data.remote.** { *; }

# Retrofit generic type fix
-keepattributes Signature
-keepattributes *Annotation*

# Retrofit/Gson keep annotations
-keep class com.google.gson.annotations.SerializedName

# ViewModel and StateFlow
-keep class androidx.lifecycle.ViewModel { *; }
-keep class kotlinx.coroutines.flow.** { *; }

# If using sealed classes like RatesUiState
-keep class com.globerate.app.ui.screens.home.viewmodel.states.RatesUiState {
    *;
}
-keep class com.globerate.app.ui.screens.home.viewmodel.states.RatesUiState$* {
    *;
}

-keep class retrofit2.** { *; }
-keep interface com.globerate.app.data.remote.CurrencyApiService { *; }


