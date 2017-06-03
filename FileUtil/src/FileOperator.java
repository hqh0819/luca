import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Created by luca on 2017/5/16.
 * <p>
 * 使用多线程来复制或删除文件或文件夹 也有壓縮解壓ZIP的功能，詳細參看main方法幫助
 *
 * @author qiheng.hu
 */
public class FileOperator {

    // 防止被new
    private FileOperator() {
    }

    /**
     * 命令列入口。使用方法： 1.copy "來源目錄" "目標目錄" "是否覆蓋檔(0為不覆蓋，其它為覆蓋)" "使用線程數" 注：copy
     * 後至少要用兩個參數，沒寫的參數："是否覆蓋檔" 為是 ，線程數默認為10 2.move "來源目錄" "目標目錄" "使用線程數" 注：move
     * 後至少要用兩個參數，沒寫的參數：線程數默認為10 3.del (或delete)"目標目錄" "使用線程數"
     * 注：del後至少要用一個參數，沒寫的參數：線程數默認為10 4.zip(或packzip) "來源目錄" "目的檔案" "緩存大小(單位為K)"
     * "附加頂層目錄" 注：zip後至少要用兩個參數，沒寫的參數：緩存大小默認為1024（小於10的參數會忽略），附加頂層目錄為""
     * 5.unzip(或unpackzip) "來源目錄" "目的檔案" "緩存大小(單位為K)" 注：unzip(或unpackzip)
     * 至少要有兩個參數，沒寫的參數：緩存大小默認為1024（小於10的參數會忽略）
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        if (args.length >= 3 && args[0].equalsIgnoreCase("copy")) {
            File f = new File(args[1]);
            File f1 = new File(args[2]);
            int temp = 1;
            try {
                temp = Integer.parseInt(args[3]);
            } catch (Exception ignored) {
            }
            int thereabout;
            thereabout = 0;
            try {
                thereabout = Integer.parseInt(args[4]);
            } catch (Exception e) {
            }

            if (f.isFile()) {
                copyFile(f, f1, temp != 0);
            }
            if (f.isDirectory()) {
                copyDirectory(f, f1, temp != 0, thereabout > 0 ? thereabout : 10);
            }
        } else if (args.length >= 3 && args[0].equalsIgnoreCase("move")) {
            File f = new File(args[1]);
            File f1 = new File(args[2]);
            int threadcount = 0;
            try {
                threadcount = Integer.parseInt(args[4]);
            } catch (Exception e) {
            }

            if (f.isFile()) {
                moveFile(f, f1);
            } else if (f.isDirectory()) {
                moveDirectory(f, f1, threadcount > 0 ? threadcount : 10);
            }
        } else if (args.length >= 2 && (args[0].equalsIgnoreCase("del") || args[0].equalsIgnoreCase("delete"))) {
            File f = new File(args[1]);
            int threadcount = 0;
            try {
                threadcount = Integer.parseInt(args[2]);
            } catch (Exception e) {
            }

            if (f.isFile()) {
                deleteFile(f);
            } else {
                deleteDirectory(f, threadcount > 0 ? threadcount : 10);
            }
        } else if (args.length >= 2 && (args[0].equalsIgnoreCase("zip") || args[0].equalsIgnoreCase("packzip"))) {
            File f = new File(args[1]);
            File f1 = new File(args[2]);
            int buffer = 0;
            String s = "";
            try {
                buffer = Integer.parseInt(args[3]);
            } catch (Exception e) {
            }
            if (args.length >= 5)
                s = args[4];

            try {
                packToZip(f, f1, buffer > 10 ? buffer : 1024, s);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (args.length >= 2 && (args[0].equalsIgnoreCase("unpackzip") || args[0].equalsIgnoreCase("unzip"))) {
                File f = new File(args[1]);
                File f1 = new File(args[2]);
                int buffer = 0;
                try {
                    buffer = Integer.parseInt(args[3]);
                } catch (Exception ignored) {
                }
                try {
                    unPackzip(new ZipFile(f), f1, buffer > 10 ? buffer : 1024);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                String s = "" + "請按以下方式輸入參數：" + "\n" +
                        "1.copy \"來源目錄\" \"目標目錄\" \"是否覆蓋檔(0為不覆蓋，其它為覆蓋)\" \"使用線程數\"" + "\n" +
                        "  注：copy 後至少要用兩個參數，沒寫的參數：\"是否覆蓋檔\" 為是 ，線程數默認為10" + "\n" +
                        "2.move \"來源目錄\" \"目標目錄\" \"使用線程數\"" + "\n" +
                        "  注：move 後至少要用兩個參數，沒寫的參數：線程數默認為10" + "\n" +
                        "3.del (或delete)\"目標目錄\" \"使用線程數\"" + "\n" +
                        "  注：del後至少要用一個參數，沒寫的參數：線程數默認為10" + "\n" +
                        "4.zip(或packzip)  \"來源目錄\" \"目的檔案\" \"緩存大小(單位為K)\" \"附加頂層目錄\"" + "\n" +
                        "  注：zip後至少要用兩個參數，沒寫的參數：緩存大小默認為1024（小於10的參數會忽略），附加頂層目錄為\"\"" + "\n" +
                        "5.unzip(或unpackzip)  \"來源目錄\" \"目的檔案\" \"緩存大小(單位為K)\"" + "\n" +
                        "  注：unzip(或unpackzip) 至少要有兩個參數，沒寫的參數：緩存大小默認為1024（小於10的參數會忽略）" + "\n";

                System.out.println(s);
            }
        }
    }

    /**
     * 删除单一一个文件.
     *
     * @param src 要删除的文件
     */
    public static boolean deleteFile(File src) {
        if (!src.isFile()) {
            System.out.println(src.getAbsoluteFile() + ",不是文件");
            return false;
        }
        return src.delete();
    }

    /**
     * 删除一个目录 此调用另外一个deleteDirectory方法，并设置默认10个线程
     *
     * @param src 要删除的目录
     */
    public static Thread deleteDirectory(File src) {
        return deleteDirectory(src, 10);
    }

    /**
     * 删除一个目录. 先使用多个线程来删除文件，再使用单个线程从内向外删除文件夹
     *
     * @param src             要删除的目录
     * @param corethreadcount 使用线程数量
     */
    public static Thread deleteDirectory(final File src, final int corethreadcount) {
        if (!src.isDirectory()) {
            System.out.println(src.getAbsoluteFile() + ",不是文件夾");
            return null;
        }
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                long time = System.currentTimeMillis();
                String s = "Delete:" + src.getAbsolutePath() + "\n" + "使用線程數：" + corethreadcount + "，耗時：";
                System.out.println("開始......");
                // 获得src中所有文件及文件夹List
                final LinkedList<File> filelist = new LinkedList<File>();
                final LinkedList<File> directorylist = new LinkedList<File>();
                // 创建线程池，核心corethreadcount个线程，线程最大数量为corethreadcount*1.5,等级队列corethreadcount个，如线程满且队列也满则执行等级策略CallerRunsPolicy
                final ThreadPoolExecutor pool = new ThreadPoolExecutor(corethreadcount, (int) (corethreadcount * 1.5), 0,
                        TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(corethreadcount),
                        new ThreadPoolExecutor.CallerRunsPolicy());


                final Lock lock = new ReentrantLock();
                // 将遍历任务添加到线程池
                final Future futere_isoverflag = pool.submit(new Runnable() {
                    @Override
                    public void run() {
                        listAllFilesToDeleteDictory(src, filelist, directorylist, lock);
                    }
                });

                // 添加文件删除任务，只删文件
                while (true) {
                    File f;
                    // System.out.println(flag.toString());
                    lock.lock();
                    try {
                        f = filelist.pollFirst();
                    } finally {
                        lock.unlock();
                    }
                    if (f != null) {
                        // 添加文件删除任务
                        f.delete();
                    } else if (futere_isoverflag.isDone()) {
                        break;
                    }
                }
                // 任务添加完后关闭线程池
                pool.shutdown();

                // 循环等待，直到所有文件被删除
                while (true) {
                    try {
                        if (pool.awaitTermination(100, TimeUnit.SECONDS))
                            break;
                    } catch (InterruptedException e) {

                        e.printStackTrace();
                    }
                }
                // 此时所有文件已被删除

                // 因为文件夹要从最里面往外删除，只有空文件夹才能删除成功。directorylist里面的文件夹按顺序排列
                // pool = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, new
                // LinkedBlockingDeque<Runnable>(30), new
                // ThreadPoolExecutor.CallerRunsPolicy());
                // 添加src目录
                directorylist.addLast(src);
                while (!directorylist.isEmpty()) {
                    directorylist.pollFirst().delete();
                }
                // 输出执行所耗时间
                System.out.println(s + (System.currentTimeMillis() - time) / 1000 + "秒");
            }
        });
        thread.start();
        return thread;
    }

    /**
     * 复制一个文件 覆盖文件
     *
     * @param src 来源文件
     * @param des 目标文件
     */
    public static Thread copyFile(File src, File des) {
        return copyFile(src, des, true);
    }

    /**
     * 复制一个文件
     *
     * @param src      来源文件
     * @param des      目标文件
     * @param override 是否覆盖文件
     */
    public static Thread copyFile(final File src, final File des, final boolean override) {
        if (!src.isFile()) {
            System.out.println(src.getAbsoluteFile() + ",不是文件");
            return null;
        } else if (des.exists() && !override) {
            System.out.println("目的檔案已存在，選擇不覆蓋。無需複製操作。");
            return null;
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                long time = System.currentTimeMillis();
                String s = "Copy:" + src.getAbsolutePath() + ",to:" + des.getAbsolutePath() + "\n" + ",耗時：";
                System.out.println("開始......");
                des.getParentFile().mkdirs();
                // 创建线程池，核心corethreadcount个线程，线程最大数量为corethreadcount*1.5,等级队列corethreadcount个，如线程满且队列也满则执行等级策略CallerRunsPolicy
                final ExecutorService pool = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS,
                        new LinkedBlockingDeque<Runnable>(1), new ThreadPoolExecutor.CallerRunsPolicy());
                if (!des.isFile() || override) copyFile(src, des, pool);
                pool.shutdown();
                // 等待，直到复制任务都执行完毕
                while (true) {
                    try {
                        if (pool.awaitTermination(100, TimeUnit.SECONDS))
                            break;
                    } catch (InterruptedException e) {

                        e.printStackTrace();
                    }
                }
                // 输出耗时
                // 输出耗时
                System.out.println(s + (System.currentTimeMillis() - time) / 1000 + "秒");

            }
        });
        thread.start();
        return thread;

    }

    /**
     * 复制一个目录. 使用默认10个线程且覆盖文件
     *
     * @param src 来源目录
     * @param des 目标目录
     */
    public static void copyDirectory(File src, File des) {
        copyDirectory(src, des, true, 10);
    }

    /**
     * 复制一个目录. 使用默认10个线程
     *
     * @param src      来源目录
     * @param des      目标目录
     * @param override 是否覆盖文件
     */
    public static Thread copyDirectory(File src, File des, boolean override) {
        return copyDirectory(src, des, override, 10);
    }

    /**
     * 复制一个目录..
     *
     * @param src             来源目录
     * @param des             目标目录
     * @param override        是否覆盖文件
     * @param corethreadcount 线程数
     */
    public static Thread copyDirectory(final File src, final File des, final boolean override, final int corethreadcount) {
        if (!src.isDirectory()) {
            System.out.println(src.getAbsoluteFile() + ",不是文件夾");
            return null;
        }
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                long time = System.currentTimeMillis();
                String s = "Copy:" + src.getAbsolutePath() + ",to:" + des.getAbsolutePath() + "\n" + "使用線程數："
                        + corethreadcount + ",耗時：";
                System.out.println("開始......");
                if (!src.isDirectory() || !src.exists())
                    return;
                if (!des.isDirectory())
                    des.mkdirs();
                // 创建线程池，核心corethreadcount个线程，线程最大数量为corethreadcount*1.5,等级队列corethreadcount个，如线程满且队列也满则执行等级策略CallerRunsPolicy
                final ThreadPoolExecutor pool = new ThreadPoolExecutor(corethreadcount, (int) (corethreadcount * 1.5), 0,
                        TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(corethreadcount),
                        new ThreadPoolExecutor.CallerRunsPolicy());

                // 调用文件夹复制，将所有的复制任务添加到pool中
                copyDirectory(src, des, override, pool);
                // 关闭线程池
                pool.shutdown();

                // 等待，直到复制任务都执行完毕
                while (true) {
                    try {
                        if (pool.awaitTermination(100, TimeUnit.SECONDS))
                            break;
                    } catch (InterruptedException e) {

                        e.printStackTrace();
                    }
                }

                // 输出耗时
                System.out.println(s + (System.currentTimeMillis() - time) / 1000 + "秒");
            }
        });
        thread.start();
        return thread;

    }

    /**
     * 复制文件夹最终执行方法,遍历所有文件后，调用copyOneFile进行复制
     * 遍历文件夹与复制文件是同时进行的，先创建遍历文件夹的线程，后一个一个创建复制文件的线程
     *
     * @param src  来源目标
     * @param des  目标目录
     * @param pool 复制文件要添加到的线程池
     */
    public static void copyDirectory(final File src, final File des, final boolean override, ExecutorService pool) {
        // 遍历文件夹，将要复制的文件与目录文件放到Map中待复制
        final LinkedList<File[]> list = new LinkedList<File[]>();

        final Lock lock = new ReentrantLock();
        // 将遍历任务添加到线程池
        final Future future_isoverflag = pool.submit(new Runnable() {
            @Override
            public void run() {
                listFilesToCopyDirectory(src, new File(des.getAbsolutePath() + "\\" + src.getName()), list, override,
                        lock);
            }
        });
        // 将要复制的文件一个一个创建执行任务
        while (true) {
            File[] fs;
            // System.out.println(flag.toString());
            lock.lock();
            try {
                fs = list.pollFirst();
            } finally {
                lock.unlock();
            }
            if (fs != null) {
                // 将复制文件任务添加到线程池
                if (!fs[1].isFile() || override) FileOperator.copyFile(fs[0], fs[1], pool);
            } else if (future_isoverflag.isDone()) {
                break;
            }

        }

    }

    /**
     * 复制文件真正执行的方法，将复制任务添加到pool线程池 覆盖文件
     *
     * @param src  来源文件
     * @param des  目标文件
     * @param pool 复制文件要添加到的线程池
     */
    public static void copyFile(final File src, final File des, ExecutorService pool) {

        pool.execute(new Runnable() {
            /**
             * 执行复制操作
             */
            @Override
            public void run() {
                try {
                    Files.copy(Paths.get(src.getAbsolutePath()), Paths.get(des.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
                    //copyFileByFileChanner(src, des);
                    //copyFileByFileOutputStream(src, dsc, 1024);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });

    }

    /**
     * 遍厍来源目录，把来源文件和目标文件一一对应放到Map对象中，用于复制文件夹时使用
     *
     * @param src      来源目录
     * @param des      目标目录
     * @param list     要放置的list
     * @param override 是否覆盖文件
     */
    protected static void listFilesToCopyDirectory(File src, File des, LinkedList<File[]> list, boolean override,
                                                   Lock lock) {
        for (File f : src.listFiles()) {
            if (f.isFile() && (override == true || !des.exists())) {
                File[] files = new File[2];
                files[0] = f;
                files[1] = new File(des.getAbsolutePath() + "\\" + f.getName());
                lock.lock();
                try {
                    list.addFirst(files);
                } finally {
                    lock.unlock();
                }

            } else if (f.isDirectory()) {
                File af = new File(des.getAbsolutePath() + "\\" + f.getName());
                af.mkdirs();
                listFilesToCopyDirectory(f, af, list, override, lock);
            }
        }
    }

    /**
     * 列出指定目录中的所有子目录以及文件，包含文件放到filelist中，包含文件夹放到directorylist中
     * 用于删除文件夹deleteDirectory时使用
     *
     * @param src           要列出包含内容的文件夹
     * @param filelist      包含的文件
     * @param directorylist 包含的文件夹，从外到内依次添加
     */
    protected static void listAllFilesToDeleteDictory(File src, LinkedList<File> filelist,
                                                      LinkedList<File> directorylist, Lock lock) {
        for (File f : src.listFiles()) {
            if (f.isDirectory()) {
                directorylist.addFirst(f);
                listAllFilesToDeleteDictory(f, filelist, directorylist, lock);
            } else if (f.isFile()) {
                lock.lock();
                try {
                    filelist.addFirst(f);
                } finally {
                    lock.unlock();
                }

            }
        }

    }

    /**
     * 使用RandomAccessFile类来复制一个文件的一部分，如果要复制整个文件可以说设置start=0,end=src.lenght()
     * 使用1024K buffer
     *
     * @param src   来源文件
     * @param dsc   目标文件
     * @param start 复制起始位置
     * @param end   复制结束位置
     * @throws IOException the io exception
     */
    public static void copyFilePartByRandomAccessFile(File src, File dsc, long start, long end) throws IOException {
        copyFilePartByRandomAccessFile(src, dsc, start, end, 1024);
    }

    /**
     * 使用RandomAccessFile类来复制一个文件的一部分，如果要复制整个文件可以说设置start=0,end=src.lenght()
     *
     * @param src    来源文件
     * @param dsc    目标文件
     * @param start  复制起始位置
     * @param end    复制结束位置
     * @param buffer 复制时使用的缓存大小
     * @throws IOException the io exception
     */
    public static void copyFilePartByRandomAccessFile(File src, File dsc, long start, long end, int buffer)
            throws IOException {
        RandomAccessFile in = null, ou = null;

        in = new RandomAccessFile(src, "r");
        ou = new RandomAccessFile(dsc, "rw");
        in.seek(start);
        ou.seek(start);

        byte[] data = new byte[((end - start >= buffer * 1024 ? buffer * 1024 : (int) (end - start)))];
        int len;
        while (start < end) {
            len = in.read(data, 0, end - start >= data.length ? data.length : (int) (end - start));
            ou.write(data, 0, len);
            start += len;
        }
        in.close();
        ou.close();

    }

    /**
     * 使用FileOutputStream复制文件
     *
     * @param src    来源文件
     * @param des    目标文件
     * @param buffer 复制时使用的缓存大小
     * @throws IOException the io exception
     */
    public static void copyFileByFileOutputStream(File src, File des, int buffer) throws IOException {

        InputStream in = new FileInputStream(src);
        OutputStream ou = new FileOutputStream(des);

        byte[] b = new byte[buffer * 1024];
        int len;
        while ((len = in.read(b)) != -1) {
            ou.write(b, 0, len);
        }
        in.close();
        ou.close();
    }

    /**
     * 使用RandomAccessFile类来复制一个文件 使用1024K buffer
     *
     * @param src 来源文件
     * @param des 目标文件
     * @throws IOException the io exception
     */
    public static void copyFileByRandomAccessFile(File src, File des) throws IOException {
        copyFileByRandomAccessFile(src, des, 1024);
    }

    /**
     * 使用RandomAccessFile类来复制一个文件
     *
     * @param src    来源文件
     * @param des    目标文件
     * @param buffer 复制时使用的缓存大小
     * @throws IOException the io exception
     */
    public static void copyFileByRandomAccessFile(File src, File des, int buffer) throws IOException {
        copyFilePartByRandomAccessFile(src, des, 0, src.length(), buffer);
    }

    /**
     * 使用RandomAccessFile类来复制一个文件，且使用多线程 每5倍buffer大小使用一个线程，使用1024K Buffer
     *
     * @param src 来源文件
     * @param des 目标文件
     * @throws IOException the io exception
     */
    public static void copyFileByRandomAccessFileMulitThread(File src, File des) throws IOException {
        copyFileByRandomAccessFileMulitThread(src, des, 1024);
    }

    /**
     * 使用RandomAccessFile类来复制一个文件，且使用多线程 每5倍buffer大小使用一个线程
     *
     * @param src    来源文件
     * @param des    目标文件
     * @param buffer 复制时使用的缓存大小
     * @throws IOException the io exception
     */
    public static void copyFileByRandomAccessFileMulitThread(File src, File des, int buffer) throws IOException {
        int threadcount = src.length() > buffer * 1024 ? (int) (src.length() / 1024 / buffer / 5) : 1;
        long copylong = src.length() / threadcount + 1;
        long start, end;
        Thread[] rs = new Thread[threadcount];
        for (int i = 0; i < threadcount; i++) {
            start = copylong * i;
            end = src.length() > start + copylong ? start + copylong : src.length();
            rs[i] = new Thread(new Runnable() {
                private File src, des;
                private int buffer;
                private long start, end;

                public Runnable init(File src, File des, int buffer, long start, long end) {
                    this.src = src;
                    this.des = des;
                    this.buffer = buffer;
                    this.start = start;
                    this.end = end;
                    return this;
                }

                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName() + ",start");
                    try {
                        copyFilePartByRandomAccessFile(src, des, start, end, buffer);
                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }
            }.init(src, des, buffer, start, end));
            rs[i].start();
        }
        for (int i = 0; i < threadcount; i++) {
            try {
                rs[i].join();
            } catch (InterruptedException e) {

                e.printStackTrace();
            }
        }
    }

    /**
     * 使用BufferOutputStream类来复制一个文件 使用1024K buffer
     *
     * @param src 来源文件
     * @param des 目标文件
     * @throws IOException the io exception
     */
    public static void copyFileByBufferOutputStream(File src, File des) throws IOException {
        copyFileByBufferOutputStream(src, des, 1024);
    }

    /**
     * 使用BufferOutputStream类来复制一个文件
     *
     * @param src    来源文件
     * @param des    目标文件
     * @param buffer 复制时使用的缓存大小
     * @throws IOException the io exception
     */
    public static void copyFileByBufferOutputStream(File src, File des, int buffer) throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream(src));
        OutputStream ou = new BufferedOutputStream(new FileOutputStream(des));

        byte[] b = new byte[buffer * 1024];
        int len;
        while ((len = in.read(b)) != -1) {
            ou.write(b, 0, len);
        }
        in.close();
        ou.close();
    }

    /**
     * 使用FileChanner类来复制一个文件
     *
     * @param src 来源文件
     * @param des 目标文件
     * @throws IOException the io exception
     */
    public static void copyFileByFileChanner(File src, File des) throws IOException {

        FileChannel in = null, ou = null;

        FileInputStream fileInputStream = new FileInputStream(src);
        in = fileInputStream.getChannel();
        FileOutputStream fileOutputStream = new FileOutputStream(des);
        ou = fileOutputStream.getChannel();
        // 复制文件
        for (long posize = 0; posize < src.length(); posize += in.transferTo(posize, src.length() - posize, ou))
            ;
        fileInputStream.close();
        fileOutputStream.close();
        in.close();
        ou.close();

    }

    /**
     * 解压zip文件 使用1024K buffer
     *
     * @param zf  要解压的zip文件
     * @param des 目标目录
     * @throws IOException the io exception
     */
    public static void unPackzip(ZipFile zf, File des) throws IOException {
        unPackzip(zf, des, 1024);
    }

    /**
     * 解压zip文件
     *
     * @param zf     要解压的zip文件
     * @param des    目标目录
     * @param buffer the buffer
     * @throws IOException the io exception
     */
    public static void unPackzip(ZipFile zf, File des, int buffer) throws IOException {
        System.out.println("開始......");
        long time = System.currentTimeMillis();

        for (Enumeration e = zf.entries(); e.hasMoreElements(); ) {
            ZipEntry ze = (ZipEntry) e.nextElement();
            File f = new File(des.getAbsolutePath() + "\\" + ze.getName());
            if (ze.isDirectory()) {
                f.mkdirs();
                continue;
            } else {
                f.getParentFile().mkdirs();
            }

            InputStream in = zf.getInputStream(ze);
            OutputStream ou = new FileOutputStream(f);

            byte[] b = new byte[ze.getSize() > 1024 * buffer ? 1024 * buffer : (int) ze.getSize()];
            int len;
            while ((len = in.read(b)) != -1) {
                ou.write(b, 0, len);
            }
            in.close();
            ou.close();
        }
        System.out.println("UnPackZip:" + zf.getName() + ",to:" + des.getAbsolutePath() + "\n" + ",耗時："
                + (System.currentTimeMillis() - time) / 1000 + "秒");
    }

    /**
     * 压缩成zip文件 使用1024K 缓存
     *
     * @param src 要压缩的文件或目录
     * @param zf  目标zip文件
     * @throws IOException the io exception
     */
    public static void packToZip(File src, File zf) throws IOException {
        packToZip(src, zf, 1024, "");
    }

    /**
     * 压缩成zip文件
     *
     * @param src    要压缩的文件或目录
     * @param zf     目标zip文件
     * @param buffer 缓存大小
     * @throws IOException the io exception
     */
    public static void packToZip(File src, File zf, int buffer) throws IOException {
        packToZip(src, zf, buffer, "");
    }

    /**
     * Pack to zip.
     *
     * @param src the src
     * @param zf  the zf
     * @param dsc the dsc
     * @throws IOException the io exception
     */
    public static void packToZip(File src, File zf, String dsc) throws IOException {
        packToZip(src, zf, 1024, dsc);
    }

    /**
     * 压缩指定文件或文件夹
     *
     * @param src     来源文件或目录
     * @param zf      目标ZIP文件
     * @param buffer  Buffer大小
     * @param subPath 在ZIP文件中附加顶层目录
     * @throws IOException the io exception
     */
    public static void packToZip(File src, File zf, int buffer, String subPath) throws IOException {
        System.out.println("開始......");
        long time = System.currentTimeMillis();

        if (src.isFile()) {
            ZipOutputStream zou = new ZipOutputStream(new FileOutputStream(zf));
            ZipEntry ze = new ZipEntry((subPath.equals("") ? "" : (subPath + "\\")) + src.getName());
            FileInputStream in = new FileInputStream(src);

            byte[] b = new byte[src.length() > buffer * 1024 ? buffer * 1024 : (int) src.length()];
            int len;
            zou.putNextEntry(ze);
            while ((len = in.read(b)) != -1) {
                zou.write(b, 0, len);
            }
            zou.closeEntry();
            in.close();
            zou.close();

        } else if (src.isDirectory()) {
            LinkedList<String[]> list = new LinkedList<String[]>();
            ListFilesToPackZip(src, list, subPath);
            ZipOutputStream zou = new ZipOutputStream(new FileOutputStream(zf));
            ZipEntry ze;
            File outfile;
            String[] ss;
            while ((ss = list.pollFirst()) != null) {
                ze = new ZipEntry(ss[0]);
                outfile = new File(ss[1]);
                FileInputStream in = new FileInputStream(outfile);

                byte[] b = new byte[outfile.length() > buffer * 1024 ? buffer * 1024 : (int) outfile.length()];
                int len;
                zou.putNextEntry(ze);
                while ((len = in.read(b)) != -1) {
                    zou.write(b, 0, len);
                }
                zou.closeEntry();
                in.close();

            }

            zou.close();
        }

        System.out.println("PackZip:" + src.getAbsolutePath() + ",to:" + zf.getAbsolutePath() + "\n" + ",耗時："
                + (System.currentTimeMillis() - time) / 1000 + "秒");
    }

    protected static void ListFilesToPackZip(File src, LinkedList<String[]> fs, String subPath) {
        if (!src.isDirectory())
            return;

        for (File f : src.listFiles()) {
            if (f.isFile()) {
                fs.addFirst(
                        new String[]{(subPath.equals("") ? "" : subPath + "\\") + f.getName(), f.getAbsolutePath()});
            } else if (f.isDirectory()) {
                ListFilesToPackZip(f, fs, (subPath.equals("") ? "" : subPath + "\\") + f.getName());
            }
        }

    }

    /**
     * 移动一个文件
     *
     * @param src 来源文件
     * @param des 目标文件
     */
    public static Thread moveFile(File src, File des) {
        if (!src.isFile()) {
            System.out.println(src.getAbsoluteFile() + ",不是文件");
            return null;
        }
        Thread thread = new Thread(new Runnable() {
            private File src;
            private File des;

            public Runnable init(File src, File des) {
                this.src = src;
                this.des = des;
                return this;
            }

            @Override
            public void run() {
                long time = System.currentTimeMillis();
                String s = "Copy:" + src.getAbsolutePath() + ",to:" + des.getAbsolutePath() + "\n" + ",耗時：";
                System.out.println("開始......");
                des.getParentFile().mkdirs();
                // 创建线程池，核心corethreadcount个线程，线程最大数量为corethreadcount*1.5,等级队列corethreadcount个，如线程满且队列也满则执行等级策略CallerRunsPolicy
                ExecutorService pool = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS,
                        new LinkedBlockingDeque<Runnable>(1), new ThreadPoolExecutor.CallerRunsPolicy());
                moveFile(src, des, pool);
                pool.shutdown();
                // 等待，直到复制任务都执行完毕
                while (true) {
                    try {
                        if (pool.awaitTermination(100, TimeUnit.SECONDS))
                            break;
                    } catch (InterruptedException e) {

                        e.printStackTrace();
                    }
                }
                // 输出耗时
                // 输出耗时
                System.out.println(s + (System.currentTimeMillis() - time) / 1000 + "秒");

            }
        }.init(src, des));
        thread.start();
        return thread;

    }

    /**
     * 将移动文件任务添加到pool线程池
     *
     * @param src  来源文件
     * @param des  目标文件
     * @param pool 复制文件要添加到的线程池
     */
    public static void moveFile(final File src, final File des, final ExecutorService pool) {
        pool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // 创建目标文件所在目录
                    des.getParentFile().mkdirs();
                    if (!src.renameTo(des)) {// 如果用renameTo移动文件不成功则使用复制再删除的方案
                        Files.copy(Paths.get(src.getAbsolutePath()), Paths.get(des.getAbsolutePath()), StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
                        //FileOperator.copyFileByFileChanner(src, des);
                        src.delete();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });

    }

    /**
     * 移动一个目录. 使用默认10个线程且覆盖文件
     *
     * @param src 来源目录
     * @param des 目标目录
     */
    public static Thread moveDirectory(File src, File des) {
        return moveDirectory(src, des, 10);
    }

    /**
     * 移动一个目录..
     *
     * @param src             来源目录
     * @param des             目标目录
     * @param corethreadcount 线程数
     */
    public static Thread moveDirectory(final File src, final File des, final int corethreadcount) {
        if (!src.isDirectory()) {
            System.out.println(src.getAbsoluteFile() + ",不是文件夾");
            return null;
        }
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                long time = System.currentTimeMillis();
                String s = "move:" + src.getAbsolutePath() + ",to:" + des.getAbsolutePath() + "\n" + "使用線程數："
                        + corethreadcount + ",耗時：";
                System.out.println("開始......");
                if (!src.isDirectory() || !src.exists())
                    return;
                if (!des.isDirectory()) {
                    des.mkdirs();
                }
                // 创建线程池，核心corethreadcount个线程，线程最大数量为corethreadcount*1.5,等级队列corethreadcount个，如线程满且队列也满则执行等级策略CallerRunsPolicy
                final ThreadPoolExecutor pool = new ThreadPoolExecutor(corethreadcount, (int) (corethreadcount * 1.5), 0,
                        TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(corethreadcount),
                        new ThreadPoolExecutor.CallerRunsPolicy());

                // 遍历文件夹，将要复制的文件与目录文件放到Map中待复制
                final LinkedList<File[]> movelist = new LinkedList<File[]>();
                final LinkedList<File> directorylist = new LinkedList<File>();

                final Lock lock = new ReentrantLock();
                // 将遍历任务添加到线程池
                Future future_isoverflag = pool.submit(new Runnable() {

                    @Override
                    public void run() {
                        listAllFilesToMoveDictory(src, new File(des.getAbsolutePath() + "\\" + src.getName()), movelist,
                                directorylist, lock);

                    }
                });
                // 将要复制的文件一个一个创建执行任务
                while (true) {
                    File[] fs;
                    // System.out.println(flag.toString());
                    lock.lock();
                    try {
                        fs = movelist.pollFirst();
                    } finally {
                        lock.unlock();
                    }
                    if (fs != null) {
                        // 将复制文件任务添加到线程池
                        moveFile(fs[0], fs[1], pool);
                    } else if (future_isoverflag.isDone()) {
                        break;
                    }

                }
                // 关闭线程池
                pool.shutdown();

                // 等待，直到复制任务都执行完毕
                while (true) {
                    try {
                        if (pool.awaitTermination(100, TimeUnit.SECONDS))
                            break;
                    } catch (InterruptedException e) {

                        e.printStackTrace();
                    }
                }
                // 此时所有文件已被移动

                // 因为文件夹要从最里面往外删除，只有空文件夹才能删除成功。directorylist里面的文件夹按顺序排列
                // pool = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, new
                // LinkedBlockingDeque<Runnable>(30), new
                // ThreadPoolExecutor.CallerRunsPolicy());
                // 添加src目录
                directorylist.addLast(src);
                while (!directorylist.isEmpty()) {
                    directorylist.pollFirst().delete();
                }
                // 输出耗时
                System.out.println(s + (System.currentTimeMillis() - time) / 1000 + "秒");
            }
        });
        thread.start();
        return thread;
    }

    /**
     * 列出目录中所有文件用于移动目录
     *
     * @param src         来源目录
     * @param des         目标目录
     * @param movelist    移动文件对
     * @param dictorylist 来源目录中的对
     */
    protected static void listAllFilesToMoveDictory(File src, File des, LinkedList<File[]> movelist,
                                                    LinkedList<File> dictorylist, Lock lock) {
        File[] files;
        for (File f : src.listFiles()) {
            if (f.isFile()) {
                files = new File[2];
                files[0] = f;
                files[1] = new File(des.getAbsolutePath() + "\\" + f.getName());
                lock.lock();
                try {
                    movelist.addFirst(files);

                } finally {
                    lock.unlock();
                }

            } else if (f.isDirectory()) {
                dictorylist.addFirst(f);
                File af = new File(des.getAbsolutePath() + "\\" + f.getName());
                af.mkdirs();
                listAllFilesToMoveDictory(f, af, movelist, dictorylist, lock);
            }
        }
    }
}
