# jpackage

2024-09-12 ⭐
@author Jiawei Mao
***

## 简介

语法：

```
jpackage [options]
```

`jpackage` 以 java-application 和 java-runtime-image 为输入，生成一个包含所有依赖项的 java-application-image。

## jpackage 选项

### 通用选项

- `@`*filename*

从文件读取选项。

该选项可以使用多次。

- `--type` or `-t` *type*

设置要创建的 package 的类型。

有效值: {"app-image", "exe", "msi", "rpm", "deb", "pkg", "dmg"}

如果不指定该选项，则创建与平台相关的默认类型。

> [!NOTE]
>
> `app-image` 对应绿色版 exe。

- `--app-version` *version*

设置 application 或 package 版本。默认为 1.0。

- `--copyright` *copyright*

设置 application 的版权声明。

- `--description` *description*

设置 application 的描述信息。

- `--help` 或 `-h`

输出使用方法和当前 platform 的有效选项。

- `--icon` *path*

指定 application package 图标。（绝对路径，或相对当前目录的相对路径）

- `--name` or `-n` *name*

设置 app 或 pkg 名称（用户见到的名称）。

- `--dest` or `-d` *destination*

文件输出目录。（绝对路径，或相对当前目录的相对路径）

默认为当前工作目录。

- `--temp` *directory*

临时文件存储目录，必须为新目录或空目录。（绝对路径，或相对当前目录的相对路径）

如果指定，任务完成后不会自动删除临时目录，需要手动删除。

如果不指定，则自动创建临时目录，任务完成时自动删除。

- `--vendor` *vendor*

设置 application 的供应商。

- `--verbose`

启用详细输出。

- `--version`

输出 jpackage 版本。

### runtime image 选项

- `--add-modules` *module-name* [`,`*module-name*...]

用于添加 module，参数为逗号 `,` 分隔的 module 列表。

该 module list 以及 main-module（如果指定）将作为 `--add-module` 参数传递给 `jlink`。

如果不指定该选项：如果使用 `--module` 指定了 main-module，则仅包含 main-module；如果使用 `--main-jar`，则包含默认 modules。

此选项可以使用多次。

- `--module-path` or `-p` *module-path* [`,`*module-path*...]

`File.pathSeparator` 分隔的路径列表。

指定包含 modules 的目录，要么是 modular-jar 的路径。（绝对路径，或相对当前目录的相对路径）

此选项可以使用多次。

- `--jlink-options` *options*

传递给 `jlink` 的选项，多个选项用空格分隔。

如果未指定，默认为 "--strip-native-commands --strip-debug --no-man-pages --no-header-files"。

此选项可以使用多次。

- `--runtime-image` *directory*

指定预定义的 runtime-image 路径，将被复制到 application-imge。（绝对路径，或相对当前目录的相对路径）

如果不指定 `--runtime-image`，jpackage 将使用 `--jlink-options` 指定的选项运行 `jlink` 创建 runtime-image。

### application image 选项

- `--input` or `-i` *directory*

指定包含要打包文件的目录。（绝对路径，或相对当前目录的相对路径）

输入目录中的所有文件都将打包到 app-image。

- `--app-content` *additional-content*[,*additional-content*...]

逗号分隔的路径列表，包含要添加到 application payload 的文件或目录。

此选项可以使用多次。

### application launcher 选项

- `--add-launcher` *name*=*path*

launcher 名称，以及属性文件路径，该属性文件包含 key-value list。（绝对路径，或相对当前目录的相对路径）

可用 keys："module", "main-jar", "main-class", "description", "arguments", "java-options", "app-version", "icon", "launcher-as-service", "win-console", "win-shortcut", "win-menu", "linux-app-category", "linux-shortcut".

该选项在原命令选项的基础上构建额外的 launcher。即 main-application launcher 使用原命令选项构建，而该选项构建额外的 launcher。

此选项可以使用多次。

- `--arguments` *arguments*

定义程序的默认命令行参数。用户可以提供参数覆盖默认值。

此选项可以使用多次。

- `--java-options` *options*

传递给 java runtime 的选项，即设置 JVM 选项。

此选项可以使用多次。

- `--main-class` *class-name*

application main-class 的限定名。如果在 `MANIFEST.MF` 文件中指定了 main-class，就不需要该选项。

只有指定 `--main-jar` 该选项才有用。

- `--main-jar` *main-jar*

application 的 main-JAR，包含 main-class (相对输入路径的路径)。

可以指定 `--module` 或 `--main-jar`，但不能同时指定。

- `--module` or `-m` *module-name*[/*main-class*]

application 的 main-module 以及 main-class(可选)。

指定的 module 必须在 module-path。

指定该选项后，main-module 会链接到 java runtime-image。

可以指定 `--module` 或 `--main-jar`，但不能同时指定。

### application launcher 中 platform dependent 选项

**Windows**

- `--win-console`

为 application 创建一个 console-launcher，适合为需要 console 交互的 application。

**macOS**

- `--mac-package-identifier` *identifier*

指定 application 在 macOS 中的唯一标识符。

默认为 main-class 名称。

只支持 alphanumeric (A-Z,a-z,0-9), hyphen (-), period (.) 字符.

- `--mac-package-name` *name*

指定 application 在菜单显示的名称。

该名称长度必须小于 16 字符。默认为 application-name。

- `--mac-package-signing-prefix` *prefix*

对 application package 签名时，该参数设置所有需要签名单没有 package 标识符的所有组件的前缀。

- `--mac-sign`

请求对 package 或预定义的 application-image 进行签名。

- `--mac-signing-keychain` *keychain-name*

keychain 名称，用于搜索签名身份。

若不指定，则使用标准 keychain。

- `--mac-signing-key-user-name` *name*

apple 签名身份中团队或用户名。

- `--mac-app-store`

表示 jpackage 输出用于 Mac App Store。

- `--mac-entitlements` *path*

签名包中可执行文件和库时要使用的权限文件路径。

- `--mac-app-category` *category*

在 applicaiton plist 中构造 LSApplicationCategoryType  使用的 String。

默认为 "utilities".

### application package 选项

- `--about-url` *url*

指定 application 主页的 URL。

- `--app-image` *directory*

用于构建安装包（所有平台）或用于签名（macOS）的预定义 application-image 的位置。（绝对路径，或相对当前目录的相对路径）

- `--file-associations` *path*

属性文件路径，包括 key-value list。（绝对路径，或相对当前目录的相对路径）

可用于描述关联的 keys: "extension", "mime-type", "icon", "description"。

此选项可以使用多次。

- `--install-dir` *path*

application 的安装目录：对 macOS 或 linux 为绝对路径，对 Windows 为安装目录的子目录，如 "Program Files" 或 "AppData" 的子目录。

- `--license-file` *path*

license 文件路径。绝对路径，或相对当前目录的相对路径）

- `--resource-dir` *path*

设置 jpackage 资源路径。（绝对路径，或相对当前目录的相对路径）

在该目录添加替换资源，可以覆盖 jpackage 的 Icons, template files 和其它资源。

- `--runtime-image` *path*

预定义 runtime-image 的路径。（绝对路径，或相对当前目录的相对路径）

创建 runtime 安装包是需要。

- `--launcher-as-service`

要求创建的安装包将 main-application-launcher 注册为后台 service 类型 application。

### application package 中 platform dependent 选项

**Windows**

- `--win-dir-chooser`

添加一个对话框，让用户能够选择安装位置。

- `--win-help-url` *url*

用户可以获取进一步信息或技术支持的 URL。

- `--win-menu`

为 application 添加开始菜单快捷方式。

- `--win-menu-group` *menu-group-name*

设置该 application 在开始菜单的 group。

- `--win-per-user-install`

安装到用户目录。

- `--win-shortcut`

创建桌面快捷方式。

- `--win-shortcut-prompt`

添加对话框，让用户选择是否创建快捷方式。

- `--win-update-url` *url*

application 更新信息的 URL。

- `--win-upgrade-uuid` *id*

与 package 升级相关的 UUID。

**Linux**

- `--linux-package-name` *name*

linux package 名称。默认为 application-name。

- `--linux-deb-maintainer` *email-address*

.deb 包的维护者。

- `--linux-menu-group` *menu-group-name*

放置 application 的 menu-group。

- `--linux-package-deps`

application 所需的 packages 或功能。

- `--linux-rpm-license-type` *type*

license 类型。("License: *value*" of the RPM .spec)

- `--linux-app-release` *release*

RPM `<name>.spec` 的 release 值，或 DEB control 文件的 Debian revision 值。

- `--linux-app-category` *category-value*

RPM `/.spec` 文件的 group 值，或 DEB control 文件的 section 值。

- `--linux-shortcut`

创建 application 的快捷方式。

**macOS**

- `--mac-dmg-content` *additional-content*[,*additional-content*...]

在 dmg 中包含所有引用内容。

此选项可以使用多次。

## jpackage 示例

1. **创建适合 host 系统的 application package**

- modular application

```
jpackage -n name -p modulePath -m moduleName/className
```

- non-moular application

```
jpackage -i inputDir -n name \
        --main-class className --main-jar myJar.jar
```

- 从与构建的 applicaiton-image

```
jpackage -n name --app-image appImageDir
```

2. **生成 application-image**

- modular application

```
jpackage --type app-image -n name -p modulePath \
         -m moduleName/className
```

- **non-modular application**

```
jpackage --type app-image -i inputDir -n name \
        --main-class className --main-jar myJar.jar
```

- 要为 jlink 提供自定义选项，可以单独运行 jlink

```
jlink --output appRuntimeImage -p modulePath \
    --add-modules moduleName \
    --no-header-files [<additional jlink options>...]
jpackage --type app-image -n name \
    -m moduleName/className --runtime-image appRuntimeImage
```

3. **生成 java runtime-package**

```
jpackage -n name --runtime-image <runtime-image>
```

4. **为预定义 application-image 签名（macOS）**

```
jpackage --type app-image --app-image <app-image> \
    --mac-sign [<additional signing options>...]
```

> [!NOTE]
>
> 在该模式仅支持两个其它选项：额外 mac 签名和 `--verbose`。

## jpackage 资源目录

可以通过向该目录添加替换资源，覆盖 jpackage 的 icon, template file 和其它资源。jpackage 会根据名称在资源目录查找文件。

针对不同平台，有不同资源文件。

### 在 Linux 运行

- `<launcher-name>.png`

application launcher icon。

默认为 *JavaApp.png*

- `<launcher-name>.desktop`

用于 `xdg-desktop-menu` 命令的 desktop 文件

用于注册关联文件或包含 icon 的 application-launchers。

默认为 *template.desktop*

### 构建 Linux DEB/RMP

- `<package-name>-<launcher-name>.service`

systemd unit 文件，用于注册为后台 service 类型 application 的 application-launcher。

默认为 *unit-template.service*

### 构建 Linux RMP

- `<package-name>.spec`

RPM spec 文件。

默认为 *template.spec*。

### 构建 Linux DEB

- `control`

Control file

默认为 *template.control*

- `copyright`

copyright file。

默认为 *template.copyright*

- `preinstall`

Pre-install shell script

默认 *template.preinstall*

- `prerm`

Pre-remove shell script

Default resource is *template.prerm*

- `postinstall`

Post-install shell script

Default resource is *template.postinstall*

- `postrm`

Post-remove shell script

Default resource is *template.postrm*

### 在 Windows 运行

- `<launcher-name>.ico`

Application launcher icon

Default resource is *JavaApp.ico*

- `<launcher-name>.properties`

Properties file for application launcher executable

Default resource is *WinLauncher.template*

### 构建 Windows MSI/EXE

- `<application-name>-post-image.wsf`

A Windows Script File (WSF) to run after building application image

- `main.wxs`

Main WiX project file

Default resource is *main.wxs*

- `overrides.wxi`

Overrides WiX project file

Default resource is *overrides.wxi*

- `service-installer.exe`

Service installer executable

Considered if some application launchers are registered as background service-type applications

- `<launcher-name>-service-install.wxi`

Service installer WiX project file

Considered if some application launchers are registered as background service-type applications

Default resource is *service-install.wxi*

- `<launcher-name>-service-config.wxi`

Service installer WiX project file

Considered if some application launchers are registered as background service-type applications

Default resource is *service-config.wxi*

- `InstallDirNotEmptyDlg.wxs`

WiX project file for installer UI dialog checking installation directory doesn't exist or is empty

Default resource is *InstallDirNotEmptyDlg.wxs*

- `ShortcutPromptDlg.wxs`

WiX project file for installer UI dialog configuring shortcuts

Default resource is *ShortcutPromptDlg.wxs*

- `bundle.wxf`

WiX project file with the hierarchy of components of application image

- `ui.wxf`

WiX project file for installer UI

### 构建 Windows EXE

- `WinInstaller.properties`

Properties file for the installer executable

Default resource is *WinInstaller.template*

- `<package-name>-post-msi.wsf`

A Windows Script File (WSF) to run after building embedded MSI installer for EXE installer

### 在 macOS 运行

- `<launcher-name>.icns`

Application launcher icon

Default resource is *JavaApp.icns*

- `Info.plist`

Application property list file

Default resource is *Info-lite.plist.template*

- `Runtime-Info.plist`

Java Runtime property list file

Default resource is *Runtime-Info.plist.template*

- `<application-name>.entitlements`

Signing entitlements property list file

Default resource is *sandbox.plist*

### 构建 macOS PKG/DMG

- `<package-name>-post-image.sh`

Shell script to run after building application image

### 构建 macOS PKG

- `uninstaller`

Uninstaller shell script

Considered if some application launchers are registered as background service-type applications

Default resource is *uninstall.command.template*

- `preinstall`

Pre-install shell script

Default resource is *preinstall.template*

- `postinstall`

Post-install shell script

Default resource is *postinstall.template*

- `services-preinstall`

Pre-install shell script for services package

Considered if some application launchers are registered as background service-type applications

Default resource is *services-preinstall.template*

- `services-postinstall`

Post-install shell script for services package

Considered if some application launchers are registered as background service-type applications

Default resource is *services-postinstall.template*

- `<package-name>-background.png`

Background image

Default resource is *background_pkg.png*

- `<package-name>-background-darkAqua.png`

Dark background image

Default resource is *background_pkg.png*

- `product-def.plist`

Package property list file

Default resource is *product-def.plist*

- `<package-name>-<launcher-name>.plist`

launchd property list file for application launcher registered as a background service-type application

Default resource is *launchd.plist.template*

### 构建 macOS DMG

- `<package-name>-dmg-setup.scpt`

Setup AppleScript script

Default resource is *DMGsetup.scpt*

- `<package-name>-license.plist`

License property list file

Default resource is *lic_template.plist*

- `<package-name>-background.tiff`

Background image

Default resource is *background_dmg.tiff*

- `<package-name>-volume.icns`

Volume icon

Default resource is *JavaApp.icns*

## 参考

- https://docs.oracle.com/en/java/javase/21/docs/specs/man/jpackage.html
- https://docs.oracle.com/en/java/javase/21/docs/specs/man/java.html