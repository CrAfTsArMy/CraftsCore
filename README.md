# CraftsCore
![Latest Release on Maven](https://repo.craftsblock.de/api/badge/latest/releases/de/craftsblock/craftscore?color=40c14a&name=CraftsCore&prefix=v)
![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/CrAfTsArMy/CraftsCore)
![GitHub](https://img.shields.io/github/license/CrAfTsArMy/CraftsCore)
![GitHub issues](https://img.shields.io/github/issues-raw/CrAfTsArMy/CraftsCore)

### Maven
```xml
<repositories>
  ...
  <repository>
    <id>craftsblock-releases</id>
    <name>CraftsBlock Repositories</name>
    <url>https://repo.craftsblock.de/releases</url>
  </repository>
</repositories>
```
```xml
<dependencies>
  ...
  <dependency>
    <groupId>de.craftsblock</groupId>
    <artifactId>craftscore</artifactId>
    <version>X.X.X-SNAPSHOT</version>
  </dependency>
</dependencies>
```

### Gradle
```gradle
repositories {
  ...
  maven { url "https://repo.craftsblock.de/releases" }
  mavenCentral()
}
```
```gradle
dependencies {
  ...
  implementation 'de.craftsblock:craftscore:X.X.X-SNAPSHOT'
}
```

## Open Source Licenses
We are using some third party open source libraries. Below you find a list of all third party open source libraries used:
| Name                                                                   | Description                                                                                                                           | Licecnse                                                                                         |
| ---------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------ |
| [MySQL Connector-J](https://github.com/mysql/mysql-connector-j)        | MySQL Connector/J                                                                                                                     | [GPLv2 with FOSS exception](https://github.com/mysql/mysql-connector-j/blob/release/8.x/LICENSE) |
| [GSON](https://github.com/google/gson)                                 | A Java serialization/deserialization library to convert Java Objects into JSON and back                                               | [Apache License 2.0](https://github.com/google/gson/blob/main/LICENSE)                           |

## Issues
If you find any issues from the plugin or documentation please [open up issue](https://github.com/CrAfTsArMy/CraftsCore/issues)
