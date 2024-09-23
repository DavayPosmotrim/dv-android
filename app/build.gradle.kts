plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.serialization)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.davay.android"
    compileSdk = libs.versions.compileSdk.get().toInt()

    val sdkKey: String = project.findProperty("mytracker_sdk_key") as String? ?: ""

    defaultConfig {
        applicationId = "com.davay.android"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField("String", "MYTRACKER_SDK_KEY", "\"$sdkKey\"")
        }
        release {
            buildConfigField("String", "MYTRACKER_SDK_KEY", "\"$sdkKey\"")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.valueOf(libs.versions.java.get())
        targetCompatibility = JavaVersion.valueOf(libs.versions.java.get())
    }
    kotlinOptions {
        jvmTarget = JavaVersion.valueOf(libs.versions.java.get()).toString()
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.dagger.impl)
    ksp(libs.dagger.compiler)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.coil)
    implementation(libs.flexbox)
    implementation(libs.circle.indicator)
    implementation(libs.physicslayout)

    ksp(libs.room.compiler)
    implementation(libs.bundles.room)
    implementation(libs.encrypted.sharedpreferences)

    implementation(libs.bundles.ktor)

    implementation(libs.tracker)

    implementation(project(":uikit"))
}