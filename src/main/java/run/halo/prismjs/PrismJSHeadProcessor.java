package run.halo.prismjs;

import lombok.Data;
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
                    model.add(modelFactory.createText(highlightJsScript(config.getCss(), config.getCustomCss(), config.getLineNumber())));
                    return Mono.empty();
                }).orElse(Mono.empty()).then();
    }

    private String highlightJsScript(String css, String customCss, Boolean lineNumber) {
        return """
                <!-- PluginPrismJS start -->
                <link rel="stylesheet" href="/plugins/PluginPrismJS/assets/static/themes/%s"/>
                <!-- 工具栏 css -->
                <link rel="stylesheet" th:href="/plugins/PluginPrismJS/assets/static/plugins/toolbar/prism-toolbar.min.css"/>
                                
                <script src="/plugins/PluginPrismJS/assets/static/prism.js"></script>
                                
                <!-- 控制 toolbar -->
                <script src="/plugins/PluginPrismJS/assets/static/plugins/toolbar/prism-toolbar.min.js"></script>
                                
                <!-- 显示语言 -->
                <script src="/plugins/PluginPrismJS/assets/static/plugins/show-language/prism-show-language.min.js"></script>
                            
                <!-- 复制到剪贴板 -->
                <script src="/plugins/PluginPrismJS/assets/static/plugins/copy-to-clipboard/prism-copy-to-clipboard.min.js"></script>
                               
                <script>
                    document.addEventListener("DOMContentLoaded", async function () {
                   
                        var customCss = %s;
                                
                        if (customCss !== null || customCss !== undefined || customCss !== '') {
                          await loadCss(`customCss`);
                          s
                          console.log("CustomCss: ", customCss);
                        }
                    
                         if (%s) {
                            
                            await loadCss(`/plugins/PluginPrismJS/assets/static/plugins/line-numbers/prism-line-numbers.min.css`);
                            await loadScript(`/plugins/PluginPrismJS/assets/static/plugins/line-numbers/prism-line-numbers.min.js`);
                            
                            console.log("ShowLineNumber");
                         }
                    
                    });
                    
                    function loadScript(url) {
                      return new Promise(function (resolve, reject) {
                        const script = document.createElement("script");
                        script.type = "text/javascript";
                        script.src = url;
                        script.onload = resolve;
                        script.onerror = reject;
                        document.head.appendChild(script);
                      });
                    }
                    
                    function loadCss(url) {
                      return new Promise(function (resolve, reject) {
                        const css = document.createElement("link");
                        css.rel = 'stylesheet';
                        css.href = url;
                        css.onload = resolve;
                        css.onerror = reject;
                        document.head.appendChild(css);
                      });
                    }
                </script>
                                
                <!-- PluginPrismJS end -->
                """.formatted(css, customCss, lineNumber);
    }

    @Data
    public static class BasicConfig {
        Boolean lineNumber;
        String customCss;
        String css;
    }
}
