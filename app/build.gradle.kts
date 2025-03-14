import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.project.feelrobot"
    compileSdk = 35

    val localPropertiesFile = rootProject.file("local.properties")
    var apiBaseUrl: String = "http://10.0.2.2:8080/" // 기본값

    // 시스템 환경변수 API_BASE_URL 우선 적용 (릴리스 빌드 시 사용)
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
        buildConfigField("String", "API_BASE_URL", "\"$apiBaseUrl\"")
    }

    buildFeatures {
        buildConfig = true // BuildConfig 기능 활성화
    }

    buildTypes {
        debug {
            // 에뮬레이터 -> 로컬 서버
            buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2:8080/\"") // 로컬 서버
        }

        release {
            // 1) .env 파일 로드
            val envFile = rootProject.file(".env")
            val props = Properties()

            if (envFile.exists()) {
                props.load(envFile.inputStream())
            }

            // 2) .env 에서 실제 서버 주소 가져오기
            val releaseServerUrl = props.getProperty("RELEASE_SERVER_URL", "https://default-server.com/")

            // 3) release 빌드에 실제 서버 주소 주입
            buildConfigField("String", "API_BASE_URL", "\"$releaseServerUrl\"")

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


}