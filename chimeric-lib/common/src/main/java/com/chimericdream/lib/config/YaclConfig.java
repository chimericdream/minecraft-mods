package com.chimericdream.lib.config;

import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.platform.YACLPlatform;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.Identifier;

import java.util.function.Consumer;

/**
 * Wraps the {@link ConfigClassHandler}/YACL boilerplate that every mod in this repo used to
 * hand-write (a {@code HANDLER} field, a {@code load()} static, and a {@code configScreen(Screen)}
 * static) into a single reusable object: {@code builder(...).screen(...).build()}, then one
 * {@link #init()} call from the mod's common {@code init()}.
 *
 * <h2>Why screen registration is deferred</h2>
 * <p>{@link #init()} only loads the config file and — if a screen builder was supplied — adds this
 * {@code YaclConfig} to the {@link YaclConfigScreens} registry. It does <em>not</em> hand a config
 * screen factory to Mod Menu or NeoForge itself. That happens later, once per platform, at a point
 * guaranteed to run after every mod's constructor/{@code ModInitializer}:
 * <ul>
 *   <li>On Fabric, chimeric-lib's own {@code modmenu} entrypoint
 *   ({@code com.chimericdream.lib.fabric.config.YaclModMenuIntegration}) implements
 *   {@code ModMenuApi#getProvidedConfigScreenFactories()}, which Mod Menu calls lazily whenever its
 *   own screen is opened — well after every mod's {@code main} entrypoint has run.</li>
 *   <li>On NeoForge, chimeric-lib's own {@code FMLClientSetupEvent} handler
 *   ({@code com.chimericdream.lib.neoforge.config.YaclConfigScreensNeoForge}) registers an
 *   {@code IConfigScreenFactory} extension point on each consumer mod's {@code ModContainer}.
 *   {@code FMLClientSetupEvent} fires only after every mod's constructor has completed.</li>
 * </ul>
 * <p>This sidesteps two problems a mod trying to register its own screen eagerly would hit:
 * <ol>
 *   <li>Fabric does <em>not</em> order {@code main} entrypoints by a mod's {@code depends} list, so a
 *   provider object set up in chimeric-lib's own Fabric entrypoint could still be {@code null} when an
 *   alphabetically-earlier mod (e.g. {@code athenaeum}) runs its {@code init()} — there is no ordering
 *   guarantee to rely on.</li>
 *   <li>It avoids {@code @ExpectPlatform} entirely — see the javadoc on
 *   {@code com.chimericdream.lib.commands.PlatformCommandArgumentTypes} for why chimeric-lib does not
 *   use it anywhere (in short: NeoForge's dev run resolves common and the neoforge source set as
 *   separate JPMS modules, which breaks the generated {@code Impl} class lookup and fails FML
 *   startup).</li>
 * </ol>
 * <p><b>The registry ({@link YaclConfigScreens}) stores {@code YaclConfig} objects themselves, never a
 * {@code Function<Screen, Screen>} or a bare method reference.</b> A method reference like
 * {@code FooConfig::screen} captured from common init code is a {@code Screen}-typed functional
 * interface instance; creating it forces the JVM to resolve the (client-only) {@link Screen} class as
 * part of the {@code invokedynamic} call site's method type, which would throw on a Fabric dedicated
 * server where the client classes are absent. Only the two platform classes above — both client-only,
 * both only ever loaded on a client — are allowed to build a {@code Screen}-typed lambda (from
 * {@link #screen(Screen)}).
 *
 * @param <T> the mod's config data class (a plain object with {@code @SerialEntry} fields, exactly
 *            like every hand-written config class this replaces)
 */
public final class YaclConfig<T> {
    private final String modId;
    private final ConfigClassHandler<T> handler;
    private final YetAnotherConfigLib.ConfigBackedBuilder<T> screenBuilder;
    private final Consumer<T> onLoad;

    private YaclConfig(
        String modId,
        ConfigClassHandler<T> handler,
        YetAnotherConfigLib.ConfigBackedBuilder<T> screenBuilder,
        Consumer<T> onLoad
    ) {
        this.modId = modId;
        this.handler = handler;
        this.screenBuilder = screenBuilder;
        this.onLoad = onLoad;
    }

    /**
     * Starts building a {@code YaclConfig} for {@code configClass}, serialized under {@code modId}'s
     * own config identity ({@code <modId>:config}) and, by default, a {@code <modId>.json5} file in
     * the platform config directory.
     */
    public static <T> Builder<T> builder(Class<T> configClass, String modId) {
        return new Builder<>(configClass, modId);
    }

    /**
     * The mod id this config was built for. Used as the registry key in {@link YaclConfigScreens}, and
     * as the target mod id when NeoForge registers the {@code IConfigScreenFactory} extension point.
     */
    public String modId() {
        return modId;
    }

    /**
     * The underlying YACL handler, for callers that need direct access (e.g. tests, or advanced
     * consumers that want to call YACL APIs this wrapper doesn't expose).
     */
    public ConfigClassHandler<T> handler() {
        return handler;
    }

    /** The live, mutable config instance — equivalent to {@code handler().instance()}. */
    public T instance() {
        return handler.instance();
    }

    /** The config class's default values — equivalent to {@code handler().defaults()}. */
    public T defaults() {
        return handler.defaults();
    }

    /** True if {@link Builder#screen(YetAnotherConfigLib.ConfigBackedBuilder)} was called. */
    public boolean hasScreen() {
        return screenBuilder != null;
    }

    /**
     * Loads the config file, then — if a screen builder was supplied — registers this config into
     * {@link YaclConfigScreens} so the Fabric/NeoForge platform glue can hand it to Mod Menu/NeoForge's
     * config-screen system later (see the class javadoc for why that step is deferred). Call this once
     * from the mod's common {@code init()}.
     *
     * <p><b>Idempotency:</b> {@link YaclConfigScreens#register(YaclConfig)} is keyed by {@link #modId()}
     * and replaces any existing entry for that id, so calling {@code init()} again (for the same mod
     * id) does not add a duplicate entry — it just re-loads and re-registers this same instance.
     */
    public void init() {
        load();

        if (hasScreen()) {
            YaclConfigScreens.register(this);
        }
    }

    /**
     * Loads the config file into {@link #instance()} (creating it with defaults if it doesn't exist
     * yet), then runs the {@link Builder#onLoad(Consumer)} hook, if one was supplied, with the freshly
     * loaded instance. Returns whatever {@link ConfigClassHandler#load()} returned.
     */
    public boolean load() {
        boolean ok = handler.load();

        if (onLoad != null) {
            onLoad.accept(instance());
        }

        return ok;
    }

    /** Saves {@link #instance()} back to the config file — equivalent to {@code handler().save()}. */
    public void save() {
        handler.save();
    }

    /**
     * Builds and returns a YACL config screen for this config. Client-only: constructing a
     * {@link Screen} requires the client classes, so only ever call this from client-only code (this
     * is exactly what the Fabric/NeoForge platform glue described in the class javadoc does).
     *
     * @throws IllegalStateException if no screen builder was supplied via
     *                                {@link Builder#screen(YetAnotherConfigLib.ConfigBackedBuilder)}
     */
    public Screen screen(Screen parent) {
        if (!hasScreen()) {
            throw new IllegalStateException(
                "YaclConfig for mod '" + modId + "' has no screen builder; call Builder#screen(...) "
                    + "before build() if this config needs a config screen."
            );
        }

        return YetAnotherConfigLib.create(handler, screenBuilder).generateScreen(parent);
    }

    public static final class Builder<T> {
        private final Class<T> configClass;
        private final String modId;
        private String fileName;
        private YetAnotherConfigLib.ConfigBackedBuilder<T> screenBuilder;
        private Consumer<T> onLoad;

        private Builder(Class<T> configClass, String modId) {
            this.configClass = configClass;
            this.modId = modId;
            this.fileName = modId + ".json5";
        }

        /** Overrides the default config file name of {@code <modId>.json5}. */
        public Builder<T> fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        /**
         * Supplies the YACL screen definition. Omitting this makes {@link #hasScreen()} false and
         * {@link #screen(Screen)} throw — the config exists but no config screen is registered on
         * either platform.
         */
        public Builder<T> screen(YetAnotherConfigLib.ConfigBackedBuilder<T> screenBuilder) {
            this.screenBuilder = screenBuilder;
            return this;
        }

        /**
         * A post-load validation/clamping hook, run with {@link #instance()} every time {@link #load()}
         * runs (including via {@link #init()}). Mirrors the {@code validatePostLoad()} pattern used by
         * hand-written configs like Athenaeum's.
         */
        public Builder<T> onLoad(Consumer<T> onLoad) {
            this.onLoad = onLoad;
            return this;
        }

        /** Builds the {@link ConfigClassHandler} exactly like every hand-written config did, and wraps it. */
        public YaclConfig<T> build() {
            ConfigClassHandler<T> handler = ConfigClassHandler.createBuilder(configClass)
                .id(Identifier.fromNamespaceAndPath(modId, "config"))
                .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(YACLPlatform.getConfigDir().resolve(fileName))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting)
                    .setJson5(true)
                    .build())
                .build();

            return new YaclConfig<>(modId, handler, screenBuilder, onLoad);
        }
    }
}
