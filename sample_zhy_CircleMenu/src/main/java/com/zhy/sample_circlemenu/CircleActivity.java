package com.zhy.sample_circlemenu;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.anupcowkur.wheelmenu.WheelMenu;
import com.zhy.view.CircleMenuLayout;
import com.zhy.view.CircleMenuLayout.OnMenuItemClickListener;
/**
 * <pre>
 * @author zhy 
 * http://blog.csdn.net/lmj623565791/article/details/43131133
 * </pre>
 */
public class CircleActivity extends Activity
{

//	private CircleMenuLayout mCircleMenuLayout;
////	private WheelMenu wheelMenu;
//	private String[] mItemTexts = new String[] { "sleeping ", "work", "game",
//			"game and work", "game and sleep", "fuck you" };
//	private int[] mItemImgs = new int[] { R.drawable.home_mbank_1_normal,
//			R.drawable.home_mbank_2_normal, R.drawable.home_mbank_3_normal,
//			R.drawable.home_mbank_4_normal, R.drawable.home_mbank_5_normal,
//			R.drawable.home_mbank_6_normal };

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		//自已切换布局文件看效果
		setContentView(R.layout.rwq);
//		setContentView(R.layout.activity_main);
//		wheelMenu = (WheelMenu) findViewById(R.id.wheelMenu);
//		mCircleMenuLayout = (CircleMenuLayout) findViewById(R.id.id_menulayout);
//		mCircleMenuLayout.setMenuItemIconsAndTexts(mItemImgs, mItemTexts);
//		wheelMenu.setDivCount(12);
//
//
//
//		mCircleMenuLayout.setOnMenuItemClickListener(new OnMenuItemClickListener()
//		{
//
//			@Override
//			public void itemClick(View view, int pos)
//			{
//				Toast.makeText(CircleActivity.this, mItemTexts[pos],
//						Toast.LENGTH_SHORT).show();
//
//			}
//
//			@Override
//			public void itemCenterClick(View view)
//			{
//				Toast.makeText(CircleActivity.this,
//						"you can do something just like ccb  ",
//						Toast.LENGTH_SHORT).show();
//
//			}
//		});

	}

}
