plugins {
    id 'com.android.application'
}
android {
    namespace 'com.example.budget'
    compileSdk 34

    defaultConfig {
        applicationId "com.example.budget"
        minSdk 24
        targetSdk 34
        versionCode 1
        versionName "1.0"

        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }

    buildFeatures {
        viewBinding true
    }
}
dependencies {
    implementation 'androidx.appcompat:appcompat:1.7.0'
    implementation 'com.google.android.material:material:1.12.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'androidx.recyclerview:recyclerview:1.3.2'
    implementation 'androidx.cardview:cardview:1.0.0'
    implementation 'androidx.lifecycle:lifecycle-runtime:2.8.4'
    implementation 'com.github.PhilJay:MPAndroidChart:v3.1.0' // charts
    implementation 'org.jetbrains:annotations:24.1.0'
}