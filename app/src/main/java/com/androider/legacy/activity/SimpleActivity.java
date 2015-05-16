package com.androider.legacy.activity;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.androider.legacy.R;
import com.androider.legacy.controller.StateController;
import com.androider.legacy.data.Constants;
import com.balysv.materialmenu.MaterialMenuDrawable;
import com.balysv.materialmenu.extras.toolbar.MaterialMenuIconToolbar;

public class SimpleActivity extends ActionBarActivity {

    Toolbar searchTool;
    MaterialMenuIconToolbar mateMenu;

    protected void setToolBar(){
        searchTool = (Toolbar)findViewById(R.id.simple_toolbar);
        setSupportActionBar(searchTool);
        searchTool.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backControl();
            }
        });
        mateMenu = new MaterialMenuIconToolbar(this, Color.WHITE, MaterialMenuDrawable.Stroke.THIN) {
            @Override
            public int getToolbarViewId() {
                return R.id.simple_toolbar;
            }
        };
        mateMenu.setNeverDrawTouch(true);
        mateMenu.setState(MaterialMenuDrawable.IconState.X);
        StateController.change(Constants.funcState);
    }

    @Override
    public void onBackPressed() {
        backControl();
    }

    private void backControl(){
        if(StateController.getCurrent() == Constants.detailState){
            getSupportFragmentManager().popBackStack();
            mateMenu.animateState(MaterialMenuDrawable.IconState.X);
            StateController.goBack();
        }else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StateController.goBack();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_simple, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
