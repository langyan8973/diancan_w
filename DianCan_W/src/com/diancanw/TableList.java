package com.diancanw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import com.diancanw.animation.HistoryRotateAnim;
import com.diancanw.animation.Rotate3dAnimation;
import com.diancanw.custom.CategoryListAdapter;
import com.diancanw.custom.CustomViewBinder;
import com.diancanw.declare.DiancanwApp;
import com.diancanw.http.HttpDownloader;
import com.diancanw.http.HttpHandler;
import com.diancanw.http.HttpRequestCallback;
import com.diancanw.http.activitytools.DeskListHttpCallback;
import com.diancanw.http.activitytools.HttpToolForDeskList;
import com.diancanw.model.Category;
import com.diancanw.model.Desk;
import com.diancanw.model.DeskType;
import com.diancanw.model.Order;
import com.diancanw.utils.DisplayUtil;
import com.diancanw.utils.JsonUtils;
import com.diancanw.utils.MenuUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class TableList extends Activity implements DeskListHttpCallback,OnClickListener,
					OnItemClickListener,TextWatcher{
	List<DeskType> types;
	List<Desk> deskList;
	List<Desk> mDeskList;
	ArrayList<HashMap<String, Object>> hashList;
	ArrayList<HashMap<String, Object>> imghashList;
	CategoryListAdapter typeAdapter;
	SimpleAdapter gridSimpleAdapter;
	SimpleAdapter listSimpleAdapter;
	ViewGroup rootLayout;
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
	int sWidth;
	int sHeight;
	boolean listVisibility;
	DeskType selectDeskType;
	private final int DIALOG_SEARCH = 0x100;
	HashMap<String, Object> selectedItem;
	
	EditText input;
	DiancanwApp m_Declare;
	HttpToolForDeskList mHttpToolForDeskList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.tablelist);
        Init();
		
		rootLayout=(ViewGroup)findViewById(R.id.contentFrame);
		listLayout=(LinearLayout)findViewById(R.id.listLayout);
		listVisibility=true;
		
		typeLayout=(LinearLayout)findViewById(R.id.typelayout);
		typeLayout.setVisibility(View.GONE);
		typeList=(ListView)findViewById(R.id.ListDeskType);
		typeTextView=(TextView)findViewById(R.id.TxtDeskType);
		typeTextView.setOnClickListener(this);
		
		searchEditText=(EditText)findViewById(R.id.searchExt);
		searchEditText.addTextChangedListener(this);
		searchEditText.setOnClickListener(this);
		
		clearImg=(ImageView)findViewById(R.id.ImgClear);
		clearImg.setOnClickListener(this);
		clearImg.setVisibility(View.INVISIBLE);
		
		mGridView=(GridView)findViewById(R.id.deskGrid);
		mListView=(ListView)findViewById(R.id.deskList);
		mGridView.setOnItemClickListener(this);
		mListView.setOnItemClickListener(this);
		
		imgControl=(ImageView)findViewById(R.id.imgcontrol);
		imgControl.setOnClickListener(this);
		
		mProgress=(ProgressBar)findViewById(R.id.httppro);
		mProgress.setVisibility(View.INVISIBLE);
		
		input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        mHttpToolForDeskList=new HttpToolForDeskList(m_Declare, this);
        mHttpToolForDeskList.RequestRecipeTypes();
        
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		RequestTypes();
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
		case R.id.TxtDeskType:
			if(typeLayout.getVisibility()==View.GONE)
			{
				ExpandList();
			}
			else {
				HideList();
			}
			break;
		case R.id.searchExt:
			//弹出软键盘
	        searchEditText.requestFocus();
			break;
		case R.id.ImgClear:
			searchEditText.setText("");
			break;
		case R.id.imgcontrol:
			if(listVisibility)
			{		
				applyRotation(0, 90);
				imgControl.setImageResource(R.drawable.image);
			}
			else {
				applyRotation(0, -90);
				imgControl.setImageResource(R.drawable.list);
			}
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		if(arg0.getId()==R.id.ListDeskType){
			selectDeskType=types.get(arg2);
			String nameString=types.get(arg2).getName();
			typeAdapter.setSelectedName(nameString);
			typeTextView.setText(nameString);
			RequestDesklist();
			HideList();
			return;
		}
		
		HashMap<String, Object> item=(HashMap<String, Object>) arg0.getItemAtPosition(arg2); 
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
	
	
	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		if(count==0)
		{
			clearImg.setVisibility(View.INVISIBLE);
		}
		else {
			clearImg.setVisibility(View.VISIBLE);
		}
		Search(s.toString());
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
		m_Declare=(DiancanwApp)this.getApplicationContext();
	}
	
	/***
	 * 请求餐桌类别
	 */
	private void RequestTypes() {
		mProgress.setVisibility(View.VISIBLE);
		mHttpToolForDeskList.RequestTypes();
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
		mHttpToolForDeskList.RequestDeskListByTid(selectDeskType.getId());
	}
	
	/***
	 * 开台操作
	 * @param id
	 * @param count
	 */
	public void RequestTable(final int id,final int count)
	{
		mProgress.setVisibility(View.VISIBLE);
		mHttpToolForDeskList.RequestTable(id, count);
	}
	
	/**
	 * 通过id获取订单
	 * @param oidString
	 */
	public void RequestOrderByOid(final String oidString)
	{
		mProgress.setVisibility(View.VISIBLE);
		mHttpToolForDeskList.RequestOrderByOid(oidString);
	}
	
	
	@Override
	public void SetDeskTypes(List<DeskType> deskTypes) {
		// TODO Auto-generated method stub
		mProgress.setVisibility(View.INVISIBLE);
		types=deskTypes;
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
		typeList.setOnItemClickListener(this);
		typeTextView.setText(type.getName());
		RequestDesklist();
		
	}

	@Override
	public void SetDeskList(List<Desk> desks) {
		// TODO Auto-generated method stub
		mProgress.setVisibility(View.INVISIBLE);
		deskList=desks;
		try {		
			Iterator<Desk> iterator;
			for(iterator=deskList.iterator();iterator.hasNext();)
			{
				Desk desk=iterator.next();
				if(desk.getWid()==0||desk.getWid()==m_Declare.loginResponse.getWaiterid()){
					
					mDeskList.add(desk);
				}
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
		rootLayout.setPersistentDrawingCache(ViewGroup.PERSISTENT_ANIMATION_CACHE);
	}

	@Override
	public void SetNewOrder(Order order) {
		// TODO Auto-generated method stub
		mProgress.setVisibility(View.INVISIBLE);

		try {
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
			ShowError(e.getMessage());
            return;
		}
	}

	@Override
	public void SetOrder(Order order) {
		// TODO Auto-generated method stub
		mProgress.setVisibility(View.INVISIBLE);
		try {
		    Intent intent=new Intent(this, OrderActivity.class);
		    intent.putExtra("order", order);
		    startActivity(intent);
		} catch (Throwable e) {
			e.printStackTrace();
			//自定义的错误，在界面上显示
			ShowError(e.getMessage());
            return;
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
		rootLayout.setPersistentDrawingCache(ViewGroup.PERSISTENT_ANIMATION_CACHE);
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
	
	
	
	private void applyRotation(float start, float end) { 
		// 计算中心点 
		final float centerX = rootLayout.getWidth() / 2.0f; 
		final float centerY = rootLayout.getHeight() / 2.0f;

		// Create a new 3D rotation with the supplied parameter 
		// The animation listener is used to trigger the next animation 
		final Rotate3dAnimation rotation = 
		new Rotate3dAnimation(start, end, centerX, centerY, 310.0f, true); 
		rotation.setDuration(300); 
		rotation.setFillAfter(true); 
		rotation.setInterpolator(new AccelerateInterpolator()); 
		//设置监听 
		rotation.setAnimationListener(new DisplayNextView());

		rootLayout.startAnimation(rotation); 
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
	
	private final class DisplayNextView implements Animation.AnimationListener { 

		private DisplayNextView() { 
		}

		public void onAnimationStart(Animation animation) { 
		} 
		//动画结束 
		public void onAnimationEnd(Animation animation) { 
			rootLayout.post(new SwapViews()); 
		}

		public void onAnimationRepeat(Animation animation) { 
		} 
	}
	
	private final class SwapViews implements Runnable { 

		public SwapViews() { 
		}

		public void run() { 
			final float centerX = rootLayout.getWidth() / 2.0f; 
			final float centerY = rootLayout.getHeight() / 2.0f; 
			Rotate3dAnimation rotation;
	
			if (listVisibility) { 
				//显示GirdView 
				listLayout.setVisibility(View.GONE); 
				mGridView.setVisibility(View.VISIBLE); 
				mGridView.requestFocus();
				rotation = new Rotate3dAnimation(-90, 0, centerX, centerY, 310.0f, false);
			} else { 
				//返回listview 
				mGridView.setVisibility(View.GONE); 
				listLayout.setVisibility(View.VISIBLE); 
				listLayout.requestFocus();
				rotation = new Rotate3dAnimation(90, 0, centerX, centerY, 310.0f, false); 
			}
			listVisibility=!listVisibility;
	
			rotation.setDuration(300); 
			rotation.setFillAfter(true); 
			rotation.setInterpolator(new DecelerateInterpolator()); 
			//开始动画 
			rootLayout.startAnimation(rotation); 
		} 
	}

}
