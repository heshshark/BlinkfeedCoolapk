package ce.hesh.blinkfeedcoolapk.util;

import org.aisen.android.common.setting.Setting;
import org.aisen.android.network.biz.ABizLogic;
import org.aisen.android.network.http.HttpConfig;
import org.aisen.android.network.http.IHttpUtility;
import org.aisen.android.network.http.Params;
import org.aisen.android.network.task.TaskException;

import ce.hesh.blinkfeedcoolapk.bean.PictureSize;

public class SDK extends ABizLogic {

    private SDK() {

    }

    private SDK(CacheMode mode) {
        super((mode));
    }

    public static SDK newInstance() {
        return new SDK();
    }

    public static SDK newInstance(CacheMode cacheMode) {
        return new SDK(cacheMode);
    }

    @Override
    protected HttpConfig configHttpConfig() {
        HttpConfig httpConfig = new HttpConfig();
        return httpConfig;
    }

    @Override
    protected IHttpUtility configHttpUtility() {
        return super.configHttpUtility();
    }

    /**
     * 获取图片大小
     *
     * @param url
     * @return
     * @throws TaskException
     */
    public PictureSize getPictureSize(String url) throws TaskException {
        Setting action = newSetting("getPictureSize", "", "读取图片的尺寸");

        action.getExtras().put(HTTP_UTILITY, newSettingExtra(HTTP_UTILITY, PictureSizeHttpUtility.class.getName(), "获取图片尺寸的HttpUtility"));

        Params params = new Params();
        params.addParameter("path", url);

        return doGet(action, params, PictureSize.class);
    }


/*    public ArrayList<SavedImageBean> getSavedImages() throws TaskException {
        ArrayList<SavedImageBean> result = new ArrayList<>();

        File file = new File(SystemUtils.getSdcardPath() + File.separator + AppSettings.getImageSavePath() + File.separator);
        if (file.exists()) {
            for (File imageFile : file.listFiles()) {
                if (imageFile.isDirectory())
                    continue;

                SavedImageBean bean = new SavedImageBean();
                bean.setPath(imageFile.getAbsolutePath());
                result.add(bean);
            }
        }

        return result;
    }*/

}
