plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.myjob.real_time_chat_final"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.myjob.real_time_chat_final"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        viewBinding = true
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

    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.okhttp)

    implementation(libs.rxjava)
    implementation(libs.rxandroid)
    implementation(libs.stomp) // Đảm bảo Stomp được gọi từ Version Catalog

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4") // Kiểm tra hoặc cập nhật phiên bản mới nhất
    implementation ("androidx.core:core:1.6.0")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")
    implementation ("com.google.android.material:material:1.11.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.3")
    implementation ("androidx.fragment:fragment:1.6.2") // Phiên bản mới nhất tính đến tháng 04/2025
    implementation ("androidx.viewpager2:viewpager2:1.0.0")
    implementation ("de.hdodenhof:circleimageview:3.1.0") // Phiên bản mới nhất
    implementation ("androidx.activity:activity:1.8.0") // Cho Activity Result API

}
