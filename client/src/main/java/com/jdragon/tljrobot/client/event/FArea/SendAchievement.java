package com.jdragon.tljrobot.client.event.FArea;


import com.jdragon.tljrobot.client.config.FinalConfig;
import com.jdragon.tljrobot.client.config.LocalConfig;
import com.jdragon.tljrobot.client.constant.Constant;
import com.jdragon.tljrobot.client.entry.Article;
import com.jdragon.tljrobot.client.event.other.ListenPlay;
import com.jdragon.tljrobot.client.utils.common.Clipboard;
import com.jdragon.tljrobot.client.utils.common.DoCheck;
import com.jdragon.tljrobot.client.utils.common.QqOperation;
import com.jdragon.tljrobot.tljutils.SystemUtil;
import lombok.SneakyThrows;

import static com.jdragon.tljrobot.client.component.SwingSingleton.QQNameLabel;
import static com.jdragon.tljrobot.client.component.SwingSingleton.SpeedButton;
import static com.jdragon.tljrobot.client.entry.TypingState.*;

public class SendAchievement {
    @SneakyThrows
    public static void start()  {
        String result;
        if(LocalConfig.typingPattern.equals(Constant.LISTEN_PLAY_PATTERN)){
            int length = ListenPlay.getLength();
            result = ListenPlay.getTitle() +
                    " 文章长度" + length + " 正确率" + String.format("%.2f",((double)length-mistake)/length*100)+"%"+
                    " 听打模式" + " 错:" + lookMis + " 多:" + lookMore + " 少:" + lookMiss +
                    " 长流跟打器" + FinalConfig.VERSION +
                    " " + SystemUtil.getSystemName() + "版" ;
            SpeedButton().setText(String.format("%.2f",((double)length-mistake)/length*100)+"%");
            Clipboard.set(result);
        }else {
            Article article = Article.getArticleSingleton();
            int paragraph = article.getParagraph();
            double speed = getSpeed();
            double noMisSpeed = getSpeedNoMistake();
            double keySpeed = getKeySpeed();
            double keyLength = getKeyLength();
            int articleLength = article.getArticle().length();
            double shortCodesNum = (article.getShortCodeEntity().getArticleAverCodes());
            String check = String.format("%.2f", speed + keySpeed + keyLength);
            String checkCode = DoCheck.buildCheckStr(check, "genda");
            String noMisSpeedStr = mistake == 0 ? "" : ("/" + String.format("%.2f", noMisSpeed));
            String lookPlayStr = LocalConfig.typingPattern.equals(Constant.WATCH_PLAY_PATTERN)
                    ? " 看打模式 错:" + lookMis + " 多:" + lookMore + " 少:" + lookMiss + "" : "";
            result =
                    "第" + paragraph + "段" +
                            " 速度" + String.format("%.2f", speed) + noMisSpeedStr +
                            " 击键" + String.format("%.2f", keySpeed) +
                            " 码长" + String.format("%.2f", keyLength) +
                            " 标顶理论" + String.format("%.2f", shortCodesNum) +
                            " 字数" + articleLength +
                            " 回改" + deleteTextNumber +
                            " 退格" + deleteNumber +
                            " 错字" + mistake +
                            " 键数" + keyNumber +
                            " 选重" + repeat +
                            " 键准" + String.format("%.2f", getKeyAccuracy()) + "%" +
                            " 键法" + String.format("%.2f", getKeyMethod()) + "%" +
                            "(左" + left + ":右" + right + ":空格" + space + ")" +
                            " 打词率" + String.format("%.2f", getWordRate()) + "%" +
                            " 选重率" + String.format("%.2f", getRepeatRate()) + "%" + lookPlayStr +
                            " 长流跟打器" + FinalConfig.VERSION +
                            " " + SystemUtil.getSystemName() + "版" +
                            " 校验码" + checkCode;
        }
        Clipboard.set(result);
        if(LocalConfig.lurk)return;
        QqOperation.start(QqOperation.SEND_ACHIEVEMENT,QQNameLabel().getText());
    }
}
