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
  <version>2.1</version> 
  <scope>compile</scope>
</dependency>
```

## Usage

You can initiate and register a NameTagAdapter using the following code
```java
import xyz.refinedev.api.nametag.NameTagHandler;
import xyz.refinedev.api.nametag.adapter.DefaultNameTagAdapter;

public class ExamplePlugin extends JavaPlugin {

    private NameTagHandler nameTagHandler;

    @Override
    public void onEnable() {
        this.nameTagHandler = new NameTagHandler(this);
        this.nameTagHandler.registerAdapter(new DefaultNameTagAdapter(), 2L);
    }
}
```

To set up NameTagAdapter, you can easily use

```java
import org.bukkit.entity.Player;
import xyz.refinedev.api.nametag.setup.NameTagTeam;

public class ExampleNameTagAdapter extends NameTagAdapter {

    /**
     * Fetch a Player's NameTagTeam
     *
     * @param toRefresh  {@link Player} The player getting their nameTag Refreshed
     * @param refreshFor {@link Player} The player that will be receiving the update
     * @return           {@link NameTagTeam} The NameTag Entry used for updates
     */
    public NameTagTeam fetchNameTag(Player toRefresh, Player refreshFor) {
        return (this.createNameTag(ChatColor.RED + "[Refine]", ""));
    }
}
```
