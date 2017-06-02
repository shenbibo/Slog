package com.sky.slog;

import android.annotation.SuppressLint;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;


import com.orhanobut.logger.Logger;
import com.sky.slog.bean.Student;
import com.sky.slog.bean.StudentParser;
import com.vise.log.ViseLog;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.*;
import java.util.concurrent.*;

import timber.log.Timber;
import timber.log.Timber.DebugTree;

/**
 * [function]
 * [detail]
 * Created by Sky on 2017/5/24.
 */
@RunWith(AndroidJUnit4.class)
public class SlogTest {
    private static CountDownLatch countDownLatch;

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

    @BeforeClass
    public static void init() {
        Slog.init(new LogcatTree()).showThreadInfo(true).prefixTag("test");
        Slog.addObjectParser(new StudentParser());
        Logger.init("printTime").methodCount(1);
        Timber.plant(new DebugTree());
        ViseLog.getLogConfig()
               .configTagPrefix("printTime")
               .configShowBorders(true)
               .configAllowLog(true)
               .configLevel(Log.VERBOSE);
        ViseLog.plant(new com.vise.log.inner.LogcatTree());
    }

    @Test
    public void logIWithDefaultTag() {
        Slog.i("i no prefixTag Test");
        Slog.i("i no prefixTag test = %d", 2);
    }

    @Test
    public void logEWithCustomTag() {
        Slog.t("custom").e("i prefixTag Test");
        Slog.t("custom2").e("i prefixTag test = %d", 2);
        Slog.t("custom3").e(new Throwable(), "i prefixTag test = %d", 3);
        //        Slog.s(true).e("112", null);
    }

    @Test
    public void logWithSwitchSimpleMode() {
        Slog.s(true).i("testSimpleMode");
    }

    @Test
    public void logWithMultiMethodCount() {
        Slog.m(3).i("test three method count println");
        Slog.m(0).i("test 0 method count print, so hide track");
    }

    @Test
    public void logWithHideThreadInfo() {
        Slog.th(false).i("hide thread info");
    }

    @Test
    public void logWithMethodHideInfoSimpleModeTag() {
        Slog.s(true).m(100).th(true).i("s(true).m(100).th(true)");
        Slog.s(false).m(100).th(false).i("set s(false).m(100).th(false)");
        Slog.s(false).m(0).th(true).t("s(false).m(0).th(true)").i("s(false).m(0).th(true).tag");
    }

    @Test
    public void logWithMethodOffset() {
        Slog.o(1).i("1 offset");
        Slog.o(2).i("2 offset");
        Slog.o(100).i("100 offset");
    }

    /**
     * 测试三个开源工具格式化输出时的性能，结论：
     * 1000条日志下， 表现从好到坏 Logger > Slog > ViseLog
     * 10000条下，  表现从好到坏  Slog > Logger > ViseLog
     */
    @Test
    public void formatLogOutputTest() {
        long startTime;
        long endTime;
        String tag = "printTime";
        String testStr = "this is a test string, so i will print the time to you, this is the log msg, good good day day up";
        // 默认设置测试1000
        Slog.getSetting().prefixTag(tag);
        Slog.getSetting().simpleMode(false).methodCount(1).showThreadInfo(true);
        Log.i(tag, "start for Slog test");
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            Slog.i(testStr);
        }
        endTime = System.currentTimeMillis();
        String slogTime = "slog time = " + (endTime - startTime) + '\n';
//
        Log.i(tag, "start for Logger test");

         //logger 工具在连续打印大量的日志时，可能会出现读错误
         //logger
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            Logger.i(testStr);
        }
        endTime = System.currentTimeMillis();
        String loggerTime = "Logger time = " + (endTime - startTime) + '\n';


        // logger 工具在连续打印大量的日志时，会出现读错误
        Log.i(tag, "start for ViseLog test");
//        // ViseLog
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            ViseLog.i(testStr);
        }
        endTime = System.currentTimeMillis();
        String viseLogTime = "ViseLog time = " + (endTime - startTime) + '\n';

        Slog.t(tag).i("stand mode test 1 method count, show thread info\n" + slogTime + loggerTime + viseLogTime);
    }

    /**
     * 开源日志工具简单模式下性能，因为三个工具对简单模式的定义都不一样，所以测试出来的数据差别会非常大
     * 结论：
     * 10000条下， Slog > Timber > Logger > ViseLog
     * 1000条下，
     */
    @Test
    public void simpleModeTest() {
        long startTime;
        long endTime;
        String tag = "printTime";
        String testStr = "this is a test string, so i will print the time to you, this is the log msg, good good day day up";
        // 默认设置测试10000
        Slog.getSetting().prefixTag(tag);
        Slog.getSetting().simpleMode(true);
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            Slog.i(testStr);
        }
        endTime = System.currentTimeMillis();
        String slogTime = "slog time = " + (endTime - startTime) + '\n';

        // logger
        startTime = System.currentTimeMillis();
        Logger.t("printTime").getSettings().methodCount(0).hideThreadInfo();
        for (int i = 0; i < 1000; i++) {
            Logger.i(testStr);
        }
        endTime = System.currentTimeMillis();
        String loggerTime = "Logger time = " + (endTime - startTime) + '\n';

        // ViseLog
        ViseLog.getLogConfig().configShowBorders(false);
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            ViseLog.i(testStr);
        }
        endTime = System.currentTimeMillis();
        String viseLogTime = "ViseLog time = " + (endTime - startTime) + '\n';

        // time test
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            Timber.tag(tag).i(testStr);
        }
        endTime = System.currentTimeMillis();
        String timberTime = "timber  time = " + (endTime - startTime) + '\n';

        Log.i(tag, "simple mode test\n" + slogTime + loggerTime + viseLogTime + timberTime);
    }

    @Test
    public void loopPrintLogTest() {
        long startTime;
        long endTime;
        String tag = "printTime";
        String testStr = "this is a test string, so i will print the time to you, this is the log msg, good good day day up";
        // 默认设置测试1000
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            Slog.i(testStr);
        }
        endTime = System.currentTimeMillis();
        String time1 = "default config time = " + (endTime - startTime) + '\n';

        // 关闭线程信息测试
        Slog.getSetting().showThreadInfo(false);
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            Slog.i(testStr);
        }
        endTime = System.currentTimeMillis();
        String time2 = "close thread info config time = " + (endTime - startTime) + '\n';

        // 关闭线程信息和调用堆栈信息测试
        Slog.getSetting().methodCount(0);
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            Slog.i(testStr);
        }
        endTime = System.currentTimeMillis();
        String time3 = "close thread info and method stack config time = " + (endTime - startTime) + '\n';

        // simple mode test
        Slog.getSetting().simpleMode(true);
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            Slog.i(testStr);
        }
        endTime = System.currentTimeMillis();
        String time4 = "simple mode config time = " + (endTime - startTime) + '\n';

        // logcat print
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            Log.i(tag, testStr);
        }
        endTime = System.currentTimeMillis();
        String time5 = "logcat print time = " + (endTime - startTime);

        Slog.getSetting().simpleMode(false);
        Slog.t(tag).i(time1 + time2 + time3 + time4 + time5);

    }

    //    @Test
    public void logCollectTest() {
        logWithSwitchSimpleMode();
        logWithMultiMethodCount();
        logWithHideThreadInfo();
        logWithMethodHideInfoSimpleModeTag();
    }

    @Test
    public void json() {
        // inval json
        String inval1 = "";
        String inval2 = "{}";
        Slog.json(inval1);
        Slog.json(inval2);

        String json = "{'xyy1':[{'test1':'test1'},{'test2':'test2'}],'xyy2':{'test3':'test3','test4':'test4'}}";

        Slog.json(json);

        String jsonArray =
                "{ 'employees': [ {'firstName':'John', 'lastName':'Doe'}, {'firstName':'Anna', 'lastName':'Smith'}, "
                        + "{'firstName':'Peter', 'lastName':'Jones'}]}";
        Slog.json(jsonArray);
    }

    @Test
    public void xml() {
        String empty = "";
        String nullString = null;
        Slog.xml(empty);
        Slog.xml(nullString);

        String invalXml = "{}";
        Slog.xml(invalXml);

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
    }

    @Test
    public void setting() {
        Setting setting = Slog.getSetting();
        Slog.i(setting.toString());

        int count = 3;
        setting.methodCount(count);
        Slog.i("global methodCount set to %d", count);

        int priority = Slog.WARN;
        setting.logPriority(priority);
        Slog.i("global logPriority set to %d", priority);

        // reset to full
        setting.logPriority(Slog.FULL);

        int methodOffset = 1;
        setting.methodOffset(methodOffset);
        Slog.i("global methodOffset set to %d", methodOffset);

        boolean showThreadInfo = false;
        setting.showThreadInfo(showThreadInfo);
        Slog.i("global showThreadInfo set to %b", showThreadInfo);


        boolean simpleMode = true;
        setting.simpleMode(simpleMode);
        Slog.i("global simpleMode set to %b", simpleMode);

        String prefixTag = "new test Tag";
        setting.prefixTag(prefixTag);
        Slog.i("global prefixTag set to %s", prefixTag);

        // after set all above param, now i user everytime setting
        Slog.t("good").m(5).o(2).s(false).th(true).i("this is the old story!!!");
    }

    @Test
    public void longLogTest() {
        StringBuilder sb = new StringBuilder((int) (8192 / 0.75));
        for (int i = 0; i < 8192; i++) {
            sb.append(i);
        }
        Slog.t("longLogTest").i(sb.toString());
    }

    @Test
    public void multiLineLogTest() {
        StringBuilder sb = new StringBuilder((int) (8192 / 0.75));
        for (int i = 0; i < 8192; i++) {
            if (i % 30 == 0) {
                sb.append("\n");
            }
            sb.append(i);
        }
        Slog.t("multiLineLogTest").i(sb.toString());
    }

    @Test
    public void primitiveValueTest() {
        Slog.dO(1);
        Slog.dO(true);
        Slog.dO('S');
        Slog.dO(1.723);
        Slog.dO(3.14f);
        Slog.dO((byte) 8);
        Slog.dO((short) 244);
        Slog.dO(12345678912L);
    }

    @Test
    public void nullObject() {
        Slog.dO(null);
    }

    @Test
    public void nullAndEmptyStringTest() {
        Slog.i(null);
        Slog.i("");
    }

    @Test
    public void object() {
        String[] name = {"sga", "gsadgsa", "sgdsfhds"};
        Slog.i("array test");
        Slog.dO(name);

        String[] name2 = {"wwt", "wetgety", "reyertu"};
        Slog.i("list test");
        Slog.dO(Arrays.asList(name2));

        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < name.length; i++) {
            map.put(name[i], name2[i]);
        }
        Slog.i("map test");
        Slog.dO(map);

        String[] name3 = {"wggsg", "hketydfhdsh", "7887reyertu"};
        Set<String> set = new HashSet<>();
        set.addAll(Arrays.asList(name3));
        Slog.i("set test");
        Slog.dO(set);

    }

    @Test
    public void logWithDefaultTagFormNoneToFull() {
        Slog.log(-100, "FormNoneToFull", null, "-100");             // 无打印
        Slog.log(Slog.NONE, "FormNoneToFull", null, "NONE1111");
        Slog.log(Slog.VERBOSE, "FormNoneToFull", null, "INFO");
        Slog.log(Slog.INFO, "FormNoneToFull", null, "INFO");
        Slog.log(Slog.WARN, "FormNoneToFull", null, "WARN");
        Slog.log(Slog.ERROR, "FormNoneToFull", null, "ERROR");
        Slog.log(Slog.ASSERT, "FormNoneToFull", null, "ASSERT");
        Slog.log(Slog.FULL, "FormNoneToFull", null, "info");        // 不打印
        Slog.log(100, "FormNoneToFull", null, "+100");              // 不答应
    }

    /**
     * 测试打印数组
     */
    @Test
    public void arrayLogTest() {
        // 打印对象数组
        Object[] objectArray = new Object[1024];
        for (int i = 0; i < objectArray.length; i++) {
            objectArray[i] = i;
        }

        Slog.iO(objectArray);

        // 打印String
        String[] stringArray = new String[1024];
        for (int i = 1024; i < stringArray.length + 1024; i++) {
            stringArray[i - 1024] = "" + i;
        }
        Slog.iO(stringArray);

        // 打印int数组
        int[] intArray = new int[1024];
        for (int i = 2048; i < intArray.length + 2048; i++) {
            intArray[i - 2048] = i;
        }
        Slog.iO(intArray);

        // 打印多维数组
        Slog.iO(objectsArray);
    }

    @Test
    public void listTest() {
        // empty list test
        List<Object> arrayList = new ArrayList<>();
        Slog.dO(arrayList);

        // string list
        List<String> stringList = new ArrayList<>();
        stringList.add("12345");
        stringList.add("7845");
        stringList.add("klslslg");
        stringList.add("skjweot");
        Slog.dO(stringList);

        // int[] list
        List<int[]> intArrayList = new LinkedList<>();
        intArrayList.add(new int[]{1, 2, 3, 4});
        intArrayList.add(new int[]{7, 83, 7893, 53});
        intArrayList.add(new int[]{1887, 4562, 3456, 463});
        intArrayList.add(new int[]{17986, 2789, 398, 4546});
        Slog.dO(intArrayList);

        // Object list
        arrayList.add(new int[]{379856, 274589, 398, 4546});
        arrayList.add(objectsArray);
        arrayList.add(new Object());
        Slog.dO(arrayList);
    }

    @Test
    public void setTest() {
        // empty list test
        Set<Object> arrayList = new HashSet<>();
        Slog.dO(arrayList);

        // string list
        Set<String> stringList = new CopyOnWriteArraySet<>();
        stringList.add("12345");
        stringList.add("7845");
        stringList.add("klslslg");
        stringList.add("skjweot");
        Slog.dO(stringList);

        // int[] list
        Set<int[]> intArrayList = new LinkedHashSet<>();
        intArrayList.add(new int[]{1, 2, 3, 4});
        intArrayList.add(new int[]{7, 83, 7893, 53});
        intArrayList.add(new int[]{1887, 4562, 3456, 463});
        intArrayList.add(new int[]{17986, 2789, 398, 4546});
        Slog.dO(intArrayList);

        // Object list
        arrayList.add(new int[]{379856, 274589, 398, 4546});
        arrayList.add(objectsArray);
        arrayList.add(new Object());
        Slog.dO(arrayList);

        // add itself
        //noinspection CollectionAddedToSelf
        arrayList.add(arrayList);
        Slog.dO(arrayList);
    }

    @Test
    public void mapTest() {
        // empty map
        @SuppressLint("UseSparseArrays")
        Map<Integer, Student> map = new HashMap<>();
        Slog.dO(map);

        // int map
        Map<Integer, Integer> intMap = new ConcurrentHashMap<>();
        intMap.put(1, 2);
        intMap.put(1543, 2745867);
        intMap.put(17687, 27678);
        intMap.put(76781, 27678);
        intMap.put(1786768, 26786);
        Slog.dO(intMap);

        // Object Map
        Map<Object, String> objectStringMap = new LinkedHashMap<>();
        objectStringMap.put(new Object(), "11223786");
        objectStringMap.put(new Object(), "475775486");
        objectStringMap.put(new Object(), "7856874757");
        Slog.dO(objectStringMap);

        // student Map
        map.put(12345, new Student(12345, 54, "kdkk", true));
        map.put(123456, new Student(123456, 56, "kdkk", true));
        map.put(1234567, new Student(1234567, 15, "kdkk", true));
        map.put(12345678, new Student(12345678, 25, "kdkk", true));
        map.put(1234555, new Student(1234555, 35, "kdkk", true));
        map.put(12345444, new Student(12345444, 45, "kdkk", true));
        Slog.dO(map);

        // map itself
        Map map1 = new Hashtable<>();
        //noinspection CollectionAddedToSelf,unchecked
        map1.put(map1, map1);
        Slog.dO(map1);
    }

    /**
     * 测试多线程设置tag的准确性
     */
    @Test
    public void multithreadingPrintLog() throws InterruptedException {
        countDownLatch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            new LogTestThread(i).start();
        }
        countDownLatch.await();
    }

    private class LogTestThread extends Thread {
        int index;

        LogTestThread(int i) {
            index = i;
            setName("Thread_" + i);
        }

        @Override
        public void run() {
            String tag = "LogTestThread_" + index;
            for (int i = 0; i < 20; i++) {
                Slog.t(tag).d(tag + "_" + i);
            }
            countDownLatch.countDown();
        }
    }
}
