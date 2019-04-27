package main.java;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.java.config.AdminConfig;
import main.java.config.TunnelsConfig;
import main.java.utils.MyUtils;

import static main.java.utils.Constants.*;


public class ConfigurationsManager {
    private Gson gson =
            new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
            //new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    private TunnelsConfig tunnelsConfig;
    private AdminConfig adminConfig;

    public ConfigurationsManager(){
        tunnelsConfig = (TunnelsConfig) MyUtils.loadConfigFromFile(TunnelsConfig.class,TUNNELS_CONFIG_PATH);
        adminConfig = (AdminConfig) MyUtils.loadConfigFromFile(AdminConfig.class,ADMIN_CONFIG_PATH);
    }

    public TunnelsConfig getTunnelsConfig() {
        return tunnelsConfig;
    }

    public AdminConfig getAdminConfig() {
        return adminConfig;
    }
}
