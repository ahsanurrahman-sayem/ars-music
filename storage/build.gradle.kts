plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.ars.storage"
    compileSdk = 34
    defaultConfig { minSdk = 26 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
    arg("room.generateKotlin", "true")
}

dependencies {
    implementation(project(":core"))
    implementation(project(":domain"))

    implementation(libs.hilt.android)
    implementation(libs.bundles.room)
    implementation(libs.bundles.coroutines)
    implementation(libs.datastore.preferences)
    implementation(libs.timber)

    ksp(libs.hilt.compiler)
    ksp(libs.room.compiler)

    testImplementation(libs.bundles.testing.unit)
    testImplementation(libs.room.testing)
}
