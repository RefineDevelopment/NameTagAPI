# NameTagAPI
Refine's NameTag API | Fork of Hylist's/Frozenorb's API

## Features
- Support for Per-viewer NameTag changing
- Asynchronous and Optimized
- Easy to use
- Can support more than one adapters (weighted priority)

## Installing
You can either shade this repository into your plugin, or run it as a plugin by itself.

1. Clone this repository
2. Enter the directory: `cd NameTagAPI`
3. Build & install with Maven: `mvn clean package install`

OR

Use this maven command to directly install this API's compiled JAR file from target into your .m2 repo

```
mvn install:install-file -Dfile=<compiled-jar> -DgroupId=xyz.refinedev.api -DartifactId=NameTagAPI -Dversion=1.0-SNAPSHOT -Dpackaging=jar
```

Next, add TablistAPI to your project's dependencies via Maven

Add this to your `pom.xml` `<dependencies>`:
```xml
<dependency>
  <groupId>xyz.refinedev.api</groupId>
  <artifactId>NameTagAPI</artifactId>
  <version>1.0-SNAPSHOT</version> <!-- At time of writing, 1.0-SNAPSHOT is latest version.  See the pom.xml for the latest version -->
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
public class ExampleNameTagAdapter extends NameTagAdapter {

    // So basically, all adapters are utilized to be update the player's nametag, but in sequence of their weights
    public ExampleNameTagAdapter() {
        super("Default Provider", 0); // (Name, Weight/Priroity), the name tag is updated of each update in sequence of the priority
    }

              //toRefresh = the player getting their nameTag Refreshed (Target)
    @Override //refreshFor = the player that will be receiving the update (Viewer)
    public NameTagInfo fetchNameTag(Player toRefresh, Player refreshFor) {
        return (this.createNameTag(ChatColor.GREEN + toRefresh.getDisplayName(), "")); //this#createNameTag is a method called from the super class
    }   //Meanwhile, it accepts arguments for (Prefix, Suffix)
}
```
