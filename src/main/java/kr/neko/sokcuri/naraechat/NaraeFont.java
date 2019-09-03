package kr.neko.sokcuri.naraechat;

import kr.neko.sokcuri.naraechat.Obfuscated.ObfuscatedField;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.fonts.Font;
import net.minecraft.client.gui.fonts.FontResourceManager;
import net.minecraft.client.gui.fonts.providers.DefaultGlyphProvider;
import net.minecraft.client.gui.fonts.providers.IGlyphProvider;
import net.minecraft.client.gui.fonts.providers.TrueTypeGlyphProvider;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.TickEvent;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.CallbackI;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NaraeFont {

    public class FontData {
        String fileName;
        ByteBuffer buffer;
        float size;
        float overSample;
        float shiftX;
        float shiftY;
        String chars;
        // TrueTypeGlyphProvider glyphProvider;
    }

    private static Map<String, FontData> fontDataMap = new HashMap();
    private static Map<String, ByteBuffer> fontBufferMap = new HashMap();

    public boolean setFontData(String fontName, String fileName, float size, float overSample, float shiftX, float shiftY, String chars) {
        FontData fontData = new FontData();
        ByteBuffer buffer = null;
        File file = new File(fileName);
        if (!file.isFile())
            return false;

        FileInputStream fis = null;
        fontData.fileName = fileName;
        try {
            fis = new FileInputStream(file);
            FileChannel fc = fis.getChannel();
            buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            fc.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (buffer == null) {
            System.out.println("font not loaded");
            return false;
        }

        fontData.buffer = buffer;
        fontData.size = size;
        fontData.overSample = overSample;
        fontData.shiftX = shiftX;
        fontData.shiftY = shiftY;
        fontData.chars = chars;

        if (fontDataMap.containsKey(fontName)) {
            fontDataMap.replace(fontName, fontData);
        } else {
            fontDataMap.put(fontName, fontData);
        }
        return true;
    }

    public void resetFontDataMap() {
        fontDataMap.clear();
    }

    public void setGlyphProvider(String fontName) {
        FontData fontData = fontDataMap.get(fontName);
        if (fontData == null) return;


        STBTTFontinfo info = STBTTFontinfo.create();
        if (!STBTruetype.stbtt_InitFont(info, fontData.buffer)) {
            try {
                throw new IOException("Invalid ttf");
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        TrueTypeGlyphProvider glyphProvider = new TrueTypeGlyphProvider(info, fontData.size, fontData.overSample, fontData.shiftX, fontData.shiftY, fontData.chars);


        Minecraft mc = Minecraft.getInstance();
        FontResourceManager fontResourceManager = mc.getFontResourceManager();
        FontRenderer fontRenderer = mc.fontRenderer;

        Map<ResourceLocation, FontRenderer> fontRenderers = ObfuscatedField.$FontResourceManager.fontRenderers.get(fontResourceManager);
        TextureManager textureManager = ObfuscatedField.$FontResourceManager.textureManager.get(fontResourceManager);

        ResourceLocation naraeResLoc = new ResourceLocation("narae");
        Map<ResourceLocation, List<IGlyphProvider>> splashList = new HashMap();

        List<IGlyphProvider> providers = new ArrayList();
        providers.add(0, glyphProvider);

        Set<IGlyphProvider> providersSet = ObfuscatedField.$FontResourceManager.glyphProviders.get(fontResourceManager);
        List<IGlyphProvider> mcProviders = providersSet.stream().distinct().collect(Collectors.toList());
        mcProviders.forEach(x -> {
            // System.out.println(x.getClass().getName());
            if (x.getClass().getName() == "net.minecraft.client.gui.fonts.providers.UnicodeTextureGlyphProvider") {
                providers.add(x);
            }
        });

        // providers.add(new DefaultGlyphProvider());
        // providers.add(glyphProvider);
        // splashList.put(naraeResLoc, providers);

        FontRenderer newRenderer = new FontRenderer(textureManager, new Font(textureManager, naraeResLoc));
        newRenderer.setGlyphProviders(providers);
        if (fontRenderers.containsKey(naraeResLoc)) {
            fontRenderers.replace(naraeResLoc, newRenderer);
        } else {
            fontRenderers.put(naraeResLoc, newRenderer);
        }

//        Stream.concat(fontRenderers.keySet().stream(), splashList.keySet().stream()).distinct().forEach((p_211508_2_) -> {
//            List<IGlyphProvider> list1 = splashList.getOrDefault(p_211508_2_, Collections.emptyList());
//            Collections.reverse(list1);
//
//            fontRenderers.computeIfAbsent(p_211508_2_, (p_211505_1_) -> {
//                FontRenderer newRenderer = new FontRenderer(textureManager, new Font(textureManager, p_211505_1_));
//                newRenderer.setGlyphProviders(list1);
//                return newRenderer;
//            });
//
//            FontRenderer newRenderer = new FontRenderer(textureManager, new Font(textureManager, naraeResLoc));
//            newRenderer.setGlyphProviders(list1);
//            fontRenderers.replace(naraeResLoc, newRenderer);
//        });

        // Collection<List<IGlyphProvider>> collection = splashList.values();
//        Set<IGlyphProvider> set = ObfuscatedField.$FontResourceManager.glyphProviders.get(fontResourceManager);
//        set.forEach(x -> {
//
//        });
//        collection.forEach(set::addAll);

        mc.fontRenderer = mc.getFontResourceManager().getFontRenderer(new ResourceLocation("narae"));
        // mc.fontRenderer.setGlyphProviders(providers);

    }

    public void renderTick(TickEvent.RenderTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        FontResourceManager fontResourceManager = mc.getFontResourceManager();
        ResourceLocation resLoc = new ResourceLocation("narae");

        String naraeFontName = "맑은 고딕";
        String naraeFontFileName = "malgun.ttf";
        float size = 12.0f;
        float overSample = 4.0f;
        float shiftX = -0.5f;
        float shiftY = 0.0f;
        String chars = "";

        if (!fontDataMap.containsKey(naraeFontName)) {
            setFontData(naraeFontName, naraeFontFileName, size, overSample, shiftX, shiftY, chars);
            setGlyphProvider(naraeFontName);
        }

        // 현재 인게임 font Renderer가 narae font renderer로 사용중일때 처리
        if (mc.fontRenderer == mc.getFontResourceManager().getFontRenderer(resLoc)) {
            Font font = ObfuscatedField.$FontRenderer.font.get(mc.fontRenderer);

            List<IGlyphProvider> glyphProviders = ObfuscatedField.$Font.glyphProviders.get(font);
            if (glyphProviders.size() <= 1) {
                setGlyphProvider(naraeFontName);
            }
        }
    }

    public void changeFont() {
        Minecraft mc = Minecraft.getInstance();
        FontResourceManager fontResourceManager = mc.getFontResourceManager();

//        ByteBuffer buffer = null;
//        String filename = "malgun.ttf";
//        if (!fontBufferMap.containsKey(filename)) {
//            File file = new File(filename);
//            if (file.isFile()) {
//                FileInputStream fis = null;
//                try {
//                    fis = new FileInputStream(file);
//                    FileChannel fc = fis.getChannel();
//                    buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
//                    fontBufferMap.put(filename, buffer);
//                    fc.close();
//                    fis.close();
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                    return;
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    return;
//                }
//            }
//        }
//
//
//        buffer = fontBufferMap.get(filename);
        FontData fontData = fontDataMap.get("맑은 고딕");
        ByteBuffer buffer = fontData.buffer;
        TrueTypeGlyphProvider truetypeglyphprovider = null;

        STBTTFontinfo stbttfontinfo = STBTTFontinfo.create();
        if (!STBTruetype.stbtt_InitFont(stbttfontinfo, buffer)) {
            try {
                throw new IOException("Invalid ttf");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            truetypeglyphprovider = new TrueTypeGlyphProvider(stbttfontinfo, fontData.size, fontData.overSample, fontData.shiftX, fontData.shiftY, fontData.chars);
        }

        Map<ResourceLocation, FontRenderer> fontRenderers = ObfuscatedField.$FontResourceManager.fontRenderers.get(fontResourceManager);
        TextureManager textureManager = ObfuscatedField.$FontResourceManager.textureManager.get(fontResourceManager);
        ResourceLocation resourceLocation = new ResourceLocation("narae");
        List<IGlyphProvider> providers = new ArrayList<IGlyphProvider>();
        providers.add(truetypeglyphprovider);
        Map<ResourceLocation, List<IGlyphProvider>> splashList = new HashMap<>();
        splashList.put(resourceLocation, providers);

        Stream.concat(fontRenderers.keySet().stream(), splashList.keySet().stream()).distinct().forEach((p_211508_2_) -> {
            List<IGlyphProvider> list1 = splashList.getOrDefault(p_211508_2_, Collections.emptyList());
            Collections.reverse(list1);

            fontRenderers.computeIfAbsent(p_211508_2_, (p_211505_1_) -> {
                FontRenderer newRenderer = new FontRenderer(textureManager, new Font(textureManager, p_211505_1_));
                newRenderer.setGlyphProviders(list1);
                return newRenderer;
            });

            FontRenderer newRenderer = new FontRenderer(textureManager, new Font(textureManager, resourceLocation));
            newRenderer.setGlyphProviders(list1);
            fontRenderers.replace(resourceLocation, newRenderer);
        });

        Collection<List<IGlyphProvider>> collection = splashList.values();
        Set set = ObfuscatedField.$FontResourceManager.glyphProviders.get(fontResourceManager);
        collection.forEach(set::addAll);

        mc.fontRenderer = mc.getFontResourceManager().getFontRenderer(new ResourceLocation("narae"));
        mc.fontRenderer.setGlyphProviders(providers);

        // fontResourceManager.func_216883_a(true, Util.getServerExecutor(), mc);
    }

}
