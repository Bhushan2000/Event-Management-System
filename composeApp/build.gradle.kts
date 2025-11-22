import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlinSerialization) // ✅ ADD THIS
}

kotlin {

    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    jvm()

    js {
        browser()
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            // Material icons (Android only)
            implementation("androidx.compose.material:material-icons-core:1.7.5")
            implementation("androidx.compose.material:material-icons-extended:1.7.5")
            implementation("androidx.navigation:navigation-compose:2.7.7")
            val ktor_version = "3.3.2"
            val koin_version = "4.1.0"
            // Ktor client core
            implementation("io.ktor:ktor-client-core:${ktor_version}")
            implementation("io.ktor:ktor-client-okhttp:${ktor_version}")
            // Ktor Android engine for network requests
            implementation("io.ktor:ktor-client-android:${ktor_version}")
            // Content negotiation for handling data formats (e.g., JSON)
            implementation("io.ktor:ktor-client-content-negotiation:${ktor_version}")
            // Kotlinx serialization for JSON serialization/deserialization
            implementation("io.ktor:ktor-serialization-kotlinx-json:${ktor_version}")
            // Optional: Logging for network requests
            implementation("io.ktor:ktor-client-logging:${ktor_version}")

            implementation("io.insert-koin:koin-compose:$koin_version")
            implementation("io.insert-koin:koin-compose-viewmodel:$koin_version")
            implementation("io.insert-koin:koin-compose-viewmodel-navigation:$koin_version")

            implementation("com.google.maps.android:maps-compose:4.3.3")
            implementation("com.google.android.gms:play-services-maps:18.2.0")

            implementation("io.coil-kt.coil3:coil-compose:3.3.0")
            implementation("io.coil-kt.coil3:coil-network-okhttp:3.3.0")
            implementation("com.google.accompanist:accompanist-pager:0.30.1")
            implementation("com.google.accompanist:accompanist-pager-indicators:0.30.1")
            implementation("com.google.accompanist:accompanist-swiperefresh:0.36.0")

        }

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(projects.shared)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }
}

android {
    namespace = "com.bhushantechsolutions.eventmanagementsystem"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.bhushantechsolutions.eventmanagementsystem"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
        val mapsApiKey: String = project.findProperty("MAPS_API_KEY")?.toString() ?: ""
        manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey
        buildConfigField("String", "MAPS_API_KEY", "\"$mapsApiKey\"")

    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "com.bhushantechsolutions.eventmanagementsystem.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.bhushantechsolutions.eventmanagementsystem"
            packageVersion = "1.0.0"
        }
    }
}
