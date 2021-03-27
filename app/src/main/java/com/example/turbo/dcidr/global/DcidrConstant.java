package com.example.turbo.dcidr.global;

/**
 * Created by Turbo on 2/20/2016.
 */
public class DcidrConstant {
    public static final String EVENT_PACKAGE_PATH = "com.example.turbo.dcidr.main.event.";
    public static final String EVENT_VIEW_HELPER_PACKAGE_PATH = "com.example.turbo.dcidr.main.event_view_helper.";
    public static final String EVENT_TYPE_PACKAGE_PATH = "com.example.turbo.dcidr.main.event_type.";
    public static final String ANDROID_ACTIVITY_PACKAGE_PATH = "com.example.turbo.dcidr.android_activity.";

    // dcidr server url
    public static final String DCIDR_SERVER_URL = "https://192.168.226.100";
    //public static final String DCIDR_SERVER_URL = "https://192.168.226.100:4443"; //for server_kjoshi.js

    //public static final String DCIDR_SERVER_URL = "https://192.168.155.102";
    //public static final String DCIDR_SERVER_URL = "https://192.168.155.102"; //for server_kjoshi.js

    public static final String BASE_URL = "/dcidr/v1";

    // yelp api keys
    public static final String YELP_CONSUMER_KEY = "";
    public static final String YELP_CONSUMER_SECRET = "";
    public static final String YELP_TOKEN = "";
    public static final String YELP_TOKEN_SECRET = "";


    // users api urls
    public static final String USERS_POST_URL = "/users";
    public static final String USER_GET_URL = "/users/:userId";
    public static final String USER_PUT_URL = "/users/:userId";
    public static final String USERS_LOGIN_POST_URL = "/users/login";

    // user device api urls
    public static final String USER_DEVICES_POST_URL = "/users/:userId/devices";
    public static final String USER_DEVICES_PUT_URL = "/users/:userId/devices";
    // group api urls
    public static final String USER_GROUPS_POST_URL = "/users/:userId/groups";
    public static final String USER_GROUPS_GET_URL = "/users/:userId/groups";
    public static final String USER_GROUP_GET_URL = "/users/:userId/groups/:groupId";
    public static final String USER_GROUP_PUT_URL = "/users/:userId/groups/:groupId";
    public static final String USER_GROUP_GET_MEMBERS_URL = "/users/:userId/groups/:groupId/members";

    // event api urls
    public static final String USER_EVENT_TYPE_GET_URL = "/users/:userId/eventTypes";
    public static final String USER_GROUP_UNREAD_EVENTS_GET_URL = "/users/:userId/groups/:groupId/unreadEvents";
    public static final String USER_GROUP_UNREAD_EVENTS_POST_URL = "/users/:userId/groups/:groupId/unreadEvents";
    public static final String USER_GROUP_EVENTS_POST_URL = "/users/:userId/groups/:groupId/events";
    public static final String USER_GROUP_EVENTS_GET_URL = "/users/:userId/groups/:groupId/events";
    public static final String USER_GROUP_EVENT_GET_URL = "/users/:userId/groups/:groupId/events/:eventId";
    public static final String USER_GROUP_EVENT_PUT_URL = "/users/:userId/groups/:groupId/events/:eventId";
    public static final String USER_EVENT_STATUS_GET_URL = "/users/:userId/groups/:groupId/events/:eventId/userEventStatus";
    public static final String USER_EVENT_STATUS_PUT_URL = "/users/:userId/groups/:groupId/events/:eventId/userEventStatus";
    public static final String USER_EVENT_TYPES_GET_URL = "/users/:userId/eventTypes";

    // Chweet api urls
    public static final String USER_EVENT_CHWEET_POST_URL = "/users/:userId/groups/:groupId/events/:parentEventId/submitChweet";
    public static final String USER_EVENT_CHWEET_GET_URL = "/users/:userId/groups/:groupId/events/:parentEventId/getChweet";


    // Buzz api urls
    public static final String USER_EVENT_BUZZ_URL = "/users/:userId/groups/:groupId/events/:eventId/buzz";

    // history api urls
    public static final String USER_HISTORY_GET_URL = "/users/:userId/history";

    // friend api urls
    public static final String USER_FRIENDS_POST_URL = "/users/:userId/friends";
    public static final String USER_FRIENDS_GET_URL = "/users/:userId/friends";
    public static final String USER_FRIEND_GET_URL = "/users/:userId/friends/:emailId";
    public static final String USER_FRIEND_POST_URL = "/users/:userId/friends/:emailId";

    //public static final String USER_CONTACTS_GET_URL = "/users/:userId/contacts";
    //public static final String USER_CONTACTS_POST_URL = "/users/:userId/contacts/:emailId";


    // media api url
    public static final String GROUPS_MEDIA_IMAGE_POST_URL = "/media/upload/users/:userId/groups/:groupId/image";
    public static final String GROUPS_MEDIA_IMAGE_GET_URL = "/media/download/users/:userId/groups/:groupId/image";
    public static final String GROUPS_MEDIA_GET_BY_URL = "/media/download/users/:userId/url";
    public static final String USER_MEDIA_IMAGE_POST_URL = "/media/upload/users/:userId/image";
    public static final String USER_MEDIA_GET_BY_URL = "/media/download/users/:userId/url";



}