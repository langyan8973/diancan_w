package com.diancanw;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import com.animationw.HistoryRotateAnim;
import com.customw.CategoryListAdapter;
import com.customw.CustomViewBinder;
import com.declarew.Declare_w;
import com.httpw.HttpDownloader;
import com.modelw.Category;
import com.modelw.Desk;
import com.modelw.DeskType;
import com.modelw.Order;
import com.utilsw.DisplayUtil;
import com.utilsw.JsonUtils;
import com.utilsw.MenuUtils;

import android.R.integer;
import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class TableList extends Activity {
	List<DeskType> types;
	List<Desk> deskList;
	List<Desk> mDeskList;
	ArrayList<HashMap<String, Object>> hashList;
	ArrayList<HashMap<String, Object>> imghashList;
	CategoryListAdapter typeAdapter;
	SimpleAdapter gridSimpleAdapter;
	SimpleAdapter listSimpleAdapter;
	RelativeLayout rootLayout;
	LinearLayout searchLayout;
	LinearLayout gridLayout;
	LinearLayout listLayout;
	LinearLayout typeLayout;
	TextView typeTextView;
	GridView mGridView;
	ListView mListView;
	ListView typeList;
	EditText searchEditText;
	ImageView clearImg;
	ImageView imgControl;
	ProgressBar mProgress;
	private SharedPreferences sharedPrefs;
	int sWidth;
	int sHeight;
	boolean listVisibility;
	DeskType selectDeskType;
	private final int DIALOG_SEARCH = 0x100;
	HashMap<String, Object> selectedItem;
	
	private final Handler mHandler = new Handler();
	EditText input;
	Declare_w m_Declare;
	
	private Handler httpHandler = new Handler() {  
        public void handleMessage (Message msg) {//此方法在ui线程运行   
            switch(msg.what) {  
            case 0: 
            	String errString=msg.obj.toString();
            	ShowError(errString);
                break;   
            case 1: 
            	InitDeskTypes();
                break;  
            case 2:
            	GetDeskList();
            	break;
            case 3:
            	String jsString=msg.obj.toString();
            	ResponseTable(jsString);
            	break;
            case 4:
            	String strJs=msg.obj.toString();
            	ResponseOrder(strJs);
            	break;
            }  
        }  
    }; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.tablelist);
        
        Init();
		
		rootLayout=(RelativeLayout)findViewById(R.id.contentFrame);
		gridLayout=new LinearLayout(this);
		gridLayout.setId(100);
		RelativeLayout.LayoutParams lp1=new RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.FILL_PARENT,
				android.widget.RelativeLayout.LayoutParams.FILL_PARENT);
		gridLayout.setLayoutParams(lp1);
		gridLayout.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
		gridLayout.setNextFocusDownId(R.id.searchExt);
		
		listLayout=new LinearLayout(this);
		listLayout.setId(101);
		RelativeLayout.LayoutParams lp2=new RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.FILL_PARENT,
				android.widget.RelativeLayout.LayoutParams.FILL_PARENT);
		listLayout.setLayoutParams(lp2);
		listLayout.setOrientation(LinearLayout.VERTICAL);
		listLayout.setBackgroundColor(Color.parseColor("#FFCCCCCC"));
		
		rootLayout.addView(gridLayout);
		gridLayout.layout(0, 0, sWidth, sHeight);
		gridLayout.setVisibility(View.INVISIBLE);
		rootLayout.addView(listLayout);
		listLayout.layout(0, 0, sWidth, sHeight);
		listVisibility=true;
		
		typeLayout=(LinearLayout)findViewById(R.id.typelayout);
		typeLayout.setVisibility(View.GONE);
		typeList=(ListView)findViewById(R.id.ListDeskType);
		typeTextView=(TextView)findViewById(R.id.TxtDeskType);
		typeTextView.setOnClickListener(new TypeTxtOnclick());
		
		mGridView=new GridView(this);
		mGridView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		mGridView.setNumColumns(GridView.AUTO_FIT);
		mGridView.setVerticalSpacing(DisplayUtil.dip2px(6.7f));
		mGridView.setHorizontalSpacing(DisplayUtil.dip2px(6.7f));
		mGridView.setColumnWidth(DisplayUtil.dip2px(80));
		mGridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);		
		gridLayout.addView(mGridView);	
		
		LayoutInflater inflater = (LayoutInflater)this.getSystemService( Context.LAYOUT_INFLATER_SERVICE);
		searchLayout = (LinearLayout)inflater.inflate(R.layout.searchlayout, null);
		
		int lHeight=DisplayUtil.dip2px(53);
		LayoutParams lParams=new LayoutParams(LayoutParams.FILL_PARENT,lHeight);
		searchLayout.setLayoutParams(lParams);
		listLayout.addView(searchLayout);
		searchEditText=(EditText)searchLayout.findViewById(R.id.searchExt);
		searchEditText.addTextChangedListener(new EditTextChange());
		searchEditText.setOnClickListener(new EditTextClick());
		
		clearImg=(ImageView)searchLayout.findViewById(R.id.ImgClear);
		clearImg.setOnClickListener(new ClearOnClick());
		clearImg.setVisibility(View.INVISIBLE);
		
		mListView=new ListView(this);
		int cHeight=sHeight;
		mListView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,cHeight));
		mListView.setCacheColorHint(Color.argb(0, 0, 0, 0));
		listLayout.addView(mListView);
		
		mGridView.setOnItemClickListener(new GridItemClickListener());
		mListView.setOnItemClickListener(new ItemClickListener());
		
		imgControl=(ImageView)findViewById(R.id.imgcontrol);
		imgControl.setOnClickListener(new imgOnClick());
		
		mProgress=(ProgressBar)findViewById(R.id.httppro);
		mProgress.setVisibility(View.INVISIBLE);
		
		input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);

        RequestRecipeTypes();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		RequestTypes();
	}

	/***
	 * 初始化
	 */
	public void Init() {
		//屏幕尺寸容器
  		DisplayMetrics dm;
  		dm = new DisplayMetrics();
  		this.getWindowManager().getDefaultDisplay().getMetrics(dm);
  		DisplayUtil.SCALE=dm.density;
  		DisplayUtil.DPWIDTH=DisplayUtil.px2dip(dm.widthPixels);
  		DisplayUtil.DPHEIGHT=DisplayUtil.px2dip(dm.heightPixels)-25;
  		System.out.println("屏幕密度："+dm.density);
  		System.out.println("dp宽度："+DisplayUtil.DPWIDTH);
  		System.out.println("dp高度："+DisplayUtil.DPHEIGHT);
        
        sWidth = DisplayUtil.dip2px(DisplayUtil.DPWIDTH);
		sHeight=DisplayUtil.dip2px(DisplayUtil.DPHEIGHT-108);	
		m_Declare=(Declare_w)this.getApplicationContext();
	}
	
	/***
	 * 新启线程请求餐桌类别
	 */
	private void RequestTypes() {
		mProgress.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				types=MenuUtils.getDeskTypes(m_Declare.loginResponse.getRestaurant().getId());
				if(types==null||types.size()==0)
				{
					httpHandler.obtainMessage(0,"获取餐桌类别失败！").sendToTarget();
				}
				else {
					httpHandler.obtainMessage(1).sendToTarget();
				}
				
			}
		}).start();
	}
	
	/***
	 * 新启线程请求菜品类别
	 */
	private void RequestRecipeTypes() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				List<Category> categories=MenuUtils.getAllCategory(m_Declare.loginResponse.getRestaurant().getId());
				if(categories==null||categories.size()==0)
				{
					httpHandler.obtainMessage(0,"获取菜品类别失败！").sendToTarget();
				}
				else {
					Iterator<Category> iterator;
					HashMap<String, String> cathash=new HashMap<String, String>();
					for(iterator=categories.iterator();iterator.hasNext();){
						Category category=iterator.next();
						cathash.put(category.getId().toString(), category.getName());
					}
					m_Declare.hashTypes=cathash;
				}
				
			}
		}).start();
	}
	
	/***
	 * 初始化餐桌类别列表
	 */
	private void InitDeskTypes()
	{		
		mProgress.setVisibility(View.INVISIBLE);
		ArrayList<HashMap<String, Object>> typeHashMaps=new ArrayList<HashMap<String,Object>>();
		for(final DeskType deskType:types)
		{		
			HashMap<String, Object> map=new HashMap<String, Object>();
			map.put("name", deskType.getName()); 
			map.put("id", deskType.getId());
			typeHashMaps.add(map);
		}
		DeskType type=types.get(0);
		selectDeskType=type;
		typeAdapter=new CategoryListAdapter(this, typeHashMaps,
				R.layout.categorylist_item, new String[] { "name","id" },
				new int[] { R.id.category_name,R.id.category_id});
		typeAdapter.setViewBinder(new CustomViewBinder());
		typeAdapter.setSelectedName(type.getName());
		typeList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		typeList.setAdapter(typeAdapter);
		typeList.setOnItemClickListener(new TypeItemClick());
		
		typeTextView.setText(type.getName());
		
		RequestDesklist();
		
	}
	
	/***
	 * 获取餐桌列表
	 */
	private void RequestDesklist() {
		mProgress.setVisibility(View.VISIBLE);
		if(deskList==null)
		{
			deskList=new ArrayList<Desk>();
		}
		if(mDeskList==null)
		{
			mDeskList=new ArrayList<Desk>();
		}
		deskList.clear();
		mDeskList.clear();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				deskList=MenuUtils.getDesksByTid(selectDeskType.getId(),m_Declare.loginResponse.getRestaurant().getId());
				if(deskList==null||deskList.size()==0)
				{
					httpHandler.obtainMessage(0,"获取餐桌列表失败").sendToTarget();
				}
				else {
					httpHandler.obtainMessage(2).sendToTarget();
				}
			}
		}).start();
	}
	
	/***
	 * 显示餐桌列表
	 */
	public void GetDeskList()
	{		
		mProgress.setVisibility(View.INVISIBLE);
		try {		
			Iterator<Desk> iterator;
			for(iterator=deskList.iterator();iterator.hasNext();)
			{
				Desk desk=iterator.next();
				mDeskList.add(desk);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast toast = Toast.makeText(TableList.this, e.getMessage(), Toast.LENGTH_SHORT); 
        	toast.show();
        	return;
		}
		if(hashList==null)
		{
			hashList=new ArrayList<HashMap<String,Object>>();
		}
		if(imghashList==null)
		{
			imghashList=new ArrayList<HashMap<String,Object>>();
		}
		UpdateHashList(mDeskList);
		if(gridSimpleAdapter==null)
		{			
			gridSimpleAdapter=CreategridAdapter(imghashList);
			mGridView.setAdapter(gridSimpleAdapter);
		}
		else {
			gridSimpleAdapter.notifyDataSetChanged();
		}
		if(listSimpleAdapter==null)
		{
			listSimpleAdapter=CreatelistAdapter(hashList);
			mListView.setAdapter(listSimpleAdapter);
		}
		else {
			listSimpleAdapter.notifyDataSetChanged();
		}
		
	}
	
	/***
	 * 按关键字搜索餐桌
	 * @param keyString
	 */
	public void Search(String keyString)
	{
		mDeskList.clear();
		if(keyString.equals(""))
		{
			Iterator<Desk> iterator;
			for(iterator=deskList.iterator();iterator.hasNext();)
			{
				Desk desk=iterator.next();
				mDeskList.add(desk);
			}
		}
		else {
			Iterator<Desk> iterator;
			for(iterator=deskList.iterator();iterator.hasNext();)
			{
				Desk desk=iterator.next();
				if(desk.getName().contains(keyString))
				{
					mDeskList.add(desk);
				}
			}
		}
		
		if(hashList==null)
		{
			hashList=new ArrayList<HashMap<String,Object>>();
		}
		if(imghashList==null)
		{
			imghashList=new ArrayList<HashMap<String,Object>>();
		}
		UpdateHashList(mDeskList);
		if(gridSimpleAdapter==null)
		{			
			gridSimpleAdapter=CreategridAdapter(imghashList);
			mGridView.setAdapter(gridSimpleAdapter);
		}
		else {
			gridSimpleAdapter.notifyDataSetChanged();
		}
		if(listSimpleAdapter==null)
		{
			listSimpleAdapter=CreatelistAdapter(hashList);
			mListView.setAdapter(listSimpleAdapter);
		}
		else {
			listSimpleAdapter.notifyDataSetChanged();
		}
	}
	
	/***
	 * 更新列表数据源
	 * @param deskInfos
	 */
	private void UpdateHashList(List<Desk> deskInfos)
	{
		hashList.clear();
		imghashList.clear();
		Iterator iterator;
		for (iterator = deskInfos.iterator(); iterator.hasNext();) {
			Desk deskInfo = (Desk) iterator.next();
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("title", deskInfo.getName());
  			map.put("id", deskInfo.getId());
  			map.put("count", deskInfo.getCapacity());
			Bitmap bmp;
			if(deskInfo.getCapacity()<=4)
			{
				bmp = MenuUtils.readBitMap(this, R.drawable.mytable, 1);
			}
			else if(deskInfo.getCapacity()>4&&deskInfo.getCapacity()<=10){
				bmp = MenuUtils.readBitMap(this, R.drawable.mytable, 1);
			}
			else {
				bmp = MenuUtils.readBitMap(this, R.drawable.mytable, 1);
			}
  			map.put("simg", bmp);
  			Bitmap duigouBitmap=null;
  			if(deskInfo.getOrderStatus()!=null)
  			{
  				if(deskInfo.getOrderStatus()==1)
  				{
  					map.put("status", 1);
  	  				map.put("code", deskInfo.getCode());
  	  				map.put("oid", deskInfo.getOid());
  	  				duigouBitmap=MenuUtils.readBitMap(this, R.drawable.duigou, 1);
  				}
  				else if(deskInfo.getOrderStatus()==3)
  				{
  					map.put("status", 3);
  	  				map.put("code", deskInfo.getCode());
  	  				map.put("oid", deskInfo.getOid());
  	  				duigouBitmap=MenuUtils.readBitMap(this, R.drawable.duihao, 1);
  				}
  			}
  			else {
  				map.put("status", null);
  				map.put("code", null);
  				map.put("oid", null);
  				duigouBitmap=Bitmap.createBitmap(30, 30, Bitmap.Config.ARGB_8888);
			}
  			map.put("duigou", duigouBitmap);
  			
  			hashList.add(map);
  			imghashList.add(map);
		}
	}
	
	/***
	 * 创建列表数据源适配器
	 * @param list
	 * @return
	 */
	public SimpleAdapter CreatelistAdapter(ArrayList<HashMap<String, Object>> list)
	{
		SimpleAdapter saImageItems = new SimpleAdapter(this,  
				list,
                R.layout.tablelistitem, 
                new String[] { "title","count","duigou"},  
                new int[] { R.id.tablename,R.id.capacity,R.id.imgselected}){
        };  

        saImageItems.setViewBinder(new CustomViewBinder());
		return saImageItems;
	}
	
	/***
	 * 创建表格数据源适配器
	 * @param list
	 * @return
	 */
	public SimpleAdapter CreategridAdapter(ArrayList<HashMap<String, Object>> list)
	{
		SimpleAdapter saImageItems = new SimpleAdapter(this,  
				list,
                R.layout.griditem, 
                new String[] { "simg", "title","duigou"},  
                new int[] { R.id.ItemImage, R.id.ItemText,R.id.ImageSelected}){
        };  

        saImageItems.setViewBinder(new CustomViewBinder());
		return saImageItems;
	}
	
	
	/***
	 * 旋转切换效果
	 * @param v1
	 * @param v2
	 */
	public void StartRotateAnimation(final View v1,final View v2)
	{
		v1.clearAnimation();
		v2.clearAnimation();
		Animation sAnimation=new HistoryRotateAnim(0, -DisplayUtil.dip2px(60), 0.0f, 0.0f,sWidth/2 , sHeight/2,true);
		sAnimation.setFillAfter(true);
		sAnimation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				rootLayout.removeAllViews();
				rootLayout.addView(v2);
				v2.layout(0, 0, sWidth, sHeight);
				//因为最初将gridLayout隐藏了所以这里要判断并设置可见
				if(v2.getVisibility()==View.INVISIBLE)
				{
					v2.setVisibility(View.VISIBLE);
				}
				Animation sAnimation1=new HistoryRotateAnim(DisplayUtil.dip2px(60), 0, 0.0f, 0.0f,sWidth/2, sHeight/2,false);
				sAnimation1.setFillAfter(true);
				v2.startAnimation(sAnimation1);
			}
		});
		v1.startAnimation(sAnimation);
	}
	
	/***
	 * 旋转切换效果
	 * @param v1
	 * @param v2
	 */
	public void StartBackRotateAnimation(final View v1,final View v2)
	{
		v1.clearAnimation();
		v2.clearAnimation();
		Animation sAnimation=new HistoryRotateAnim(0, DisplayUtil.dip2px(60), 0.0f, 0.0f,sWidth/2 , sHeight/2,true);
		sAnimation.setFillAfter(true);
		sAnimation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				rootLayout.removeAllViews();
				rootLayout.addView(v2);
				v2.layout(0, 0, sWidth, sHeight);
				Animation sAnimation1=new HistoryRotateAnim(-DisplayUtil.dip2px(60), 0, 0.0f, 0.0f,sWidth/2, sHeight/2,false);
				sAnimation1.setFillAfter(true);
				v2.startAnimation(sAnimation1);
				
				searchEditText.dispatchWindowFocusChanged(true);
			}
		});
		v1.startAnimation(sAnimation);
	}
	
	/**
	 * 显示错误信息
	 * @param strMess
	 */
	public void ShowError(String strMess) {
		mProgress.setVisibility(View.INVISIBLE);
		Toast toast = Toast.makeText(TableList.this, strMess, Toast.LENGTH_SHORT); 
        toast.show();
	}
	
	/***
	 * 开台操作（新启线程）
	 * @param id
	 * @param count
	 */
	public void RequestTable(final int id,final int count)
	{
		mProgress.setVisibility(View.VISIBLE);
		//开台
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					String resultString = HttpDownloader.submitOrder(MenuUtils.initUrl, id, count,
							m_Declare.loginResponse.getRestaurant().getId(),m_Declare.loginResponse.getToken());
					httpHandler.obtainMessage(3,resultString).sendToTarget();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					httpHandler.obtainMessage(0,e.getMessage()).sendToTarget();
				}
				
			}
		}).start();
		
			
	}
	
	/***
	 * 解析开台操作结果，跳转到餐桌页
	 * @param resultString
	 */
	public void ResponseTable(String resultString) {
		mProgress.setVisibility(View.INVISIBLE);

		try {
			System.out.println("resultString:"+resultString);
			Order order=JsonUtils.ParseJsonToOrder(resultString);
			
			Bitmap duigouBitmap=MenuUtils.readBitMap(TableList.this, R.drawable.duigou, 1);
  			selectedItem.put("duigou", duigouBitmap);
  			selectedItem.put("status", 1);
  			selectedItem.put("code", order.getCode());
  			selectedItem.put("oid", order.getId());
  			gridSimpleAdapter.notifyDataSetChanged();
  			listSimpleAdapter.notifyDataSetChanged();
            
		    Iterator<Desk> iterator;
		    for(iterator=deskList.iterator();iterator.hasNext();)
		    {
		    	Desk dObj=iterator.next();
		    	if(dObj.getId()==order.getDesk().getId())
		    	{
		    		dObj.setOrderStatus(order.getStatus());
		    		dObj.setCapacity(order.getNumber());
		    		dObj.setCode(order.getCode());
		    		dObj.setOid(order.getId());
		    		break;
		    	}
		    }
		    
		    
		    Intent intent=new Intent(this, OrderActivity.class);
		    intent.putExtra("order", order);
		    startActivity(intent);
		} catch (Throwable e) {
			e.printStackTrace();
			//自定义的错误，在界面上显示
			Toast toast = Toast.makeText(TableList.this, e.getMessage(), Toast.LENGTH_SHORT); 
            toast.show();
            return;
		}
	}
	
	/***
	 * 显示餐桌类别列表
	 */
	public void ExpandList()
	{
		typeLayout.clearAnimation();
		typeLayout.setVisibility(View.VISIBLE);
		Animation animation=new TranslateAnimation(0, 0, -DisplayUtil.dip2px(133), 0);
		animation.setDuration(300);
		animation.setInterpolator(new OvershootInterpolator());
		typeLayout.startAnimation(animation);
	}
	
	/***
	 * 隐藏餐桌类别列表
	 */
	public void HideList()
	{
		typeLayout.clearAnimation();
		Animation animation=new TranslateAnimation(0, 0, 0, -DisplayUtil.dip2px(133));
		animation.setDuration(300);
		animation.setInterpolator(new AccelerateInterpolator());
		typeLayout.startAnimation(animation);
		typeLayout.setVisibility(View.GONE);
	}
	
	/***
	 * 通过code获取订单
	 * @param codeString
	 */
	public void RequestOrderByCode(final String codeString) {
		mProgress.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					String resultString = HttpDownloader.getString(MenuUtils.initUrl+"orders/desk/"+codeString,m_Declare.loginResponse.getToken());
					if(resultString==null)
					{
						httpHandler.obtainMessage(0,"编码错误！").sendToTarget();
						return;
					}
					httpHandler.obtainMessage(4,resultString).sendToTarget();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					httpHandler.obtainMessage(0,e.getMessage()).sendToTarget();
				}
			}
		}).start();
	}
	
	/**
	 * 通过id获取订单
	 * @param oidString
	 */
	public void RequestOrderByOid(final String oidString)
	{
		mProgress.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					String resultString = HttpDownloader.getString(MenuUtils.initUrl+ "restaurants/"+m_Declare.loginResponse.getRestaurant().getId()+"/orders/"+oidString,
							m_Declare.loginResponse.getToken());
					if(resultString==null)
					{
						httpHandler.obtainMessage(0,"编码错误！").sendToTarget();
						return;
					}
					httpHandler.obtainMessage(4,resultString).sendToTarget();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					httpHandler.obtainMessage(0,e.getMessage()).sendToTarget();
				}
			}
		}).start();
	}
	
	/**
	 * 解析订单并跳转到订单页
	 * @param jsString
	 */
	public void ResponseOrder(String jsString)
	{
		mProgress.setVisibility(View.INVISIBLE);
		try {
			System.out.println("resultString:"+jsString);
			Order order=JsonUtils.ParseJsonToOrder(jsString);
			
		    Intent intent=new Intent(this, OrderActivity.class);
		    intent.putExtra("order", order);
		    startActivity(intent);
		} catch (Throwable e) {
			e.printStackTrace();
			//自定义的错误，在界面上显示
			Toast toast = Toast.makeText(TableList.this, e.getMessage(), Toast.LENGTH_SHORT); 
            toast.show();
            return;
		}
	}
	
	/***
	 * 点击餐桌类别框
	 * @author liuyan
	 *
	 */
	class TypeTxtOnclick implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(typeLayout.getVisibility()==View.GONE)
			{
				ExpandList();
			}
			else {
				HideList();
			}
		}
		
	}
	
	/***
	 * 点选餐桌类别
	 * @author liuyan
	 *
	 */
	class TypeItemClick implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			selectDeskType=types.get(arg2);
			String nameString=types.get(arg2).getName();
			typeAdapter.setSelectedName(nameString);
			typeTextView.setText(nameString);
			RequestDesklist();
			HideList();
		}		
	}
	
	/***
	 * 列表、缩略图切换处理
	 * @author liuyan
	 *
	 */
	class imgOnClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(listVisibility)
			{			
				StartRotateAnimation(listLayout,gridLayout);
				imgControl.setImageResource(R.drawable.image);
			}
			else {
				StartBackRotateAnimation(gridLayout, listLayout);
				imgControl.setImageResource(R.drawable.list);
			}
			listVisibility=!listVisibility;
		}
		
	}
	
	/***
	 * 列表项点击处理
	 * @author liuyan
	 *
	 */
	class  ItemClickListener implements OnItemClickListener     
	{     
		public void onItemClick(AdapterView<?> arg0,    
	                                  View arg1,   
	                                  int arg2,    
	                                  long arg3    
	                                  ) {     
		    HashMap<String, Object> item=(HashMap<String, Object>) arg0.getItemAtPosition(arg2); 
		    System.out.println("position:"+arg3);
		    System.out.println("name:"+item.get("title"));
		    if(item.get("status")!=null)
		    {
		    	String oidString=item.get("oid").toString();
		    	RequestOrderByOid(oidString);

	            return;
		    }
		    selectedItem=item;

		    TableList.this.showDialog(DIALOG_SEARCH);
		    //弹出软键盘
	        input.requestFocus();
	        Timer timer = new Timer(); //设置定时器
	        timer.schedule(new TimerTask() {
	        @Override
	        	public void run() { //弹出软键盘的代码
	        		InputMethodManager imm = (InputMethodManager)getSystemService(TableList.this.INPUT_METHOD_SERVICE);
	        		imm.showSoftInput(input, InputMethodManager.RESULT_SHOWN);
	        	}
	        }, 300); //设置300毫秒的时长
		} 
	}
	
	/***
	 * 缩略图表格项点击处理
	 * @author liuyan
	 *
	 */
	class  GridItemClickListener implements OnItemClickListener     
	{     
		public void onItemClick(AdapterView<?> arg0,    
	                                  View arg1,   
	                                  int arg2,    
	                                  long arg3    
	                                  ) {   
			TextView view=(TextView)arg1.findViewById(R.id.ItemText);
			String nameString=view.getText().toString();
			System.out.println("nameString:"+nameString);
		    HashMap<String, Object> item=(HashMap<String, Object>) arg0.getItemAtPosition(arg2); 
		    System.out.println("position:"+arg3);
		    System.out.println("name:"+item.get("title"));
		    if(item.get("status")!=null)
		    {
		    	String oidString=item.get("oid").toString();
		    	RequestOrderByOid(oidString);

	            return;
		    }
		    selectedItem=item;

		    TableList.this.showDialog(DIALOG_SEARCH);
		    //弹出软键盘
	        input.requestFocus();
	        Timer timer = new Timer(); //设置定时器
	        timer.schedule(new TimerTask() {
	        @Override
	        	public void run() { //弹出软键盘的代码
	        		InputMethodManager imm = (InputMethodManager)getSystemService(TableList.this.INPUT_METHOD_SERVICE);
	        		imm.showSoftInput(input, InputMethodManager.RESULT_SHOWN);
	        		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	        	}
	        }, 300); //设置300毫秒的时长
		} 
	}
	
	/***
	 * 弹出对话框
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		switch(id){
		case DIALOG_SEARCH:
			
			return new AlertDialog.Builder(this)
			
			.setTitle("输入人数").setView(input)
			.setPositiveButton("确定", new DialogInterface.OnClickListener(){
				
				public void onClick(DialogInterface dialog, int which) {
					String value = input.getText().toString();
					if(value.equals(""))
					{
						//隐藏软键盘
						InputMethodManager imm = (InputMethodManager)getSystemService(TableList.this.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(input.getWindowToken(), 0); 
						Toast toast = Toast.makeText(TableList.this, "没有输入就餐人数，开台失败。", Toast.LENGTH_SHORT); 
			            toast.show();
						return;
					}
					
	                int count=Integer.parseInt(value);		
					int id=(Integer)(selectedItem.get("id"));
					//隐藏软键盘
					InputMethodManager imm = (InputMethodManager)getSystemService(TableList.this.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(input.getWindowToken(), 0);  
					
					RequestTable(id, count);
				}
			
			})
			.setNegativeButton("取消", new DialogInterface.OnClickListener(){
				
				public void onClick(DialogInterface dialog, int which) {
					//隐藏软键盘
					InputMethodManager imm = (InputMethodManager)getSystemService(TableList.this.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(input.getWindowToken(), 0);  
				}
			
			}).create();			
		default:
			return null;
			
		}
	}
	/***
	 * 搜索输入框文字改变处理
	 * @author liuyan
	 *
	 */
	class EditTextChange implements TextWatcher{

		@Override
		public void afterTextChanged(Editable s) {}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if(count==0)
			{
				clearImg.setVisibility(View.INVISIBLE);
			}
			else {
				clearImg.setVisibility(View.VISIBLE);
			}
			Search(s.toString());
		}
		
	}
	
	/***
	 * 点击搜索框呼出输入法
	 * @author liuyan
	 *
	 */
	class EditTextClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			//弹出软键盘
	        searchEditText.requestFocus();
	        
//	        searchEditText.setCursorVisible(true);
//	        Timer timer = new Timer(); //设置定时器
//	        timer.schedule(new TimerTask() {
//	        @Override
//	        	public void run() { //弹出软键盘的代码
//	        		InputMethodManager imm = (InputMethodManager)getSystemService(TableListPage.this.INPUT_METHOD_SERVICE);
//	        		imm.showSoftInput(searchEditText, InputMethodManager.RESULT_SHOWN);
//	        		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//	        	}
//	        }, 100); //设置100毫秒的时长
		}
		
	}
	
	
	/***
	 * 清空搜索框
	 * @author liuyan
	 *
	 */
	class ClearOnClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			searchEditText.setText("");
			//隐藏软键盘
//			InputMethodManager imm = (InputMethodManager)getSystemService(TableList.this.INPUT_METHOD_SERVICE);
//			imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
		}
		
	}
}
