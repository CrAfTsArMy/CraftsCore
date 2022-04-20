# CraftsCore
![GitHub release (latest by date)](https://img.shields.io/github/v/release/CrAfTsArMy/CraftsCore)
![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/CrAfTsArMy/CraftsCore)
![GitHub](https://img.shields.io/github/license/CrAfTsArMy/CraftsCore)
![GitHub all releases](https://img.shields.io/github/downloads/CrAfTsArMy/CraftsCore/total)
![GitHub issues](https://img.shields.io/github/issues-raw/CrAfTsArMy/CraftsCore)
![YouTube Channel Subscribers](https://img.shields.io/youtube/channel/subscribers/UCtBJZHmZqOvWJ0t8hjhWSAA?label=CraftsCodesArmy&style=flat)
![Twitch Status](https://img.shields.io/twitch/status/crafts__army?label=CrAfTs__ArMy)

### Maven
```xml
</repositories>
  ...
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
  <repository> 
    <id>jcenter</id>
    <url>https://jcenter.bintray.com</url>
    <name>jcenter-bintray</name>
  </repository>
</repositories>
```
```xml
<dependencies>
  ...
  <dependency>
    <groupId>com.github.CrAfTsArMy</groupId>
    <artifactId>CraftsCore</artifactId>
    <version>v3.0-SNAPSHOT</version>
  </dependency>
  <dependency>
    <groupId>com.squareup.okhttp</groupId>
    <artifactId>okhttp</artifactId>
    <version>2.7.5</version>
  </dependency>
  <dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>okhttp</artifactId>
    <version>5.0.0-alpha.6</version>
  </dependency>
  <dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.9.0</version>
  </dependency>
</dependencies>
```

### Gradle
```gradle
repositories {
  ...
  maven { url 'https://jitpack.io' }
  mavenCentral()
  jcenter()
}
```
```gradle
dependencies {
  ...
  implementation 'com.github.CrAfTsArMy:CraftsCore:3.0-SNAPSHOT'
  implementation 'club.minnced:java-discord-rpc:2.0.2'
  implementation 'com.squareup.okhttp3:okhttp:5.0.0-alpha.6'
  implementation 'com.google.code.gson:gson:2.9.0'
}
```

## Issues
If you find any issues from the plugin or documentation please [open up issue](https://github.com/CrAfTsArMy/CraftsCore/issues)
