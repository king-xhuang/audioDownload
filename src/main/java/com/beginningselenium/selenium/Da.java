package com.beginningselenium.selenium;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static java.awt.event.KeyEvent.VK_CONTROL;
import static java.util.concurrent.TimeUnit.*;

public class Da {
    static int count = 0;
    static final String prefix = "安娜·卡列尼娜_";
    static final String suffix = ".mp3";
    static final String path = "D:/用户目录/下载"; //g:/emule/audio/ana"; //
    //static final String path = "g:\\emule\\audio\\ana";
    public static void main(String[] args) {
        test();

        List<String> failedUrl = new ArrayList<String>();
        int index = 53071;
        int start = 108;
        int count = 1;
        if (args.length == 2){
            start = Integer.getInteger(args[0]);
            count = Integer.getInteger(args[1]);
        }
        for(int i = start; i < (start + count); i++){
            boolean s = false;
            int tt = 0;
            String url = "http://www.pingshu8.com/down_" + (index + i) + ".html";
            while(!s) {
                tt++;
                print("download link " + url + " for " + tt + " try");
                s = download(url, i);
                if (!s && tt >= 4){
                    failedUrl.add(url);
                    break;
                }
                sleepInSecond(10);
            }
        }
        if (failedUrl.size() > 0){
            print("following urls are failed");
            for (String url: failedUrl){
                print(url);
            }
        }


        //test();
    }

    private static boolean download(String url, int fileIndex)   {
        boolean ok = true;
        print("start page at url: " + url);
        WebDriver driver = getChromeDriver(); // getFirefoxDriver(); //getChromeDriver();

        driver.manage().window().setSize(new Dimension(800, 500));
        driver.get(url);
        System.out.println("loaded");
        String p = driver.getWindowHandle();
        System.out.println("w:" + p);

        new WebDriverWait(driver, 200).until(new ExpectedCondition<Boolean>(){

             public Boolean apply(WebDriver webDriver) {
                 return ((JavascriptExecutor) webDriver).executeScript("return document.readyState").toString().equals("complete");
             }
         });
        sleepInSecond(5);


        //wait.until( d -> {  ((JavascriptExecutor) d).executeScript("return document.readyState").toString().equals("complete"); } );
//        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//img[@src='/img/dl3.gif']")));


        WebElement img = driver.findElement(By.xpath("//img[@src='/img/dl3.gif']"));
        print("download img" + img.toString());
        img.click();

        int count = 0;
        boolean audioWindow = false;
        while(count++ < 10 ){
            sleepInSecond(2);
            if (switchToAudioPage(driver, p)){
                audioWindow = true;
                break;
            }
        }// end of while loop, wait for audio page
        if(!audioWindow){
            ok = false;
            print("#### no audio window for file " + fileIndex + " ####");

        }else{
            ok = doAudioPage(driver);
            if (!ok) print("#### failed  download for " + fileIndex + " ####");
        }
        if (!ok){
            System.out.println("#### no audio window for file " + fileIndex + " ####");
        }
        driver.quit();
        return ok;
    }
    private static void test(){
        print("user dir: " + System.getProperty("user.dir"));
        //        https://down01.pingshu8.com:8011/2/ys/%E5%AE%89%E5%A8%9C%C2%B7%E5%8D%A1%E5%88%97%E5%B0%BC%E5%A8%9C/%E5%AE%89%E5%A8%9C%C2%B7%E5%8D%A1%E5%88%97%E5%B0%BC%E5%A8%9C_001.mp3?t=3xckza421f0da7a069b3419369138bd116184&m=5CB9E91E";
//
//        String fi = getFileIndex(u);
//        print("file index: " + fi);
        String f = "安娜·卡列尼娜_001.mp3";
        waitForDownload(f);
        if(fileExist(f)) print( f + " exist");
    }
    private static ChromeDriver getChromeDriver( ){
//        String downloadFilepath = path;
//        HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
//        chromePrefs.put("profile.default_content_settings.popups", 0);
//        chromePrefs.put("download.default_directory", downloadFilepath);
//        ChromeOptions options = new ChromeOptions();
//        options.setExperimentalOption("prefs", chromePrefs);
//        return new ChromeDriver(options);
        String downloadFilepath =  path;
        HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
        chromePrefs.put("profile.default_content_settings.popups", 0);
        chromePrefs.put("download.default_directory", downloadFilepath);
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("prefs", chromePrefs);
        DesiredCapabilities cap = DesiredCapabilities.chrome();
        cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        cap.setCapability(ChromeOptions.CAPABILITY, options);
        return new ChromeDriver(cap);

    }
    private static void print(String s){
        System.out.println(s);
    }

    private static String getFileIndex (String url){
        int i = url.indexOf("mp3?");
        if (i > 10){
            return url.substring(i-4, i-1);
        }else return "";

    }
    private static boolean fileExist(String fi) {

        String fp = path + "/" + fi;
        File file = new File(fp);
        if(file.exists()) return true;
        else{ // continie wait
            int waitTime = 0;
            while(true){
                waitTime++;
                if(waitTime > 180) return false;
                try {
                    Thread.sleep(1000);
                    print("wait time =" + waitTime);
                    if(file.exists()){

                        return true;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private static boolean waitForDownload(String fi){

        final String f = fi;
        String fp = path + "\\" + fi;
        File file = new File(fp);
        if(file.exists()){
            print(file.getPath() + " exists");
            long fileSize = file.length();
            int equalCount = 0;
            int waitTime = 0;

            while(true){
                waitTime++;
                if (file.length() == fileSize ){
                    equalCount++;
                    if(equalCount > 3){
                        print("wait time " + (waitTime - 3));
                        break;
                    }

                }else{
                    fileSize = file.length();
                    print("fileSize: " +  fileSize);
                    equalCount = 0;
                    try {
                        SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            return true;
        }else return false;
    }
    private static FirefoxDriver getFirefoxDriver(){
        FirefoxOptions options = new FirefoxOptions();
        FirefoxProfile pf = new FirefoxProfile();
        pf.setPreference("browser.download.folderList", 2);
        pf.setPreference("browser.download.dir", path);
        pf.setPreference("browser.download.useDownloadDir", true);
        pf.setPreference("browser.helperApps.neverAsk.saveToDisk", "audio/mpeg");
        options.setProfile(pf);
        return new FirefoxDriver( options );
    }

    private static boolean switchToAudioPage(WebDriver driver, String pWinHandle){
        Set<String> handles = driver.getWindowHandles();
        if (handles.size() < 2){
            print("wait for audio page");
        }
        else{
            print("window count: " + handles.size());
            for (String h: handles){
                if (!h.equalsIgnoreCase(pWinHandle)){
                    driver.switchTo().window(h);
                    return true;
                }
            } //end of for loop

        }

        return false;
    }
    private static boolean doAudioPage(WebDriver driver) {

        boolean getVideo = true;
//        new WebDriverWait(driver, 20).until(new ExpectedCondition<Boolean>(){
//
//            public Boolean apply(WebDriver webDriver) {
//                print("audio page ready");
//                return ((JavascriptExecutor) webDriver).executeScript("return document.readyState").toString().equals("complete");
//            }
//        });
        sleepInSecond(5);
        try {
            driver.findElement(By.tagName("video"));
        }catch(Exception e){
            getVideo = false;
            print("cannot find video tag");
            e.printStackTrace();
        }finally {

        }
        if (!getVideo) return false;
        String Durl = driver.getCurrentUrl();
        if(Durl.indexOf("mp3") < 0 ){
            // get wrong page, skip to next
            print("cannot load audio page for : ");
            return false;
        }
        System.out.println("url:" + Durl);
        String file = getFileIndex(Durl);
        System.out.println("#### found audio window for file " + file + " #### after " + count*2 + " '");


//                       Actions action = new Actions(driver);
//                       WebElement video = driver.findElement(By.tagName("video"));
//                       action.contextClick(video).sendKeys(Keys.ARROW_DOWN)
//                               .sendKeys(Keys.ARROW_DOWN)
//                               .sendKeys(Keys.RETURN).perform();
        Robot robot = null;
        try {
            robot = new Robot();
        } catch (AWTException e1) {
            e1.printStackTrace();
        }
        // PRESSS short cut keys CTRL_S to save as audio
        robot.keyPress(VK_CONTROL);
        robot.keyPress(KeyEvent.VK_S);
        robot.keyRelease(KeyEvent.VK_S);
        robot.keyRelease(VK_CONTROL);
        sleepInSecond(1);
        //
        long delay = 1000;
        try {
            Thread.sleep(delay);  // sleep has only been used to showcase each event separately
            robot.keyPress(KeyEvent.VK_TAB);
            Thread.sleep(delay);
            robot.keyPress(KeyEvent.VK_TAB);
            Thread.sleep(delay);
            robot.keyPress(KeyEvent.VK_TAB);
            Thread.sleep(delay);
            robot.keyPress(KeyEvent.VK_ENTER);
            //Thread.sleep(5000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        boolean s = fileExist(prefix + file + suffix );
        print("download: " + prefix + file + suffix + " " + s);
        return s;
    }
    static void sleepInSecond(long s){
        try {
            Thread.sleep(s*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

