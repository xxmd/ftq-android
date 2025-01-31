plugins {
    kotlin("android")
    kotlin("kapt")
    id("com.android.library")
}

dependencies {
    implementation(project(":common"))
    implementation(project(":core"))
    implementation(project(":service"))

    implementation(libs.kotlin.coroutine)
    implementation(libs.androidx.core)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.coordinator)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.viewpager)
    implementation(libs.google.material)

    implementation ("com.github.bumptech.glide:glide:4.15.1")  // Glide 库
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")  // Glide 注解处理器
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

}
