# 注释

- **`javax.validation.constraints.NotNull`**
  *Created for runtime validation, not static analysis.*
  [documentation](http://download.oracle.com/javaee/6/api/javax/validation/constraints/NotNull.html)
- **`edu.umd.cs.findbugs.annotations.NonNull`**
  *Used by [FindBugs](http://findbugs.sourceforge.net/) ([dead project](https://mailman.cs.umd.edu/pipermail/findbugs-discuss/2016-November/004321.html)) and its successor [SpotBugs](https://spotbugs.github.io/) static analysis and therefore Sonar (now [Sonarqube](https://www.sonarqube.org/))*
  [FindBugs documentation](http://findbugs.sourceforge.net/manual/annotations.html), [SpotBugs documentation](https://spotbugs.readthedocs.io/en/stable/annotations.html)
- **`javax.annotation.Nonnull`**
  *This might work with FindBugs too, but [JSR-305](https://jcp.org/en/jsr/detail?id=305) is inactive. (See also: [What is the status of JSR 305?](https://stackoverflow.com/questions/2289694/what-is-the-status-of-jsr-305))* [source](http://code.google.com/p/jsr-305/source/browse/trunk/ri/src/main/java/javax/annotation/Nonnull.java)
- **`org.jetbrains.annotations.NotNull`**
  *Used by IntelliJ IDEA IDE for static analysis.*
  [documentation](https://www.jetbrains.com/help/idea/nullable-and-notnull-annotations.html)
- **`lombok.NonNull`**
  *Used to control code generation in [Project Lombok](https://projectlombok.org/).*
  *Placeholder annotation since there is no standard.*
  [source](https://github.com/rzwitserloot/lombok/blob/master/src/core/lombok/NonNull.java), [documentation](https://projectlombok.org/features/NonNull.html)
- **`androidx.annotation.NonNull`**
  *Marker annotation available in Android, provided by annotation package*
  [documentation](https://developer.android.com/reference/androidx/annotation/NonNull)
- **`org.eclipse.jdt.annotation.NonNull`**
  *Used by Eclipse for static code analysis*
  [documentation](http://help.eclipse.org/oxygen/topic/org.eclipse.jdt.doc.user/tasks/task-improve_code_quality.htm)

| Package                         | FIELD | METHOD | PARAMETER | LOCAL_VARIABLE |
| ------------------------------- | ----- | ------ | --------- | -------------- |
| android.support.annotation      | ✔️     | ✔️      | ✔️         |                |
| edu.umd.cs.findbugs.annotations | ✔️     | ✔️      | ✔️         | ✔️              |
| org.jetbrains.annotation        | ✔️     | ✔️      | ✔️         | ✔️              |
| lombok                          | ✔️     | ✔️      | ✔️         | ✔️              |
| javax.validation.constraints    | ✔️     | ✔️      | ✔️         |                |

如果不使用检查框架，退阿金 javax.annotation (JSR-305)；

如果使用检查框架，则根据框架进行选择。