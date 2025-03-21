import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.project.feelrobot"
    compileSdk = 35

    val envFile = rootProject.file(".env")
    val props = Properties()
    var apiBaseUrl: String = "http://10.0.2.2:8080/" // 기본값

    // .env 파일이 존재하면 환경 변수 로드
    if (envFile.exists()) {
        props.load(envFile.inputStream())
        apiBaseUrl = props.getProperty("API_BASE_URL", apiBaseUrl)
    }

    // 시스템 환경변수 API_BASE_URL 우선 적용
    if (project.hasProperty("API_BASE_URL")) {
        apiBaseUrl = project.property("API_BASE_URL") as String
    }

    defaultConfig {
        applicationId = "com.project.feelrobot"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // API_BASE_URL을 모든 빌드 타입에서 동일하게 사용
        buildConfigField("String", "API_BASE_URL", "\"$apiBaseUrl\"")    }

    buildFeatures {
        buildConfig = true // BuildConfig 기능 활성화
    }

    buildTypes {
        debug {
            // 기본적으로 .env에서 설정한 API_BASE_URL 사용
            buildConfigField("String", "API_BASE_URL", "\"$apiBaseUrl\"")
        }

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // 기본적으로 .env에서 설정한 API_BASE_URL 사용
            buildConfigField("String", "API_BASE_URL", "\"$apiBaseUrl\"")
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
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson) // JSON 변환을 위한 Gson Converter

    // OkHttp
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor) // 네트워크 로그 확인을 위한 Interceptor

    // data store
    implementation(libs.androidx.datastore.preferences)

}