plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.ars.player"
    compileSdk = 34
    defaultConfig { minSdk = 26 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":domain"))
    implementation(project(":shared-utils"))

    implementation(libs.hilt.android)
    implementation(libs.bundles.media3)
    implementation(libs.bundles.coroutines)
    implementation(libs.timber)
    implementation(libs.androidx.core.ktx)

    ksp(libs.hilt.compiler)

    testImplementation(libs.bundles.testing.unit)
}
