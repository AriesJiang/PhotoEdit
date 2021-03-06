/*
 *          Copyright (C) 2016 jarlen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package cn.jarlen.photoedit.operate;

import android.content.Context;
import android.graphics.*;
import android.text.TextUtils;
import android.util.Log;

/**
 * @author jarlen
 */
public class TextObject extends ImageObject
{

	private int textSize = 400;
	private final int textSizeMax = 400;
	private int color = Color.YELLOW ;
	private String typeface;
	private String text;
	private String textOld;
	private boolean bold = false;
	private boolean italic = false;
	private Context context;

	private Paint paint = new Paint();

	/**
	 * 构造方法
	 * 
	 * @param context
	 *            上下文
	 * @param text
	 *            输入的文字
	 * @param rotateBm
	 *            旋转按钮的图片
	 * @param deleteBm
	 *            删除按钮的图片
     *            https://blog.csdn.net/momo_ibeike/article/details/60324344
	 */
	public TextObject(Context context, String text, int quadrant, int width, int height,
			Bitmap rotateBm, Bitmap deleteBm)
	{
		super();
		this.context = context;
		this.text = text;
		this.textOld = null;
		this.rotateBm = rotateBm;
		this.deleteBm = deleteBm;
		regenerateBitmap();
		setScale(0.3f);
		regenerateBitmap();

		switch (quadrant)
		{
			case OperateUtils.LEFTTOP :
				mPoint.x = (int) Math.max(srcBm.getWidth() * getScale()/2 + deleteBm.getWidth(), 0);
				mPoint.y = (int) Math.max(srcBm.getHeight() * getScale()/2  + deleteBm.getHeight(), 0);
				break;
			case OperateUtils.RIGHTTOP :
				mPoint.x = (int) Math.max(width - srcBm.getWidth() * getScale() + srcBm.getWidth() * getScale()/2 - rotateBm.getWidth(), 0);
				mPoint.y = (int) Math.max(srcBm.getHeight() * getScale()/2  + deleteBm.getHeight(), 0);
				break;
			case OperateUtils.LEFTBOTTOM :
				mPoint.x = (int) Math.max(srcBm.getWidth() * getScale()/2 + deleteBm.getWidth(), 0);
				mPoint.y = (int) Math.max(height - srcBm.getHeight() * getScale() + srcBm.getHeight() * getScale()/2  - rotateBm.getHeight(), 0);
				break;
			case OperateUtils.RIGHTBOTTOM :
				mPoint.x = (int) Math.max(width - srcBm.getWidth() * getScale() + srcBm.getWidth() * getScale()/2 - rotateBm.getWidth(), 0);
				mPoint.y = (int) Math.max(height - srcBm.getHeight() * getScale() + srcBm.getHeight() * getScale()/2  - rotateBm.getHeight(), 0);
				break;
			case OperateUtils.CENTER :
				mPoint.x = width / 2;
				mPoint.y = height / 2;
				break;
			default :
				break;
		}
		Log.d("TextObject","initTextPosition mPoint.x=" + mPoint.x + ", mPoint.y=" + mPoint.y);
	}

	public TextObject()
	{
	}

	/**
	 * 绘画出字体
	 */
	public void regenerateBitmap()
	{
		if (textSize == textSizeMax && TextUtils.equals(textOld, text)) {
			return;
		}
		this.textOld = text;
		textSize = (int) Math.ceil(textSize * mScale);
		Log.d("TextObject","TextObject=" + this + ", textSize=" + textSize);
		textSize = Math.min(textSizeMax, textSize);
		paint.setAntiAlias(true);
		paint.setTextSize(textSize);
		paint.setTypeface(getTypefaceObj());
		paint.setColor(color);
		paint.setStyle(Paint.Style.FILL);
		paint.setDither(true);
		paint.setFlags(Paint.SUBPIXEL_TEXT_FLAG);
		String lines[] = text.split("\n");

		int textWidth = 0;
		for (String str : lines)
		{
			int temp = (int) paint.measureText(str);
			if (temp > textWidth)
				textWidth = temp;
		}
		if (textWidth < 1)
			textWidth = 1;

		Paint.FontMetrics fontMetrics = paint.getFontMetrics();
		int textHeight = (int) Math.ceil(fontMetrics.bottom - fontMetrics.top);
		int baseline = (int) ((textHeight - fontMetrics.bottom - fontMetrics.top) / 2); //调整基准线，使得整个text绘制出来后居中显示
		Log.d("TextObject","textHeight=" + textHeight + ", textSize=" + textSize + ", baseline=" + baseline);

		int bitmapWidth = textWidth;
		int bitmapHeight = textHeight * (lines.length) + 8;
		if (srcBm != null)
			srcBm.recycle();
		srcBm = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(srcBm);
//		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawARGB(56, 200, 56, 56);
		for (int i = 1; i <= lines.length; i++)
		{
			canvas.drawText(lines[i - 1], 0, baseline + (i - 1) * textHeight, paint);
		}
		setCenter();
	}

	/**
	 * 设置字体样式
	 * 
	 * @return Typeface 默认系统字体，设置属性后变换字体 目前支持本地两种字体 by3500.ttf 、 bygf3500.ttf
	 */

	public Typeface getTypefaceObj()
	{
		Typeface tmptf = Typeface.DEFAULT;
		if (typeface != null)
		{
			if (OperateConstants.FACE_BY.equals(typeface)
					|| OperateConstants.FACE_BYGF.equals(typeface))
			{
				tmptf = Typeface.createFromAsset(context.getAssets(), "fonts/"
						+ typeface + ".ttf");
			}
		}
		if (bold && !italic)
			tmptf = Typeface.create(tmptf, Typeface.BOLD);
		if (italic && !bold)
			tmptf = Typeface.create(tmptf, Typeface.ITALIC);
		if (italic && bold)
			tmptf = Typeface.create(tmptf, Typeface.BOLD_ITALIC);
		return tmptf;
	}

	/**
	 * 设置属性值后，提交方法
	 */
	public void commit()
	{
		regenerateBitmap();
	}

	/**
	 * 公共的getter和setter方法
	 */
	public int getTextSize()
	{
		return textSize;
	}

	public void setTextSize(int textSize)
	{
		this.textSize = textSize;
	}

	public int getColor()
	{
		return color;
	}

	public void setColor(int color)
	{
		this.color = color;
	}

	public String getTypeface()
	{
		return typeface;
	}

	public void setTypeface(String typeface)
	{
		this.typeface = typeface;
	}

	public boolean isBold()
	{
		return bold;
	}

	public void setBold(boolean bold)
	{
		this.bold = bold;
	}

	public boolean isItalic()
	{
		return italic;
	}

	public void setItalic(boolean italic)
	{
		this.italic = italic;
	}

	public int getX()
	{
		return mPoint.x;
	}

	public void setX(int x)
	{
		this.mPoint.x = x;
	}

	public int getY()
	{
		return mPoint.y;
	}

	public void setY(int y)
	{
		this.mPoint.y = y;
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

}
