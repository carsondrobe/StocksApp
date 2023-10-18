import org.gradle.initialization.Environment.Properties
import java.util.*
val properties = Properties().apply {
    load(rootProject.file("local.properties").reader())
}
val myProp = properties["tiingoAPI"]
plugins {
    id("com.android.application")
}
android {
    namespace = "com.example.stocksapp"
    compileSdk = 33
    defaultConfig {
        applicationId = "com.example.stocksapp"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        buildConfig = true // Enable custom BuildConfig fields
    }
    buildTypes {
        debug {
            buildConfigField("String", "TIINGO_API_KEY", "\"$myProp\"")
        }
        release {
            buildConfigField("String", "TIINGO_API_KEY", "\"$myProp\"")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.google.code.gson:gson:2.8.8")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

}