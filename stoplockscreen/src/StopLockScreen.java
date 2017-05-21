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
public class StopLockScreen extends JFrame implements Runnable {

    private int sleeptime;

    public StopLockScreen(int sleeptime) {
        this.sleeptime = sleeptime;
        this.setTray();
    }

    public static void main(String args[]) {
        int sleeptime;
        if (args.length > 0 && args[0].matches("\\d*")) {
            sleeptime = Integer.parseInt(args[0]);
        } else {
            sleeptime = 30;
        }

        StopLockScreen s = new StopLockScreen(sleeptime);
        if (s.isAppActive()) {
            //JOptionPane.showMessageDialog(null, "程序已运行，请勿重复运行！");
            System.exit(0);
        } else {
            new Thread(s).start();
        }
    }

    //内部类中不方便直接调用外部类的实例（this不能指向）

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

    public void Exit() {
        System.exit(0);
    }

    /*
    检查程序是否已经运行
    每运行一次都会尝试去锁定临时文件夹中的文件《!StopLockScreen.lock》，只能第一次运行能锁定成功，后面运行都会失败，锁定失败时程序退出
     */
    public boolean isAppActive() {
        FileLock lock;
        try {
            lock = new RandomAccessFile(new File(System.getProperty("java.io.tmpdir") + "\\!StopLockScreen.lock"), "rw").getChannel().tryLock();
        } catch (IOException e) {
            return true;
        }
        return lock == null;
    }

    /*
    检查程序是否已经运行
    每运行一次都会尝试去锁定临时文件夹中的文件《!StopLockScreen.lock》，只能第一次运行能锁定成功，后面运行都会失败，锁定失败时程序退出
     */
    public boolean isAppActive() {
        FileLock lock;
        try {
            lock = new RandomAccessFile(new File(System.getProperty("java.io.tmpdir") + "\\!StopLockScreen.lock"), "rw").getChannel().tryLock();
        } catch (IOException e) {
            return true;
        }
        return lock == null;
    }

    @Override
    public void run() {

        new ShowMessageFrame("程序將每隔<" + sleeptime + ">秒激活一次鼠標防止鎖屏\n程序已隱藏至系統托盤，請勿退出後臺javaw.exe進程!", 15);

        Robot r = null;
        Point p;

        try {
            r = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }

        while (true) {

            p = MouseInfo.getPointerInfo().getLocation();
            r.mouseMove(p.x, p.y);
//System.out.println("\n"+System.currentTimeMillis());
            try {
                Thread.sleep(1000 * sleeptime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}


class ShowMessageFrame extends javax.swing.JFrame {
    private JLabel text;
    private Toolkit tk = Toolkit.getDefaultToolkit();
    private Dimension screensize = tk.getScreenSize();
    private int height = screensize.height;
    private int width = screensize.width;
    private String str = null;
    private int visibletime;

    public ShowMessageFrame(String str, int visibletime) {
        this.str = str;
        this.visibletime = visibletime;
        new Thread(new Runnable() {
            @Override
            public void run() {
                initGUI();
            }
        }).start();
    }

    private void initGUI() {
        try {
            setUndecorated(true);
            //setLocationRelativeTo(null);
            setVisible(true);
            //setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
//            {
            text = new JLabel("<html>" + str + "</html>", JLabel.CENTER);
            getContentPane().add(text, BorderLayout.CENTER);
//                text.setBackground(new java.awt.Color(255, 251, 240));
//            }
            pack();
            setBounds(width / 2 - 180, height / 2 - 50, 360, 100);
            try {
                Thread.sleep(visibletime * 1000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}