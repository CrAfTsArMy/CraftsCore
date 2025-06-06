# CraftsCore
![Latest Release on Maven](https://repo.craftsblock.de/api/badge/latest/releases/de/craftsblock/craftscore/all?color=40c14a&name=CraftsCore&prefix=v)
![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/CrAfTsArMy/CraftsCore)
![GitHub](https://img.shields.io/github/license/CrAfTsArMy/CraftsCore)
![GitHub issues](https://img.shields.io/github/issues-raw/CrAfTsArMy/CraftsCore)

## Modules

![Latest Release on Maven](https://repo.craftsblock.de/api/badge/latest/releases/de/craftsblock/craftscore/actions?color=40c14a&name=actions&prefix=v)
![Latest Release on Maven](https://repo.craftsblock.de/api/badge/latest/releases/de/craftsblock/craftscore/all?color=40c14a&name=all&prefix=v)
![Latest Release on Maven](https://repo.craftsblock.de/api/badge/latest/releases/de/craftsblock/craftscore/cache?color=40c14a&name=cache&prefix=v)
![Latest Release on Maven](https://repo.craftsblock.de/api/badge/latest/releases/de/craftsblock/craftscore/encryption?color=40c14a&name=encryption&prefix=v)
![Latest Release on Maven](https://repo.craftsblock.de/api/badge/latest/releases/de/craftsblock/craftscore/event?color=40c14a&name=event&prefix=v)
![Latest Release on Maven](https://repo.craftsblock.de/api/badge/latest/releases/de/craftsblock/craftscore/json?color=40c14a&name=json&prefix=v)
![Latest Release on Maven](https://repo.craftsblock.de/api/badge/latest/releases/de/craftsblock/craftscore/queue?color=40c14a&name=queue&prefix=v)
![Latest Release on Maven](https://repo.craftsblock.de/api/badge/latest/releases/de/craftsblock/craftscore/sql?color=40c14a&name=sql&prefix=v)
![Latest Release on Maven](https://repo.craftsblock.de/api/badge/latest/releases/de/craftsblock/craftscore/utils?color=40c14a&name=utils&prefix=v)

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
    <groupId>de.craftsblock.craftscore</groupId>
    <artifactId>MODULE</artifactId>
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
  implementation 'de.craftsblock.craftscore:MODULE:X.X.X-SNAPSHOT'
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
