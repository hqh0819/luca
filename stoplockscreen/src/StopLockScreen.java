import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;

/**
 * Created by luca on 2017/5/6.
 */
public class StopLockScreen implements Runnable {

    private int sleeptime;
    private MenuItem itmChangeSleeptime;

    public StopLockScreen(int sleeptime) {
        this.sleeptime = sleeptime;

    }

    public static void main(String args[]) {
        int sleeptime;
        if (args.length > 0 && args[0].matches("\\d*")) {
            sleeptime = Integer.parseInt(args[0]);
        } else {
            sleeptime = 60;
        }
        StopLockScreen s = new StopLockScreen(sleeptime);
        if (s.isAppActive()) {
            //JOptionPane.showMessageDialog(null, "程序已运行，请勿重复运行！");
            System.exit(0);
        } else {
            new Thread(s).start();
        }
    }

    //添加托盘显示：1.先判断当前平台是否支持托盘显示
    public void setTray() {

        if (SystemTray.isSupported()) {//判断当前平台是否支持托盘功能
            //创建托盘实例
            SystemTray tray = SystemTray.getSystemTray();
            //创建托盘图标：1.显示图标Image 2.停留提示text 3.弹出菜单popupMenu 4.创建托盘图标实例
            //1.创建Image图像
            Image image = Toolkit.getDefaultToolkit().getImage(StopLockScreen.class.getResource("stop.png"));

            //3.弹出菜单popupMenu
            PopupMenu popMenu = new PopupMenu();
            itmChangeSleeptime = new MenuItem("ChangeSleepTime:<" + sleeptime + ">s");
            itmChangeSleeptime.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ChangeSleepTime();
                }
            });
            popMenu.add(itmChangeSleeptime);
            MenuItem itmExit = new MenuItem("Exit");
            itmExit.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Exit();
                }
            });
            popMenu.add(itmExit);

            //创建托盘图标
            TrayIcon trayIcon = new TrayIcon(image, "阻止鎖屏", popMenu);

            trayIcon.setImageAutoSize(true);
            //将托盘图标加到托盘上
            try {
                tray.add(trayIcon);

            } catch (AWTException e1) {
                e1.printStackTrace();
            }
        }
    }

    //修改SleepTime時間
    public void ChangeSleepTime() {

        String s = JOptionPane.showInputDialog("請輸入新的時間時隔");
        if (s != null && s.matches("\\d*")) {
            sleeptime = Integer.parseInt(s);
            JOptionPane.showMessageDialog(null, "修改時間間隔為：<" + sleeptime + ">秒");
            itmChangeSleeptime.setLabel("ChangeSleepTime:<" + sleeptime + ">s");
            synchronized (this) {
                this.notifyAll();
            }

        } else {
            JOptionPane.showMessageDialog(null, "請輸入數字！");
        }
        //System.out.println(sleeptime);
    }

    //内部类中不方便直接调用外部类的实例（this不能指向）
    public void Exit() {
        System.exit(0);
    }

    /*
    检查程序是否已经运行
    每运行一次都会尝试去锁定临时文件夹中的文件《!StopLockScreen.lock》，只能第一次运行能锁定成功，后面运行都会失败，锁定失败时程序退出
     */
    public boolean isAppActive() {
        File f = new File(System.getProperty("java.io.tmpdir") + "\\!StopLockScreen.lock");
        f.deleteOnExit();
        FileLock lock;
        try {
            lock = new RandomAccessFile(f, "rw").getChannel().tryLock();

        } catch (IOException e) {
            return true;
        }
        return lock == null;
    }

    private void beforeRun() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Thread t1 = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JOptionPane.showMessageDialog(null, "程序將每隔<" + sleeptime + ">秒激活一次鼠標防止鎖屏,可在系統托盤修改時間間隔\n請勿退出後臺javaw.exe進程!");
                    }
                });
                t1.start();
                try {
                    Thread.sleep(1000 * 10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    t1.interrupt();
                    //t1=null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        this.setTray();
    }

    @Override
    public void run() {
        beforeRun();
        Robot r = null;
        Point p;
        try {
            r = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }

        while (true) {
            System.gc();
            p = MouseInfo.getPointerInfo().getLocation();
            r.mouseMove(p.x, p.y);
            try {
                synchronized (this) {
                    this.wait(1000 * sleeptime);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}


