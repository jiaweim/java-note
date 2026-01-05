# String

## format

```java
public static String format(String format, Object... args) {
    return new Formatter().format(format, args).toString();
}

public static String format(Locale l, String format, Object... args) {
    return new Formatter(l).format(format, args).toString();
}
```

