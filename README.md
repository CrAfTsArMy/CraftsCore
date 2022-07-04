# CraftsCore
![GitHub release (latest by date)](https://img.shields.io/github/v/release/CrAfTsArMy/CraftsCore)
![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/CrAfTsArMy/CraftsCore)
![GitHub](https://img.shields.io/github/license/CrAfTsArMy/CraftsCore)
![GitHub all releases](https://img.shields.io/github/downloads/CrAfTsArMy/CraftsCore/total)
![GitHub issues](https://img.shields.io/github/issues-raw/CrAfTsArMy/CraftsCore)

### Maven
```xml
<repositories>
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
  
  <!--
  // If you have specified a Minecraft Jar as a library, then you can omit
  // this repository!
  -->
  <repository>
    <id>spigot-repo</id>
    <url>https://hub.spigotmc.org/nexus/content/repositories/snapshots/</url>
  </repository>
  
</repositories>
```
```xml
<dependencies>
  ...
  <dependency>
    <groupId>com.github.CrAfTsArMy</groupId>
    <artifactId>CraftsCore</artifactId>
    <version>v3.4-SNAPSHOT</version>
  </dependency>
  <dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>okhttp</artifactId>
    <version>4.10.0</version>
  </dependency>
  <dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.9.0</version>
  </dependency>
  <dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.29</version>
  </dependency>
  
  <!--
  // Replace "%VERSION%" below with your version of Minecraft.
  // Currently all versions from 1.13 are supported!
  // If you do not want to use the core for a Minecraft plugin,
  // please replace "%VERSION%" with "1.13". You can also add 
  // the corresponding Minecraft Server Jar, then you can omit 
  // this dependency.
  -->
  <dependency>
    <groupId>org.spigotmc</groupId>
    <artifactId>spigot-api</artifactId>
    <version>%VERSION%-R0.1-SNAPSHOT</version>
    <scope>provided</scope>
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
  
  // If you have specified a Minecraft Jar as library, then you can omit
  // the following two repository!
  maven {
    name = 'spigotmc-repo'
    url = 'https://hub.spigotmc.org/nexus/content/repositories/snapshots/'
  }
  maven {
    name = 'sonatype'
    url = 'https://oss.sonatype.org/content/groups/public/'
  }
    
}
```
```gradle
dependencies {
  ...
  implementation 'com.github.CrAfTsArMy:CraftsCore:v3.3-SNAPSHOT'
  implementation 'club.minnced:java-discord-rpc:2.0.2'
  implementation 'com.squareup.okhttp3:okhttp:4.10.0'
  implementation 'com.google.code.gson:gson:2.9.0'
  implementation 'mysql:mysql-connector-java:8.0.29'
  
  // Replace "%VERSION%" below with your version of Minecraft.
  // Currently all versions from 1.13 are supported!
  // If you do not want to use the core for a Minecraft plugin,
  // please replace "%VERSION%" with "1.13". You can also add 
  // the corresponding Minecraft Server Jar, then you can omit 
  // this implementation.
  implementation 'org.spigotmc:spigot-api:%VERSION%-R0.1-SNAPSHOT'
}
```

## Issues
If you find any issues from the plugin or documentation please [open up issue](https://github.com/CrAfTsArMy/CraftsCore/issues)

## CraftsBlock
**Who or what is CraftsBlock?**
> CraftsBlock is a fairly new community server, which deals with the topic of programming. On our server you can ask questions about programming. But of course you can also just chill with the community.
>
> Also we are currently building our own small company, where you can ask certain questions for free, for your own app and or other programs.
>
> But we also offer you a free cloud where you get a storage limit of **momentarily** 500MB for free. (Later it will be 2.5GB!).

**Is everything really free?**
> CraftsBlock stands for implementing everything for free! That means you can get **all** services from CraftsBlock for free. However, we are currently working on a system where you can earn points by being active. With these points you can later buy more things, like the VIP rank.

**How do you find us?**
> We have a website where you can read more about it! https://craftsblock.de
>
> Of course you can also come to our Discord Server! https://discord.gg/craftsblock
>
> We are looking forward to you!
