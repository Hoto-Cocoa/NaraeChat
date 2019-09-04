package kr.neko.sokcuri.naraechat;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.SystemUtils;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;

import static org.lwjgl.stb.STBTruetype.*;

public class FontManager {

    public static FontManager instance = new FontManager();
    public float fontSize;
    public float overSample;
    public float shiftX;
    public float shiftY;

    private static final String TAG = FontManager.class.getCanonicalName();
    private HashMap<String, String> systemFontMap = new HashMap<>();

    public FontManager() {
        preCacheSystemFontsMap();

        fontSize = ConfigHelper.getFontSize();
        overSample = ConfigHelper.getOversample();
        shiftX = ConfigHelper.getShiftX();
        shiftY = ConfigHelper.getShiftY();
    }

    public HashMap<String, String> getSystemFontMap() {
        return systemFontMap;
    }

    public String[] getSystemFontNames() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
    }

    public String[] getSystemFontsPaths() {
        String[] result;
        if (SystemUtils.IS_OS_WINDOWS) {
            result = new String[1];
            String path = System.getenv("WINDIR");
            result[0] = path + "\\" + "Fonts";
            return result;
        } else if (SystemUtils.IS_OS_MAC_OSX || SystemUtils.IS_OS_MAC) {
            result = new String[3];
            result[0] = System.getProperty("user.home") + File.separator + "Library/Fonts";
            result[1] = "/Library/Fonts";
            result[2] = "/System/Library/Fonts";
            return result;
        } else if (SystemUtils.IS_OS_LINUX) {
            String[] pathsToCheck = {
                    System.getProperty("user.home") + File.separator + ".fonts",
                    "/usr/share/fonts/truetype",
                    "/usr/share/fonts/TTF"
            };
            ArrayList<String> resultList = new ArrayList<>();

            for (int i = pathsToCheck.length - 1; i >= 0; i--) {
                String path = pathsToCheck[i];
                File tmp = new File(path);
                if (tmp.exists() && tmp.isDirectory() && tmp.canRead()) {
                    resultList.add(path);
                }
            }

            if (resultList.isEmpty()) {
                // TODO: show user warning, TextTool will be crash editor, because system font directories not found
                result = new String[0];
            }
            else {
                result = new String[resultList.size()];
                result = resultList.toArray(result);
            }

            return result;
        }

        return null;
    }

    public List<File> getSystemFontFiles() {
        // only retrieving ttf files
        String[] extensions = new String[]{"ttf", "TTF", "ttc", "TTC"};
        String[] paths = getSystemFontsPaths();

        ArrayList<File> files = new ArrayList<>();

        for (int i = 0; i < paths.length; i++) {
            File fontDirectory = new File(paths[i]);
            if (!fontDirectory.exists()) break;
            files.addAll(FileUtils.listFiles(fontDirectory, extensions, true));
        }

        return files;
    }

    public void preCacheSystemFontsMap() {
        List<File> fontFiles = getSystemFontFiles();

        for (File file : fontFiles) {
            Font f;
            System.out.println(file.getAbsolutePath());
            if (file.getAbsolutePath().endsWith("gulim.ttc")) {
                System.out.println("ends");
            }
            try {
                //if (!systemFontMap.containsValue(file.getAbsolutePath())) {
                    f = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(file.getAbsolutePath()));
                    String name = f.getFontName();

                    ByteBuffer buffer;
                    FileInputStream fis;

                    fis = new FileInputStream(file);
                    FileChannel fc = fis.getChannel();
                    buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
                    fc.close();
                    fis.close();

                    if (buffer == null) {
                        throw new IOException("font not loaded");
                    }

                    if (file.getAbsolutePath().endsWith(".ttc") || file.getAbsolutePath().endsWith(".TTC")) {
                        int number = STBTruetype.stbtt_GetNumberOfFonts(buffer);
                        for (int i = 0; i < number; i++) {

                            STBTTFontinfo info = STBTTFontinfo.create();
                            if (!STBTruetype.stbtt_InitFont(info, buffer, STBTruetype.stbtt_GetFontOffsetForIndex(buffer, i)))
                                throw new IOException("Invalid ttf");
//
//                            ByteBuffer bf = STBTruetype.stbtt_GetFontNameString(info, STBTT_PLATFORM_ID_UNICODE, STBTT_UNICODE_EID_UNICODE_2_0_FULL, 0, 2);
//                            if (bf != null) {
//                                String s = StandardCharsets.UTF_16.decode(bf).toString();
//                                System.out.println(s);
//                            }

                            if (!systemFontMap.containsKey(name))
                                systemFontMap.put(name, file.getAbsolutePath());

                            break;
                        }
                        continue;
                    }

                    STBTTFontinfo info = STBTTFontinfo.create();
                    if (!STBTruetype.stbtt_InitFont(info, buffer))
                        throw new IOException("Invalid ttf");

                    systemFontMap.put(name, file.getAbsolutePath());
                // }
            } catch (FontFormatException e) {
                //e.printStackTrace();
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }

        // prefs.put(systemFontMap);
        // prefs.flush();
    }

//    public void loadCachedSystemFontMap() {
//        systemFontMap = (HashMap<String, String>) prefs.get();
//    }

//    public void invalidateFontMap() {
//        Array<String> names = new Array<>(getSystemFontNames());
//        for (Iterator<Map.Entry<String, String>> it = systemFontMap.entrySet().iterator(); it.hasNext(); ) {
//            Map.Entry<String, String> entry = it.next();
//            if (!names.contains(entry.getKey(), false)) {
//                it.remove();
//            }
//        }
//    }

//    public void generateFontsMap() {
//        loadCachedSystemFontMap();
//        preCacheSystemFontsMap();
//        invalidateFontMap();
//    }

    public HashMap<String, String> getFontsMap() {
        return systemFontMap;
    }

//    public Array<String> getFontNamesFromMap() {
//        AlphabeticalComparator comparator = new AlphabeticalComparator();
//        Array<String> fontNames = new Array<>();
//
//        for (Map.Entry<String, String> entry : systemFontMap.entrySet()) {
//            fontNames.add(entry.getKey());
//        }
//        fontNames.sort(comparator);
//
//        return fontNames;
//    }

//    public FileHandle getTTFByName(String fontName) {
//        return new FileHandle(systemFontMap.get(fontName));
//    }

    public String getShortName(String longName) {
        String path = systemFontMap.get(longName);
        return FilenameUtils.getBaseName(path);
    }

    public String getFontFilePath(String fontFaily) {
        return systemFontMap.get(fontFaily);
    }


    public class AlphabeticalComparator implements Comparator<String> {

        @Override
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    }
}