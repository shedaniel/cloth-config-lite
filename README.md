# Cloth Config Lite

A project to provide an acceptable Config Screen Library with a small footprint.

---

## How Small is Cloth Config Lite?
Cloth Config Lite is **right under 13KB.**

## Some classes are obfuscated?
Yes, we run the jar with proguard to repackage them and minimize the jar.

## Maven
```groovy
repositories {
    maven { url "https://maven.shedaniel.me/" }
}

dependencies {
    modApi "me.shedaniel.cloth:cloth-config-lite:1.0.+"
    include "me.shedaniel.cloth:cloth-config-lite:1.0.+"
}
```

## Differences with the main Cloth Config?
Cloth Config Lite:
- Only offers a config screen builder
- Only basic options (String, Enum, Int, Long, Float, Double, BigInt, BigDecimal, Identifier)

Cloth Config:
- Advanced options (Sections, Dropdowns, Sliders, Color Picker, Lists, Keybinds)
- More feature rich (Tooltips, Categories, Tabs, Custom Backgrounds, Un-editable)
- Display Errors Details
- Globalized Display
- Config serializers

## Can this get smaller?
shut up

## License
MIT, Since the fabric.mod.json already includes MIT, you don't need to credit this.

## Do you accept contributions?
Please talk to me via discord before bigger PRs, otherwise feel free!
