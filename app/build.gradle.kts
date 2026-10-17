plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.afinal"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.afinal"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // Google Play Services for Location
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // Optional: Firebase for real-time location sharing
    implementation("com.google.firebase:firebase-database:20.2.2")
    implementation(libs.gridlayout)

    // SMS Permissions (No extra dependency needed for basic SMS sharing)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}