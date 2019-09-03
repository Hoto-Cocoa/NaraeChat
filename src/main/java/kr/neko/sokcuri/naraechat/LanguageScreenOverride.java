package kr.neko.sokcuri.naraechat;

import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.screen.LanguageScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.settings.AbstractOption;

public class LanguageScreenOverride extends LanguageScreen {
    private final Screen parentScreen;
    private final GameSettings game_settings_3;
    private final LanguageManager languageManager;

    public LanguageScreenOverride(Screen screen, GameSettings gameSettingsObj, LanguageManager manager) {
        super(screen, gameSettingsObj, manager);

        this.parentScreen = screen;
        this.game_settings_3 = gameSettingsObj;
        this.languageManager = manager;
    }

    @Override
    protected void init() {
        this.addButton(new Button(this.width / 2 - 155, this.height - 38, 150, 20, I18n.format("naraechat.font"), (p_213037_1_) -> {

            // AbstractOption.FORCE_UNICODE_FONT.func_216740_a(this.game_settings_3);
            this.game_settings_3.saveOptions();
            System.out.println("Force Unicode");
            // p_213037_1_.setMessage(AbstractOption.FORCE_UNICODE_FONT.func_216743_c(this.game_settings_3));
            this.minecraft.updateWindowSize();
        }));
        this.children.forEach(x -> {
            if (x.getClass().getName() == "net.minecraft.client.gui.widget.button.OptionButton") {
            }
        });
        super.init();
        System.out.println("LanguageScreenOverride #1");
    }

    @Override
    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        // this.list.render(p_render_1_, p_render_2_, p_render_3_);
        // this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 16, 16777215);
        // this.drawCenteredString(this.font, "(" + I18n.format("options.languageWarning") + ")", this.width / 2, this.height - 56, 8421504);
        super.render(p_render_1_, p_render_2_, p_render_3_);
    }
}
