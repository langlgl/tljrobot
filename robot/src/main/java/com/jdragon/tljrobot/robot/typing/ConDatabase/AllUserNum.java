//package com.jdragon.tljrobot.robot.typing.ConDatabase;
//
//import cc.moecraft.icq.command.CommandProperties;
//import cc.moecraft.icq.command.interfaces.EverywhereCommand;
//import cc.moecraft.icq.event.events.message.EventMessage;
//import cc.moecraft.icq.user.User;
//
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.util.ArrayList;
//
//public class AllUserNum implements EverywhereCommand {
//
//    private String getAllUser(){
//        String s = "";
//        try {
//            String sql = "SELECT * FROM client ORDER BY num DESC";
//            Connection con = Conn.getConnection();
//            ResultSet rs = Conn.getStmtSet(con,sql);
//            while(rs.next()){
//                String name = fullStr(rs.getString(1));
//                String all = fullStr(String.valueOf(rs.getInt(3)));
//                String right = fullStr(String.valueOf(rs.getInt(4)));
//                String mis = String.valueOf(rs.getInt(5));
//                s += "“"+name+"” 总"+all+" 对"+right+" 错"+mis+"\n";
//            }
//            con.close();
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//        return s;
//    }
//    private String  fullStr(String str){
//        if(str.length()<10){
//            StringBuilder strBuilder = new StringBuilder(str);
//            for(int i = 0; i<10- strBuilder.length(); i++) {
//                strBuilder.append("—");
//            }
//            str = strBuilder.toString();
//        }
//        return str;
//    }
//    @Override
//    public String run(EventMessage eventMessage, User user, String s, ArrayList<String> arrayList) {
//        return getAllUser();
//    }
//    @Override
//    public CommandProperties properties() {
//        return new CommandProperties("allnum", "n", "用户字数");
//    }
//}
