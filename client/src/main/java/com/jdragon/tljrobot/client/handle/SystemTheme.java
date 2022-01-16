package com.jdragon.tljrobot.client.handle;

import javax.swing.*;

/**
 * <p></p>
 * <p>create time: 2022/1/17 1:17 </p>
 *
 * @author : Jdragon
 */
public class SystemTheme extends WindowThemeHandle {

    SystemTheme() {
        super("系统默认", UIManager.getSystemLookAndFeelClassName());
    }

    @Override
    public void handle() throws Exception {
        UIManager.setLookAndFeel(getClassName());
        UIManager.put("ToolBar.isPaintPlainBackground",true);
    }
}
