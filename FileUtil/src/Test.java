import java.io.File;

/**
 * Created by luca on 2017/5/20.
 */
public class Test {
    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        long time = System.currentTimeMillis();

        File src = new File("E:\\qycache");
        File des = new File("d:\\b\\c\\qycache");
        FileOperator.moveDirectory(src, des);

        System.out.println((System.currentTimeMillis() - time) / 1000);
    }


}
