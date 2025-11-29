    import org.jetbrains.compose.desktop.application.dsl.TargetFormat
    import org.jetbrains.kotlin.gradle.dsl.JvmTarget

    plugins {
        alias(libs.plugins.kotlinMultiplatform)
        alias(libs.plugins.androidApplication)
        alias(libs.plugins.composeMultiplatform)
        alias(libs.plugins.composeCompiler)
        alias(libs.plugins.composeHotReload)

        id("com.google.gms.google-services") version "4.4.0" apply false
    }

    kotlin {
        androidTarget {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_11)
            }
        }

        jvm()

        sourceSets {
            val commonMain by getting {
                dependencies {
                    implementation(compose.runtime)
                    implementation(compose.foundation)               // HorizontalPager 포함
                    implementation(compose.material3)
                    implementation(compose.materialIconsExtended)
                }
            }
            androidMain.dependencies {
                implementation(compose.preview)
                implementation(libs.androidx.activity.compose)
                implementation("androidx.work:work-runtime-ktx:2.9.0")


                //firebase firestore
                //implementation("com.google.firebase:firebase-bom:33.5.1")


                implementation("com.google.firebase:firebase-firestore-ktx:25.0.0")
                implementation("com.google.firebase:firebase-auth-ktx:23.0.0")
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
                implementation(compose.materialIconsExtended)
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")
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
        namespace = "com.example.smarttodo"
        compileSdk = libs.versions.android.compileSdk.get().toInt()

        sourceSets {
            getByName("main") {
                java.srcDir("src/androidMain/kotlin")
                res.srcDir("src/androidMain/res")

                // 🔥 Firebase google-services.json이 여기 있다고 명시적으로 알려주기
              //  resources.srcDir("src/androidMain")
            }
        }
        defaultConfig {
            applicationId = "com.example.smarttodo"
            minSdk = libs.versions.android.minSdk.get().toInt()
            targetSdk = libs.versions.android.targetSdk.get().toInt()
            versionCode = 1
            versionName = "1.0"
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
    }

    dependencies {
        debugImplementation(compose.uiTooling)
        implementation(compose.runtime)
        implementation(compose.foundation)
        implementation(compose.material3)
        implementation(compose.materialIconsExtended)
    }

    compose.desktop {
        application {
            mainClass = "com.example.smarttodo.MainKt"

            nativeDistributions {
                targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
                packageName = "com.example.smarttodo"
                packageVersion = "1.0.0"
            }
        }
    }
