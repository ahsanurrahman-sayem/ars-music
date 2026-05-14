plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.ars.data"
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
    implementation(libs.timber)

    ksp(libs.hilt.compiler)

    testImplementation(libs.bundles.testing.unit)
}
