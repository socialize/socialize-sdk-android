package com.socialize.loopy;

import android.app.Activity;
import com.sharethis.loopy.sdk.Item;
import com.sharethis.loopy.sdk.Loopy;
import com.sharethis.loopy.sdk.ShareCallback;
import com.socialize.api.action.ShareType;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;

/**
 * Provides integration to the loopy analytics service.
 * @author Jason Polites
 */
public class LoopyService {

    private SocializeConfig config;

    public boolean isLoopyEnabled() {
        return config.isLoopyEnabled();
    }

    public SocializeConfig getConfig() {
        return config;
    }

    public void setConfig(SocializeConfig config) {
        this.config = config;
    }

    public void onStop(Activity context) {
        if(config.isLoopyEnabled()) {
            Loopy.onStop(context);
        }
    }

    public void onStart(Activity context) {
        if(config.isLoopyEnabled()) {
            Loopy.onStart(context);
        }
    }

    public void onCreate(Activity context) {
        if(config.isLoopyEnabled()) {
            Loopy.onCreate(context, config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_KEY), config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_SECRET));
        }
    }

    public void onDestroy(Activity context) {
        if(config.isLoopyEnabled()) {
            Loopy.onDestroy(context);
        }
    }

    public void getTrackableUrl(Entity entity, ShareType shareType, String url, ShareCallback callback) {
        Item item = new Item();
        item.setUrl(url);
        item.setTitle(entity.getDisplayName());
        item.setType(entity.getType());
        Loopy.shareAndLink(item, shareType.getDisplayName(), callback);
    }
}
