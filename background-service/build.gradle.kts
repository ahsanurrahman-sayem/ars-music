plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.ars.background"
    compileSdk = 34
    defaultConfig { minSdk = 26 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions { jvmTarget = "21" }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":domain"))
    implementation(project(":storage"))
    implementation(project(":shared-utils"))

    implementation(libs.hilt.android)
    implementation(libs.bundles.coroutines)
    implementation(libs.bundles.media3)
    implementation(libs.androidx.core.ktx)
    implementation(libs.timber)

    ksp(libs.hilt.compiler)
}
