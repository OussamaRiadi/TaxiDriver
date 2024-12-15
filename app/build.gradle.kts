plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services) // For Firebase or Play services, if applicable
}

android {
    namespace = "com.example.taxidriver"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.taxidriver"
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

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Google Maps
    implementation (libs.play.services.maps)
    implementation (libs.zxing.android.embedded)
    implementation (libs.core)

    implementation (libs.androidx.navigation.fragment.ktx)
    implementation ("androidx.navigation:navigation-ui-ktx:2.8.5")
    implementation ("com.google.android.gms:play-services-base:18.1.0")


    // AndroidX and Material Components
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Firebase Authentication (if applicable)
    // Remove if not used
    implementation(libs.firebase.auth)
    implementation(libs.play.services.location)

    // Testing libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
