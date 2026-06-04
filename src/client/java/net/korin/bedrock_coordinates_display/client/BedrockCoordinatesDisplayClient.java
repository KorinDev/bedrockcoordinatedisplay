package net.korin.bedrock_coordinates_display.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.korin.bedrock_coordinates_display.client.config.BedrockCoordinatesDisplayConfig;
import net.korin.bedrock_coordinates_display.client.config.ConfigModel;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class BedrockCoordinatesDisplayClient implements ClientModInitializer {

    public static final String MOD_ID = "bedrock_coordinates_display";

    public static final BedrockCoordinatesDisplayConfig CONFIG = BedrockCoordinatesDisplayConfig.createAndLoad();

    public static int y_offset = 10;
    public static int x_offset = 10;

    public static boolean visibilityToggle = true;

	@Override
	public void onInitializeClient() {
        KeyMapping.Category CATEGORY = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath(BedrockCoordinatesDisplayClient.MOD_ID, "keybinds")
        );

        KeyMapping toggleVisibilityKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.bedrock_coordinates_display.toggle_visibility",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_BACKSPACE,
                CATEGORY
        ));

        HudElementRegistry.attachElementAfter(
                VanillaHudElements.HOTBAR,
                Identifier.fromNamespaceAndPath(BedrockCoordinatesDisplayClient.MOD_ID, "after_hotbar"), BedrockCoordinatesDisplayClient::extract
        );



        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleVisibilityKey.consumeClick()) {
                if (client.player != null) {
                    visibilityToggle = !visibilityToggle;
                }
            }
        });
	}

    private static void extract(GuiGraphicsExtractor graphics, DeltaTracker tickCounter) {
        if (!CONFIG.enabled()) return;
        if (!visibilityToggle) return;

        x_offset = CONFIG.offsetX();
        y_offset = CONFIG.offsetY();

        Minecraft client = Minecraft.getInstance();
        Font font = client.font;
        Player player = client.player;

        if (player == null) return;

        String coords = String.format("Position: %s, %s, %s", (int)client.player.getX(), (int)client.player.getY(), (int)client.player.getZ());

        graphics.nextStratum();

        int textWidth = font.width(coords);
        int textHeight = font.lineHeight - 1;
        int padding = CONFIG.padding();

        graphics.fill(
                x_offset - padding,
                y_offset - padding,
                x_offset + textWidth + padding,
                y_offset + textHeight + padding,
                ARGB.color(CONFIG.backgroundOpacity(), 0, 0, 0));


        graphics.text(font, Component.literal(coords), x_offset, y_offset, ARGB.color(255, 255, 255, 255), true);

        graphics.nextStratum();
    }
}