# About

An Eclipse feature of OSGi bundles packaging the Java Runtime Engine (JRE) for Windows, Mac, and Linux.
It is used to bundle and distribute the JRE with the [Portfolio Performance](https://www.portfolio-performance.info) Eclipse e4 application.

From a license perspective, I wanted to use the [OpenJDK](http://openjdk.java.net/) for my open source program.
The OpenJDK itself is not providing binaries for all platforms, but thankfully Azul does with [Zulu](https://www.azul.com/downloads/zulu/).
Check out this [OpenSource StackExchange](https://opensource.stackexchange.com/questions/4824/is-it-legal-to-bundle-oracles-jre-with-an-open-source-program/4826) on a discussion why the Oracle JRE does not work well with FOSS.

## The Idea

In short, the idea is outlined in the blog post [Including a JRE in a Tycho build](https://codeiseasy.wordpress.com/2012/07/31/including-a-jre-in-a-tycho-build/).

The [Tycho](https://www.eclipse.org/tycho/) build in this repository does:

* Download the JDK builds from [Zulu](https://www.azul.com/downloads/zulu/)
* Create bundles with the appropriate ```setJvm``` [p2 Touchpoint Instructions](http://help.eclipse.org/oxygen/index.jsp?topic=/org.eclipse.platform.doc.isv/guide/p2_actions_touchpoints.html)
* Create a feature with all JRE bundles and a repository for use in other builds
* Create an RCP e4 application with self-update functionality to test the packaged JRE

## Notes

The Java version is configured in [bundles/pom.xml](bundles/pom.xml#L20):

```xml
<properties>
  <download.url>https://cdn.azul.com/zulu/bin/</download.url>
  <download.file>zulu11.33.15-ca-jre11.0.4-</download.file>
  ...
</properties>
```

Review the [p2.inf](bundles/name.abuchen.zulu.jre.macosx.x86_64/src/main/template/META-INF/p2.inf) - no change needed.
The p2.inf files have to be generated during the build because they must contain the correct path - including the version.
On Linux and macOS, additionally executable permissions must be set.

```
instructions.configure = \
  org.eclipse.equinox.p2.touchpoint.eclipse.setJvm(jvm:../Eclipse/plugins/name.abuchen.zulu.jre.macosx.x86_64_${version}/jre/lib/server/libjvm.dylib);

instructions.unconfigure = \
  org.eclipse.equinox.p2.touchpoint.eclipse.setJvm(jvm:null);

instructions.install = \
  org.eclipse.equinox.p2.touchpoint.eclipse.chmod(targetDir:${installFolder}/plugins/name.abuchen.zulu.jre.macosx.x86_64_${version}/jre/,targetFile:bin,permissions:755,options:-R);\
  org.eclipse.equinox.p2.touchpoint.eclipse.chmod(targetDir:${installFolder}/plugins/name.abuchen.zulu.jre.macosx.x86_64_${version}/jre/lib,targetFile:jspawnhelper,permissions:755);
```

Then add the feature to the product definition - see [test.product](test/test.product/test.product):
```xml
<?xml version="1.0" encoding="UTF-8"?>
<?pde version="3.5"?>

<product name="Test Product" ... >

   <features>
      <feature id="test.feature"/>
      <feature id="org.eclipse.e4.rcp"/>
      ...

      <feature id="name.abuchen.zulu.jre.feature"/>
      
   </features>
</product>
```


## Building

Build with Maven 3.x:
```
mvn clean verify
```

Then run the test product created in ~/test/test.product/target/products.

Update the versions using the Tycho versions plugin:
```
mvn org.eclipse.tycho:tycho-versions-plugin:1.0.0:set-version -DnewVersion=11.0.4
```

Of course, this does not update the JRE version itself (update the Java version in the [bundles/pom.xml](bundles/pom.xml#L20)).
But it is helpful to test the self-update: build once, then copy the test product to a separate location, update the version number,
and build again. Then you can run the self-update in the test product. Check that ```java.home``` environment points to the JRE in the updated bundle.
