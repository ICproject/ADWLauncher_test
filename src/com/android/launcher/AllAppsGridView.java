/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.launcher;

import android.widget.GridView;
import android.widget.AdapterView;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Canvas;

public class AllAppsGridView extends GridView implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener, DragSource {

    private DragController mDragger;
    private Launcher mLauncher;
    private Bitmap mTexture;
    private Paint mPaint;
    private int mTextureWidth;
    private int mTextureHeight;
    //ADW:Hack the texture thing to make scrolling faster
    private boolean forceOpaque=false;
    public AllAppsGridView(Context context) {
        super(context);
    }

    public AllAppsGridView(Context context, AttributeSet attrs) {
        this(context, attrs, com.android.internal.R.attr.gridViewStyle);
    }

    public AllAppsGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AllAppsGridView, defStyle, 0);
        //TODO: ADW-Check if it's necessary
        boolean bootOpaque=AlmostNexusSettingsHelper.getDrawerFast(context);
        setForceOpaque(bootOpaque);
        //if(!forceOpaque){
	        final int textureId = a.getResourceId(R.styleable.AllAppsGridView_texture, 0);
	        if (textureId != 0) {
	            mTexture = BitmapFactory.decodeResource(getResources(), textureId);
	            mTextureWidth = mTexture.getWidth();
	            mTextureHeight = mTexture.getHeight();
	
	            mPaint = new Paint();
	            mPaint.setDither(false);
	        }
        //}
        a.recycle();
    }

    @Override
    public boolean isOpaque() {
        if(forceOpaque) return true;
        else return !mTexture.hasAlpha();
    }

    @Override
    protected void onFinishInflate() {
        setOnItemClickListener(this);
        setOnItemLongClickListener(this);
    }

    @Override
    public void draw(Canvas canvas) {
        //TODO: ADW-Check if this is necessary
    	if(!forceOpaque){
	    	final Bitmap texture = mTexture;
	        final Paint paint = mPaint;
	
	        final int width = getWidth();
	        final int height = getHeight();
	
	        final int textureWidth = mTextureWidth;
	        final int textureHeight = mTextureHeight;
	
	        int x = 0;
	        int y;
	
	        while (x < width) {
	            y = 0;
	            while (y < height) {
	                canvas.drawBitmap(texture, x, y, paint);
	                y += textureHeight;
	            }
	            x += textureWidth;
	        }
    	}
        super.draw(canvas);
    }

    public void onItemClick(AdapterView parent, View v, int position, long id) {
        ApplicationInfo app = (ApplicationInfo) parent.getItemAtPosition(position);
        mLauncher.startActivitySafely(app.intent);
    }

    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (!view.isInTouchMode()) {
            return false;
        }

        ApplicationInfo app = (ApplicationInfo) parent.getItemAtPosition(position);
        app = new ApplicationInfo(app);

        mDragger.startDrag(view, this, app, DragController.DRAG_ACTION_COPY);
        if(!mLauncher.isDockBarOpen()){
        	mLauncher.closeAllApplications();
        }

        return true;
    }

    public void setDragger(DragController dragger) {
        mDragger = dragger;
    }

    public void onDropCompleted(View target, boolean success) {
    }

    void setLauncher(Launcher launcher) {
        mLauncher = launcher;
    }
    public void setForceOpaque(boolean value){
    	if(value!=forceOpaque){
	    	forceOpaque=value;
	    	if(value){
	    		this.setBackgroundColor(0xFF000000);
	    		this.setCacheColorHint(0xFF000000);
	    		this.setDrawingCacheBackgroundColor(0xFF000000);
	    		setScrollingCacheEnabled(true);
	    	}else{
	    		this.setBackgroundDrawable(null);
	    		this.setCacheColorHint(Color.TRANSPARENT);
	    		super.setCacheColorHint(Color.TRANSPARENT);
	    		this.setDrawingCacheBackgroundColor(Color.TRANSPARENT);
	    		setScrollingCacheEnabled(true);
	    	}
    	}
    }
}
