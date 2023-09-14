# NameTagAPI
Refine's NameTag API | Fork of Hylist's/Frozenorb's API

## Features
- Support for Per-viewer NameTag changing
- Asynchronous and Optimized
- Support for Hex-Color (1.16+)
- Supports all Major Versions (1.8, 1.12, 1.16, 1.17+)
- Easy to use

## Installing
You need to shade this repository into your plugin.

1. Clone this repository
2. Enter the directory: `cd NameTagAPI`
3. Build & install with Maven: `mvn clean package install`

OR

Use this maven command to directly install this API's compiled JAR file from target into your .m2 repo

```
mvn install:install-file -Dfile=<compiled-jar> -DgroupId=xyz.refinedev.api -DartifactId=NameTagAPI -Dversion=2.0 -Dpackaging=jar
```

Next, add NameTagAPI to your project's dependencies via Maven

Add this to your `pom.xml` `<dependencies>`:
```xml
<dependency>
  <groupId>xyz.refinedev.api</groupId>
  <artifactId>NameTagAPI</artifactId>
  <version>2.0</version> <!-- At time of writing, 1.0-SNAPSHOT is latest version.  See the pom.xml for the latest version -->
  <scope>compile</scope> <!-- Change scope to 'provided' if you are running the api as a plugin rather than shading it -->
</dependency>
```

## Usage

You can initiate and register a TablistAdapter using the following code
```java
NameTagHandler nameTagHandler = new NameTagHandler(plugin);
nameTagHandler.registerAdapter(new DefaultNameTagAdapter())
```

To setup NameTagAdapter, you can easily use

```java
import org.bukkit.entity.Player;
import xyz.refinedev.api.nametag.setup.NameTagInfo;

public class ExampleNameTagAdapter extends NameTagAdapter {

    /**
     * Fetch a Player's NameTag update information
     *
     * @param toRefresh  {@link Player Target} the player getting their nameTag Refreshed
     * @param refreshFor {@link Player Viewer} the player that will be receiving the update
     * @return           {@link NameTagInfo} The NameTag Entry used for updates
     */
    public NameTagInfo fetchNameTag(Player toRefresh, Player refreshFor) {
        return (this.createNameTag(ChatColor.GREEN + toRefresh.getDisplayName(), ""));
    }
}
```
