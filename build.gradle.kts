plugins {
    //trick: for the same plugin versions in all sub-modules
    alias(libs.plugins.androidApplication).apply(false)
    alias(libs.plugins.androidLibrary).apply(false)
    alias(libs.plugins.kotlinAndroid).apply(false)
    alias(libs.plugins.kotlinMultiplatform).apply(false)
    alias(libs.plugins.compose.compiler).apply(false)
    alias(libs.plugins.sqldelight).apply(false)
    id("com.google.devtools.ksp").version("2.0.0-1.0.24").apply(false)
    id("com.rickclephas.kmp.nativecoroutines").version("1.0.0-ALPHA-38").apply(false)
}
