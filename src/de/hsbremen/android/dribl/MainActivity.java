package de.hsbremen.android.dribl;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MainActivity extends Activity {
	
	public SlidingMenu slideMenu = null;
	public ListView sideList = null;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        slideMenu = new SlidingMenu(this);
        
        slideMenu.setMenu(R.layout.layout_sidemenu);
        slideMenu.setMode(SlidingMenu.LEFT);
        slideMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        slideMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        slideMenu.setBehindOffsetRes(R.dimen.slidemenu_offset);
        sideList = (ListView) slideMenu.findViewById(R.id.sideList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.menu_items));
        sideList.setAdapter(adapter);
        slideMenu.isSecondaryMenuShowing();
    }

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void onButtonClick(View view) {
    	switch (view.getId()) {
		case R.id.btn_sidebar_open:
			slideMenu.showMenu();
			break;

		default:
			break;
		}
    }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// close the sidemenu on backbutton click
		if (slideMenu.isMenuShowing()) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				slideMenu.showContent();
				Toast.makeText(this, "close menu", Toast.LENGTH_SHORT).show();
				return true;
			}
		}
		
		return super.onKeyDown(keyCode, event);
	}
    
    
}
