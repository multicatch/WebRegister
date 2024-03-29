package pl.cezaryregec;

import com.google.inject.name.Names;
import com.google.inject.servlet.ServletModule;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import pl.cezaryregec.auth.HashGenerator;
import pl.cezaryregec.auth.ShaHashGenerator;
import pl.cezaryregec.config.DefaultConfig;
import pl.cezaryregec.config.PropertiesConfig;


/**
 *
 * @author SudoWaster <cezaryre@gmail.com>
 */
public class APIServletModule extends ServletModule {
    
    @Override
    public void configureServlets() {
        tryLoadingConfig();
        
        bind(HashGenerator.class).to(ShaHashGenerator.class);
    }
    
    private void tryLoadingConfig() {
        try {
            loadConfig();
            bind(Config.class).to(PropertiesConfig.class);
            
        } catch (IOException|NullPointerException ex) {
            bind(Config.class).to(DefaultConfig.class);
        } 
    }
    
    private void loadConfig() throws IOException {
        InputStream input = getServletContext().getResourceAsStream("/WEB-INF/wr.properties");
        Properties properties = new Properties();
        properties.load(input);
        
        Names.bindProperties(binder(), properties);
    }
}
