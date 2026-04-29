# Build and Run

This project uses the Java Platform Module System (JPMS)

All compiled modules are placed in `out/module`

## Requirements

- JDK 9 or later
- `javac`, `java`, and `jar` available in `PATH`
- `make` installed

## Project Structure

- `demo.plugin.api`: `Plugin` and `PluginManager` API
- `demo.plugin.core`: `PluginManager` implementation
- `demo.plugin.usage`: Plugin manager usage example
- `demo.plugin.plugin-test`: Test plugin

## Command Line

### Build Plugin

```shell
make build-plugin
````

The generated plugin package will be:

```text
plugins/plugin-test.jar
```

### Run

Run `PluginManagerTest`:

```shell
make run
```

You can observe class loading and unloading behavior in the console output

## IntelliJ IDEA

### Build Plugin

1. Open the project in IntelliJ IDEA
2. Make sure the JDK is configured correctly
3. Build the plugin using **Build → Build Artifacts**

### Run

Run: [PluginManagerTest](usage/src/demo.plugin.usage/com/demo/usage/PluginManagerTest.java) with VM options:

```text
-verbose:class
```