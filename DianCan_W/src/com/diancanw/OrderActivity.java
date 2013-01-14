package com.diancanw;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.client.ClientProtocolException;

import com.customw.ImageDownloader;
import com.declarew.Declare_w;
import com.httpw.HttpDownloader;
import com.modelw.Order;
import com.modelw.OrderItem;
import com.utilsw.DisplayUtil;
import com.utilsw.JsonUtils;
import com.utilsw.MenuUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class OrderActivity extends Activity {

	ListView orderListView;
	Button flashButton;
	Button overButton;
	Button cacelButton;
	TextView sumTextView;
	TextView titleTextView;
	ProgressBar mProgressBar;
	String sumString;
	Order mOrder;
	public int totalCount=0;
	public double totalPrice=0;
	NotifiReceiver receiver;
	Declare_w m_Declare;
	HashMap<String, List<OrderItem>> hashOrderItems;
	int Flag;
	
	private List<Map<String, Object>> itemlist = new ArrayList<Map<String, Object>>();  
	private List<Map<String, Object>> tagList = new ArrayList<Map<String, Object>>();
	
	private Handler httpHandler = new Handler() {  
        public void handleMessage (Message msg) {//此方法在ui线程运行   
            switch(msg.what) {  
            case 0: 
            	String errString=msg.obj.toString();
            	ShowError(errString);
                break;   
            case 1:
            	UpdateElement();
                break;  
            case 2:
            	ClosePage();
            	break;
            case 3:
            	String strJs=msg.obj.toString();
            	UpdateListView(strJs);
            	break;
            case 4:
            	String strJs1=msg.obj.toString();
            	break;
            }  
        }  
    }; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.orderactivity);
		
		m_Declare=(Declare_w)this.getApplicationContext();
		
		sumString=getResources().getString(R.string.infostr_sum);
		orderListView=(ListView)findViewById(R.id.orderList);
		sumTextView=(TextView)findViewById(R.id.sumText);
		sumTextView.setTextColor(Color.WHITE);
		sumTextView.setTextSize(DisplayUtil.dip2px(16));	
		
		titleTextView=(TextView)findViewById(R.id.deskTitle);
		titleTextView.setTextColor(Color.WHITE);
		flashButton=(Button)findViewById(R.id.BtnFlash);
		flashButton.setOnClickListener(new FlashOnClick());
		
		overButton=(Button)findViewById(R.id.overBtn);
		overButton.setOnClickListener(new overClick());
		cacelButton=(Button)findViewById(R.id.cancelBtn);
		cacelButton.setOnClickListener(new cancelClick());
		
		mProgressBar=(ProgressBar)findViewById(R.id.oprogress);
		mProgressBar.setVisibility(View.INVISIBLE);
		Intent intent=getIntent();
		Flag=intent.getFlags();
		mOrder=(Order)intent.getSerializableExtra("order");
		
		titleTextView.setText(mOrder.getDesk().getName()+"("+mOrder.getCode()+")");
		hashOrderItems=new HashMap<String, List<OrderItem>>();
    	if(mOrder!=null)
    	{
    		InitHashOrderItems();
    		InitListDatasource();
    		OrderAdapter listAdapter=new OrderAdapter(this, itemlist, tagList);
    		orderListView.setAdapter(listAdapter);
    		sumTextView.setText(sumString+totalPrice);
//    		if(mOrder.getStatus()==3)
//    		{
//    			overButton.setVisibility(View.VISIBLE);
//    		}
//    		else{
//    			overButton.setVisibility(View.GONE);
//    		}
    	}
    	
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		//注册一个广播接收器
		super.onResume();
	    receiver = new NotifiReceiver();
	    IntentFilter filter = new IntentFilter();
	    filter.addAction("diancan");
	    filter.addCategory(Intent.CATEGORY_DEFAULT);
	    registerReceiver(receiver, filter);
    	
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unregisterReceiver(receiver);
	}
	
	/**
	 * 显示错误信息
	 * @param strMess
	 */
	public void ShowError(String strMess) {
		mProgressBar.setVisibility(View.INVISIBLE);
		Toast toast = Toast.makeText(OrderActivity.this, strMess, Toast.LENGTH_SHORT); 
        toast.show();
	}
	
	/***
     * 初始化订单
     */
    public void InitHashOrderItems()
    {
    	hashOrderItems.clear();
    	totalCount=0;
    	totalPrice=0;
    	Iterator<OrderItem> iterator;
    	for(iterator=mOrder.getOrderItems().iterator();iterator.hasNext();)
    	{
    		OrderItem orderItem=iterator.next();
    		String cnameString=m_Declare.hashTypes.get(orderItem.getRecipe().getCid().toString());
    		if(!hashOrderItems.containsKey(cnameString))
    		{
    			List<OrderItem> orderItems=new ArrayList<OrderItem>();
    			hashOrderItems.put(cnameString, orderItems);
    		}
    		List<OrderItem> oItems=hashOrderItems.get(cnameString);
    		oItems.add(orderItem);
    		totalCount+=orderItem.getCount();
    		totalPrice+=orderItem.getCount()*orderItem.getRecipe().getPrice();
    	}
    }
    
    /**
     * 初始化列表数据源
     */
    public void InitListDatasource(){
    	String strKey;
    	Set<String> meenum=hashOrderItems.keySet();
    	itemlist.clear();
    	tagList.clear();
    	Iterator<String> iterator;
		for (iterator = meenum.iterator(); iterator.hasNext();) {
			strKey = iterator.next();
			List<OrderItem> orderItemList=hashOrderItems.get(strKey);
			Map<String, Object> tagMap=new HashMap<String, Object>();
			tagMap.put("tagTitle", strKey);
			tagList.add(tagMap);
			itemlist.add(tagMap);
			
			Iterator<OrderItem> iterator2;
			for(iterator2=orderItemList.iterator();iterator2.hasNext();){
				OrderItem orderItem=iterator2.next();
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", orderItem.getId());
				map.put("title", orderItem.getRecipe().getName());
				map.put("price", "¥ "+orderItem.getRecipe().getPrice());
				String strCount=orderItem.getCount()+"";
				map.put("count", strCount);
				Integer kk=orderItem.getStatus();
				map.put("status", kk);
				String urlString=MenuUtils.imageUrl+orderItem.getRecipe().getImage();
				map.put("img", urlString);
				itemlist.add(map);
			}
		}
    }
    
    public void UpdateElement(){
    	mProgressBar.setVisibility(View.INVISIBLE);
    	hashOrderItems.clear();
		tagList.clear();
		itemlist.clear();
		InitHashOrderItems();
		InitListDatasource();
		OrderAdapter listAdapter=new OrderAdapter(OrderActivity.this, itemlist, tagList);
		orderListView.setAdapter(listAdapter);
		sumTextView.setText(sumString+totalPrice);
    }
    public void UpdateListView(String iidString){
    	mProgressBar.setVisibility(View.INVISIBLE);
    	Iterator<Map<String, Object>> iterator;
    	for(iterator=itemlist.iterator();iterator.hasNext();){
    		Map<String, Object> map=iterator.next();
    		if(!map.containsKey("id"))
    		{
    			continue;
    		}
    		String idString=map.get("id").toString();
    		if(idString.equals(iidString)){
    			map.put("status", 1);
    			break;
    		}
    	}
    	OrderAdapter listAdapter=(OrderAdapter)orderListView.getAdapter();
    	listAdapter.notifyDataSetChanged();
    }
    
    public void FlashOrder(){
    	mProgressBar.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					String resultString = HttpDownloader.getString(MenuUtils.initUrl+ "restaurants/"+m_Declare.loginResponse.getRestaurantid()+"/orders/"+mOrder.getId(),
							m_Declare.loginResponse.getToken(),m_Declare.udidString);
					if(resultString==null)
					{
						httpHandler.obtainMessage(0,"编码错误！").sendToTarget();
						return;
					}
					Order order=JsonUtils.ParseJsonToOrder(resultString);
					mOrder=order;
					httpHandler.obtainMessage(1,"").sendToTarget();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					httpHandler.obtainMessage(0,e.getMessage()).sendToTarget();
				}
			}
		}).start();
    }
    
    public void ClosePage(){
    	mProgressBar.setVisibility(View.INVISIBLE);
    	this.finish();
    }
    
    public void ChangeItemStatus(final int iid){
    	mProgressBar.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String resultString;
				try {
					resultString = HttpDownloader.PutOrder(
							MenuUtils.initUrl+ "restaurants/"+m_Declare.loginResponse.getRestaurantid()+"/orders/"+mOrder.getId()+"/"+iid,
							m_Declare.loginResponse.getToken(),m_Declare.udidString);
					OrderItem orderItem=JsonUtils.ParseJsonToOrderItem(resultString);
					if(orderItem!=null&&orderItem.getStatus()==1){
						httpHandler.obtainMessage(3,""+iid).sendToTarget();
					}
					else {
						httpHandler.obtainMessage(0,"修改状态失败！").sendToTarget();
					}
					
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					httpHandler.obtainMessage(0,e.getMessage()).sendToTarget();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					httpHandler.obtainMessage(0,e.getMessage()).sendToTarget();
				}
				
			}
		}).start();
    }
	
	/**
     * 点击刷新按钮
     * @author liuyan
     *
     */
    class FlashOnClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			FlashOrder();
		}
    	
    }
    /**
     * 结账按钮点击
     * @author liuyan
     *
     */
    class overClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			AlertDialog.Builder builder = new Builder(OrderActivity.this);
			builder.setMessage("确认结账吗？");
			builder.setTitle("提示");
			builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					mProgressBar.setVisibility(View.VISIBLE);
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							String resultString;
							try {
								resultString = HttpDownloader.PutOrder(
										MenuUtils.initUrl+ "restaurants/"+m_Declare.loginResponse.getRestaurantid()+"/orders/"+mOrder.getId()+"/check",
										m_Declare.loginResponse.getToken(),m_Declare.udidString);
								Order order=JsonUtils.ParseJsonToOrder(resultString);
								if(order!=null&&order.getStatus()==4){
									httpHandler.obtainMessage(2,"").sendToTarget();
								}
								else {
									httpHandler.obtainMessage(0,"结账失败！").sendToTarget();
								}
								
							} catch (ClientProtocolException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								httpHandler.obtainMessage(0,e.getMessage()).sendToTarget();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								httpHandler.obtainMessage(0,e.getMessage()).sendToTarget();
							}
							
						}
					}).start();
				}
			});
			builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.create().show();

		}
    	
    }
    
    class cancelClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			AlertDialog.Builder builder = new Builder(OrderActivity.this);
			builder.setMessage("确认撤销吗？");
			builder.setTitle("提示");
			builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					mProgressBar.setVisibility(View.VISIBLE);
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							String resultString;
							try {
								resultString = HttpDownloader.PutOrder(
										MenuUtils.initUrl+ "restaurants/"+m_Declare.loginResponse.getRestaurantid()+"/orders/"+mOrder.getId()+"/cancel",
										m_Declare.loginResponse.getToken(),m_Declare.udidString);
								Order order=JsonUtils.ParseJsonToOrder(resultString);
								if(order!=null&&order.getStatus()==5){
									httpHandler.obtainMessage(2,"").sendToTarget();
								}
								else {
									httpHandler.obtainMessage(0,"撤单失败！").sendToTarget();
								}
								
							} catch (ClientProtocolException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								httpHandler.obtainMessage(0,e.getMessage()).sendToTarget();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								httpHandler.obtainMessage(0,e.getMessage()).sendToTarget();
							}
							
						}
					}).start();
				}
			});
			builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.create().show();
		}
    	
    }
	/**
     * 通知的接收器，只接收点菜消息
     */
    class NotifiReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(action.equals("diancan")){
				String idString=intent.getSerializableExtra("message").toString();
				int id=Integer.parseInt(idString);
				if(id==mOrder.getId())
				{
					FlashOrder();
				}
			}
		}
		
	}
    
    class OrderAdapter extends BaseAdapter{
    	private ImageDownloader imageDownloader;
    	private LayoutInflater mInflater;  
    	private List<Map<String, Object>> mListData;  
    	private List<Map<String, Object>> mSplitData;
    	public OrderAdapter(Context context,List<Map<String, Object>> listData,List<Map<String, Object>> splitData){
    		mInflater = LayoutInflater.from(context);  
    		mListData = listData;  
    		mSplitData = splitData;
    		imageDownloader=new ImageDownloader();
    	}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mListData.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mListData.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		
		@Override  
		public boolean isEnabled(int position) {  
		   if (mSplitData.contains(mListData.get(position))) {  
		     return false;  
		   }  
		   return super.isEnabled(position);  
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (mSplitData.contains(mListData.get(position))) {  
			     convertView = mInflater.inflate(R.layout.listtagitem, null);  
			     TextView titleTextView=(TextView)convertView.findViewById(R.id.tagTitle);
			     titleTextView.setText(mListData.get(position).get("tagTitle").toString());
			   } else {  
			     convertView = mInflater.inflate(R.layout.select_list_item, null); 
			     Map<String, Object> map=mListData.get(position);
			     TextView titleView=(TextView)convertView.findViewById(R.id.mtitle);
			     titleView.setText(map.get("title").toString());
			     TextView priceView=(TextView)convertView.findViewById(R.id.mprice);
			     priceView.setText(map.get("price").toString());
			     TextView countView=(TextView)convertView.findViewById(R.id.mcount);
			     countView.setText(map.get("count").toString());
			     ImageView recipeImg=(ImageView)convertView.findViewById(R.id.imgctrl);
			     String strUrl=map.get("img").toString();
			     imageDownloader.download(strUrl, recipeImg);
			     
			     final int iid=Integer.parseInt(map.get("id").toString());
			     ImageView statusImg=(ImageView)convertView.findViewById(R.id.imgadd);
			     int status=Integer.parseInt(map.get("status").toString());
			     if(status==0){
			    	 statusImg.setVisibility(View.VISIBLE);
			    	 statusImg.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							
							ChangeItemStatus(iid);
						}
					});
			     }
			     else{
			    	 statusImg.setVisibility(View.INVISIBLE);
			     }
			   }    
			   return convertView; 
		}
    	
    }
}
