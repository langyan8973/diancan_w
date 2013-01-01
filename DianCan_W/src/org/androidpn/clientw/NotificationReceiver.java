/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.androidpn.clientw;

import java.io.IOException;
import java.util.UUID;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import com.declarew.Declare_w;
import com.diancanw.ServicePage;
import com.diancanw.OrderPage;
import com.httpw.HttpDownloader;
import com.modelw.MessContent;
import com.modelw.Order;
import com.modelw.ServiceMess;
import com.utilsw.JsonUtils;
import com.utilsw.MenuUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/** 
 * Broadcast receiver that handles push notification messages from the server.
 * This should be registered as receiver in AndroidManifest.xml. 
 * 
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public final class NotificationReceiver extends BroadcastReceiver {

    private static final String LOGTAG = LogUtil
            .makeLogTag(NotificationReceiver.class);

    //    private NotificationService notificationService;

    public NotificationReceiver() {
    }

    //    public NotificationReceiver(NotificationService notificationService) {
    //        this.notificationService = notificationService;
    //    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOGTAG, "NotificationReceiver.onReceive()...");
        String action = intent.getAction();
        Log.d(LOGTAG, "action=" + action);

        if (Constants.ACTION_SHOW_NOTIFICATION.equals(action)) {
            String notificationId = intent
                    .getStringExtra(Constants.NOTIFICATION_ID);
            String notificationApiKey = intent
                    .getStringExtra(Constants.NOTIFICATION_API_KEY);
            String notificationTitle = intent
                    .getStringExtra(Constants.NOTIFICATION_TITLE);
            String notificationMessage = intent
                    .getStringExtra(Constants.NOTIFICATION_MESSAGE);
            String notificationUri = intent
                    .getStringExtra(Constants.NOTIFICATION_URI);

            Log.d(LOGTAG, "notificationId=" + notificationId);
            Log.d(LOGTAG, "notificationApiKey=" + notificationApiKey);
            Log.d(LOGTAG, "notificationTitle=" + notificationTitle);
            Log.d(LOGTAG, "notificationMessage=" + notificationMessage);
            Log.d(LOGTAG, "notificationUri=" + notificationUri);

            if(notificationTitle.equals("11"))
            {
            	return;
            	
            }
            else if(notificationTitle.equals("21")||notificationTitle.equals("22")||notificationTitle.equals("23"))
            {
            	Declare_w declare_w=(Declare_w)context.getApplicationContext();
            	
            	if(declare_w.isMessPage)
            	{
            		return;
            	}
            	else {
            		UUID uuid = UUID.randomUUID();
           			ServiceMess Mess=new ServiceMess();
           			Mess.setId(uuid.toString());
           			
            		String sMess="";
            		String sTitle="";
            		MessContent content=MenuUtils.getMessContentByjs(notificationMessage);
            		if(notificationTitle.equals("21"))
                    {
            		   sTitle=MenuUtils.Service_2;
                 	   sMess=content.getName()+"请求"+MenuUtils.Service_2;                	   
                    }
                    else if(notificationTitle.equals("22"))
                    {
                    	sTitle=MenuUtils.Service_1;
                 	   sMess=content.getName()+"请求"+MenuUtils.Service_1;
                    }
                    else if(notificationTitle.equals("23"))
                    {
                    	sTitle=MenuUtils.Service_3;
                 	   sMess=content.getName()+MenuUtils.Service_3;
                    }
            		Mess.setsText(sMess);
            		Mess.setComplete(false);
           		    Mess.setMessReturned("已收到");
           		    declare_w.messArrayList.add(Mess);
            		
            		Notifier notifier = new Notifier(context);
                    notifier.notify(notificationId, notificationApiKey,
                            sTitle, sMess, notificationUri,ServicePage.class);
				}
            }
            else {
            	MessContent content=MenuUtils.getMessContentByjs(notificationMessage);
//            	try {
//					String jsString=HttpDownloader.GetOrderForm(MenuUtils.initUrl+ "orders/"+content.getOid());
//				} catch (ClientProtocolException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
            	Notifier notifier = new Notifier(context,content.getOid());
                notifier.notify(notificationId, notificationApiKey,
                        notificationTitle, content.getName()+"要求结账", notificationUri,OrderPage.class);
			}
            
        }

        //        } else if (Constants.ACTION_NOTIFICATION_CLICKED.equals(action)) {
        //            String notificationId = intent
        //                    .getStringExtra(Constants.NOTIFICATION_ID);
        //            String notificationApiKey = intent
        //                    .getStringExtra(Constants.NOTIFICATION_API_KEY);
        //            String notificationTitle = intent
        //                    .getStringExtra(Constants.NOTIFICATION_TITLE);
        //            String notificationMessage = intent
        //                    .getStringExtra(Constants.NOTIFICATION_MESSAGE);
        //            String notificationUri = intent
        //                    .getStringExtra(Constants.NOTIFICATION_URI);
        //
        //            Log.e(LOGTAG, "notificationId=" + notificationId);
        //            Log.e(LOGTAG, "notificationApiKey=" + notificationApiKey);
        //            Log.e(LOGTAG, "notificationTitle=" + notificationTitle);
        //            Log.e(LOGTAG, "notificationMessage=" + notificationMessage);
        //            Log.e(LOGTAG, "notificationUri=" + notificationUri);
        //
        //            Intent detailsIntent = new Intent();
        //            detailsIntent.setClass(context, NotificationDetailsActivity.class);
        //            detailsIntent.putExtras(intent.getExtras());
        //            //            detailsIntent.putExtra(Constants.NOTIFICATION_ID, notificationId);
        //            //            detailsIntent.putExtra(Constants.NOTIFICATION_API_KEY, notificationApiKey);
        //            //            detailsIntent.putExtra(Constants.NOTIFICATION_TITLE, notificationTitle);
        //            //            detailsIntent.putExtra(Constants.NOTIFICATION_MESSAGE, notificationMessage);
        //            //            detailsIntent.putExtra(Constants.NOTIFICATION_URI, notificationUri);
        //            detailsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //            detailsIntent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        //
        //            try {
        //                context.startActivity(detailsIntent);
        //            } catch (ActivityNotFoundException e) {
        //                Toast toast = Toast.makeText(context,
        //                        "No app found to handle this request",
        //                        Toast.LENGTH_LONG);
        //                toast.show();
        //            }
        //
        //        } else if (Constants.ACTION_NOTIFICATION_CLEARED.equals(action)) {
        //            //
        //        }

    }

}
