package com.jdragon.tljrobot.robot.newTyping.config;

/**
 * Create by Jdragon on 2020.01.21
 */
public class HttpAddr {
    private final static String SERVER_ADDR = "http://localhost:8081";
//    private final static String SERVER_ADDR = "https://tlj.wiki";

    private final static String ROBOT_HOME = "/robot";

    private final static String TLJ_MATCH_ADDR = "/tljMatch";

    private final static String UNION_ADDR = "/unionMatch";

    private final static String QUERY_ADDR = "/query";

    private final static String CHAT_ADDR = "/chat";

    public final static String UPLOAD_HISTORY = SERVER_ADDR + ROBOT_HOME + "/uploadHistory";

    public final static String GROUP_MATCH_ADDR = "/groupMatch";

    public final static String UPLOAD_UNION_MATCH = SERVER_ADDR + ROBOT_HOME + UNION_ADDR + "/uploadUnionMatch";

    public final static String GET_UNION_MATCH = SERVER_ADDR + ROBOT_HOME + UNION_ADDR + "/getUnionMatch";

    public final static String GET_UNION_FIRST_MATCH_RANK = SERVER_ADDR + ROBOT_HOME + UNION_ADDR + "/getUnionFirstAchRank";

    public final static String GET_UNION_MATCH_RANK = SERVER_ADDR + ROBOT_HOME + UNION_ADDR + "/getUnionAchRank";

    public final static String GET_TLJ_MATCH_RANK_ALL = SERVER_ADDR + ROBOT_HOME + TLJ_MATCH_ADDR + "/getTljMatchAchByDate";

    public final static String UPLOAD_GROUP_MATCH_ADDR = SERVER_ADDR + ROBOT_HOME +GROUP_MATCH_ADDR + "/uploadGroupMatch";

    public final static String GET_GROUP_MATCH_ADDR = SERVER_ADDR + ROBOT_HOME +GROUP_MATCH_ADDR + "/getGroupMatch";

    public final static String QUERY_GROUP_AVR_TYPE_INFO = SERVER_ADDR + ROBOT_HOME + QUERY_ADDR + "/groupTypeInfo";

    public final static String QUERY_TLJ_AVR_TYPE_INFO = SERVER_ADDR + ROBOT_HOME + QUERY_ADDR + "/tljTypeInfo";

    public final static String QUERY_TLJ_ALL_TYPE_INFO = SERVER_ADDR + ROBOT_HOME + QUERY_ADDR + "/tljAllTypeInfo";

    public final static String QUERY_GROUP_MAP = SERVER_ADDR + ROBOT_HOME + QUERY_ADDR + "/getGroupMap";

    public final static String CHAT_ADD_NUM_ADDR = SERVER_ADDR + ROBOT_HOME + CHAT_ADDR + "/addNum";
}