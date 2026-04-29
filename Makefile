JAVAC = javac
JAVA  = java
JAR   = jar

OUT_DIR = out/module
DIST_DIR = out
PLUGIN_DIR = plugins

API_MODULE   = demo.plugin.api
CORE_MODULE  = demo.plugin.core
USAGE_MODULE = demo.plugin.usage
PLUGIN_TEST_MODULE = demo.plugin.test

API_SRC         = api/src
CORE_SRC        = core/src
USAGE_SRC       = usage/src
PLUGIN_TEST_SRC = plugin-test/src
PLUGIN_TEST_RES = plugin-test/resources

PLUGIN_TEST_OUT = $(OUT_DIR)/$(PLUGIN_TEST_MODULE)
PLUGIN_NAME = plugin-test
PLUGIN_TEST_JAR = $(PLUGIN_DIR)/$(PLUGIN_NAME).jar

JAVA_OPTS = -verbose:class

MODULE_PATH = $(OUT_DIR)/$(USAGE_MODULE):$(OUT_DIR)/$(API_MODULE):$(OUT_DIR)/$(CORE_MODULE)

.PHONY: all clean build run api core usage build-plugin

all: clean build

build: api core usage build-plugin

api:
	$(JAVAC) -d $(OUT_DIR) \
		--module-source-path $(API_SRC) \
		-m $(API_MODULE)

core: api
	$(JAVAC) -d $(OUT_DIR) \
		--module-source-path $(CORE_SRC) \
		-m $(CORE_MODULE) \
		--module-path $(OUT_DIR)/$(API_MODULE)

usage: core
	$(JAVAC) -d $(OUT_DIR) \
		--module-source-path $(USAGE_SRC) \
		-m $(USAGE_MODULE) \
		--module-path $(OUT_DIR)/$(CORE_MODULE):$(OUT_DIR)/$(API_MODULE)

run: build
	$(JAVA) \
		$(JAVA_OPTS) \
		-p $(MODULE_PATH) \
		-m $(USAGE_MODULE)/com.demo.usage.PluginManagerTest

build-plugin: core
	$(JAVAC) -d $(OUT_DIR) \
		--module-source-path $(PLUGIN_TEST_SRC) \
		-m $(PLUGIN_TEST_MODULE) \
		--module-path $(OUT_DIR)/$(CORE_MODULE):$(OUT_DIR)/$(API_MODULE)
	mkdir -p $(DIST_DIR)
	$(JAR) --create \
		--file $(PLUGIN_TEST_JAR) \
		--module-version=1.0 \
		-C $(PLUGIN_TEST_OUT) . \
		-C $(PLUGIN_TEST_RES) .

clean:
	rm -rf out