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

## Notes

The Java version is configured in [bundles/pom.xml](bundles/pom.xml#L20):

```xml
<properties>
  <download.url>https://cdn.azul.com/zulu/bin/</download.url>
  <download.file>zulu21.36.17-ca-jre21.0.4-</download.file>
  ...
</properties>
```

Review the [p2.inf](bundles/name.abuchen.zulu.jre.macosx.x86_64/src/main/template/META-INF/p2.inf) - no change needed.
The p2.inf files have to be generated during the build because they must contain the correct path - including the version.
On Linux and macOS, additionally executable permissions must be set.

```
instructions.configure = \
  org.eclipse.equinox.p2.touchpoint.eclipse.setJvm(jvm:../Eclipse/plugins/name.abuchen.zulu.jre.macosx.aarch64_${version}/jre/Contents/Home/lib/libjli.dylib);

instructions.unconfigure = \
  org.eclipse.equinox.p2.touchpoint.eclipse.setJvm(jvm:null);

instructions.install = \
  org.eclipse.equinox.p2.touchpoint.eclipse.chmod(targetDir:${installFolder}/plugins/name.abuchen.zulu.jre.macosx.aarch64_${version}/jre/Contents/Home/,targetFile:bin,permissions:755,options:-R);\
  org.eclipse.equinox.p2.touchpoint.eclipse.chmod(targetDir:${installFolder}/plugins/name.abuchen.zulu.jre.macosx.aarch64_${version}/jre/Contents/Home/lib,targetFile:jspawnhelper,permissions:755);\
```

Then add the feature to the product definition:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<?pde version="3.5"?>

<product name="My Product" ... >

   <features>
      <feature id="my.feature"/>
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

Update the versions using the Tycho versions plugin:
```
mvn org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion=21.0.4
```
