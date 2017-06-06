# Slog
## 概述

`Slog`是一个轻量级的`Android`平台的日志库，其是基于对当前开源的日志框架`Logger`和`Timber`的一个组合与扩展。具有极大的可扩展性，相比于原生`Android Log`，有以下新特性。

- 支持对日志的格式化排版输出，显示效果更清晰，更方便查看。
- 支持输出打印日志方法的栈和当前线程信息。
- 支持打印对象，支持自定义对象解析器，默认提供对数组，集合等解析。
- 支持使用多个自定义日志适配器，以决定日志的不同处理方式，默认提供`LogcatTree`适配器。
- 支持每次打印日志之前自定义日志输出配置，从而达到不同的日志输出效果。

## 引用方法

```groovy
compile 'com.sky.slog:slog:0.3.0'
```

## 用法示例

### 初始化`Slog`

一般地在应用的`Application`类的`onCreate()`方法中调用如下类似代码。

```java
Slog.init(new LogcatTree());
```

初始化时，至少需要传入一个日志适配器，当然我们也可以添加多个适配器，这个后面详解。

以上是最简单的初始化方式，我们还可以在初始化的时候对日志输出做全局配置，如下。

```java
Slog.init(new LogcatTree())     // 初始化，设置适配器
    .showThreadInfo(true)       // 设置是否打印日志的线程的信息
    .prefixTag("test")          // 设置全局日志的前缀
    .logPriority(Slog.FULL)     // 设置日志输出级别
    .methodCount(2)             // 显示栈中方法的个数，默认从调用日志接口的方法往stack下计算
    .methodOffset(1)            // 显示从调用日志打印接口的方法往stack下计算的偏移数
    .simpleMode(false);         // 设置简单模式，无任何格式，等同于调用logcat，默认值为false.
```

以上方法的作用与默认值。

方法 | 默认值 | 作用 |
--------|--------------|-------------------|
prefixTag      | "Android" | 设置全局日志前缀。|
logPriority    | Slog.FULL | 日志的输出级别，FULL表示可以输出任何级别的日志， NONE，表示不输出任何日志。|
methodCount    | 1         | 设置显示栈中方法到最终组装的日志中的个数，默认从调用日志接口的方法往stack下计算。|
methodOffset   | 0         | 设置从调用日志打印接口的方法往stack下的偏移数。|
showThreadInfo | false     | 设置是否打印日志的线程的信息。|
simpleMode     | false     | 设置简单模式，无任何格式，不显示线程信息，方法调用，等同于调用logcat。|

**注意，上表中，如果`simpleMode`为`true`，则`methodCount, methodOffset, showThreadInfo`将无效**

另外我们还可以通过`Slog.getSetting()`获取`Setting`对象之后，在任何时候对全局Log输出配置项进行修改。

### 基本使用

使用以下方式初始化`Slog`。

```java
Slog.init(LogcatTree()).perfixTag("TestSlog").showThreadInfo(true);
```

#### 打印普通日志

```java
// 打印普通日志
Slog.d("sky debug");
Slog.i("sky info");

// 打印格式化字符串
Slog.d("this is a format string log, str1 = %s, int value2 = %d, boolean3 = %b", "string1", 2, true);

```

![normal_log](http://of6l49ylt.bkt.clouddn.com/20170606-005507_normal_log.png)

#### 打印错误日志

```java
// 打印throwable
Slog.e(new Throwable());
Slog.w(new RuntimeException(), "test log with warn priority = %d", Slog.WARN);
```

![error_warn_log](http://of6l49ylt.bkt.clouddn.com/20170607-003259_error_warn_log.png)

### 打印`json`和`xml`

`json`和`xml`字符串采用的日志级别都是`Debug`的。

#### 打印json字符串

```java
String jsonEmpty = "";
String jsonEmpty2 = "{}";
String jsonNull = null;
Slog.json(jsonEmpty);
Slog.json(jsonEmpty2);
Slog.json(jsonNull);

String json = "{'xyy1':[{'test1':'test1'},{'test2':'test2'}],'xyy2':{'test3':'test3','test4':'test4'}}";

Slog.json(json);

String jsonArray =
        "{ 'employees': [ {'firstName':'John', 'lastName':'Doe'}, {'firstName':'Anna', 'lastName':'Smith'}, "
                + "{'firstName':'Peter', 'lastName':'Jones'}]}";
Slog.json(jsonArray);
```

![json1](http://of6l49ylt.bkt.clouddn.com/20170606-143648_json1.png)
![json2](http://of6l49ylt.bkt.clouddn.com/20170606-143709_json2.png)

#### 打印`xml`字符串

```java
String androidXml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
        "<manifest xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
        "    package=\"com.sky.tools\" >\n" +
        "\n" +
        "    <application\n" +
        "        android:name=\".application.MainApplication\"\n" +
        "        android:allowBackup=\"true\"\n" +
        "        android:icon=\"@mipmap/ic_launcher\"\n" +
        "        android:label=\"@string/app_name\"\n" +
        "        android:roundIcon=\"@mipmap/ic_launcher_round\"\n" +
        "        android:supportsRtl=\"true\"\n" +
        "        android:theme=\"@style/AppTheme\" >\n" +
        "        <activity android:name=\".main.MainActivity\" >\n" +
        "            <intent-filter>\n" +
        "                <action android:name=\"android.intent.action.MAIN\" />\n" +
        "\n" +
        "                <category android:name=\"android.intent.category.LAUNCHER\" />\n" +
        "            </intent-filter>\n" +
        "        </activity>\n" +
        "    </application>\n" +
        "\n" +
        "</manifest>";

Slog.xml(androidXml);
```

![xml_log](http://of6l49ylt.bkt.clouddn.com/20170606-144001_xml_log.png)

### 打印对象

`Slog` 支持对对象的打印，其原理是给每个不同的对象类型添加对应的对象解析器，默认提供对数组，集合等解析，支持自定义对象解析器。

#### 打印`null`对象

```java
Slog.i(null);
Slog.i("");
```

![null_object](http://of6l49ylt.bkt.clouddn.com/20170606-145029_nullObject.png)

#### 打印数组对象

```java
// 全局多维对象数组
private static Object[] objectsArray = new Object[]{
        new boolean[]{false, true, true, false},
        new String[][]{
                new String[]{"22", "23", "24"},
                new String[]{"123", "456", "789"}},
        new int[][][]{new int[][]{
                new int[]{666, 555, 444},
                new int[]{111, 222, 333, 444}},
                new int[][]{
                        new int[]{1, 2, 3, 4, 5, 6},
                        new int[]{7878, 6565, 84155, 7542, 0}}}};


// 打印对象数组
Object[] objectArray = new Object[1024];
for (int i = 0; i < objectArray.length; i++) {
        objectArray[i] = i;
}

Slog.i(objectArray);

// 打印String
String[] stringArray = new String[1024];
for (int i = 1024; i < stringArray.length + 1024; i++) {
        stringArray[i - 1024] = "is " + i;
}
Slog.i(stringArray);

// 打印int数组
int[] intArray = new int[1024];
for (int i = 2048; i < intArray.length + 2048; i++) {
        intArray[i - 2048] = i;
}
Slog.i(intArray);

// 打印多维数组
Slog.i(objectsArray);
```

![array_log](http://of6l49ylt.bkt.clouddn.com/20170606-214007_arrayLog.png)

#### 打印自定义对象解析器的对象

有如下一个`Student`类。

```java
public class Student {
    private int number;
    private int age;
    private String name;
    private boolean isBoy;

    public Student(int number, int age, String name, boolean isBoy){
        this.number = number;
        this.age = age;
        this.name = name;
        this.isBoy = isBoy;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public boolean isBoy() {
        return isBoy;
    }

    public int getNumber() {
        return number;
    }
}
```

自定义一个`Student`对象解析器，所有的对象解析器都必须要实现`Parser`接口。

```java
public class StudentParser implements Parser<Student> {
    @Override
    public Class<Student> getParseType() {
        return Student.class;
    }

    @Override
    public String parseToString(Student student) {
        return student.getName() + " is a " + student.getAge() + " years old " + (student.isBoy() ? "boy" : "girl");
    }
}
```

上面的`parseToString`将在解析对象时调用，由它返回被解析的对象最终要表示的字符串内容。

```java
// 没有添加解析器之前
Student s = new Student(12345, 54, "sky", true);
Slog.d(s);

// 添加解析器之后
Slog.addObjectParser(new StudentParser());
Slog.d(s);
```

![object_parser](http://of6l49ylt.bkt.clouddn.com/20170606-220410_objectParser.png)

#### 打印`Collection`对象

这里以`Map`举例。

```java
// empty map
Map<Integer, Student> map = new HashMap<>();
Slog.d(map);

// int map
Map<Integer, Integer> intMap = new ConcurrentHashMap<>();
intMap.put(1, 2);
intMap.put(1543, 2745867);
intMap.put(17687, 27678);
intMap.put(76781, 27678);
intMap.put(1786768, 26786);
Slog.d(intMap);

// Object Map
Map<Object, String> objectStringMap = new LinkedHashMap<>();
objectStringMap.put(new Object(), "11223786");
objectStringMap.put(new Object(), "475775486");
objectStringMap.put(new Object(), "7856874757");
Slog.d(objectStringMap);

// student Map
map.put(12345, new Student(12345, 54, "sky", true));
map.put(123456, new Student(123456, 56, "sky2", true));
map.put(1234567, new Student(1234567, 15, "sky3", true));
map.put(12345678, new Student(12345678, 25, "sky4", true));
map.put(1234555, new Student(1234555, 35, "sky5", true));
map.put(12345444, new Student(12345444, 45, "sky6", true));
Slog.d(map);

// map itself
Map map1 = new Hashtable<>();
//noinspection CollectionAddedToSelf,unchecked
map1.put(map1, map1);
Slog.d(map1);
```

![map_log](http://of6l49ylt.bkt.clouddn.com/20170606-215157_map_log.png)

### 指定临时的配置项打印日志

前面的示例都是不修改输出配置项时打印的日志，但是在很多时候我们可能根据不同的场景，需要采用不同的日志输出配置。

除了前面说的使用`Slog.getSetting()`方法获取`Setting`对象之后，修改全局输出配置外，`Slog`框架还支持指定临时输出配置，只在下次当前线程打印日志时生效一次的方式更改日志输出效果。

具体包括以下几个方法。

```java
Slog.t()        // 指定下一次当前线程打印日志的tag，最终输出的日志的Tag组合为: prefixTag-tag
Slog.th()       // 指定下一次当前线程打印日志是否显示线程信息
Slog.m()        // 指定下一次当前线程打印日志显示的调用stack中方法的数目
Slog.o()        // 指定下一次当前线程打印日志显示stack方法时的偏移值
Slog.s()        // 指定下一次当前线程打印日志是否采用简单模式输出
```

上面的配置方法可以单个使用也可以组合使用。

#### 单个使用

```java
Slog.t("custom22").i("set tag to custom");
Slog.th(false).i("hide the threadInfo");
Slog.m(0).i("test 0 method count print, so hide track");
Slog.m(3).i("test three method count println");
Slog.o(1).i("method offset 1");
Slog.s(true).i("set to simple mode");
```

![temp_setting_log](http://of6l49ylt.bkt.clouddn.com/20170606-223102_temp_setting_log.png)

#### 联合使用

```java
Slog.s(false).t("fiveSetting").th(true).m(5).o(2).i("this time set five temp setting for test");
```

![](http://of6l49ylt.bkt.clouddn.com/20170606-223327_five_temp_setting_log.png)


### 添加日志适配器

`Slog`框架目前只提供一个实现的日志适配器`LogcatTree`，本框架支持自定义日志适配器，所有的日志适配器都必须要继承`Tree`抽象类或者其子类，为了保证足够的扩展性，我们在`Tree`的接口中除了可以接收到封装处理好的日志之外，也可以对原始的日志数据进行处理。

自定义一个`FileTree`。

```java
public class FileTree extends Tree {

    // ... 还有其他的方法也可以根据需要复写

    // 处理对象类型的日志，注意该接口方法，也可以根据原始的`originalObject`参数进行自定义处理
    @Override
    protected void prepareObjectLog(int priority, String tag, String[] compoundMessages, @Nullable Object originalObject) {
        super.prepareObjectLog(priority, tag, compoundMessages, originalObject);
    }

    // 处理String类型的日志，注意该接口方法，也可以根据原始的`originalMessage`参数进行自定义处理
    @Override
    protected void prepareStringLog(int priority, String tag, Throwable t, String[] compoundMessages, @Nullable String originalMessages, @Nullable Object... args) {
        super.prepareStringLog(priority, tag, t, compoundMessages, originalMessages, args);
    }

    // 该方法为必须要实现的父类抽象方法
    @Override
    protected void log(int priority, String tag, String message) {
        // ... 省略代码将日志保存到文件中
    }
}
```
 
 将其添加到日志适配器列表中，以后就可以正常使用了。

 ```java
 Slog.plantTree(new FileTree());
 ```

**注意：** 在每个日志适配器中，我们可以根据需要最终自己确定将组装之后的日志或者原始日志如何处理。

### 结构概述

Slog打印日志的基本流程可以归纳为以下几个步骤。
- 打印日志，调用对应的`Slog`接口。
- 根据当前日志全局配置，判断是否对需要输出日志（当前是只判断允许输出的日志级别`Priority`）。
- 结合全局日志配置和单次指定的日志配置(单次优先级高于全局)，对原始日志进行组装。
- 将组装好的日志和原始日志数据通过日志分发器分发到各个日志适配器。
- 每个日志适配器最终根据自身实现对日志进行处理。

简单的流程图。

![slog_流程图](http://of6l49ylt.bkt.clouddn.com/20170606-234051_slog流程图.png)


简单的类图。

![Slog结构类图](http://of6l49ylt.bkt.clouddn.com/20170606-235307_Slog结构类图.png)

- `LogAssembler`，日志组装器的抽象类，负责对日志进行组装，调用分发器将组装好的日志进行分发。
- `LogDispatcher`，日志分发器接口。
- `LogController`，分别实现了`TreeManger`，`LogDispatcher`接口，通过其分发日志功能，将日志分发到其管理的日志适配器中。


### 致谢

本库最终形成，分别参考了以下三个库，本库的设计借鉴了它们的设计思想与代码实现，十分感谢。

`Logger`  : [https://github.com/orhanobut/logger](https://github.com/orhanobut/logger)

`Timber`  : [https://github.com/JakeWharton/timber](https://github.com/JakeWharton/timber)

`ViseLog` : [https://github.com/xiaoyaoyou1212/ViseLog](https://github.com/xiaoyaoyou1212/ViseLog)
