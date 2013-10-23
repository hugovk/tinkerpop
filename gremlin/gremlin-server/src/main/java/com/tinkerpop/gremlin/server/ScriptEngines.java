package com.tinkerpop.gremlin.server;

import com.tinkerpop.gremlin.groovy.jsr223.DefaultImportCustomizerProvider;
import com.tinkerpop.gremlin.groovy.jsr223.GremlinGroovyScriptEngine;
import com.tinkerpop.gremlin.groovy.jsr223.GremlinGroovyScriptEngineFactory;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Holds a batch of the configured ScriptEngines for the server.
 *
 * @author Stephen Mallette (http://stephen.genoprime.com)
 */
public class ScriptEngines {
    /**
     * ScriptEngines configured for the server keyed on the language name.
     */
    private Map<String,ScriptEngine> scriptEngines = new ConcurrentHashMap<>();

    private static final GremlinGroovyScriptEngineFactory gremlinGroovyScriptEngineFactory = new GremlinGroovyScriptEngineFactory();

    /**
     * Evaluate a script with Bindings for a particular language.
     */
    public Object eval(final String script, final Bindings bindings, final String language) throws ScriptException {
        if (!scriptEngines.containsKey(language))
            throw new IllegalArgumentException("Language [%s] not configured on this server.");

        return scriptEngines.get(language).eval(script, bindings);
    }

    /**
     * Reload a ScriptEngine with fresh imports.
     */
    public void reload(final String language, final Set<String> imports, final Set<String> staticImports) {
        // TODO: request a block on eval when reloading a language script engine.

        final Optional<ScriptEngine> scriptEngine;
        if (scriptEngines.containsKey(language))
            scriptEngines.remove(language);

        scriptEngine = createScriptEngine(language, imports, staticImports);
        scriptEngines.put(language,
                scriptEngine.orElseThrow(() -> new IllegalArgumentException("Language [%s] not supported.")));
    }

    private static Optional<ScriptEngine> createScriptEngine(final String language, final Set<String> imports,
                                                             final Set<String> staticImports) {
        if (language.equals(gremlinGroovyScriptEngineFactory.getLanguageName())) {
            // gremlin-groovy gets special initialization for custom imports and such.  not sure how to implement
            // this generically for other ScriptEngine implementations.
            return Optional.of((ScriptEngine) new GremlinGroovyScriptEngine(1500,
                    new DefaultImportCustomizerProvider(imports, staticImports)));
        } else {
            final ScriptEngineManager manager = new ScriptEngineManager();
            return Optional.ofNullable(manager.getEngineByName(language));
        }
    }

}
