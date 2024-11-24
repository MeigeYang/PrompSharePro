plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.promptsharepro"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.promptsharepro"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    testOptions {
        animationsDisabled = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(platform("com.google.firebase:firebase-bom:32.7.2"))
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-analytics")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.hamcrest:hamcrest:2.2")

    androidTestImplementation(libs.ext.junit)
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("org.hamcrest:hamcrest:2.2")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1") {
        exclude(group = "org.checkerframework", module = "checker")
    }
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.2.0")
}

configurations.all {
    resolutionStrategy {
        force("org.hamcrest:hamcrest:2.2")
        force("junit:junit:4.13.2")
    }
}
