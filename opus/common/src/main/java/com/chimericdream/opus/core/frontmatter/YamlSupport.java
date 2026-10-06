package com.chimericdream.opus.core.frontmatter;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.error.MarkedYAMLException;
import org.yaml.snakeyaml.error.YAMLException;

import java.util.LinkedHashMap;
import java.util.Map;

/** Thin, safe wrapper around SnakeYAML so the rest of the mod never touches it directly. */
public final class YamlSupport {
    private YamlSupport() {
    }

    public static final class YamlException extends Exception {
    private static final long serialVersionUID = 1L;

        private final int line;

        public YamlException(String message, int line) {
            super(message);
            this.line = line;
        }

        /** 1-based line within the YAML text, or 0 when unknown. */
        public int line() {
            return line;
        }
    }

    /** Parses arbitrary YAML into maps/lists/scalars. Only plain data types are ever constructed. */
    public static Object load(String yaml) throws YamlException {
        LoaderOptions options = new LoaderOptions();
        options.setAllowDuplicateKeys(false);
        options.setMaxAliasesForCollections(20);
        options.setCodePointLimit(1_000_000);

        try {
            return new Yaml(new SafeConstructor(options)).load(yaml);
        } catch (MarkedYAMLException e) {
            int line = e.getProblemMark() == null ? 0 : e.getProblemMark().getLine() + 1;
            String problem = e.getProblem() == null ? e.getMessage() : e.getProblem();
            throw new YamlException(problem, line);
        } catch (YAMLException e) {
            throw new YamlException(e.getMessage(), 0);
        }
    }

    /** Parses YAML that must be a mapping (or empty). Keys are converted to strings. */
    public static Map<String, Object> loadMap(String yaml) throws YamlException {
        Object loaded = load(yaml);
        if (loaded == null) {
            return new LinkedHashMap<>();
        }
        if (loaded instanceof Map<?, ?> map) {
            Map<String, Object> out = new LinkedHashMap<>();
            map.forEach((k, v) -> out.put(String.valueOf(k), v));
            return out;
        }

        throw new YamlException("expected a mapping of keys to values", 0);
    }
}
