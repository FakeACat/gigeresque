package mods.cybercat.gigeresque.common;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mods.cybercat.gigeresque.CommonMod;

public class Log {
    private enum Level { INFO, WARNING, ERROR }
    private static final Logger CONSOLE_LOGGER = LogManager.getLogger(CommonMod.MOD_ID);

    private static void log(Level level, String message, Object... args) {
        if (!CommonMod.config.generalConfigs.enableLogging) return;

        var formatted = String.format(message, args);
        var player = Minecraft.getInstance().player;

        if (player != null) {
            var prefix = switch (level) {
                case INFO -> "INFO: ";
                case WARNING -> "WARNING: ";
                case ERROR -> "ERROR: ";
            };
            player.displayClientMessage(Component.literal(prefix + formatted), false);
        } else switch (level) {
            case INFO -> CONSOLE_LOGGER.info(formatted);
            case WARNING -> CONSOLE_LOGGER.warn(formatted);
            case ERROR -> CONSOLE_LOGGER.error(formatted);
        }
    }

    public static void info(String message, Object... args) {
        log(Level.INFO, message, args);
    }

    public static void warn(String message, Object... args) {
        log(Level.WARNING, message, args);
    }

    public static void err(String message, Object... args) {
        log(Level.ERROR, message, args);
    }
}
