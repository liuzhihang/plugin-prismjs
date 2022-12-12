package run.halo.prismjs;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import reactor.core.publisher.Mono;
import run.halo.app.plugin.SettingFetcher;
import run.halo.app.theme.dialect.TemplateHeadProcessor;

/**
 * prismjs 插件
 *
 * @author liuzhihang
 * @date 2022/10/23
 */
@Component
public class PrismJSHeadProcessor implements TemplateHeadProcessor {

    private final SettingFetcher settingFetcher;

    public PrismJSHeadProcessor(SettingFetcher settingFetcher) {
        this.settingFetcher = settingFetcher;
    }

    @Override
    public Mono<Void> process(ITemplateContext context, IModel model, IElementModelStructureHandler structureHandler) {
        return settingFetcher.fetch("basic", BasicConfig.class)
                .map(config -> {
                    final IModelFactory modelFactory = context.getModelFactory();
                    model.add(modelFactory.createText(highlightJsScript(config)));
                    return Mono.empty();
                }).orElse(Mono.empty()).then();
    }

    private String highlightJsScript(BasicConfig config) {
        // language=html
        String script = "";

        if (config.getAutoSupport()) {
            // language=html
            script = script + """
                    <script src="/plugins/PluginPrismJS/assets/static/highlight.js/highlight.min.js"></script>
                
                    <script>
                    	document.addEventListener("DOMContentLoaded", function () {
                    		hljs.highlightAll()
                    	})
                    </script>
                    """;
        }

        // language=html
        script = script + """
                <link rel="stylesheet" href="/plugins/PluginPrismJS/assets/static/themes/%s"/>
                <!-- 工具栏 css -->
                <link rel="stylesheet" href="/plugins/PluginPrismJS/assets/static/plugins/toolbar/prism-toolbar.min.css"/>
                                
                <script src="/plugins/PluginPrismJS/assets/static/components/prism-core.min.js"></script>
                                
                <!-- autoloader -->
                <script src="/plugins/PluginPrismJS/assets/static/plugins/autoloader/prism-autoloader.min.js"></script>
                <!-- 控制 toolbar -->
                <script src="/plugins/PluginPrismJS/assets/static/plugins/toolbar/prism-toolbar.min.js"></script>
                                
                <!-- 显示语言 -->
                <script src="/plugins/PluginPrismJS/assets/static/plugins/show-language/prism-show-language.min.js"></script>
                            
                <!-- 复制到剪贴板 -->
                <script src="/plugins/PluginPrismJS/assets/static/plugins/copy-to-clipboard/prism-copy-to-clipboard.min.js"></script>
                """.formatted(config.getCss());

        if (config.getLineNumber()) {
            // language=html
            script = script + """
                    <!-- 行号 css -->
                    <link rel="stylesheet" href="/plugins/PluginPrismJS/assets/static/plugins/line-numbers/prism-line-numbers.min.css"/>
                                    
                    <script src="/plugins/PluginPrismJS/assets/static/plugins/line-numbers/prism-line-numbers.min.js"></script>
                    """;
        }

        if (StringUtils.isNotBlank(config.getCustomCss())) {
            // language=html
            script = script + """
                    <link rel="stylesheet" href="%s"/>
                    """.formatted(config.getCss());
        }

        return script;
    }

    @Data
    public static class BasicConfig {
        Boolean autoSupport;
        Boolean lineNumber;
        String customCss;
        String css;
    }
}
