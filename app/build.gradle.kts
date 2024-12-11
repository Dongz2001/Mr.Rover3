plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}
val navSdkVersion by extra("6.0.0")

android {
    namespace = "com.example.mrrover"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.mrrover"
        minSdk = 26
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("com.hbb20:ccp:2.5.0")
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.android.gms:play-services-auth:19.0.0")
    implementation("com.google.android.play:integrity:1.1.0")
    implementation("com.github.dhaval2404:imagepicker:2.1")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation ("com.google.android.gms:play-services-maps:18.0.2")
    implementation ("com.google.android.gms:play-services-location:21.0.1")
    implementation ("com.google.maps.android:android-maps-utils:2.2.3")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.firebaseui:firebase-ui-firestore:8.0.2")
    implementation ("com.google.firebase:firebase-firestore:24.5.0")
    implementation ("com.firebaseui:firebase-ui-firestore:8.0.0")
    implementation ("com.google.firebase:firebase-database:20.0.0")
    implementation ("com.google.firebase:firebase-auth:22.1.1")
    implementation ("com.google.firebase:firebase-storage:20.0.0")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation ("com.google.firebase:firebase-bom:32.2.0")
    implementation ("com.google.firebase:firebase-firestore")
    implementation ("com.google.firebase:firebase-auth")
    implementation(libs.firebase.messaging)
    implementation ("com.github.jd-alexander:library:1.1.0")
    implementation ("com.google.android.material:material:1.1.0")
    implementation ("androidx.work:work-runtime:2.9.1")
    implementation("com.google.guava:guava:30.1-android")

    //api("com.google.android.libraries.navigation:navigation:${navSdkVersion}")

    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
    implementation(libs.firebase.storage)
    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    //Add those line in dependencies
    implementation(files("libs/activation.jar"))
    implementation(files("libs/additionnal.jar"))
    implementation(files("libs/mail.jar"))

}