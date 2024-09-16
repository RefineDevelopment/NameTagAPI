# NameTagAPI
Refine's NameTag API | Fork of Hylist's/Frozenorb's API

## Features
- Support for per-player NameTags.
- Lightweight with no performance overhead.
- Supports hex colors on supported versions.
- Completely asynchronous and off-thread.
- Supports all versions from 1.8+
- Easy to use

## Installing
You can either shade this repository into your plugin, or run it as a plugin by itself.

1. Clone this repository
2. Enter the directory: `cd NameTagAPI`
3. Build & install with Maven: `mvn clean package install`

OR
```xml
<repositories>
    <repository>
        <id>refine-public</id>
        <url>https://maven.refinedev.xyz/public-repo/</url>
    </repository>
</repositories>
```
Next, add TablistAPI to your project's dependencies via Maven

Add this to your `pom.xml` `<dependencies>`:
```xml
<dependency>
  <groupId>xyz.refinedev.api</groupId>
  <artifactId>NameTagAPI</artifactId>
  <version>2.4</version>
  <scope>compile</scope>
</dependency>
```

## Usage
It requires PacketEvents as a dependency, refer to their [wiki guide](https://github.com/retrooper/packetevents/wiki/)

You can initiate and register a NameTagAdapter using the following code
```java
import xyz.refinedev.api.nametag.NameTagHandler;
import xyz.refinedev.api.nametag.adapter.DefaultNameTagAdapter;

public class ExamplePlugin extends JavaPlugin {

    private NameTagHandler nameTagHandler;
    private PacketEventsAPI<?> packetEvents; // Initialize this yourself

    @Override
    public void onEnable() {
        this.nameTagHandler = new NameTagHandler(this);
        this.nameTagHandler.init(this.packetevents);
        this.nameTagHandler.registerAdapter(new DefaultNameTagAdapter(), 20L); // Every 1 second
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
