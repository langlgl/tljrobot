package com.jdragon.tljrobot.client.window;

import com.jdragon.tljrobot.client.component.JMenuComponent;
import com.jdragon.tljrobot.client.component.SwingSingleton;
import com.jdragon.tljrobot.client.config.LocalConfig;
import com.jdragon.tljrobot.client.constant.Constant;
import com.jdragon.tljrobot.client.entry.Article;
import com.jdragon.tljrobot.client.event.FArea.*;
import com.jdragon.tljrobot.client.event.other.ListenPlayEvent;
import com.jdragon.tljrobot.client.event.other.SwitchFollowPlayEvent;
import com.jdragon.tljrobot.client.event.other.SwitchListenPlayEvent;
import com.jdragon.tljrobot.client.event.other.SwitchWatchPlayEvent;
import com.jdragon.tljrobot.client.listener.common.ArticleTreeListener;
import com.jdragon.tljrobot.client.listener.common.MixListener;
import com.jdragon.tljrobot.client.listener.common.TypingListener;
import com.jdragon.tljrobot.client.listener.core.SystemListener;
import com.jdragon.tljrobot.client.utils.common.BetterTypingSingleton;
import com.jdragon.tljrobot.client.utils.common.Clipboard;
import com.jdragon.tljrobot.client.utils.common.DrawUnLookPlayResult;
import com.jdragon.tljrobot.client.window.dialog.LogonDialog;
import com.jdragon.tljrobot.client.window.dialog.SendArticleDialog;
import com.jdragon.tljrobot.client.window.dialog.SetDialog;
import com.jdragon.tljrobot.client.window.dialog.ShowArticleDialog;
import com.jdragon.tljrobot.tljutils.ArticleUtil;
import com.jdragon.tljrobot.tljutils.string.Comparison;
import lombok.Data;
import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;

import static com.jdragon.tljrobot.client.component.SwingSingleton.*;
import static com.jdragon.tljrobot.client.utils.core.Layout.*;

@Data
public class MainFra extends JFrame {
    private static MainFra mainFra;
    public static MainFra getInstance(){
        if(mainFra==null){
            mainFra = new MainFra();
        }
        return mainFra;
    }
    private Point pressedPoint;
    public MainFra(){
        this.setTitle("tlj");
        this.setBounds(LocalConfig.windowX,LocalConfig.windowY,
                LocalConfig.windowWidth,LocalConfig.windowHeight);
        //美化UI
        try {
            BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.FrameBorderStyle
                    .generalNoTranslucencyShadow;
            BeautyEyeLNFHelper.launchBeautyEyeLNF();
            UIManager.put("RootPane.setupButtonVisible", false);
        } catch (Exception e) {
//            e.printStackTrace();
        }
        init();
    }
    public void init(){
//        addOnBounds(this,F1(),0,0,0,0);
//        addOnBounds(this,F2(),0,0,0,0);
//        addOnBounds(this,F3(),0,0,0,0);
//        addOnBounds(this,F4(),0,0,0,0);
//        addOnBounds(this,F5(),0,0,0,0);
//        addOnBounds(this,F6(),0,0,0,0);
//        addOnBounds(this,F7(),0,0,0,0);

        jMenu().add(JMenuComponent.getInstance().getMenu());
        addOnBounds(this, jMenu(),5, 10, 45, 32);
        addOnBounds(this, speedButton(),
                rowAddSpacing(jMenu(),7),10,150,30);
        addOnBounds(this, keySpeedButton(),
                rowAddSpacing(speedButton(),10),10,150,30);
        addOnBounds(this, keyLengthButton(),
                rowAddSpacing(keySpeedButton(),10),10,150,30);
        addOnBounds(this, theoreticalCodeLengthButton(),
                rowAddSpacing(keyLengthButton(),10),10,150,30);

        addOnBounds(this, typingAndWatching(),10,
                columnAddSpacing(speedButton(),10),getWidth()-10, getHeight()-100);
        typingAndWatching().setDividerLocation(400);

        addOnBounds(this, typingProgress(),10,
                columnAddSpacing(typingAndWatching(),0),getWidth()-10,10);

        addOnBounds(this, closeButton(),this.getWidth()-20,0,20,10);
        addOnBounds(this, maxButton(),this.getWidth()-42,0,20,10);
        addOnBounds(this, minButton(),this.getWidth()-63,0,20,10);
        addOnBounds(this, sizeButton(),this.getWidth()-10,this.getHeight()-10,10,10);

        addOnBounds(this, qQNameLabel(), typingAndWatching().getX(),columnAddSpacing(typingAndWatching(),10),120,40);
        addOnBounds(this, numberLabel(),rowAddSpacing(qQNameLabel(),10),columnAddSpacing(typingAndWatching(),10),150,40);
        addOnBounds(this, numberRecordLabel(),rowAddSpacing(numberLabel(),10),columnAddSpacing(typingAndWatching(),10),250,40);
        addOnBounds(this, tipsLabel(),rowAddSpacing(numberRecordLabel(),10),columnAddSpacing(typingAndWatching(),10),120,40);
        addOnBounds(this, sendArticleLabel(),rowAddSpacing(tipsLabel(),10),columnAddSpacing(typingAndWatching(),10),120,40);
    }
    int preButton;
    public void addListener(){
        closeButton().addActionListener(SystemListener.getInstance());
        maxButton().addActionListener(SystemListener.getInstance());
        minButton().addActionListener(SystemListener.getInstance());
        sizeButton().addMouseListener(SystemListener.getInstance());
        sizeButton().addMouseMotionListener(SystemListener.getInstance());
        (typingText().getDocument()).addDocumentListener(TypingListener.getInstance());
        typingText().addKeyListener(TypingListener.getInstance());
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();



        manager.addKeyEventPostProcessor(event->{
            if (event.getID() != KeyEvent.KEY_PRESSED) return false;
            if(preButton==17){
                switch (event.getKeyCode()){
                    case 'Z': SetDialog.getInstance().setVisible(true);break;
                    case 'P': ArticleTreeListener.getInstance().nextOrder();break;
                    case 'S': ArticleTreeListener.getInstance().save();break;
                    case 'L':
                        MixListener.getInstance().mixButton("该段乱序");break;
                    case 'E':
                        Article.getArticleSingleton(1,"剪贴板载文", Clipboard.get());
                        ReplayEvent.start();
                        break;
                    case 'Q':
                        SwitchListenPlayEvent.start();break;
                    case 'K':
                        SwitchWatchPlayEvent.start();break;
                    case 'G':
                        SwitchFollowPlayEvent.start();break;
                    case KeyEvent.VK_ENTER:
                        if(LocalConfig.typingPattern.equals(Constant.WATCH_PLAY_PATTERN)) {
                            if(SwingSingleton.typingText().getText().length()==0)break;
                            typingText().setEditable(false); // 设置不可打字状态
                            TypingListener.delaySendResultSign = true;
                        }else if(LocalConfig.typingPattern.equals(Constant.LISTEN_PLAY_PATTERN)){
                            List<HashMap<String,Integer>> hashMapList =
                                    Comparison.getComparisonListenResult(ListenPlayEvent.getContent(),
                                            ArticleUtil.clearSpace(typingText().getText()), BetterTypingSingleton.getInstance().getSymbolCode());
                            TypingListener.getInstance().changeListenPlayFontColor(hashMapList);
                            SendAchievementEvent.start();
                            ListenPlayEvent.stop();
                            DrawUnLookPlayResult.drawUnFollowPlayResultImg(ListenPlayEvent.getTitle(),hashMapList,"听打");
                        }
                        break;
                    default:break;
                }
                typingText().requestFocusInWindow();
            }
            preButton = event.getKeyCode();
            switch (event.getKeyCode()) {
                case KeyEvent.VK_F1:
                    SendAchievementEvent.start();
                    break;
                case KeyEvent.VK_F2:
                    SendArticleEvent.start();
                    break;
                case KeyEvent.VK_F3:
                    ReplayEvent.start();
                    break;
                case KeyEvent.VK_F4:
                    if(!LocalConfig.typingPattern.equals(Constant.LISTEN_PLAY_PATTERN))
                        QQGetArticleEvent.start();
                    else
                        ListenPlayEvent.start();
                    break;
                case KeyEvent.VK_F5:
                    ChangeQQGroupEvent.start();
                    break;
                case KeyEvent.VK_F6 :
                    ShareArticleEvent.start();
                    break;
                case KeyEvent.VK_F7 :
                    TypingPauseEvent.start();
                    break;
//                    case KeyEvent.VK_F8: new web(); break;
//                    case KeyEvent.VK_F9 : break;
//                    case KeyEvent.VK_F9 :
//                        TypingText().setEditable(false); // 设置不可打字状态
//                        Typing.delaySendResultSign = true;
//                        break;
//                    case KeyEvent.VK_F11 : break;
//                    case KeyEvent.VK_F12 : break;
                case KeyEvent.VK_ESCAPE:
                    LogonDialog.getInstance().setVisible(false);
                    SendArticleDialog.getInstance().setVisible(false);
                    SetDialog.getInstance().setVisible(false);
                    ShowArticleDialog.getInstance("").setVisible(false);
                    break;
                default:break;
            }
            return true;
        });
    }
}
