package com.diancanw;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.androidpn.clientw.Constants;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import com.httpw.HttpDownloader;
import com.modelw.Recipe;
import com.modelw.OrderItem;
import com.modelw.Order;
import com.utilsw.MenuUtils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.utilsw.DisplayUtil;
import com.customw.CustomViewBinder;
import com.utilsw.JsonUtils;
import com.customw.ClassListViewWidget;
import com.diancanw.R;

public class TableW extends Activity {
	/***
	 * 类成员变量声明
	 */
	LinearLayout rootLayout;
	LinearLayout rootLyt;
	ListView selectListView;
	Hashtable<String, ClassListViewWidget> dicWidgets;
	HashMap<String, List<OrderItem>> hashOrderItems;
	Button overButton;
	Button flashButton;
	TextView sumTextView;
	TextView titleTextView;
	boolean isSelf=false;
	String sumString;	
	int sendId,sendCount;
	Order mOrder;
	public int totalCount=0;
	public double totalPrice=0;
	NotifiReceiver receiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tablew);
		
		sumString=getResources().getString(R.string.infostr_sum);
		rootLayout=(LinearLayout)findViewById(R.id.rootLayout);
		rootLyt=(LinearLayout)findViewById(R.id.rootLyt);
		sumTextView=(TextView)findViewById(R.id.sumText);
		sumTextView.setTextColor(Color.WHITE);
		sumTextView.setTextSize(DisplayUtil.dip2px(16));	
		
		titleTextView=(TextView)findViewById(R.id.deskTitle);
		titleTextView.setTextColor(Color.WHITE);
		
		overButton=(Button)findViewById(R.id.overbtn);
		overButton.setText(getResources().getString(R.string.btnstr_over));
		overButton.setOnClickListener(new OverBtnOnclick());
		overButton.setVisibility(View.GONE);
		
		flashButton=(Button)findViewById(R.id.BtnFlash);
		flashButton.setOnClickListener(new FlashOnClick());
		
		dicWidgets=new Hashtable<String, ClassListViewWidget>();
		hashOrderItems=new HashMap<String, List<OrderItem>>();
		Intent intent=getIntent();
		mOrder=(Order)intent.getSerializableExtra("order");
		
		titleTextView.setText(mOrder.getDesk().getName()+"("+mOrder.getCode()+")");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		//注册一个广播接收器，启动餐桌抖动动画  
	    receiver = new NotifiReceiver();
	    IntentFilter filter = new IntentFilter();
	    filter.addAction(Constants.ACTION_SHOW_NOTIFICATION);
        filter.addAction(Constants.ACTION_NOTIFICATION_CLICKED);
        filter.addAction(Constants.ACTION_NOTIFICATION_CLEARED);
	    registerReceiver(receiver, filter);
	    
    	dicWidgets.clear();
    	hashOrderItems.clear();
    	rootLayout.removeAllViews();
    	if(mOrder!=null)
    	{
    		InitHashOrderItems();
    		CreateElements();
    	}
    	
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unregisterReceiver(receiver);
	}
    
    public void InitHashOrderItems()
    {
    	hashOrderItems.clear();
    	totalCount=0;
    	totalPrice=0;
    	Iterator<OrderItem> iterator;
    	for(iterator=mOrder.getOrderItems().iterator();iterator.hasNext();)
    	{
    		OrderItem orderItem=iterator.next();
    		if(!hashOrderItems.containsKey(orderItem.getRecipe().getCname()))
    		{
    			List<OrderItem> orderItems=new ArrayList<OrderItem>();
    			hashOrderItems.put(orderItem.getRecipe().getCname(), orderItems);
    		}
    		List<OrderItem> oItems=hashOrderItems.get(orderItem.getRecipe().getCname());
    		oItems.add(orderItem);
    		totalCount+=orderItem.getCount();
    		totalPrice+=orderItem.getCount()*orderItem.getRecipe().getPrice();
    	}
    }
	
	private void UpdateHashList(List<OrderItem> orderItems,ArrayList<HashMap<String, Object>> hashList)
	{
		hashList.clear();
		for (Iterator iterator = orderItems.iterator(); iterator.hasNext();) {
			OrderItem orderItem = (OrderItem) iterator.next();
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("title", orderItem.getRecipe().getName());
			map.put("price", "¥ "+orderItem.getRecipe().getPrice());
			String strCount=orderItem.getCount()+"";
			map.put("count", strCount);
			Bitmap imgBitmap=HttpDownloader.getStream(MenuUtils.imageUrl+orderItem.getRecipe().getImage());
			map.put("img", imgBitmap);
			
			hashList.add(map);
		}
	}
    private void CreateElements()
    {
    	String strKey;
    	Set<String> meenum=hashOrderItems.keySet();
    	
    	Iterator iterator;
		for (iterator = meenum.iterator(); iterator.hasNext();) {
			strKey = (String) iterator.next();
			List<OrderItem> orderItemList=hashOrderItems.get(strKey);
			ClassListViewWidget clvw=CreateListWidget(orderItemList, strKey);
			clvw.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
			clvw.setScrollContainer(true);
			rootLayout.addView(clvw);
			dicWidgets.put(strKey, clvw);			
		}
    	sumTextView.setText(sumString+totalPrice);
    	
    }
    private void  UpdateElement() {
    	if(mOrder==null)
    	{
			dicWidgets.clear();
			hashOrderItems.clear();
			rootLayout.removeAllViews();
			sumTextView.setText("");
    		return;
    	}
    	
    	String strKey;
    	Set<String> meenum;
    	if(hashOrderItems.size()>=dicWidgets.size())
    	{
    		meenum= hashOrderItems.keySet();
    		Iterator iterator;
    		for (iterator = meenum.iterator(); iterator.hasNext();) {
    			strKey = (String) iterator.next();
    			if(dicWidgets.containsKey(strKey))
        		{
        			ClassListViewWidget classListViewWidget=dicWidgets.get(strKey);
        			TableListAdapter sListAdapter=(TableListAdapter)classListViewWidget.listView.getAdapter();
        			ArrayList<HashMap<String, Object>> hashList=classListViewWidget.getHashList();
        			List<OrderItem> orderItems=hashOrderItems.get(strKey);
        			UpdateHashList(orderItems, hashList);
        			sListAdapter.setmItemList(hashList);
        			sListAdapter.notifyDataSetChanged();
        			sListAdapter.setOrderItemList(orderItems);
        			classListViewWidget.setListViewHeight(classListViewWidget.getListView());
        		}
        		else {
        			List<OrderItem> orderItems=hashOrderItems.get(strKey);
        			ClassListViewWidget clvw=CreateListWidget(orderItems, strKey);
        			clvw.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
        			clvw.setScrollContainer(true);
        			rootLayout.addView(clvw);
        			dicWidgets.put(strKey, clvw);
        			registerForContextMenu(clvw.getListView());
				}
    		}
        	
    	}
    	else {
    		meenum= dicWidgets.keySet();
    		Iterator iterator;
    		for (iterator = meenum.iterator(); iterator.hasNext();) {
    			strKey = (String) iterator.next();
    			if(hashOrderItems.containsKey(strKey))
        		{
        			ClassListViewWidget classListViewWidget=dicWidgets.get(strKey);
        			TableListAdapter sListAdapter=(TableListAdapter)classListViewWidget.listView.getAdapter();
        			ArrayList<HashMap<String, Object>> hashList=classListViewWidget.getHashList();
        			List<OrderItem> orderItems=hashOrderItems.get(strKey);
        			UpdateHashList(orderItems, hashList);
        			sListAdapter.setmItemList(hashList);
        			sListAdapter.notifyDataSetChanged();
        			sListAdapter.setOrderItemList(orderItems);
        			classListViewWidget.setListViewHeight(classListViewWidget.getListView());
        		}
        		else {
        			ClassListViewWidget classListViewWidget1=dicWidgets.get(strKey);
        			rootLayout.removeView(classListViewWidget1);
        			dicWidgets.remove(strKey);
				}
    		}
		}
    	
    	sumTextView.setText(sumString+totalPrice);
	}
    private ClassListViewWidget CreateListWidget(List<OrderItem> orderItemlist,String strtitle)
    {
    	ArrayList<HashMap<String, Object>> hashList=new ArrayList<HashMap<String, Object>>();
    	UpdateHashList(orderItemlist, hashList);
    	TableListAdapter simpleAdapter1=new TableListAdapter(this, hashList,
				R.layout.select_list_item, new String[] { "title", "price","count","img"},
				new int[] { R.id.mtitle, R.id.mprice,R.id.mcount,R.id.imgctrl});
    	simpleAdapter1.setOrderItemList(orderItemlist);

    	ClassListViewWidget cLvWidget=new ClassListViewWidget(this, orderItemlist, hashList, strtitle);
    	simpleAdapter1.setViewBinder(new CustomViewBinder());
    	cLvWidget.listView.setAdapter(simpleAdapter1);
    	setListViewHeight(cLvWidget.listView);
    	return cLvWidget;
    }


    public void setListViewHeight(ListView lv) {
        ListAdapter la = lv.getAdapter();
        if(null == la) {
            return;
        }
        // calculate height of all items.
        int h = 0;
        final int cnt = la.getCount();
        for(int i=0; i<cnt; i++) {
            View item = la.getView(i, null, lv);
            item.measure(0, 0);
            h += item.getMeasuredHeight();
        }
        // reset ListView height
        ViewGroup.LayoutParams lp = lv.getLayoutParams();
        lp.height = h + (lv.getDividerHeight() * (cnt - 1));
        lp.width=android.view.ViewGroup.LayoutParams.FILL_PARENT;
        lv.setLayoutParams(lp);
    }
    
    public void PostToServer()
    {
    	new Thread(){
            public void run(){
            	//加减菜
        		JSONObject object = new JSONObject();
        		try {
        			object.put("rid", sendId);
        			object.put("count", sendCount);
        		} catch (JSONException e) {
        		}		
        		try {
        			String resultString = HttpDownloader.alterRecipeCount(MenuUtils.initUrl, mOrder.getId(), object);
        			System.out.println("resultString:"+resultString);
        		} catch (ClientProtocolException e) {
        		} catch (JSONException e) {
        		} catch (IOException e) {
        		} catch (Throwable e) {
        			e.printStackTrace();
        			//自定义的错误，在界面上显示
        			Toast toast = Toast.makeText(TableW.this, e.getMessage(), Toast.LENGTH_SHORT); 
        			toast.show();
        		}
            }
        }.start();
    }

    
    public class OverBtnOnclick implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
//			List<SelectedProduct> hiSelectedProducts=declare.getHistory().getHisSelectedProducts();
//			if(hiSelectedProducts==null)
//			{
//				hiSelectedProducts=new ArrayList<SelectedProduct>();
//			}
//			final Calendar c = Calendar.getInstance();
//			int mYear = c.get(Calendar.YEAR); //获取当前年份
//			int mMonth = c.get(Calendar.MONTH)+1;//获取当前月份
//			int mDay = c.get(Calendar.DAY_OF_MONTH);//获取当前月份的日期号码
//			int mHour = c.get(Calendar.HOUR_OF_DAY);//获取当前的小时数
//			selectedProduct.setStrdate(mYear+"年"+mMonth+"月"+mDay+"日"+mHour+"时");
//			hiSelectedProducts.add(selectedProduct);
			
			v.setVisibility(View.GONE);			
			mOrder=null;
			UpdateElement();
		}    	
    }
    
    class FlashOnClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String jsonString = HttpDownloader.getString(MenuUtils.initUrl+ "orders/" +mOrder.getId() );
			final Order order=JsonUtils.ParseJsonToOrder(jsonString);
			mOrder=order;
			dicWidgets.clear();
			hashOrderItems.clear();
	    	rootLayout.removeAllViews();
			InitHashOrderItems();
			CreateElements();
		}
    	
    }
    
    public OrderItem GetItemById(int id,Order order)
	{
		OrderItem orderItem=null;
		
		Iterator<OrderItem> iterator;
		for(iterator=order.getOrderItems().iterator();iterator.hasNext();)
		{
			OrderItem oItem=iterator.next();
			Recipe recipe=oItem.getRecipe();
			if(id==recipe.getId())
			{
				orderItem=oItem;
				break;
			}
		}
		
		return orderItem;
	}
    
    public class TableListAdapter extends SimpleAdapter {

		public ArrayList<HashMap<String, Object>> getmItemList() {
			return mItemList;
		}
		public void setmItemList(ArrayList<HashMap<String, Object>> hashList) {
			this.mItemList = hashList;
		}
		int count = 0;
		int[] idArray;
		List<OrderItem> orderItemList;
		Context thisContext;
	    private ArrayList<HashMap<String, Object>> mItemList;
	    public TableListAdapter(Context context, List<? extends Map<String, Object>> data,
	            int resource, String[] from, int[] to) {
	        super(context, data, resource, from, to);
	        thisContext=context;
	        mItemList = (ArrayList<HashMap<String, Object>>) data;
	        if(data == null){
	            count = 0;
	        }else{
	            count = data.size();
	        }
	        idArray=to;
	    }
	    public int getCount() {
	        return mItemList.size();
	    }

	    public Object getItem(int pos) {
	        return pos;
	    }

	    public long getItemId(int pos) {
	        return pos;
	    }
	    

		public List<OrderItem> getOrderItemList() {
			return orderItemList;
		}
		public void setOrderItemList(List<OrderItem> orderItemList) {
			this.orderItemList = orderItemList;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			final View localView = super.getView(position, convertView, parent);
			final ListView listView=(ListView)parent;
			final int index=position;        
			ImageView imgadd=(ImageView)localView.findViewById(R.id.imgadd);	        
	        OrderItem orderItem=TableListAdapter.this.orderItemList.get(position);
	        Map<String, Object> mMap=TableListAdapter.this.mItemList.get(position);
            int mCount=Integer.parseInt(mMap.get("count").toString());

	        imgadd.setTag(position);
	        imgadd.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
//					int position = Integer.parseInt(v.getTag().toString());
//					OrderItem oItem=TableListAdapter.this.orderItemList.get(position);
//					sendId=oItem.getRecipe().getId();
//		            sendCount=1;
//					
//		             Map<String, Object> map=TableListAdapter.this.mItemList.get(position);
//		             int count=Integer.parseInt(map.get("count").toString());
//		             count++;
//		             map.put("count", count+"");
//		             
//		             TableListAdapter.this.notifyDataSetChanged();
//		             oItem.setCount(count);
//		             declare.AddItemToOrder(oItem); 
//		             declare.getMenuListDataObj().ChangeRecipeMapByObj(oItem);
//			         MenuUtils.bUpdating=true;
//			         sumTextView.setText(sumString+declare.getTotalPrice());	
//			         setListViewHeight(listView);
//			         SendSetCountMessage();
//			         PostToServer();
				}
			});
	        
			return localView;
		}
	}
    
    /*
     * 通知的接收器，只接收点菜消息
     */
    class NotifiReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
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

                if(notificationTitle.equals("11")&&notificationMessage.equals(mOrder.getId().toString()))
                {
                	String jsonString = HttpDownloader.getString(MenuUtils.initUrl+ "orders/" +mOrder.getId() );
        			final Order order=JsonUtils.ParseJsonToOrder(jsonString);
        			mOrder=order;
        			dicWidgets.clear();
        			hashOrderItems.clear();
        	    	rootLayout.removeAllViews();
        			InitHashOrderItems();
        			CreateElements();
                }
    		}
		}
		
	}
}
