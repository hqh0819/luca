import java.io.File;
import java.io.IOException;

/**
 * Created by luca on 2017/6/3.
 */
public class Test {
    public static void main(String[] args) throws IOException, InterruptedException {
        FileOperator.deleteDirectory(new File("E:\\Temp"));
        //FileOperator.copyDirectory(new File("d:\\IdeaProjects"),new File("e:\\"));
//       Path src= Paths.get("E:\\Videos\\灰姑娘中英字幕.mkv");
//       Path des= Paths.get("e:\\灰姑娘中英字幕.mkv");
//       Path des2= Paths.get("e:\\灰姑娘中英字幕2.mkv");
//       long time=System.currentTimeMillis();
//
//       Files.copy(src,des, StandardCopyOption.COPY_ATTRIBUTES,StandardCopyOption.REPLACE_EXISTING);
//
//       System.out.println("Files.copy:"+(System.currentTimeMillis()-time)/1000);
//       //time=System.currentTimeMillis();
////FileOperator.copyFile(new File(src.toString()),new File(des2.toString())).join();
//       //System.out.println("FileOperator.copyFile:"+(System.currentTimeMillis()-time)/1000);

    }
}
