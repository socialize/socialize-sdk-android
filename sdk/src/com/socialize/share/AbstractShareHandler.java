/*
 * Copyright (c) 2012 Socialize Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.socialize.share;

import android.app.Activity;
import android.location.Location;
import com.sharethis.loopy.sdk.Item;
import com.sharethis.loopy.sdk.ShareCallback;
import com.sharethis.loopy.sdk.util.StringUtils;
import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.api.action.ShareType;
import com.socialize.entity.DefaultPropagationInfo;
import com.socialize.entity.PropagationInfo;
import com.socialize.entity.PropagationInfoResponse;
import com.socialize.entity.SocializeAction;
import com.socialize.error.SocializeException;
import com.socialize.log.SocializeLogger;
import com.socialize.loopy.LoopyService;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;


/**
 * @author Jason Polites
 *
 */
public abstract class AbstractShareHandler implements ShareHandler {

    protected SocializeLogger logger;
    protected LoopyService loopyService;

	/* (non-Javadoc)
	 * @see com.socialize.share.ShareHandler#handle(android.app.Activity, com.socialize.entity.Share, com.socialize.share.ShareHandlerListener)
	 */
	@Override
	public void handle(final Activity context, final SocializeAction action, Location location, final String text, final SocialNetworkListener listener) {
        ShareType shareType = getShareType();
        final SocialNetwork network = SocialNetwork.valueOf(shareType);
		PropagationInfoResponse propagationInfoResponse = action.getPropagationInfoResponse();
		
		if(propagationInfoResponse != null) {
			final PropagationInfo propagationInfo = propagationInfoResponse.getPropagationInfo(shareType);
			
			if(propagationInfo != null) {
				try {
                    if(loopyService.isLoopyEnabled()) {

                        if(shareType.equals(ShareType.OTHER)) {
                            loopyService.showShareDialog(context, "", text, action.getEntity(), propagationInfo, listener);
                        } else {
                            loopyService.getTrackableUrl(action.getEntity(), shareType, propagationInfo.getEntityUrl(), new ShareCallback() {
                                @Override
                                public void onResult(Item item, Throwable error) {

                                    PropagationInfo infoInUse = loopyService.setShortlinks(propagationInfo, item, error);

                                    try {
                                        handle(context, action, text, infoInUse, listener);
                                    } catch (Exception e) {
                                        if(logger != null) {
                                            logger.error("Error handling share", e);
                                        }

                                        if(listener != null) {
                                            listener.onNetworkError(context, network, e);
                                        }
                                    }
                                }
                            });
                        }
                    } else {
                        handle(context, action, text, propagationInfo, listener);
                    }
				}
				catch (Exception e) {
					if(logger != null) {
						logger.error("Error handling share", e);
					}

					if(listener != null) {
						listener.onNetworkError(context, network, e);
					}
				}
			}
			else {
				logError(context, network, "No propagation info found for type [" +
                        shareType +
								"].  Share will not propagate", listener);
			}
		}
		else {
			logError(context, network, "No propagation info found for type [" +
                    shareType +
							"].  Share will not propagate", listener);
		}
	}

	protected void logError(Activity context, SocialNetwork network, String msg, SocialNetworkListener listener) {
		if(logger != null) {
			logger.warn(msg);
		}
		else {
			System.err.println(msg);
		}
		
		if(listener != null) {
			listener.onNetworkError(context, network, new SocializeException(msg));
		}
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}

    @SuppressWarnings("unused")
    public void setLoopyService(LoopyService loopyService) {
        this.loopyService = loopyService;
    }

    protected abstract void handle(Activity context, SocializeAction action, String text, PropagationInfo info, SocialNetworkListener listener) throws Exception;
	
	protected abstract ShareType getShareType();
	
	protected SocializeService getSocialize() {
		return Socialize.getSocialize();
	}
}
