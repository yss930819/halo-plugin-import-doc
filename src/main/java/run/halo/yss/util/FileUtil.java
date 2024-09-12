package run.halo.yss.util;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {
    private static final String ROOT_PATH = "/.halo2/";

    public enum DirPath {
        IMPORT;
    }

    public static Path getAttachmentsPath(String path) {
        return getDocFile("attachments/upload/" + path);
    }

    public static Path getPluginTemp() {
        return getDocFile("plugins/import-doc");
    }

    /**
     * 获取一个文件路径，在 workspace下的
     *
     * @return
     */
    public static Path getDocFile(String dirPath) {
        String userHome = System.getProperty("user.home");
        Path path = Paths.get(userHome, ROOT_PATH).resolve(dirPath);
        if (!path.toFile().exists()) {
            path.toFile().mkdirs();
        }
        return path;
    }
}

