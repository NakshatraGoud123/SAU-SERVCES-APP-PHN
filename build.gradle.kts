// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApp) apply false
    alias(libs.plugins.kotlinCompose) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
}
