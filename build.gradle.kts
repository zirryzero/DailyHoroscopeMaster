// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        // ✅ Plugin de Google Services para AdMob
        classpath("com.google.gms:google-services:4.4.1")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}
