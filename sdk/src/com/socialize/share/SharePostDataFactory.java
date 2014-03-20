package com.socialize.share;

import com.socialize.ShareUtils;
import com.socialize.api.ShareMessageBuilder;
import com.socialize.entity.Entity;
import com.socialize.entity.PropagationInfo;
import com.socialize.networks.DefaultPostData;
import com.socialize.networks.PostData;

import java.util.HashMap;

/**
 * @author Jason Polites
 */
public class SharePostDataFactory {

    private ShareMessageBuilder shareMessageBuilder;

    public PostData create(Entity entity, PropagationInfo info, String text, boolean html, boolean includeAppLink) {
        HashMap<String, Object> postValues = new HashMap<String, Object>();

        postValues.put(ShareUtils.EXTRA_TITLE, "Share");
        postValues.put(ShareUtils.EXTRA_SUBJECT, shareMessageBuilder.buildShareSubject(entity));
        postValues.put(ShareUtils.EXTRA_TEXT, shareMessageBuilder.buildShareMessage(entity, info, text, html, includeAppLink));

        DefaultPostData postData = new DefaultPostData();

        postData.setEntity(entity);
        postData.setPropagationInfo(info);
        postData.setPostValues(postValues);

        return postData;
    }

    public void setShareMessageBuilder(ShareMessageBuilder shareMessageBuilder) {
        this.shareMessageBuilder = shareMessageBuilder;
    }
}
