package com.jdragon.tljrobot.robot.typing;

import cc.moecraft.icq.event.EventHandler;
import cc.moecraft.icq.event.IcqListener;
import cc.moecraft.icq.event.events.message.EventGroupMessage;
import cc.moecraft.icq.event.events.message.EventMessage;
import cc.moecraft.icq.event.events.message.EventPrivateMessage;
import cc.moecraft.icq.event.events.notice.groupmember.EventNoticeGroupMemberChange;
import cc.moecraft.icq.event.events.request.EventGroupInviteRequest;
import cc.moecraft.icq.sender.IcqHttpApi;
import cc.moecraft.icq.sender.message.MessageBuilder;
import cc.moecraft.icq.sender.message.components.ComponentAt;
import cc.moecraft.icq.sender.message.components.ComponentImage;
import com.jdragon.tljrobot.robot.club.robot;
import com.jdragon.tljrobot.robot.newTyping.tools.GroupCache;
import com.jdragon.tljrobot.robot.typing.ConDatabase.ComArti;
import com.jdragon.tljrobot.robot.typing.ConDatabase.Conn;
import com.jdragon.tljrobot.robot.typing.ConDatabase.InConn;
import com.jdragon.tljrobot.robot.typing.ConDatabase.OutConn;
import com.jdragon.tljrobot.robot.typing.GroupFollowTeamWar.GroupFollowTeamThread;
import com.jdragon.tljrobot.robot.typing.GroupFollowWar.GroupFollowThread;
import com.jdragon.tljrobot.robot.typing.GroupWar.GroupThread;
import com.jdragon.tljrobot.robot.typing.Tools.Createimg;
import com.jdragon.tljrobot.robot.typing.Tools.RegexText;
import com.jdragon.tljrobot.robot.typing.Tools.SortMap;
import com.jdragon.tljrobot.robot.typing.Tools.initGroupList;
import com.jdragon.tljrobot.tljutils.compShortCode.BetterTyping;
import com.jdragon.tljrobot.tljutils.downLoad.DownloadMsg;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.io.File.separator;

public class RobotGroupClient extends IcqListener {
    boolean respondSign;
    boolean init = false;
    boolean noticesign = false;
    public static boolean automati_inclusion_sign;//收集赛文成绩标记
    public static HashMap<Long,Boolean> grouplist;//判断群是否已收


    HashMap<String, BetterTyping> betterTypingHashMap = new HashMap<>();
    public RobotGroupClient(){
        //定时收集赛文程序
        grouplist = OutConn.getGroupList();
        automati_inclusion_sign = false;
        Automatic_Inclusion automatic_inclusion = new Automatic_Inclusion();
        automatic_inclusion.start();

//        loadCodeFile();
        betterTypingHashMap.put("词组提示码表",new BetterTyping("编码文件"+separator+"输入法编码"+separator+"词组提示码表.txt"));
    }
    @EventHandler void groupAdd(EventGroupInviteRequest eventGroupInviteRequest){
        eventGroupInviteRequest.accept();
        eventGroupInviteRequest.getBot().getAccountManager().refreshCache();
        initGroupList.init(eventGroupInviteRequest.getHttpApi());
    }
    @EventHandler void peopleAdd(EventNoticeGroupMemberChange event){
        event.getBot().getAccountManager().refreshCache();
        initGroupList.init(event.getHttpApi());
    }
    @EventHandler
    public void CarryBoth(EventMessage event) throws IOException {
        String message = event.getMessage();
        if(message.length()>1&&message.substring(0,1).equals("？")){
            BetterTyping betterTyping = betterTypingHashMap.get("词组提示码表");
            betterTyping.changecolortip(message.substring(1));
            betterTyping.compalllength();
            event.respond("标点顶点屏理论码长："+ RegexText.FourOutFiveIn(betterTyping.getDingKeylength())+" 总键数"+betterTyping.getDingalllength()
                    +"\n"+new ComponentImage(Createimg.drawTipImg(betterTyping.getSubscriptInstances())));
        }else if(message.length()>5&&message.substring(0,5).equals("#一词不漏 ")){
            BetterTyping betterTyping = betterTypingHashMap.get("词组提示码表");
            betterTyping.changecolortip(message.substring(5));
            betterTyping.compalllength();
            event.respond("标点顶点屏理论码长："+ RegexText.FourOutFiveIn(betterTyping.getDingKeylength())+" 总键数"+betterTyping.getDingalllength()
                    +"\n"+new ComponentImage(Createimg.drawTipImg(betterTyping.getSubscriptInstances())));
        }
//        else if(message.contains("？")&&betterTypingHashMap.containsKey(message.substring(0,message.indexOf("？")))){
//            BetterTyping betterTyping = betterTypingHashMap.get(message.substring(0,message.indexOf("？")));
//            betterTyping.changecolortip(message.substring(message.indexOf("？")+1));
//            betterTyping.compalllength();
//            event.respond(message.substring(0,message.indexOf("？"))+" 标点顶点屏理论码长："+ RegexText.FourOutFiveIn(betterTyping.getDingKeylength())+" 总键数："+betterTyping.getDingalllength()
//                    +"\n"+new ComponentImage(Createimg.drawTipImg(betterTyping.getSubscriptInstances())));
//        }else if(message.equals("#更新词库")){
//            loadCodeFile();
//        }else if(message.equals("#词库列表")){
//            List<String> codeNameList = CodeFilesName.getCodeFilesName();
//            StringBuffer stringBuffer = new StringBuffer();
//            for(String codeName : codeNameList)
//                stringBuffer.append(codeName+"\n");
//            event.respond(stringBuffer.toString());
//        }
//        BetterTyping.setSubscriptInstanceNull();
    }
    @EventHandler
    public void CarryPrivate(EventPrivateMessage event){
        String message = event.getMessage();
        if(message.equals("#我的投稿")){
            event.respond(OutConn.lookmeAllGroupSaiwen(event.getSenderId()));
        }else if(message.equals("#取消广播")){
            noticesign = false;
            event.respond("广播被关闭");
        }else if (message.equals("#广播")){
            noticesign = true;
            event.respond("广播开启");
        }else if(noticesign&&initGroupList.adminList.contains(event.senderId)){
            for (Long aLong : grouplist.keySet()) {
                event.getHttpApi().sendGroupMsg(aLong,message);
            }
        }
        String []s = message.split("\\s+");
    }
    @EventHandler
    public void Carry(EventGroupMessage event)
    {
        IcqHttpApi httpApi = event.getHttpApi();
        try {
            if (!init) {
                initGroupList.init(httpApi);
                init = true;
            }
            respondSign = false;
            CarryName(event);
            if (!respondSign)
                CarryComGrade(event);//收集赛文成绩
            if (!respondSign)
                CarryComShow(event);//发送成绩和赛文
            if (!respondSign)
                ShowGroupList(event);//群映射列表
            //每天收图
            if (automati_inclusion_sign) {
                Automatic_inclusion(event);
            }
            //发送各群的历史成绩
            if (!respondSign)
                grouphistory(event);
            //创建比赛场地
            if (!respondSign) {
                CreateCijicom(event);//创建跟打战场
                CreateFollowCom(event);//创建跟打战场
                CreateFollowTeamCom(event);//创建团队跟打战场
            }
            if(chanllgelist.containsKey(event.getSenderId())&&!respondSign){
                event.respond("[CQ:at,qq=" + event.getSenderId() + "]你还有文章未完成，如果不想打了请输入指令：\n#取消上传\n若你使用该指令时对群友造成骚扰，会对你进行拉黑。");
            }
            if(!respondSign&&RegexText.returnduan(event.getMessage())==-1){
                InConn.AddGroupCheatNum(event.getMessage().length(),event.getSenderId());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
//    public void loadCodeFile(){
//        List<String> codeNameList = CodeFilesName.getCodeFilesName();
//
//        //最佳编码码表加载
//        for(String codeName : codeNameList){
//            betterTypingHashMap.put(codeName,new BetterTyping(codeName));
//        }
//    }
    public void grouphistory(EventGroupMessage event){
        String message = event.getMessage();
        long groupid = event.getGroupId();
        String []s = message.split(" ");
        if(s[0].equals("#历史成绩")){
            if(s.length==2) {
                s[1] = RegexText.AddZero(s[1]);
                String image = "typinggroup" + separator + groupid + "-" + s[1] + ".jpg";
                event.respond("[CQ:image,file=" + image + "]");
                respondSign = true;
            }else if(s.length==3){
                if(initGroupList.QQGroupNameMap.containsKey(s[1])) {
                    s[2] = RegexText.AddZero(s[2]);
                    String image = "typinggroup" + separator + initGroupList.QQGroupNameMap.get(s[1]) + "-" + s[2] + ".jpg";
                    event.respond("[CQ:image,file=" + image + "]");
                    respondSign = true;
                }else{
                    event.respond("无该群");
                }
            }
        }
    }
    private void Automatic_inclusion(EventGroupMessage event){
        System.out.println("收图");
        String message = event.getMessage();
        int imageindex = message.indexOf("url=");
        long groupid = event.getGroupId();
        long sendid = event.getSenderId();
        String []s = message.split(" ");
        if(sendid == robot.xiaochaiQ&&grouplist.containsKey(groupid)) {
            if (grouplist.get(groupid)&&imageindex != -1) {
                System.out.println(grouplist.get(groupid));
                message = message.substring(imageindex + 4, message.length() - 1);
                String filename = "typinggroup/"+groupid +"-"+ new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()) + ".jpg";
                    String image = "/root/coolq/data/image/" + filename;
                System.out.println(image);
                DownloadMsg.downloadMsg(message, image);
                grouplist.put(groupid,false);
                event.getHttpApi().sendGroupMsg(robot.tljGroupNum, "[CQ:image,file="+filename+"]");
                for (Long o : grouplist.keySet()) {
                    if(grouplist.get(o)==false){
                        automati_inclusion_sign = false;
                    }else {
                        automati_inclusion_sign = true;
                        break;
                    }
                }
            }else if(message.equals("没有找到今天的比赛成绩！")){
                grouplist.put(groupid,false);
                for (Long o : grouplist.keySet()) {
                    if(grouplist.get(o)==false){
                        automati_inclusion_sign = false;
                    }else {
                        automati_inclusion_sign = true;
                        break;
                    }
                }
            }
        }
    }
    private void CarryName(EventGroupMessage event){
        try {
            System.out.println("名片操作");
            Long id = event.getSenderId();
            String message = "[CQ:at,qq=" + id + "]\n";
            if(event.getMessage().equals("#刷新缓存")){
                event.getGroupSender().refreshInfo();
                event.getGroup().refreshInfo();
                event.getBot().getAccountManager().refreshCache();
                initGroupList.init(event.getHttpApi());
//                event.respond("刷新成功，你现在的群名片为"+event.getGroupSender().getInfo().getCard());
                event.respond("刷新成功，你现在的群名片为"+event.getHttpApi().getGroupMemberInfo(event.getGroupId(),id).getData().getCard());
                respondSign = true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void CarryComGrade(EventMessage event){
        try {
            Long GroupID = RegexText.getGroupID(event.toString());
            Long SendID = event.getSenderId();
            if (event.getMessage().length()>5&&event.getMessage().substring(0, 5).equals("第999段")) {
                double Grade[] = RegexText.getGrade(event.getMessage());
                String message = "群号:" + GroupID + "用户:" + SendID + "\n速度:" + Grade[0] + " 击键:" + Grade[1] + " 码长:" + Grade[2];
                System.out.println(message);
                long MaxQQ = OutConn.getTodayMaxByGroupId(GroupID,Grade[0],RegexText.returnduan(event.getMessage()));
                if(MaxQQ!=0L&&MaxQQ!=SendID){
                    event.respond(new MessageBuilder().add(new ComponentAt(MaxQQ)).add("你第一被抢了").toString());
                }
                if(GroupID!=726064238L)
                    InConn.AddRobotHistory(SendID, GroupID, Grade);
                InConn.addMaxComMath(SendID, GroupID, Grade);
//                event.getHttpApi().sendPrivateMsg(1061917196L, message);
                respondSign = true;
            }else if(RegexText.returnduan(event.getMessage())==1&&chanllgelist.containsKey(SendID)){
                System.out.println("挑战段");
                double Grade[] = RegexText.getGrade(event.getMessage());
                event.respond(InConn.addRobotSaiwenMath(SendID,GroupID,Grade,
                        event.getSender().getInfo().getNickname(),
                        chanllgelist.get(SendID)));
                chanllgelist.remove(SendID);
                respondSign = true;
            }else if(event.getMessage().equals("#取消上传")){
                if(chanllgelist.containsKey(SendID)) {
                    chanllgelist.remove(SendID);
                    event.respond("取消成功");
                }
                else
                    event.respond("取消你妹，你都没开始！");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    HashMap<Long,String> chanllgelist = new HashMap<>();
    private void CarryComShow(EventGroupMessage event){
        try {
            String message = event.getMessage();
            System.out.println("赛文和成绩操作");
            Long QQnum = event.getSenderId();
            if(message.equals("#成绩")){
                String path = OutConn.ShowGroupIdMath(QQnum, GroupCache.typeGroupMap.get(event.getGroupId()), Conn.getdate(),event.getHttpApi().getGroupMemberInfo(event.getGroupId(),QQnum).getData().getCard());
                if(path.equals("无收录成绩"))event.respond(path);
                else event.respond("[CQ:image,file="+ path +"]");
            }else if(message.equals("#统计成绩")){
                String path =  ComArti.responseStr(Conn.getdate().toString(), QQnum, Conn.getdate(), 4);
                if(path.equals("无该天赛文成绩"))event.respond(path);
                else event.respond("[CQ:image,file="+ path +"]");
                respondSign = true;
            }
            String s[] = message.split(" ");
            if(s.length==2&&s[0].equals("#文章")){
                event.respond(OutConn.ShowRobotSaiwen(s[1],0,1));
                respondSign = true;
            }
            else if(s.length==3&&s[0].equals("#文章")){
                chanllgelist.put(QQnum,s[1]+"%"+s[2]);
                event.respond(OutConn.ShowRobotSaiwen(s[1],Integer.valueOf(s[2]),2));
                respondSign = true;
            }else if (s.length==3&&s[0].equals("#文章成绩")){
                String path = ComArti.getRobotArMathImgPath(s[1],Integer.valueOf(s[2]));
                if(path.equals("没有该文章成绩"))event.respond(path);
                else event.respond("[CQ:image,file="+path+"]");
                respondSign = true;
            }
            if(respondSign==true)return;
        }catch (Exception e){
//            e.printStackTrace();
        }
    }
    private void ShowGroupList(EventMessage event){
        try {
            StringBuilder message = new StringBuilder(event.getMessage());
            System.out.println("群映射操作");
            boolean sessced = false;
            if (message.toString().equals("#群映射列表")) {
                try {
                    String sql = "select * from groupmap";
                    Connection con = Conn.getConnection();
                    ResultSet rs = Conn.getStmtSet(con, sql);
                    message = new StringBuilder();
                    while (rs.next()) {
                        message.append(rs.getString("groupname")).append("：").append(rs.getString("groupid")).append("\n");
                        sessced = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (sessced)
                    event.respond(message.substring(0, message.length() - 1));
                respondSign = true;
            }
        }catch (Exception ignored){}
    }

    static HashMap<Long, GroupThread> GroupWarList = new HashMap<>();
    private void CreateCijicom(EventGroupMessage event) {
        try {
            String message = event.getMessage();
            String[] s = message.split(" ");
            int length = s.length;
            Long GroupID = RegexText.getGroupID(event.toString());
            Long ID = event.getSenderId();
            String card = GroupCache.groupCardCache.get(GroupID).get(ID);
            if(card==null||card.equals("")){
                event.getSender().getInfo().getNickname();
            }
            String at = "[CQ:at,qq="+ID+"]\n";
            if (length == 4 && s[0].equals("#随机战场")) {
                if (GroupWarList.containsKey(GroupID))
                    event.respond(at+"随机战场已存在");
                else {//总长 段长 段时间
                    GroupThread gp = new GroupThread(Integer.parseInt(s[1]), Integer.parseInt(s[2]),
                            Integer.parseInt(s[3]),event,GroupID);
                    GroupWarList.put(GroupID, gp);
                    gp.start();
                    event.respond(at+"已开启一个总长度为" + s[1] + "，一段字数为" + s[2] + "，每段间隔为" + s[3] + "秒的战场");
                }
            } else if (message.equals("#加入战场")) {
                if (GroupWarList.containsKey(GroupID)) {
                    GroupThread gp = GroupWarList.get(GroupID);
                    if (gp.getStartSign())
                        event.respond(at+"加入失败，战场已开始，请等待下一场");
                    else if (gp.getIDlist().containsKey(ID))
                        event.respond(at+"请勿重复加入,退出战场指令：#退出战场");
                    else {
                        gp.addID(ID,card);
                        event.respond(at+"加入成功");
                    }
                } else
                    event.respond(at+"该群还未创建战场，指令：#随机战场");
            } else if (message.equals("#退出战场")) {
                if (GroupWarList.containsKey(GroupID)) {
                    GroupThread gp = GroupWarList.get(GroupID);
                    if (gp.getStartSign())
                        event.respond(at+"退出失败，战场已开始，战场结束自动退出");
                    else if (!gp.getIDlist().containsKey(ID))
                        event.respond(at+"你未曾加入战场，无法执行退出");
                    else {
                        gp.removeID(ID);
                        event.respond(at+"退出成功");
                    }
                } else
                    event.respond(at+"该群还未创建战场，指令：#随机战场");
            }else if(message.equals("#战场启动")){
                if (GroupWarList.containsKey(GroupID)) {
                    GroupThread gp = GroupWarList.get(GroupID);
                    if (gp.getStartSign())
                        event.respond(at+"战场已启动");
                    else if (!gp.getIDlist().containsKey(ID))
                        event.respond(at+"你未曾加入战场，无法执行启动");
                    else {
                        gp.setStartSign(true);
                        event.respond("战场启动！战斗开始！");
                    }
                } else
                    event.respond(at+"该群还未创建战场，指令：#随机战场");
            }
            else if(message.equals("#战场销毁")){
                if(GroupWarList.containsKey(GroupID)){
                    GroupThread gp = GroupWarList.get(GroupID);
                    gp.stop();
                    GroupWarList.remove(GroupID);
                    event.respond(at+"战场已销毁");
                }else
                    event.respond(at+"该群还未创建战场，指令：#随机战场");
            }
            else if(message.equals("#战场帮助")||message.equals("#随机战场")){
                message = "#随机战场 文章总长度 分段长度 间隔时间 = 创建一个战场\n"+
                        "#加入战场 = 加入战场\n"+
                        "#战场成员 = 查询已加入战场的群友\n"+
                        "#退出战场 = 退出本群战场\n"+
                        "#战场启动 = 与已加入战场的群友一起进行限时分段跟打\n"+
                        "#战场销毁 = 将本群创建的战场删除";
                event.respond(message);
            }
            else if(GroupWarList.containsKey(GroupID)){
                GroupThread gp = GroupWarList.get(GroupID);
                if(message.equals(gp.message)){
                    Map<Long,Integer> idlist = gp.getIDlist();
                    int i = idlist.get(ID);
                    idlist.put(ID,i+1);
                    System.out.println("加分："+(i+1));
                }
                else if(message.equals("#战场成员")){
                    Map<Long,Integer> idlist = gp.getIDlist();
                    StringBuilder number = new StringBuilder();
                    for(Long k:idlist.keySet()){
                        number.append("用户Q号：").append(k).append("\n");
                    }
                    number.append("共").append(idlist.size()).append("个成员准备进入战场");
                    event.respond(number.toString());
                }
            }
        }catch (Exception ignored){}
    }
    static HashMap<Long, GroupFollowThread> GroupFollowWarList = new HashMap<>();
    private void CreateFollowCom(EventGroupMessage event){
        try{
            String message = event.getMessage();
            String[] s = message.split(" ");
            int length = s.length;
            Long GroupID = RegexText.getGroupID(event.toString());
            Long ID = event.getSenderId();
            String card = GroupCache.groupCardCache.get(GroupID).get(ID);
            if(card==null||card.equals("")){
                event.getSender().getInfo().getNickname();
            }
            String at = "[CQ:at,qq="+ID+"]\n";
            if(length==2&&s[0].equals("#随机混战")) {
                if (GroupFollowWarList.containsKey(GroupID))
                    event.respond(at + "随机混战已存在，若重开请先销毁");
                else {//总长 段长 段时间
                    GroupFollowThread gp = new GroupFollowThread(event, Integer.parseInt(s[1]), GroupID);
                    GroupFollowWarList.put(GroupID, gp);
                    gp.start();
                    event.respond(at + "已开启一个每段字数为" + s[1] + "的混战");
                }
            } else if (message.equals("#加入混战")) {
                if (GroupFollowWarList.containsKey(GroupID)) {
                    GroupFollowThread gp = GroupFollowWarList.get(GroupID);
                    if (gp.getIDlist().containsKey(ID))
                        event.respond(at+"请勿重复加入,退出混战指令：#退出混战");
                    else {
                        gp.addID(ID,card);
                        event.respond(at+"加入成功");
                    }
                } else
                    event.respond(at+"该群还未创建混战，指令：#随机混战");
            } else if (message.equals("#退出混战")) {
                if (GroupFollowWarList.containsKey(GroupID)) {
                    GroupFollowThread gp = GroupFollowWarList.get(GroupID);
                    if (!gp.getIDlist().containsKey(ID))
                        event.respond(at + "你未曾加入混战，无法执行退出");
                    else {
                        gp.removeID(ID);
                        event.respond(at + "退出成功");
                    }
                } else
                    event.respond(at + "该群还未创建混战，指令：#随机混战");
            }else if(message.equals("#混战启动")){
                if (GroupFollowWarList.containsKey(GroupID)) {
                    GroupFollowThread gp = GroupFollowWarList.get(GroupID);
                    if (gp.getStartSign())
                        event.respond(at+"混战已启动");
                    else if (!gp.getIDlist().containsKey(ID))
                        event.respond(at+"你未曾加入混战，无法执行启动");
                    else {
                        gp.setStartSign(true);
                        event.respond("混战启动！战斗开始！");
                        gp.send();
                    }
                } else
                    event.respond(at+"该群还未创建混战，指令：#随机混战");
            }else if(message.equals("#混战结算")){
                if(GroupFollowWarList.containsKey(GroupID)){
                    String message1 = "";
                    GroupFollowThread gp = GroupFollowWarList.get(GroupID);
                    if (!gp.getIDlist().containsKey(ID)){
                        event.respond(at+"你未曾加入混战，无法执行启动");
                        return;
                    }
                    message1 += SortMap.SendsortValue(gp.getIDlist(),gp.getIDnamelist());
                    gp.stop();
                    GroupFollowWarList.remove(GroupID);
                    event.respond(at+"混战已结算\n"+message1);
                }else
                    event.respond(at+"该群还未创建混战，指令：#随机混战");
            }else if(message.equals("#混战帮助")||message.equals("#随机混战")){
                message = "#随机混战 一段长度 = 创建一个混战\n"+
                        "#加入混战 = 加入混战\n"+
                        "#混战成员 = 查询已加入混战的群友\n"+
                        "#退出混战 = 退出本群混战\n"+
                        "#混战启动 = 与已加入混战的群友一起进行分段跟打\n"+
                        "#混战结算 = 将本群创建的混战结算成绩并删除\n"+
                        "#让速 速度 = 结算临时成绩时，将实际成绩减去让速得出本段成绩\n"+
                        "记分规则：第一名3分，第二名2分，第三名1分，其他名次无分";
                event.respond(message);
            }else if(length==2&&s[0].equals("#让速")){
                if(GroupFollowWarList.containsKey(GroupID)){
                    GroupFollowThread gp = GroupFollowWarList.get(GroupID);
                    if (!gp.getIDlist().containsKey(ID)){
                        event.respond(at+"你未曾加入混战，无法执行启动");
                        return;
                    }
                    try{

                        double letSpeed = Double.parseDouble(s[1]);
                        if(letSpeed<1&&letSpeed>0) {
                            gp.setLetSpeed(ID, letSpeed);
                            event.respond(at + "让速设置成功");
                        }else
                            event.respond(at+"让速参数必须在0到1之间");
                    }catch (Exception ignored){}
                }
            }else if(GroupFollowWarList.containsKey(GroupID)){
                GroupFollowThread gp = GroupFollowWarList.get(GroupID);
                boolean next = true;
                try {
                    String regex = "[^0123456789]+";
                    if (message.substring(0, 1).equals("第")&&
                            Integer.parseInt(message.substring(1,5).replaceAll(regex,""))==gp.getDuan()) {
                        double[] Grade = RegexText.getGrade(event.getMessage());
                        System.out.println(gp.getIDspend(ID));
                        if(gp.getIDspend(ID)==0.0) {
                            gp.setIDspend(ID, Grade[0]*gp.getLetSpeed(ID));
                            System.out.println(Grade[0]+" "+Grade[1]+" "+Grade[2]);
                        }
                        for(Long k:gp.getIDspendlist().keySet())
                            if(gp.getIDspendlist().get(k)==0.0){
                                System.out.println(k);
                                next=false;
                            }
                        if(next){
                            gp.nextDuan();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                if(message.equals("#混战成员")){
                    Map<Long,Integer> idlist = gp.getIDlist();
                    StringBuilder number = new StringBuilder();
                    for(Long k:idlist.keySet()){
                        number.append("用户Q号：").append(k).append("\n");
                    }
                    number.append("共").append(idlist.size()).append("个成员准备进入团场");
                    event.respond(number.toString());
                }
            }
        }catch (Exception ignored){
        }
    }
    static HashMap<Long, GroupFollowTeamThread> GroupFolloTeamWarList = new HashMap<>();
    private void CreateFollowTeamCom(EventGroupMessage event){
        try{
            String message = event.getMessage();
            String[] s = message.split(" ");
            int length = s.length;
            Long GroupID = RegexText.getGroupID(event.toString());
            Long ID = event.getSenderId();
            String card = GroupCache.groupCardCache.get(GroupID).get(ID);
            if(card==null||card.equals("")){
                event.getSender().getInfo().getNickname();
            }
            String at = "[CQ:at,qq="+ID+"]\n";
            if(length==2&&s[0].equals("#随机团战")) {
                if (GroupFolloTeamWarList.containsKey(GroupID))
                    event.respond(at + "随机团战已存在，若重开请先销毁");
                else {//总长 段长 段时间
                    GroupFollowTeamThread gp = new GroupFollowTeamThread(event, Integer.parseInt(s[1]));
                    GroupFolloTeamWarList.put(GroupID, gp);
//                    gp.start();
                    event.respond(at + "已开启一个每段字数为" + s[1] + "的团战");
                }
            } else if (length==2&&s[0].equals("#加入团战")) {
                if (GroupFolloTeamWarList.containsKey(GroupID)) {
                    GroupFollowTeamThread gp = GroupFolloTeamWarList.get(GroupID);
                    gp.addID(Integer.parseInt(s[1]),ID,card);
                } else
                    event.respond(at+"该群还未创建团战，指令：#随机团战");
            } else if (message.equals("#退出团战")) {
                if (GroupFolloTeamWarList.containsKey(GroupID)) {
                    GroupFollowTeamThread gp = GroupFolloTeamWarList.get(GroupID);
                    gp.removeID(ID);

                } else
                    event.respond(at + "该群还未创建团战，指令：#随机团战");
            }else if(message.equals("#团战启动")){
            if (GroupFolloTeamWarList.containsKey(GroupID)) {
                GroupFollowTeamThread gp = GroupFolloTeamWarList.get(GroupID);
                if (gp.getStartSign())
                    event.respond(at+"团战已启动");
                else if (gp.isEmpty(ID)==-1)
                    event.respond(at+"你未曾加入团战，无法执行启动");
                else {
                    gp.setStartSign(true);
                    event.respond("团战启动！战斗开始！");
                    gp.send();
                }
            } else
                event.respond(at+"该群还未创建团战，指令：#随机团战");
        }else if(message.equals("#团战结算")){
            if(GroupFolloTeamWarList.containsKey(GroupID)){
                String message1 = "";
                GroupFollowTeamThread gp = GroupFolloTeamWarList.get(GroupID);
                message1 += SortMap.SendsortValueTeamMath(gp.getMath());
//                gp.stop();
                GroupFolloTeamWarList.remove(GroupID);
                event.respond(at+"团战已结算\n"+message1);
            }else
                event.respond(at+"该群还未创建团战，指令：#随机团战");
        }else if(message.equals("#团战帮助")||message.equals("#随机团战")){
            message = "#随机团战 一段长度 = 创建一个混战\n"+
                    "#加入团战 队伍号 = 加入某个队伍准备团战（加入团战 1/2）\n"+
                    "#团战成员 = 查询已加入该队伍的群友\n"+
                    "#退出团战 = 退出已加入的队伍\n"+
                    "#团战启动 = 与已加入团战的群友一起进行分段跟打\n"+
                    "#团战结算 = 将本群创建的团战结算成绩并删除\n"+
                    "记分规则：只分两只队伍，赢的得一分，按照队伍平均速度计算";
            event.respond(message);
        }else if(GroupFolloTeamWarList.containsKey(GroupID)){
                GroupFollowTeamThread gp = GroupFolloTeamWarList.get(GroupID);
                boolean next = true;
                try {
                    String regex = "[^0123456789]+";
                    if (message.substring(0, 1).equals("第")&&
                            Integer.parseInt(message.substring(1,5).replaceAll(regex,""))==gp.getDuan()) {
                        double[] Grade = RegexText.getGrade(event.getMessage());
//                        System.out.println(gp.getIDspend(ID));
                        if(gp.getSpeedlist().get(ID)==0.0) {
                            gp.setIDspend(ID, Grade[0]);
                            System.out.println(Grade[0]+" "+Grade[1]+" "+Grade[2]);
                        }
                        for(Integer k:gp.getMember().keySet()){
                            List<Long> member = gp.getMember().get(k);
                            Map<Long,Double> speedlist = gp.getSpeedlist();
                            for (Long aLong : member) {
                                if (speedlist.get(aLong) == 0.0) {
                                    next = false;
                                }
                            }
                        }
                        if(next){
                            gp.nextDuan();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                if(message.equals("#团战成员")){
                    StringBuilder message1 = new StringBuilder();
                    for(Integer k:gp.getMember().keySet()){
                        List<Long> member = gp.getMember().get(k);
                        message1.append(k).append("队成员：\n");
                        for (Long aLong : member) {
                            message1.append(aLong).append("\n");
                        }
                    }
                    event.respond(message1.toString());
                }
            }

        }catch (Exception ignored){}
    }
}