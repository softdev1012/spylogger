plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.spy.logger"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.spy.logger"
        minSdk = 21
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.lifecycle)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    def cameraxVersion = "1.3.0"  // Use the latest available version

    implementation "androidx.camera:camera-camera2:$cameraxVersion"
    implementation "androidx.camera:camera-lifecycle:$cameraxVersion"
    implementation "androidx.camera:camera-view:$cameraxVersion"
    implementation "androidx.camera:camera-extensions:$cameraxVersion"

    implementation "org.jetbrains.kotlin:kotlin-stdlib:1.7.10" // Example, replace with exact latest version



}