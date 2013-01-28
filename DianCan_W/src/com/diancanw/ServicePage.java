package com.diancanw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.diancanw.declare.DiancanwApp;
import com.diancanw.model.MessContent;
import com.diancanw.model.ServiceMess;
import com.diancanw.utils.MenuUtils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class ServicePage extends Activity {

	ListView messListView;
	ArrayList<ServiceMess> messages;
	ArrayList<HashMap<String, Object>> messHashMaps=new ArrayList<HashMap<String,Object>>();
	boolean isNew=false;
	DiancanwApp declare;
	MessageListAdapter messageListAdapter;
	NotifiReceiver receiver;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.servicepage);
		
		messListView=(ListView)findViewById(R.id.MessList);
		declare=(DiancanwApp)getApplicationContext();
		
		messages=declare.messArrayList;
		InitMessageList();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		declare.isMessPage=false;
		unregisterReceiver(receiver);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//注册一个广播接收器，启动餐桌抖动动画  
		declare.isMessPage=true;
	    receiver = new NotifiReceiver();
	    IntentFilter filter = new IntentFilter();
//	    filter.addAction(Constants.ACTION_SHOW_NOTIFICATION);
//        filter.addAction(Constants.ACTION_NOTIFICATION_CLICKED);
//        filter.addAction(Constants.ACTION_NOTIFICATION_CLEARED);
	    registerReceiver(receiver, filter);
	}

	private void InitMessageList() {
		for(int i=0;i<messages.size();i++)
		{
			ServiceMess sem=messages.get(i);
			HashMap<String, Object> map=new HashMap<String, Object>();
			map.put("id", sem.getId());
			map.put("content", sem.getsText());
			map.put("returnmess", sem.getMessReturned());
			String strCom="";
			if(sem.isComplete())
			{
				strCom="true";
			}
			else {
				strCom="false";
			}
			map.put("iscomplete", strCom);
			messHashMaps.add(map);
		}
		messageListAdapter=new MessageListAdapter(this, messHashMaps, 
				R.layout.messagelist_item, new String[]{"id","content","returnmess","iscomplete"}, 
				new int[]{R.id.messid,R.id.messtext,R.id.messreturn,R.id.isover});
		messListView.setAdapter(messageListAdapter);
	}
	
	class MessageListAdapter extends SimpleAdapter{

		private ArrayList<HashMap<String, Object>> mItemList;
		
		public ArrayList<HashMap<String, Object>> getmItemList() {
			return mItemList;
		}
		public void setmItemList(ArrayList<HashMap<String, Object>> hashList) {
			this.mItemList = hashList;
		}

		public MessageListAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
			// TODO Auto-generated constructor stub
			mItemList = (ArrayList<HashMap<String, Object>>) data;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			return super.getView(position, convertView, parent);
		}

		
		
	}
	class NotifiReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
//    		if (Constants.ACTION_SHOW_NOTIFICATION.equals(action)) {
//                String notificationId = intent
//                        .getStringExtra(Constants.NOTIFICATION_ID);
//                String notificationApiKey = intent
//                        .getStringExtra(Constants.NOTIFICATION_API_KEY);
//                String notificationTitle = intent
//                        .getStringExtra(Constants.NOTIFICATION_TITLE);
//                String notificationMessage = intent
//                        .getStringExtra(Constants.NOTIFICATION_MESSAGE);
//                String notificationUri = intent
//                        .getStringExtra(Constants.NOTIFICATION_URI);
//                
//                if(notificationTitle.equals("21")||notificationTitle.equals("22")||notificationTitle.equals("23"))
//                {
//                	UUID uuid = UUID.randomUUID();
//           			ServiceMess sMess=new ServiceMess();
//           			sMess.setId(uuid.toString());     			
//           			
//                   if(notificationTitle.equals("21"))
//                   {
//                	   MessContent content=MenuUtils.getMessContentByjs(notificationMessage);
//                	   
//                	   sMess.setsText(content.getName()+"请求"+MenuUtils.Service_2);
//                   }
//                   else if(notificationTitle.equals("22"))
//                   {
//                	   MessContent content=MenuUtils.getMessContentByjs(notificationMessage);
//                	   
//                	   sMess.setsText(content.getName()+"请求"+MenuUtils.Service_1);
//                   }
//                   else if(notificationTitle.equals("23"))
//                   {
//                	   MessContent content=MenuUtils.getMessContentByjs(notificationMessage);
//                	   sMess.setsText(content.getName()+MenuUtils.Service_3);
//                   }
//                   
//                   sMess.setComplete(false);
//          		   sMess.setMessReturned("已收到");
//          		   messages.add(sMess);
//          		   
//          		   HashMap<String, Object> mapnew=new HashMap<String, Object>();
// 				   mapnew.put("id", sMess.getId());
// 				   mapnew.put("content", sMess.getsText());
// 				   mapnew.put("returnmess", sMess.getMessReturned());
// 				   String strCom="";
// 				   if(sMess.isComplete())
// 				   {
// 				    	strCom="true";
// 				   }
// 				   else {
// 					    strCom="false";
// 				   }
// 				   mapnew.put("iscomplete", strCom);
// 				   messHashMaps.add(mapnew);
// 				   messageListAdapter.notifyDataSetChanged();
//                }
//                
//    		}
		}
		
	}
}
