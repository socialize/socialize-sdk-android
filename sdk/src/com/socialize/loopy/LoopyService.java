package com.socialize.loopy;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import com.sharethis.loopy.sdk.Item;
import com.sharethis.loopy.sdk.Loopy;
import com.sharethis.loopy.sdk.ShareCallback;
import com.sharethis.loopy.sdk.ShareDialogListener;
import com.sharethis.loopy.sdk.util.StringUtils;
import com.socialize.ShareUtils;
import com.socialize.api.action.ShareType;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.DefaultPropagationInfo;
import com.socialize.entity.Entity;
import com.socialize.entity.PropagationInfo;
import com.socialize.networks.PostData;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.share.SharePostDataFactory;

/**
 * Provides integration to the loopy analytics service.
 * @author Jason Polites
 */
public class LoopyService {

    private SocializeConfig config;
    private SharePostDataFactory sharePostDataFactory;

    public boolean isLoopyEnabled() {
        return config.isLoopyEnabled();
    }

    public SocializeConfig getConfig() {
        return config;
    }

    // Set by DI
    @SuppressWarnings("unused")
    public void setConfig(SocializeConfig config) {
        this.config = config;
    }

    // Set by DI
    @SuppressWarnings("unused")
    public void setSharePostDataFactory(SharePostDataFactory sharePostDataFactory) {
        this.sharePostDataFactory = sharePostDataFactory;
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

    public PropagationInfo setShortlinks(PropagationInfo propagationInfo, Item item, Throwable error) {
        PropagationInfo infoInUse = propagationInfo;

        if(error == null && !StringUtils.isEmpty(item.getShortlink())) {

            // Replace the entity url with the loopy shortlink
            DefaultPropagationInfo dpi = new DefaultPropagationInfo();
            dpi.setAppUrl(infoInUse.getAppUrl());
            dpi.setEntityUrl(item.getShortlink());

            infoInUse = dpi;
        }

        return infoInUse;
    }

    public void getTrackableUrl(Entity entity, ShareType shareType, String url, ShareCallback callback) {
        Item item = toItem(entity, url);
        Loopy.shareAndLink(item, shareType.getDisplayName(), callback);
    }

    public void showShareDialog(final Activity context, String title, final String text, final Entity entity, final PropagationInfo info, final SocialNetworkListener listener) {
        Item item = toItem(entity, info.getEntityUrl());
        Intent intent = new Intent(Intent.ACTION_SEND);
        Loopy.showShareDialog(context, title, item, intent, new ShareDialogListener() {

            boolean shareCancelled = false;

            @Override
            public void onLinkGenerated(Item item, Intent shareIntent, Throwable error) {

                PropagationInfo loopyInfo = setShortlinks(info, item, error);

                PostData postData = sharePostDataFactory.create(entity, loopyInfo, text, false, true);

                if(listener != null) {
                    shareCancelled = listener.onBeforePost(context, null, postData);
                }

                String title = String.valueOf(postData.getPostValues().get(ShareUtils.EXTRA_TITLE));
                String body = String.valueOf(postData.getPostValues().get(ShareUtils.EXTRA_TEXT));
                String subject = String.valueOf(postData.getPostValues().get(ShareUtils.EXTRA_SUBJECT));

                shareIntent.putExtra(Intent.EXTRA_TITLE, title);
                shareIntent.putExtra(Intent.EXTRA_TEXT, body);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            }

            @Override
            public void onShow(DialogInterface dialog) {

                if(listener != null) {
                    listener.onAfterPost(context, null, null);
                }

                super.onShow(dialog);
            }
        });
    }

    Item toItem(Entity entity, String url) {
        Item item = new Item();
        item.setUrl(url);
        item.setTitle(entity.getDisplayName());
        item.setType(entity.getType());
        return item;
    }
}
