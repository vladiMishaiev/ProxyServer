package main.java.utils;

public final class Constants {
    public static String CONFIG_DIR_PATH = "./src/main/resources/config";
    public static String TUNNELS_CONFIG_PATH = CONFIG_DIR_PATH + "/tunnelsConfig.json";
    public static String ADMIN_CONFIG_PATH = CONFIG_DIR_PATH + "/adminConfig.json";
    public static String SECURITY_CONFIG_PATH = CONFIG_DIR_PATH +  "/securityConfig.json";
    public static String STATE_INFO_PATH = CONFIG_DIR_PATH +  "/stateInfo.json";

    public static String CREATE_ADMIN_EVENT_URI = "/api/events/admin/newEvent";
    public static String CREATE_SECURITY_EVENT_URI = "/api/events/security/newEvent";

    private Constants(){}

}
