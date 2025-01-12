package com.jdragon.tljrobot.client.event.other;

import com.jdragon.tljrobot.client.component.JMenuComponent;
import com.jdragon.tljrobot.client.config.LocalConfig;
import com.jdragon.tljrobot.client.constant.Constant;
import com.jdragon.tljrobot.client.event.FArea.ReplayEvent;
import com.jdragon.tljrobot.client.handle.document.DocumentStyleHandler;

import java.awt.*;

/**
 * Create by Jdragon on 2false2false.false2.false6
 */
public class SwitchFollowPlayEvent {

    private static final DocumentStyleHandler documentStyleHandler = DocumentStyleHandler.INSTANCE;

    public static void start(){
        ListenPlayEvent.stop();

        LocalConfig.typingPattern = Constant.FOLLOW_PLAY_PATTERN;
        documentStyleHandler.defineStyle("黑", 
                LocalConfig.fontSize, false, false, false, Color.BLACK, LocalConfig.family,
                LocalConfig.rightColor);
        documentStyleHandler.defineStyle("红", 
                LocalConfig.fontSize, false, false, false, Color.BLACK, LocalConfig.family,
                LocalConfig.mistakeColor);
        JMenuComponent.getInstance().switchingMode.setText("当前模式："+ LocalConfig.typingPattern);
        JMenuComponent.getInstance().getMenu().setText(LocalConfig.typingPattern);
        ReplayEvent.start();
    }
}
