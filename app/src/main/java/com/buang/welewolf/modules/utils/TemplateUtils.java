package com.buang.welewolf.modules.utils;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.buang.welewolf.base.http.services.OnlineServices;
import com.buang.welewolf.base.utils.DirContext;
import com.buang.welewolf.base.utils.FileUtils;
import com.buang.welewolf.base.utils.PreferencesController;
import com.buang.welewolf.base.utils.ZipUtils;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.network.utils.HttpUtils;
import com.buang.welewolf.base.bean.OnlineTemplateInfo;

import java.io.File;

/**
 * Created by weilei on 16/5/5.
 */
public class TemplateUtils {

    private static final String PREF_TEMPLATE_URL_MD5 = "prefs_template_url_md5";
    private static final String ASSETS_FILEPATH = DirContext.getTemplateFile() + File.separator + "assets.zip";
    public static String RESOURCE_URL_BUNDLE_JS = "";
    public static String RESOURCE_URL_LLKT_CSS = "";

    public TemplateUtils() {
    }

    public static String clearExtraLlktCss(String template) {
        template = template.replace("<link href=\""+RESOURCE_URL_LLKT_CSS+"\" rel=\"stylesheet\" />", "");
        return template;
    }

    public static String replaceLlktCss(String template) {
        if(TextUtils.isEmpty(TemplateUtils.RESOURCE_URL_LLKT_CSS)) {
            template = template.replace("${llkt_css_url}", "<link type=\"text/css\" rel=\"stylesheet\" href=\"css/llkt.css\"/>");
        }else {
            template = template.replace("${llkt_css_url}", "<link type=\"text/css\" rel=\"stylesheet\" href=\""+TemplateUtils.RESOURCE_URL_LLKT_CSS+"\"/>");
        }
        return template;
    }

    public static String replaceBundleJs(String template) {
        if(TextUtils.isEmpty(TemplateUtils.RESOURCE_URL_BUNDLE_JS)) {
            template = template.replace("${bundle_js_url}", "<script type=\"text/javascript\" charset=\"utf-8\" src=\"js/bundle.js\"></script>\n");
        }else {
            template = template.replace("${bundle_js_url}", "<script type=\"text/javascript\" charset=\"utf-8\" src=\""+TemplateUtils.RESOURCE_URL_BUNDLE_JS+"\"></script>\n");
        }
        return template;
    }

    public static String replaceAudioIcon(String template){
        template=template.replace("audio icon-0","audio-default");
        return template;
    }

    /**
     * 更新模板
     */
    public void loadOnlineTemplate() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params) {
                String url = OnlineServices.getOnlineTemplateUrl();
                OnlineTemplateInfo info = new DataAcquirer<OnlineTemplateInfo>().get(url, new OnlineTemplateInfo());
                if (info != null && info.isAvailable()) {
                    String oldUrl = PreferencesController.getStringValue(PREF_TEMPLATE_URL_MD5);
                    RESOURCE_URL_BUNDLE_JS = info.mJsUrl;
                    RESOURCE_URL_LLKT_CSS = info.mCssUrl;
                    if ((TextUtils.isEmpty(oldUrl) || !oldUrl.equals(info.urlMD5)) && !TextUtils.isEmpty(info.urlMD5)) {
                        if (HttpUtils.storeFile(info.mTempleUrl, ASSETS_FILEPATH)) {
                            File file = new File(ASSETS_FILEPATH);
                            if (file.exists()) {
                                try {
                                    ZipUtils.unzipFolder(file.getAbsolutePath(), DirContext.getTemplateFile().getAbsolutePath());
                                    PreferencesController.setStringValue(PREF_TEMPLATE_URL_MD5, info.urlMD5);
                                    FileUtils.deleteFile(file.getAbsolutePath());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    FileUtils.deleteFile(DirContext.getTemplateFile().getAbsolutePath());
                                    PreferencesController.setStringValue(PREF_TEMPLATE_URL_MD5, "");
                                }
                            }
                        }
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
            }
        }.execute();
    }

    /**
     * 获取模板文件路径
     * @param fileName
     * @return
     */
    public String getTemplatePath(String fileName) {
        File templateFile = DirContext.getTemplateFile();
        if (templateFile == null || !templateFile.exists()) {
            return "";
        }
        String[] lists = templateFile.list();
        String assets = null;
        for (int i = 0; i < lists.length; i++) {
            File file = new File(DirContext.getTemplateFile().getAbsolutePath() + File.separator + lists[i]);
            if (file.isDirectory()) {
                assets = DirContext.getTemplateFile().getAbsolutePath() + File.separator + lists[i];
                break;
            }
        }
        if (!TextUtils.isEmpty(assets)) {
            return assets + File.separator + fileName;
        }
        return "";
    }

}
