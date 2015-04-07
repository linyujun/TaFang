package com.bn.tag;
/*
 * 主控制activity
 * 
 */
import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import static com.bn.tag.Constant.*;

enum WhichView {WELCOME_VIEW,GAME_VIEW,MUSIC_VIEW,HELP_VIEW,JIFEN_VIEW,MAINMENU_VIEW};
public class TafangGameActivity extends Activity {
	GameView gameView;//游戏界面
	WelcomeView welcomeview;//欢迎界面
	MainMenuSurfaceView MainMenuView;//主界面
	HighJifenSurfaceView ScoreView;//积分界面
	MusicSurfaceView musicView;//音乐设置界面
	GameOverView gameoverView;//游戏结束界面
	HelpView helpview;//帮助界面
	static SQLiteDatabase sldd;//数据库
	Dialog Inputdialog;//游戏存档对话框
	Dialog GoonGamedialog;//继续游戏选择存档进度对话框
	String name;//继续游戏对话框中选择的存档name
	int screenWidth;
	int screenHeight;
	List<String> nearlist=new Vector<String>();//所有存档结果
	//声音播放标志位
	private boolean backGroundMusicOn=true;//背景音乐是否开启的标志
	private boolean soundOn=true;//音效是否开启的标志
	boolean cundang_Flag=false;//标志进入游戏界面是否由继续游戏选择存档进入
	
	//震动
	Vibrator mVibrator;//声明振动器
	boolean shakeflag=true;//是否震动
	
	
	WhichView curr;//标志当前界面
	Handler myHandler = new Handler(){//处理各个SurfaceView发送的消息
		@Override
		public void handleMessage(Message msg) {
        	switch(msg.what)
        	{
        	case 0:
        		gotoGameView();
        		break;
        	case 1:
        		gotoMainMenuView();
        		break;
        	case 2:
        		gotoHelpView();
        		break;
        	case 3:
        		gotoJifenView();
        		break;
        	case 4:
        		gotoMusicView();
        		break;
        	case 5:
        		gotoGameOverView();
        		break;
        	}
        }
	};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth=dm.widthPixels;
        screenHeight=dm.heightPixels;
		//设置全屏
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN ,  
		              WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//设置为横屏
		collisionShake();//设置震动
		gotoWelcomeView();      
    }   
    
    //欢迎界面
    private void gotoWelcomeView()
    {
    	if(welcomeview==null)
    	{
    		 welcomeview = new WelcomeView(this);
    	}
    	this.setContentView(welcomeview);
    	curr=WhichView.WELCOME_VIEW;
    }
    
    private void gotoGameOverView()
    {
    	if(gameoverView==null)
    	{
    		gameoverView=new GameOverView(this);
    	}
    	this.setContentView(gameoverView);
    }
    //游戏界面
    private void gotoGameView()
    {
    	gameView=new GameView(this);   	
    	this.setContentView(gameView);
    	curr=WhichView.GAME_VIEW;
    	
    }
    //主界面
    private void gotoMainMenuView()
    {
    	MainMenuView=new MainMenuSurfaceView(this);
    	this.setContentView(MainMenuView);
    	curr=WhichView.MAINMENU_VIEW;
    }
    //帮助界面
    public void gotoHelpView()
    {
    	if(helpview==null)
    	{
    		helpview=new HelpView(this);
    	}
    	this.setContentView(helpview);
    	curr=WhichView.HELP_VIEW;
    }
    //积分界面
    public void gotoJifenView()
    {
    	if(ScoreView==null)
    	{
    		ScoreView=new HighJifenSurfaceView(this);
    	}
    	this.setContentView(ScoreView);
    	curr=WhichView.JIFEN_VIEW;
    }
    //音乐设置界面
    public void gotoMusicView()
    {
    	if(musicView==null)
    	{
    		musicView=new MusicSurfaceView(this);
    	}
    	this.setContentView(musicView);
    	curr=WhichView.MUSIC_VIEW;
    }
    //游戏中弹出的存档对话框
    @SuppressWarnings("deprecation")
	public void gototachu()
    {
    	showDialog(CUN_DANG_DIALOG_ID);
    }
    //选择继续游戏弹出选择存档进度对话框
    @SuppressWarnings("deprecation")
	public void gotoTaChuGame()
    {
//    	查询数据库里面所有的存档记录
    	nearlist=DBUtil.getcundang();
    	showDialog(GO_ONGAME_DIALOG_ID);
//    	调用此方法会继续回调onCreateDialog（）和onPrepareDialog（）方法创建对话框
    }
    //传播信息
    public void sendMessage(int what)
    {
    	Message msg1 = myHandler.obtainMessage(what); 
    	myHandler.sendMessage(msg1);
    } 
    //获得声音播放标志位的函数
    public boolean isBackGroundMusicOn() {
		return backGroundMusicOn;
	}
	public void setBackGroundMusicOn(boolean backGroundMusicOn) {
		this.backGroundMusicOn = backGroundMusicOn;
	}
	
	public boolean isSoundOn() {
		return soundOn;
	}
	public void setSoundOn(boolean soundOn) {
		this.soundOn = soundOn;
	}
    
	
	//手机震动
    public void collisionShake()
    {
    		mVibrator=(Vibrator)getApplication().getSystemService
            (Service.VIBRATOR_SERVICE);	
    }
    //震动
    public void shake()
    {
    	if(shakeflag)
    	{
    		mVibrator.vibrate( new long[]{0,80},-1);
    	}
    }
//    监听手机返回键
    @Override
    public boolean onKeyDown(int keyCode,KeyEvent e) 
    {
    	if(keyCode==4)//调制上一个界面的键
    	{
    		if(curr==WhichView.HELP_VIEW)
    		{
    			gotoMainMenuView();
    			return true;
    		}
    		if(curr==WhichView.JIFEN_VIEW||curr==WhichView.MUSIC_VIEW)
    		{
    			gotoMainMenuView();
    			return true;
    		}
    	}
		return false;
    		
    }
    
//    打开或者创建数据库，用于存储积分信息
    public static void openOrCreateDatabase()
    {
    	try
    	{
	    	sldd=SQLiteDatabase.openDatabase
	    	(
	    			"/data/data/com.bn.tag/ttdb", //数据库所在路径
	    			null, 								//CursorFactory
	    			SQLiteDatabase.OPEN_READWRITE|SQLiteDatabase.CREATE_IF_NECESSARY //读写、若不存在则创建
	    	);
	    	String sql="create table if not exists highScore" +
	    			"( " +
	    			"score integer," +
	    			"date varchar(20)" +
	    			");";
	    	sldd.execSQL(sql);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
	
	//关闭数据库的方法
    public void closeDatabase()
    {
    	try
    	{
	    	sldd.close();
    	}
		catch(Exception e)
		{
			e.printStackTrace();
		}
    }
    
  //插入积分记录的方法
    public void insert(int score,String date)
    {
    	try
    	{
    		openOrCreateDatabase();
        	String sql="insert into highScore values("+score+",'"+date+"');";
        	sldd.execSQL(sql);
        	sldd.close();
    	}
		catch(Exception e)
		{
			e.printStackTrace();
		}
    }
    //分页查询积分信息的方法
    public String query(int posFrom,int length)//开始的位置，要查寻的记录条数
    {
    	StringBuilder sb=new StringBuilder();//要返回的结果
    	Cursor cur=null;
    	openOrCreateDatabase();
        String sql="select score,date from highScore order by score desc;";    	
        cur=sldd.rawQuery(sql, null);
    	try
    	{
    		
        	cur.moveToPosition(posFrom);//将游标移动到指定的开始位置
        	int count=0;//当前查询记录条数
        	while(cur.moveToNext()&&count<length)
        	{
        		int score=cur.getInt(0);
        		String date=cur.getString(1);        		
        		sb.append(score);
        		sb.append("/");
        		sb.append(date);
        		sb.append("/");//将记录用"/"分隔开
        		count++;
        	}        			
    	}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			cur.close();
			closeDatabase();
		}
		//转换成字符，并返回
		return sb.toString();
    }
    //得到数据库中记录条数的方法
    public int getRowCount()
    {
    	int result=0;
    	Cursor cur=null;
    	openOrCreateDatabase();
    	try
    	{
    		String sql="select count(score) from highScore;";    	
            cur=sldd.rawQuery(sql, null);
        	if(cur.moveToNext())
        	{
        		result=cur.getInt(0);
        		
        	}
    	}
    	catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			cur.close();
			closeDatabase();
		}
        
    	return result;
    }
    //将得分和时间插入数据库，并跳转到相应的结束界面
    public void insertScoreAndDate(int currScore)
    {
    	//Cursor cur=null;
    	//openOrCreateDatabase();//打开或创建数据库
    	try
    	{	
        	insert(currScore,DateUtil.getCurrentDate());//获得当前分数和日期并插入数据库        	
    	}
		catch(Exception e)
		{
			Toast.makeText(this, "数据库错误："+e.toString(), Toast.LENGTH_SHORT).show();
		}
		finally
		{
			//closeDatabase();
			//cur.close();
			
		}		
    }
    
    @Override
    public Dialog onCreateDialog(int id)//回调创建对话框方法
    {    	
        Dialog result=null;
    	switch(id)
    	{
	    	case CUN_DANG_DIALOG_ID://存档对话框
		    	Inputdialog=new MyDialogue(this); 	    
				result=Inputdialog;				
			break;
	    	case GO_ONGAME_DIALOG_ID:	 //选择游戏存档对话框   		
	    		AlertDialog.Builder b2=new AlertDialog.Builder(this); 
				  b2.setItems(
					null, 
				    null
				   );
				  GoonGamedialog=b2.create();       //创建Dialog
				  result=GoonGamedialog;	    		
		    	break;
    	}   
		return result;
    }
    
    //每次弹出对话框时被回调以动态更新对话框内容的方法
    @Override
    public void onPrepareDialog(int id, final Dialog dialog)
    {
    	//若不是等待对话框则返回
    	switch(id)
    	{
    	   case CUN_DANG_DIALOG_ID://存档输入对话框
    		   //确定按钮
    		   Button bjhmcok=(Button)Inputdialog.findViewById(R.id.bjhmcOk);
    		   //取消按钮
       		   Button bjhmccancel=(Button)Inputdialog.findViewById(R.id.bjhmcCancle);
       		   //给确定按钮添加监听器
       		   bjhmcok.setOnClickListener
               (
    	          new OnClickListener()
    	          {
    				@Override
    				public void onClick(View v) 
    				{
    					//获取对话框里的内容并用Toast显示
    					EditText et=(EditText)Inputdialog.findViewById(R.id.etname);
    					String name=et.getText().toString();
    					 String currentDate=DateUtil.getCurrentDate();
//    					 插入存档名称
    					 String sql1="insert into save values('"+name+"','"+currentDate+"')";
    					 DBUtil.updatetable(sql1);    					 
    					 //插入箭塔信息到箭塔表
    					 for(int i=0;i<gameView.jiantaList.size();i++)
    					 {    						
    						 String sql="insert into jiant values ('"+name+"','"+gameView.jiantaList.get(i).clo+"','"+gameView.jiantaList.get(i).row+"','"+gameView.jiantaList.get(i).state+"')";
    						 DBUtil.updatetable(sql);
    					 }
    					 //插入怪物信息到怪物表
    					 for(int i=0;i<gameView.alTarget1.size();i++)
    					 {    						 
    						 String sql="insert into guaiw values ('"+name+"','"+gameView.alTarget1.get(i).ballx+"','"+gameView.alTarget1.get(i).bally+"','"+gameView.alTarget1.get(i).state+"','"+gameView.alTarget1.get(i).direction*180/Math.PI+"','"+gameView.alTarget1.get(i).ii+"','"+gameView.alTarget1.get(i).bloodsum+"','"+gameView.alTarget1.get(i).bloodsumNO+"')";
    						 DBUtil.updatetable(sql);
    					 }
//    					 插入玩家血量金钱水晶等信息
    					 String sql="insert into nochange values ('"+name+"','"+gameView.doller+"','"+gameView.bloodNUM+"','"+gameView.shaNUM+"','"+gameView.shuijingMiddleNum+"')";
    					 DBUtil.updatetable(sql);
    					//关闭对话框
    					Inputdialog.cancel();
    				}        	  
    	          }
    	        );   
       		    //给取消按钮添加监听器
       		    bjhmccancel.setOnClickListener
	            (
	 	          new OnClickListener()
	 	          {
	 				@Override
	 				public void onClick(View v) 
	 				{
	 					//关闭对话框
	 					Inputdialog.cancel();					
	 				}        	  
	 	          }
	 	        );   
    	   break;
    	   case GO_ONGAME_DIALOG_ID://存档选择对话框
    			//对话框对应的总垂直方向LinearLayout
    		   	LinearLayout ll=new LinearLayout(TafangGameActivity.this);
    			ll.setOrientation(LinearLayout.VERTICAL);		//设置朝向	
    			ll.setGravity(Gravity.CENTER_HORIZONTAL);   			
    			ll.setBackgroundResource(R.drawable.dialog);
    			
    			//标题行的水平LinearLayout
    			LinearLayout lln=new LinearLayout(TafangGameActivity.this);
    			lln.setOrientation(LinearLayout.HORIZONTAL);		//设置朝向	
    			lln.setGravity(Gravity.CENTER);    		
    			
    			//标题行的文字
    			TextView tvTitle=new TextView(TafangGameActivity.this);
    			tvTitle.setText("选择游戏相应存档");
    			tvTitle.setTextSize(20);//设置字体大小
    			tvTitle.setTextColor(Color.WHITE);//设置字体颜色
    			lln.addView(tvTitle);
    			
    			//将标题行添加到总LinearLayout
    			ll.addView(lln);		
    		   	
    		   	//为对话框中的历史记录条目创建ListView
    		    //初始化ListView
    	        ListView lv=new ListView(this);
    	        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);         		   
    		        BaseAdapter ba=new BaseAdapter()
    		        {
    					@Override
    					public int getCount() {
    						return nearlist.size();//总共几个选项
    					}

    					@Override
    					public Object getItem(int arg0) { return null; }

    					@Override
    					public long getItemId(int arg0) { return 0; }

    					@Override
    					public View getView(int arg0, View arg1, ViewGroup arg2) {
    						//动态生成每条历史记录对应的TextView
    						TextView tv=new TextView(TafangGameActivity.this);
    						tv.setGravity(Gravity.CENTER);
    						tv.setText(nearlist.get(arg0));//设置内容
    						tv.setTextSize(20);//设置字体大小
    						tv.setTextColor(Color.WHITE);//设置字体颜色
    						tv.setPadding(0,0,0,0);//设置四周留白				
    						return tv;
    					}        	
    		        };       
    		        lv.setAdapter(ba);//为ListView设置内容适配器
    		        
    		        //设置选项被单击的监听器
    		        lv.setOnItemClickListener(
    		           new OnItemClickListener()
    		           {
    					@Override
    					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
    							long arg3) {//重写选项被单击事件的处理方法 
    						//记录选择的是哪个游戏存档
    						name=nearlist.get(arg2);
    						//将进入游戏界面的标志设置为选择存档进入
    						cundang_Flag=true;
    						gotoGameView();			
    						GoonGamedialog.cancel();
    					}        	   
    		           }
    		        );             
    		        //将历史记录条的ListView加入总LinearLayout
    		        ll.addView(lv);
    		        dialog.setContentView(ll);
    		   	}
    	}
}    
    
    

	
