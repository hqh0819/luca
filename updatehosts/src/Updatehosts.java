import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by luca on 2017/5/13.
 */
public class Updatehosts {
    File hosts_zip_file = new File(System.getProperty("java.io.tmpdir") + "\\hosts.zip");
    File hosts_file = new File(System.getProperty("java.io.tmpdir") + "\\hosts");
    File system_hosts = new File("C:\\Windows\\System32\\drivers\\etc\\hosts");
    URL url = new URL("https://github.com/racaljk/hosts/archive/master.zip");


    public Updatehosts() throws MalformedURLException {
    }

    public static void main(String[] args) throws MalformedURLException {
        Updatehosts u = new Updatehosts();
        u.Download_hosts();
        u.upzip_hosts();
        u.copy_hosts();
    }

    boolean upzip_hosts() {
        if (hosts_zip_file == null) return false;

        try {
            ZipFile zf = new ZipFile(hosts_zip_file);
            Enumeration<ZipEntry> e = (Enumeration<ZipEntry>) zf.entries();
            while (e.hasMoreElements()) {
                ZipEntry ze = e.nextElement();
                if (ze.getName().equals("hosts-master/hosts")) {
                    InputStream in = zf.getInputStream(ze);

                    OutputStream ou = new BufferedOutputStream(new FileOutputStream(hosts_file));
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = in.read(b)) != -1) ou.write(b, 0, len);
                    ou.flush();
                    in.close();
                    ou.close();
                    break;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    boolean Download_hosts() {
        try {
            InputStream in = url.openStream();
            OutputStream ou = new BufferedOutputStream(new FileOutputStream(hosts_zip_file));

            int len;
            byte[] b = new byte[1024 * 10];
            while ((len = in.read(b)) != -1) {
                ou.write(b, 0, len);
            }
            ou.flush();
            in.close();
            ou.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    void copy_hosts() {
        if (!hosts_file.isFile()) return;
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(hosts_file));
            OutputStream ou = new BufferedOutputStream(new FileOutputStream(system_hosts));
            byte[] b = new byte[1024 * 10];
            int len;
            while ((len = in.read(b)) != -1) ou.write(b, 0, len);
            ou.flush();
            in.close();
            ou.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
