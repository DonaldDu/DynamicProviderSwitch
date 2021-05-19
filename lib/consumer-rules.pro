-keep class org.chickenhook.restrictionbypass.NativeReflectionBypass

-keepclasseswithmembers class * {
    native <methods>;
}