# Insert

## 简介

## 插入多行

如果要插入多行数据，推荐使用如下的 `INSERT` 语法：

```sql
INSERT INTO table1 (column1,column2 ,..)
VALUES
   (value1,value2 ,...),
   (value1,value2 ,...),
    ...
   (value1,value2 ,...);
```

`VALUES` 后面对应每一行的值。
