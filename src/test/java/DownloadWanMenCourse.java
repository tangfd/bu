import com.tfd.base.utils.HttpUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 下载万门课程
 * 2633
 *
 * @author TangFD
 * @since 2019/2/16.
 */
public class DownloadWanMenCourse {
    private static final String target = "F:/学习视频/万门人工智能课程/";
    private static final String urlSuffix = "http://api.wanmen.org/4.0/content/lectures/";
    private static final String tsSuffix = "http://media.wanmen.org/";
    private static final Executor executor = Executors.newFixedThreadPool(2);
    private static int count = 1;

    public static void main2(String[] args) throws IOException {
        del(new File(target));
    }

    private static void del(File file) throws IOException {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (ArrayUtils.isEmpty(files)) {
                System.out.println(file.getAbsolutePath());
                FileUtils.deleteDirectory(file);
                return;
            }

            for (File file1 : files) {
                del(file1);
            }
            return;
        }

        if (file.getName().endsWith(".mp4")) {
            return;
        }

        System.out.println(file.getAbsolutePath());
        FileUtils.deleteQuietly(file);
    }


    public static void main(String[] args) throws Exception {
        Map<String, Map<String, List<String>>> map = analysis();
        download(map);
    }

    public static void download(Map<String, Map<String, List<String>>> map) throws Exception {
        CountDownLatch latch = new CountDownLatch(getSize(map));
        Map<String, Map<String, List<String>>> errors = new HashMap<>();
        for (String course : map.keySet()) {
            System.out.println("开始下载课程：" + course);
            Map<String, List<String>> listMap = map.get(course);
            for (String chapter : listMap.keySet()) {
                executor.execute(() -> {
                    try {
                        String filePath = target + course + File.separator;
                        if (StringUtils.isNotBlank(chapter) && !"null".equals(chapter)) {
                            filePath += chapter + File.separator;
                        }

                        int index = 1;
                        List<String> lectures = listMap.get(chapter);
                        for (String lecture : lectures) {
                            Course object;
                            String m3u8 = filePath + lecture;
                            String error = m3u8 + ",下载失败";
                            try {
                                object = HttpUtils.doGetAsObject(urlSuffix + lecture, Course.class);
                                if (object == null) {
                                    System.out.println(error);
                                    addError(errors, course, chapter, lecture);
                                    continue;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                System.out.println(error);
                                addError(errors, course, chapter, lecture);
                                continue;
                            }

                            String lecturesName = index++ + "." + object.getName();
                            String mp4File = filePath + lecturesName + ".mp4";
                            if (new File(mp4File).exists()) {
                                synchronized (Object.class) {
                                    System.out.println(count++ + "-- " + mp4File + " 已经下载完成");
                                    continue;
                                }
                            }

                            String lecturesPath = filePath + lecturesName + File.separator;
                            File file = new File(lecturesPath);
                            if (!file.exists()) {
                                boolean mkdirs = file.mkdirs();
                                if (!mkdirs) {
                                    file.mkdirs();
                                }
                            }

                            synchronized (Object.class) {
                                System.out.println(count++ + "-- 开始下载章节：" + lecturesPath);
                            }

                            InputStream inputStream;
                            try {
                                inputStream = HttpUtils.doGetAsStream(object.getPcHigh());
                                if (inputStream == null) {
                                    System.out.println(error);
                                    addError(errors, course, chapter, lecture);
                                    continue;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                System.out.println(error);
                                addError(errors, course, chapter, lecture);
                                continue;
                            }

                            String tsListFile = lecturesPath + "tsListFile.txt";
                            BufferedReader reader = null;
                            BufferedWriter writer = null;
                            try {
                                reader = new BufferedReader(new InputStreamReader(inputStream));
                                writer = new BufferedWriter(new FileWriter(tsListFile));
                                String content;
                                while ((content = reader.readLine()) != null) {
                                    if (StringUtils.isBlank(content)) {
                                        continue;
                                    }

                                    content = content.trim();
                                    if (!content.endsWith(".ts")) {
                                        continue;
                                    }

                                    writer.write("file " + content);
                                    writer.newLine();
                                    InputStream ts = null;
                                    OutputStream outputStream = null;
                                    try {
                                        ts = HttpUtils.doGetAsStream(tsSuffix + content);
                                        if (ts == null) {
                                            System.out.println(error);
                                            addError(errors, course, chapter, lecture);
                                            return;
                                        }

                                        outputStream = new FileOutputStream(lecturesPath + content);
                                        byte[] tsContent = new byte[1024 * 4];
                                        int read;
                                        while ((read = ts.read(tsContent)) != -1) {
                                            outputStream.write(tsContent, 0, read);
                                        }

                                        outputStream.flush();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        System.out.println(error);
                                        addError(errors, course, chapter, lecture);
                                        return;
                                    } finally {
                                        IOUtils.closeQuietly(ts);
                                        IOUtils.closeQuietly(outputStream);
                                    }
                                }
                                writer.flush();
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                IOUtils.closeQuietly(inputStream);
                                IOUtils.closeQuietly(reader);
                                IOUtils.closeQuietly(writer);
                            }

                            System.out.println("分片视频下载完成，开始合并mp4文件");
                            new Thread(() -> {
                                try {
                                    String cmd = "E:/ffmpeg/bin/ffmpeg -f concat -i "
                                            + tsListFile.replace("\\", "/").replace(File.separator, "/")
                                            + " -c copy -bsf:a aac_adtstoasc "
                                            + mp4File;
                                    Runtime.getRuntime().exec(cmd);
                                    System.out.println(mp4File + " 文件，合并完成");
                                } catch (Exception e) {
                                    System.out.println(mp4File + " 文件，合并失败");
                                }
                            }).start();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        latch.countDown();
                    }
                });
            }
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (errors.size() > 0) {
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            download(errors);
            return;
        }

        System.out.println("------------end-----------");
    }

    private static int getSize(Map<String, Map<String, List<String>>> map) {
        int count = 0;
        for (Map<String, List<String>> listMap : map.values()) {
            for (List<String> strings : listMap.values()) {
                count += strings.size();
            }
        }
        return count;
    }

    private static void addError(Map<String, Map<String, List<String>>> map,
                                 String course, String chapter, String lecture) {
        Map<String, List<String>> stringListMap = map.get(course);
        if (stringListMap == null) {
            stringListMap = new HashMap<>();
            map.put(course, stringListMap);
        }

        List<String> strings = stringListMap.get(chapter);
        if (strings == null) {
            strings = new ArrayList<>();
            stringListMap.put(chapter, strings);
        }

        strings.add(lecture);
    }

    private static Map<String, Map<String, List<String>>> analysis() throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader("E:\\mygit_work\\bu\\src\\test\\java\\courseId.txt"));
        String content, courseName = null, chapterName = null;
        Map<String, Boolean> courseIdMap = new HashMap<>();
        Map<String, Map<String, List<String>>> courseAndChapterAndLecturesMap = new HashMap<>();
        while ((content = reader.readLine()) != null) {
            if (StringUtils.isBlank(content)) {
                continue;
            }

            content = content.trim();
            if (content.startsWith("course")) {
                courseName = content.replace("course", "");
                continue;
            }

            if (content.startsWith("第")) {
                chapterName = content;
                continue;
            }

            if (BooleanUtils.isTrue(courseIdMap.get(content))) {
                continue;
            }

            Map<String, List<String>> chapterAndLecturesMap = courseAndChapterAndLecturesMap.get(courseName);
            if (chapterAndLecturesMap == null) {
                chapterAndLecturesMap = new HashMap<>();
                courseAndChapterAndLecturesMap.put(courseName, chapterAndLecturesMap);
            }

            List<String> lectures = chapterAndLecturesMap.get(chapterName);
            if (lectures == null) {
                lectures = new ArrayList<>();
                chapterAndLecturesMap.put(chapterName, lectures);
            }

            lectures.add(content);
            courseIdMap.put(content, true);
        }

        return courseAndChapterAndLecturesMap;
    }

    class Course {
        private String name;
        private Video video;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Video getVideo() {
            return video;
        }

        public void setVideo(Video video) {
            this.video = video;
        }

        public String getMobileLow() {
            return this.video.getHls().getMobileLow();
        }

        public String getPcLow() {
            return this.video.getHls().getPcLow();
        }

        public String getPcHigh() {
            return this.video.getHls().getPcHigh();
        }

        public String getPcMid() {
            return this.video.getHls().getPcMid();
        }

        public String getMobileMid() {
            return this.video.getHls().getMobileMid();
        }
    }

    class Video {
        private Hls hls;

        public Hls getHls() {
            return hls;
        }

        public void setHls(Hls hls) {
            this.hls = hls;
        }
    }

    class Hls {
        private String mobileLow;
        private String pcLow;
        private String pcHigh;
        private String pcMid;
        private String mobileMid;

        public String getMobileLow() {
            return mobileLow;
        }

        public void setMobileLow(String mobileLow) {
            this.mobileLow = mobileLow;
        }

        public String getPcLow() {
            return pcLow;
        }

        public void setPcLow(String pcLow) {
            this.pcLow = pcLow;
        }

        public String getPcHigh() {
            return pcHigh;
        }

        public void setPcHigh(String pcHigh) {
            this.pcHigh = pcHigh;
        }

        public String getPcMid() {
            return pcMid;
        }

        public void setPcMid(String pcMid) {
            this.pcMid = pcMid;
        }

        public String getMobileMid() {
            return mobileMid;
        }

        public void setMobileMid(String mobileMid) {
            this.mobileMid = mobileMid;
        }
    }
}
