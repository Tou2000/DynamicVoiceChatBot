import java.net.URI

plugins {
    id("java")
}

group = "de.toutariel.dynamic_voice_chat_bot"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        name = "m2-dv8tion"
        url =  URI("https://m2.dv8tion.net/releases")
    }

}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("net.dv8tion:JDA:4.4.0_350")
}

tasks.test {
    useJUnitPlatform()
}
