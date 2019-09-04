package kr.neko.sokcuri.naraechat;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public final class ConfigHelper {
    public static ModConfig clientConfig;

    public static void bakeClient(final ModConfig config) {
        clientConfig = config;
    }

    public static String getFontFamily() {
        return clientConfig.getConfigData().getOrElse("general.naraechat.fontfamily", "맑은 고딕");
    }

    public static void setFontFamily(final String value) {
        setValueAndSave(clientConfig, "general.naraechat.fontfamily", value);
    }

    public static float getFontSize() {
        return clientConfig.getConfigData().getOrElse("general.naraechat.fontsize", 10.0f);
    }

    public static void setFontSize(final float value) {
        setValueAndSave(clientConfig, "general.naraechat.fontsize", value);
    }

    public static float getOversample() {
        return clientConfig.getConfigData().getOrElse("general.naraechat.oversample", 4.0f);
    }

    public static void setOversample(final float value) {
        setValueAndSave(clientConfig, "general.naraechat.oversample", value);
    }

    public static float getShiftX() {
        return clientConfig.getConfigData().getOrElse("general.naraechat.shiftx", 0.0f);
    }

    public static void setShiftX(final float value) {
        setValueAndSave(clientConfig, "general.naraechat.shiftx", value);
    }

    public static float getShiftY() {
        return clientConfig.getConfigData().getOrElse("general.naraechat.shifty", 0.0f);
    }

    public static void setShiftY(final float value) {
        setValueAndSave(clientConfig, "general.naraechat.shifty", value);
    }

    public static void setValueAndSave(final ModConfig modConfig, final String path, final Object newValue) {
        modConfig.getConfigData().set(path, newValue);
        modConfig.save();
    }
}
