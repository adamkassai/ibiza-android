package com.kassaiweb.ibiza;

public class Constant {

    // For Shared Preferences:
    /**
     * the name for the SharedPreferences file
     */
    public static final String SP_NAME = "nyaralas2018";
    /**
     * String: displayed name after facebook, ... login
     */
    public static final String ACCOUNT_NAME = "account_name";
    /**
     * String: displayed image URL after facebook, ... login
     */
    public static final String ACCOUNT_IMAGE_URL = "account_image_url";
    /**
     * String: facebook, ... account's unique id
     */
    public final static String ACCOUNT_ID = "account_id";
    /**
     * String: fb_user, ... 's id of the current user in firebase
     */
    public final static String CURRENT_USER_ID = "current_user_id";
    /**
     * String: ID of the firebase group, which we are currently in. <p> null if we are not in any
     */
    public final static String CURRENT_GROUP_ID = "current_group_id";
    /**
     * String: name of the firebase group, which we are currently in. <p> null if we are not in any
     */
    public final static String CURRENT_GROUP_NAME = "current_group_name";

    public static final String USER_ID = "userId"; // String
    public static final String VERSION = "version"; // int

    public static final String CHOICE_SINGLE = "single";
    public static final String CHOICE_MULTIPLE = "multiple";

    public static final String TASK_ASSIGNED = "assigned";
    public static final String TASK_VOLUNTEERS = "volunteers";
    public static final String TASK_RANDOM = "random";

    public static final String GOOGLE_PLACES_API_KEY = "AIzaSyDUCMcDzJt7jRg_jfVJdmszlMaUcYrBTFI";
}
