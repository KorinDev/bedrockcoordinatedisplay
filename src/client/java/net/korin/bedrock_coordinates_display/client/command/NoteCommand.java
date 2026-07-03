package net.korin.bedrock_coordinates_display.client.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.korin.bedrock_coordinates_display.client.BedrockCoordinatesDisplayClient;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;

public class NoteCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(literal("note")
                    .then(argument("text", StringArgumentType.greedyString())
                            .executes(NoteCommand::executeSetNote)));
            dispatcher.register(literal("clearnote").executes(NoteCommand::executeClearNote));
        });
    }

    private static int executeSetNote(CommandContext<FabricClientCommandSource> context) {
        String noteText = StringArgumentType.getString(context, "text");

        if (noteText.startsWith("\"") && noteText.endsWith("\"")) {
            noteText = noteText.substring(1, noteText.length() - 1);
        }

        BedrockCoordinatesDisplayClient.CONFIG.noteText = noteText;
        BedrockCoordinatesDisplayClient.CONFIG.save();

        return 1;
    }

    private static int executeClearNote(CommandContext<FabricClientCommandSource> context) {
        BedrockCoordinatesDisplayClient.CONFIG.noteText = "";
        BedrockCoordinatesDisplayClient.CONFIG.save();
        return 1;
    }
}
