/**
 * Copyright (C) 2014 The KnowboxTeacher Project
 */
package com.buang.welewolf.base.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Html.TagHandler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.hyena.framework.app.coretext.span.ClickableImageSpan;
import com.hyena.framework.security.MD5Util;
import com.hyena.framework.utils.BaseApp;
import com.hyena.framework.utils.ToastUtils;
import com.hyena.framework.utils.UIUtils;
import com.hyena.framework.utils.UiThreadHandler;
import com.knowbox.base.utils.ImageFetcher;
import com.knowbox.base.utils.ImageFetcher.ImageFetcherListener;
import com.buang.welewolf.modules.utils.ConstantsUtils;

import org.xml.sax.XMLReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.droidsonroids.gif.GifDrawable;

public class StringUtils {

	private static final String REG_EX_NUMBER = "[0-9]*";
	private static final String REG_EX_PHONE = "^1[3|4|5|7|8][0-9]\\d{8}$";
	private static final String REG_EX_TAG_AND_ALIAS = "^[\u4E00-\u9FA50-9a-zA-Z_-]{0,}$";

	/**
	 * 校验Tag Alias 只能是数字,英文字母和中文
	 *
	 * @param s
	 * @return
	 */
	public static boolean isValidTagAndAlias(String str) {
		return isMatch(REG_EX_TAG_AND_ALIAS, str);
	}

	/**
	 * 验证邮箱
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email){
		boolean flag = false;
		try{
			String check = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(email);
			flag = matcher.matches();
		} catch(Exception e) {
			flag = false;
		}
		return flag;
	}

	/**
	 * 是否是数字
	 *
	 * @param str
	 * @return
	 */
	public static boolean isNumber(String str) {
		return isMatch(REG_EX_NUMBER, str);
	}

	public static boolean isPhone(String phoneNo) {
		return isMatch(REG_EX_PHONE, phoneNo);
	}

	private static boolean isMatch(String regEx, String value) {
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(value);
		return matcher.matches();
	}

	public static String getReplaceStr(String replace) {
		return MD5Util.encode(replace + "lanjj@knwobox.comlanjunjia4@qq.com232713267@qq.com");
	}


	/**
	 * 设置可点击图片的图文
	 * @param context
	 * @param textview
	 * @param html
	 * @param id
     * @param listener
     */
    public static void setTextImage(final Context context, TextView textview, String html,
									final int id, final View.OnClickListener listener) {
        Drawable drawable = context.getResources().getDrawable(id);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        SpannableString spannable = new SpannableString(html + "[smile]");
        ClickableImageSpan span = new ClickableImageSpan(drawable, ImageSpan.ALIGN_BASELINE) {
			@Override
			public void onClick(View v) {
				if (listener != null) {
					listener.onClick(v);
				}
			}
		};
        spannable.setSpan(span, html.length(), html.length() + "[smile]".length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		textview.setMovementMethod(ClickableMovementMethod.getInstance());
		textview.setText(spannable);
    }

	/**
	 * 为TextView设置Html
	 *
	 * @param textview
	 * @param html
	 */
	public static void setTextHtml(final TextView textview, final String html) {
		try {
			Spannable spannable = (Spannable) Html.fromHtml(html == null ? "" : html,
					new URLImageGetter(textview), new TableTagHandler());
//			enableImageClick(spannable);
//			textview.setMovementMethod(LinkMovementMethod.getInstance());
			textview.setText(spannable);
		} catch (Exception e) {
			textview.setText(html);
		}
	}

	public static class URLImageGetter implements ImageGetter {

		private TextView mTextView;

		public URLImageGetter(TextView textView){
			this.mTextView = textView;
		}

		@Override
		public Drawable getDrawable(String source) {
			final URLDrawable urlDrawable = new URLDrawable();
			ImageFetcher.getImageFetcher().loadImage(source, "", new ImageFetcherListener() {
				@Override
				public void onLoadComplete(String imageUrl, Bitmap bitmap, Object object) {
					if(bitmap == null)
						return;

					if(imageUrl.endsWith("gif")){
						File localFile = ImageFetcher.getImageFetcher()
								.getCacheFilePath(imageUrl);
						try {
							Drawable drawable = new GifDrawable(localFile);
							float d = BaseApp.getAppContext().getResources()
									.getDisplayMetrics().density;
							drawable.setBounds(0, 0,
									(int) (drawable.getIntrinsicWidth() * d),
									(int) (drawable.getIntrinsicHeight() * d));
							urlDrawable.mDrawable = drawable;
			                mTextView.invalidate();
			                mTextView.setText(mTextView.getText()); // 解决图文重叠
						} catch (Throwable e) {
							e.printStackTrace();
						}
					}else{
						try {
							if(bitmap != null && !bitmap.isRecycled()) {
								urlDrawable.bitmap = ImageUtil.resizeBitmap(bitmap, 300, 300);
				                urlDrawable.setBounds(0, 0, urlDrawable.bitmap.getWidth(), urlDrawable.bitmap.getHeight());
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

		                mTextView.invalidate();
		                mTextView.setText(mTextView.getText()); // 解决图文重叠
					}
				}
			});
	        return urlDrawable;
		}

	}

	@SuppressWarnings("deprecation")
	public static class URLDrawable extends BitmapDrawable {
	    protected Bitmap bitmap;
	    protected Drawable mDrawable;

	    @Override
	    public void draw(Canvas canvas) {
	        if (bitmap != null) {
	            canvas.drawBitmap(bitmap, 0, 0, getPaint());
	        }
	        if(mDrawable != null){
	        	mDrawable.draw(canvas);
	        }
	    }
	}

	/**
	 * 使ImageSpan可点击
	 * @param spannable
	 */
	public static void enableImageClick(Spannable spannable){
		ImageSpan[] image_spans = spannable.getSpans(0, spannable.length(), ImageSpan.class);
		for (ImageSpan span : image_spans) {
			final String image_src = span.getSource();
			final int start = spannable.getSpanStart(span);
			final int end = spannable.getSpanEnd(span);
			ClickableSpan click_span = new ClickableSpan() {
				@Override
				public void onClick(View widget) {
					ToastUtils.showShortToast(BaseApp.getAppContext(), "Image Clicked " + image_src);
				}
			};
			ClickableSpan[] click_spans = spannable.getSpans(start, end,
					ClickableSpan.class);
			if (click_spans.length != 0) {
				for (ClickableSpan c_span : click_spans) {
					spannable.removeSpan(c_span);
				}
			}
			spannable.setSpan(click_span, start, end,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}

	@SuppressWarnings("unused")
	private static Drawable decodeFile(File f) {
		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// The new size we want to scale to
			final int REQUIRED_SIZE = 400;

			// Find the correct scale value. It should be the power of 2.
			int scale = 1;
			while (o.outWidth / scale / 2 >= REQUIRED_SIZE
					&& o.outHeight / scale / 2 >= REQUIRED_SIZE)
				scale *= 2;

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f),
					null, o2);
			return new BitmapDrawable(BaseApp.getAppContext().getResources(),
					bitmap);
		} catch (FileNotFoundException e) {

		}
		return null;
	}

	/**
	 * 处理table标签
	 */
	private static class TableTagHandler implements TagHandler {
		@SuppressWarnings("unused")
		private int startIndex;
		@SuppressWarnings("unused")
		private int endIndex;

		public TableTagHandler() {
			super();
		}

		@Override
		public void handleTag(boolean opening, String tag, Editable output,
				XMLReader xmlReader) {
			if (tag.equalsIgnoreCase("table")) {
				tableTagHanlder(opening, tag, output, xmlReader);
			} else if (tag.equalsIgnoreCase("tbody")) {
				tBodyTagHandler(opening, tag, output, xmlReader);
			} else if (tag.equalsIgnoreCase("tr")) {
				trTagHandler(opening, tag, output, xmlReader);
			} else if (tag.equalsIgnoreCase("td")) {
				tdTagHandler(opening, tag, output, xmlReader);
			}
		}

		private void tableTagHanlder(boolean opening, String tag,
				Editable output, XMLReader xmlReader) {
			if (opening) {
				output.append("\n");
				startIndex = output.length();
			} else {
				endIndex = output.length();
			}
		}

		private void tBodyTagHandler(boolean opening, String tag,
				Editable output, XMLReader xmlReader) {
			if (opening) {
				startIndex = output.length();
			} else {
				endIndex = output.length();
			}
		}

		private void trTagHandler(boolean opening, String tag, Editable output,
				XMLReader xmlReader) {
			if (opening) {
				startIndex = output.length();
			} else {
				output.append("\n");
				endIndex = output.length();
			}

		}

		private void tdTagHandler(boolean opening, String tag, Editable output,
				XMLReader xmlReader) {
			if (opening) {
				startIndex = output.length();
			} else {
				output.append("\t");
				endIndex = output.length();
			}

		}
	}

	/**
	 * 验证是否为手机号码
	 *
	 * @param number
	 * @return
	 */
	public static boolean isMobilePhoneNumber(String number) {
		Pattern pattern = Pattern.compile("^1[3|4|5|7|8][0-9]\\d{8}$");
		Matcher mathcer = pattern.matcher(number);
		return mathcer.matches();
	}

	/**
	 * 初始化题干内容
	 *
	 * @param html
	 */
	public static void setStemHtml(final WebView webView, final String html, final String color) {

	}

	/**
	 * 小数相乘
	 * 使用正则表达式去掉多余的.与0
	 */
	public static String subZeroAndDot(String f) {
		BigDecimal b1 = new BigDecimal(f);
		BigDecimal b2 = new BigDecimal(100);
		String s = b1.multiply(b2).toString();
		if (s.indexOf(".") > 0) {
			s = s.replaceAll("0+?$", "");// 去掉多余的0
			s = s.replaceAll("[.]$", "");// 如最后一位是.则去掉
		}
		return s;
	}

	public static String getDoubleString(double d) {
		if (d <= 0) {
			d = 0;
			return "0.00";
		}
		if (d > 1) {
			d = 1;
		}
		d = d * 100;
		DecimalFormat decimalFormat=new DecimalFormat("#.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
		String p = decimalFormat.format(d);//format 返回的是字符串
		return p;
	}

	/**
	 * 数字过万用x.yw 表示
	 * 不到万显示原数据
	 * @param count
	 * @return
	 */
	public static String getUnitByWan(int count) {
		if (count / 10000 <= 0) {
			return count + "";
		}
		String s = String.format("%.1f", Double.parseDouble(count / 10000 + "." + count % 10000));
		if(s.indexOf(".") > 0){
			s = s.replaceAll("0+?$", "");//去掉多余的0
			s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
		}
		return s + "w";
	}

	/**
	 * 数字过千用x.yw 表示
	 * 不到万显示原数据
	 * @param count
	 * @return
	 */
	public static String getUnitByK(int count) {
		if (count > 10000) {
			return getUnitByWan(count);
		}
		if (count / 1000 <= 0) {
			return count + "";
		}
		String s = String.format("%.1f", Double.parseDouble(count / 1000 + "." + count % 1000));
		if(s.indexOf(".") > 0){
			s = s.replaceAll("0+?$", "");//去掉多余的0
			s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
		}
		return s + "k";
	}

	public static String getSecondByMS(long ms) {
		int second = (int) (ms / 1000 + 1);
		int minute = second / 60;
		if (minute > 0) {
			second = second % 60;
			return minute + "分" + second + "秒";
		}else {
			return second + "秒";
		}
	}

}
