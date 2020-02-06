package com.jdragon.tljrobot.client.listener.common;

import com.jdragon.tljrobot.client.component.SwingSingleton;
import com.jdragon.tljrobot.client.config.LocalConfig;
import com.jdragon.tljrobot.client.constant.Constant;
import com.jdragon.tljrobot.client.entry.Article;
import com.jdragon.tljrobot.client.entry.TypingState;
import com.jdragon.tljrobot.client.event.FArea.Replay;
import com.jdragon.tljrobot.client.event.FArea.ShareArticle;
import com.jdragon.tljrobot.client.utils.common.Clipboard;
import com.jdragon.tljrobot.client.utils.common.Code;
import com.jdragon.tljrobot.client.window.SendArticleDialog;
import com.jdragon.tljrobot.tljutils.ArticleUtil;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;

/**
 * Create by Jdragon on 2020.01.25
 */
public class ArticleTreeListener implements TreeSelectionListener, ActionListener {
    private static ArticleTreeListener articleTreeListener = new ArticleTreeListener();
    private ArticleTreeListener(){}
    public static ArticleTreeListener getInstance(){return articleTreeListener;}
    public static int fontnum = 0, fontweizhi = 0;
    byte[] s;
    public static String all, wen;
    public static long length = 0;
    File open;
    RandomAccessFile in = null;

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        try {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) SendArticleDialog.tree
                    .getLastSelectedPathComponent();
            if (node.isLeaf()) {
                Article article = Article.getArticleSingleton();
                fontweizhi = 0;
                article.setTitle(node.toString());
                if(article.getTitle().equals("随机一文")){
                    all = ArticleUtil.getRandomContent2();
                }else if(article.getTitle().equals("剪贴板")){
                    all = Clipboard.get();
                }else{
                    if (article.getTitle().substring(0, 4).equals("跟打进度")) {
                        readjindu();
                    }
                    open = new File("文章//" + node.getParent(), article.getTitle());
                    in = new RandomAccessFile(open, "r");
                    length = in.length();
                    s = new byte[(int) length];
                    in.readFully(s);
                    all = new String(s);
                }
                if(LocalConfig.clearSpace)
                    all = ArticleUtil.clearSpace(all);
                if(LocalConfig.replace)
                    all = ArticleUtil.replace(all);
                length = all.length();
                SendArticleDialog.getInstance().setTitle("文章总长度:"+length);
                getNumber();
                showContent();
            }
        } catch (Exception ignored) {
        }
    }
    public static void getNumber() {
        try {
            fontnum = Integer.parseInt(SendArticleDialog.number.getText());
        } catch (Exception e) {
//			JOptionPane.showMessageDialog(new JTextArea(), "字数框输入数字");
        }
    }
    public static void showContent() {
        if (fontnum > all.length())
            wen = all.substring(fontweizhi);
        else
            wen = all.substring(fontweizhi, fontweizhi + fontnum);
        if(wen.length()>500)
            SendArticleDialog.wenben.setText(wen.substring(0,500));
        else
            SendArticleDialog.wenben.setText(wen);
        // fontweizhi += fontnum;
        SwingSingleton.SendArticleLabel().setText(fontweizhi
                + "/"
                + all.length()
                + ":"
                + String.format("%.2f",
                (double) fontweizhi * 100 / all.length()) + "%");
    }
    void readjindu() throws IOException {
        try {
            open = new File("文章//文章类", Article.getArticleSingleton().getTitle());
            Reader read = new FileReader(open);
            BufferedReader br = new BufferedReader(read);
            Article.getArticleSingleton().setTitle(br.readLine());
            fontweizhi = Integer.parseInt(br.readLine());
            br.close();
            read.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void sendOrder(){
        Article article = Article.getArticleSingleton();
        showContent();
        article.setArticle(wen);//设置跟打内容
        if (article.getArticle() == null
                || article.getArticle().equals(""))
            return;
        Replay.start();
        TypingState.sendArticle = 1; // 顺序发文标志
        article.setParagraph(1);//设置段号
        fontweizhi += fontnum;
        SwingSingleton.SendArticleLabel().setVisible(true);
        SendArticleDialog.getInstance().setVisible(false);
        if (!LocalConfig.lurk)
            ShareArticle.start();
    }
    public void nextOrder(){
        try {
            if (fontweizhi >= all.length()) {
                JOptionPane.showMessageDialog(new JTextArea(), "发文结束");
                SwingSingleton.SendArticleLabel().setVisible(false);
                TypingState.sendArticle = 0;
                return;
            }
            if (fontweizhi + fontnum >= all.length()) {
                wen = all.substring(fontweizhi);
                fontweizhi = all.length();
            } else {
                wen = all.substring(fontweizhi, fontweizhi + fontnum);
                fontweizhi += fontnum;
            }
            Article article = Article.getArticleSingleton();
            article.setArticle(wen);
            article.addParagraph();// 发文增段
            Replay.start();
            SwingSingleton.SendArticleLabel().setText(fontweizhi
                    + "/"
                    + all.length()
                    + ":"
                    + String.format("%.2f", (double) fontweizhi * 100
                    / all.length()) + "%");
            ShareArticle.start();
        } catch (Exception ex) {
            System.out.println("发文处失败");
        }
    }
    public void save(){
        try {
            Article article = Article.getArticleSingleton();
            if(article.getTitle().equals("随机一文")){
                JOptionPane.showMessageDialog(new JTextArea(), "随机一文暂时不支持保存进度");
                return;
            }
            String jindufile = "跟打进度" + article.getTitle() + ".txt";
            open = new File("文章//文章类", jindufile);
            FileOutputStream testfile = new FileOutputStream(open);
            testfile.write("".getBytes());
            byte[] baocun = (article.getTitle() + "\r\n" + (fontweizhi - fontnum)).getBytes();
            testfile.write(baocun);
            testfile.close();
            JOptionPane.showMessageDialog(new JTextArea(), "已保存当前跟打进度");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(new JTextArea(), "保存进度失败");
        }
    }
    public static List<String> chouqulist = new ArrayList<>();
    public static List<String> chouqubufenlist = new ArrayList<>();
    public void chouqu(String model){
        Article article = Article.getArticleSingleton();
        getNumber();
        article.setArticle(randomCommon(all, fontnum));
        if (article.getArticle() == null)
            return;
        if (model.equals("抽取模式发文")) {
            SendArticleDialog.getInstance().setVisible(false);
            article.setParagraph(1);
            TypingState.sendArticle = Constant.SEND_EXTRACT;
        }else if(model.equals("下一段")){
            article.addParagraph(); // 发文增段
        }
        Replay.start();
        ShareArticle.start();
    }
    public static int wordNum;
    public void ciKu(){
        StringBuilder temp = new StringBuilder();
        chouqulist.clear();
        chouqubufenlist.clear();
        Article article = Article.getArticleSingleton();
        article.setTitle("词库练习");
        Code code = Code.getInstance(LocalConfig.codeTable);
        HashMap<String,Integer> selectTable;
        try {
            if(Objects.requireNonNull(SendArticleDialog.weizhi.getSelectedItem()).toString().equals("首选")){
                selectTable = code.firstTable;
            }else if(SendArticleDialog.weizhi.getSelectedItem().toString().equals("次选")){
                selectTable = code.otherTable;
            }else {
                selectTable = code.allTable;
            }
            for(Map.Entry<String,Integer> entry:selectTable.entrySet()){
                int wordLength = entry.getKey().length();
                int codeLength = entry.getValue();
                int wordLength1 = Integer.parseInt(String.valueOf(SendArticleDialog.cichang1.getValue()));
                int wordLength2 = Integer.parseInt(String.valueOf(SendArticleDialog.cichang2.getValue()));
                int codeLength1 = Integer.parseInt(String.valueOf(SendArticleDialog.machang1.getValue()));
                int codeLength2 = Integer.parseInt(String.valueOf(SendArticleDialog.machang2.getValue()));
                if((wordLength1==0&&wordLength2==0)||(wordLength >= wordLength1 && wordLength <= wordLength2)){
                    if((codeLength1==0&&codeLength2==0)||(codeLength >= codeLength1 && codeLength <= codeLength2)) {
                        chouqulist.add(entry.getKey());
                    }
                }
            }
            Collections.shuffle(chouqulist);
            // System.out.println(chouqulist.size()+" "+y);
            wordNum = Integer.parseInt(String.valueOf(SendArticleDialog.cishu.getValue()));
            if(wordNum==0)wordNum = 200;
            for (int i = 0; i < (Math.min(chouqulist.size(), wordNum)); i++) {
                temp.append(chouqulist.get(i));
                chouqubufenlist.add(chouqulist.get(i));
            }
            article.setArticle(temp.toString());
            article.setParagraph(1);
            Replay.start();
            TypingState.sendArticle = Constant.SEND_WORDS;
            SendArticleDialog.getInstance().setVisible(false);
        } catch (Exception ex) {
             ex.printStackTrace();
        }
    }
    public void ciKuNext(){
        if(TypingState.sendArticle == Constant.SEND_WORDS){
            chouqubufenlist.clear();
            Collections.shuffle(chouqulist);
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < (Math.min(chouqulist.size(), wordNum)); i++) {
                str.append(chouqulist.get(i));
                chouqubufenlist.add(chouqulist.get(i));
            }
            Article.getArticleSingleton().setArticle(str.toString());
            Article.getArticleSingleton().addParagraph();
            Replay.start();
            ShareArticle.start();
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println(e.getActionCommand());
        switch (e.getActionCommand()) {
            case "下一段":
                if (TypingState.sendArticle == Constant.SEND_ORDER) {
                    nextOrder();
                } else if (TypingState.sendArticle == Constant.SEND_EXTRACT) {
                    chouqu(e.getActionCommand());
                } else if (TypingState.sendArticle == Constant.SEND_WORDS) {
                    ciKuNext();
                }
                break;
            case "顺序模式发文":
                sendOrder();
                break;
            case "抽取模式发文":
                chouqu(e.getActionCommand());
                break;
            case "词库练习":
                System.out.println(e.getActionCommand() + "!!!");
                ciKu();
                break;
            case "保存进度":
                save();
                break;
            case "发送全文":
                sendAll();
                break;
        }
        SwingSingleton.TypingText().requestFocusInWindow();
    }
    public void sendAll() {
        Article article = Article.getArticleSingleton();
        article.setArticle(all);//设置跟打内容
        if (article.getArticle() == null
                || article.getArticle().equals(""))
            return;
        Replay.start();
        TypingState.sendArticle = 1; // 顺序发文标志
        article.setParagraph(1);//设置段号
        fontweizhi += fontnum;
        SwingSingleton.SendArticleLabel().setVisible(true);
        SendArticleDialog.getInstance().setVisible(false);
        ShareArticle.start();
    }
    public static String randomCommon(String wen, int n) {
        if (wen == null)
            return null;
        int min = 0;
        int max = wen.length();
        char[] c = wen.toCharArray();
        StringBuilder resultstr = new StringBuilder();
        if (n > (max - min + 1) || max < min) {
            return null;
        }
        int[] result = new int[n];
        int count = 0;
        while (count < n) {
            int num = (int) (Math.random() * (max - min)) + min;
            boolean flag = true;
            for (int j = 0; j < n; j++) {
                if (num == result[j]) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                result[count] = num;
                count++;
            }
        }
        for (int value : result) resultstr.append(c[value]);
        return resultstr.toString();
    }
}