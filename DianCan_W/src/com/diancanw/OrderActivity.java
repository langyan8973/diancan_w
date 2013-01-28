package com.diancanw;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.client.ClientProtocolException;

import com.diancanw.custom.ImageDownloader;
import com.diancanw.declare.DiancanwApp;
import com.diancanw.http.HttpDownloader;
import com.diancanw.http.HttpHandler;
import com.diancanw.http.HttpRequestCallback;
import com.diancanw.http.activitytools.HttpToolForOrder;
import com.diancanw.http.activitytools.OrderHttpCallback;
import com.diancanw.model.Order;
import com.diancanw.model.OrderItem;
import com.diancanw.utils.DisplayUtil;
import com.diancanw.utils.JsonUtils;
import com.diancanw.utils.MenuUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView.ScaleType;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class OrderActivity extends Activity implements OnClickListener,OrderHttpCallback {

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
	DiancanwApp m_Declare;
	HashMap<String, List<OrderItem>> hashOrderItems;
	private List<Map<String, Object>> itemlist = new ArrayList<Map<String, Object>>();  
	private List<Map<String, Object>> tagList = new ArrayList<Map<String, Object>>();
	HttpToolForOrder httpToolForOrder;
													
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.orderactivity);
		m_Declare=(DiancanwApp)this.getApplicationContext();
		httpToolForOrder=new HttpToolForOrder(m_Declare, this);
		sumString=getResources().getString(R.string.infostr_sum);
		
		orderListView=(ListView)findViewById(R.id.orderList);
		sumTextView=(TextView)findViewById(R.id.sumText);
		titleTextView=(TextView)findViewById(R.id.deskTitle);
		flashButton=(Button)findViewById(R.id.BtnFlash);
		flashButton.setOnClickListener(this);
		overButton=(Button)findViewById(R.id.overBtn);
		overButton.setOnClickListener(this);
		cacelButton=(Button)findViewById(R.id.cancelBtn);
		cacelButton.setOnClickListener(this);
		mProgressBar=(ProgressBar)findViewById(R.id.oprogress);
		mProgressBar.setVisibility(View.INVISIBLE);
		
		Intent intent=getIntent();
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
	
	@Override
	public void UpdateOrderPage(Order order) {
		// TODO Auto-generated method stub
		mProgressBar.setVisibility(View.INVISIBLE);
		mOrder=order;
    	hashOrderItems.clear();
		tagList.clear();
		itemlist.clear();
		InitHashOrderItems();
		InitListDatasource();
		OrderAdapter listAdapter=new OrderAdapter(OrderActivity.this, itemlist, tagList);
		orderListView.setAdapter(listAdapter);
		sumTextView.setText(sumString+totalPrice);
	}

	@Override
	public void OrderChecked() {
		// TODO Auto-generated method stub
		ClosePage();
	}

	@Override
	public void OrderCancelled() {
		// TODO Auto-generated method stub
		ClosePage();
	}

	@Override
	public void UpdateOrderItemById(String iidString) {
		// TODO Auto-generated method stub
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

	@Override
	public void RequestError(String errString) {
		// TODO Auto-generated method stub
		ShowError(errString);
	}
    
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.BtnFlash:
			FlashOrder();
			break;
		case R.id.overBtn:
			CheckOrder();
			break;
		case R.id.cancelBtn:
			CancelOrder();
			break;
		default:
			break;
		}
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
    	}
    	totalPrice=mOrder.getPrice();
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
    
    public void FlashOrder(){
    	mProgressBar.setVisibility(View.VISIBLE);
		httpToolForOrder.RefreshOrder(mOrder.getId());
    }
    
    public void ChangeItemStatus(final int iid){
    	mProgressBar.setVisibility(View.VISIBLE);
		httpToolForOrder.ChangeItemStatus(iid, mOrder.getId());
    }
    
    public void CheckOrder(){
    	AlertDialog.Builder builder = new Builder(OrderActivity.this);
		builder.setMessage("确认结账吗？");
		builder.setTitle("提示");
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				mProgressBar.setVisibility(View.VISIBLE);
				httpToolForOrder.CheckOrder(mOrder.getId());
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
    
    public void CancelOrder(){
    	AlertDialog.Builder builder = new Builder(OrderActivity.this);
		builder.setMessage("确认撤销吗？");
		builder.setTitle("提示");
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				mProgressBar.setVisibility(View.VISIBLE);
				httpToolForOrder.CancelOrder(mOrder.getId());
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
	
    
    public void ClosePage(){
    	mProgressBar.setVisibility(View.INVISIBLE);
    	this.finish();
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
				 convertView = mInflater.inflate(R.layout.select_list_item,null);
				 TextView titleView=(TextView)convertView.findViewById(R.id.mtitle);
				 TextView priceView=(TextView)convertView.findViewById(R.id.mprice);
				 TextView countView=(TextView)convertView.findViewById(R.id.mcount);
				 ImageView imageView=(ImageView)convertView.findViewById(R.id.imgctrl);
				 ImageView statusImageView=(ImageView)convertView.findViewById(R.id.imgstatus);
			     Map<String, Object> map=mListData.get(position);
			     Log.d("Adapter", map.get("title").toString());
			     if(titleView==null){
			    	 Log.d("Adapter", "titleView空"); 
			     }
			     titleView.setText(map.get("title").toString());
			     priceView.setText(map.get("price").toString());
			     countView.setText(map.get("count").toString());
			     String strUrl=map.get("img").toString();
			     imageDownloader.download(strUrl, imageView);
			     
			     final int iid=Integer.parseInt(map.get("id").toString());
			     int status=Integer.parseInt(map.get("status").toString());
			     if(status==0){
			    	 statusImageView.setVisibility(View.INVISIBLE);
			    	 
			    	 convertView.setOnLongClickListener(new OnLongClickListener() {
							
							@Override
							public boolean onLongClick(View v) {
								// TODO Auto-generated method stub
								AlertDialog.Builder builder = new Builder(OrderActivity.this);
								builder.setMessage("确认划菜吗？");
								builder.setTitle("提示");
								builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										dialog.dismiss();
										ChangeItemStatus(iid);
									}
								});
								builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										dialog.dismiss();
									}
								});
								builder.create().show();
								return false;
							}
					   });
			     }
			     else{
			    	 statusImageView.setVisibility(View.VISIBLE);
			     }
			   }  
			   return convertView; 
		}
    	
    }
	
}
