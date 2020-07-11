# 名称和别名

## 简介

别名是列的新名称，可以使用别名选择列。列的别名使用关键字 "AS" 指定。

例如，你希望用 "Student Name" 而不是 "StudentName" 选择列，可以按如下语法设置别名：

```sql
SELECT StudentName AS 'Student Name' FROM Students;
```

别名设置并不改变列名称。

设置表格别名语法：

```sql
SELECT column1, column2....
FROM table_name AS alias_name
WHERE [condition];
```

设置列别名：

```sql
SELECT column_name AS alias_name
FROM table_name
WHERE [condition];
```

## 实例

现在有两个表格。 COMPANY:

```sql
sqlite> select * from COMPANY;
ID          NAME                  AGE         ADDRESS     SALARY
----------  --------------------  ----------  ----------  ----------
1           Paul                  32          California  20000.0
2           Allen                 25          Texas       15000.0
3           Teddy                 23          Norway      20000.0
4           Mark                  25          Rich-Mond   65000.0
5           David                 27          Texas       85000.0
6           Kim                   22          South-Hall  45000.0
7           James                 24          Houston     10000.0
```

以及 DEPARTMENT：

```sql
ID          DEPT                  EMP_ID
----------  --------------------  ----------
1           IT Billing            1
2           Engineering           2
3           Finance               7
4           Engineering           3
5           Finance               4
6           Engineering           5
7           Finance               6
```

