package com.buang.welewolf.modules.login;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.utils.UIUtils;
import com.buang.welewolf.R;
import com.buang.welewolf.modules.utils.UIFragmentHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weilei on 16/6/3.
 */
public class IntroduceImageFragment extends BaseUIFragment<UIFragmentHelper> {

    private int[] imageIds;
    private int[] imageDescIds;
    private List<View> dots;
    private int oldPostion;
    private LinearLayout mDotslayout;
    private int buttomMargin;

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        View view = View
                .inflate(getActivity(), R.layout.layout_introduce_image, null);
        return view;
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        imageIds = new int[]{R.drawable.introduce_show_1,
                R.drawable.introduce_show_2, R.drawable.introduce_show_3};
        imageDescIds = new int[]{R.drawable.introduce_showdesc_1,
                R.drawable.introduce_showdesc_2, R.drawable.introduce_showdesc_3};
        ImageView mButtomView = (ImageView) view.findViewById(R.id.introduce_buttom_bg);
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;

        Rect rect = new Rect();
        getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        int appViewHeight = rect.height();//应用高度
        buttomMargin = (int) ((appViewHeight - UIUtils.dip2px(250)) / 2 + 0.5f);

        int mButtomViewHeight = (int) ((screenWidth * 254) / 751 + 0.5f);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(screenWidth, mButtomViewHeight);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.bottomMargin = buttomMargin - UIUtils.dip2px(43);
        mButtomView.setLayoutParams(params);
        mButtomView.setImageResource(R.drawable.introduce_bg_buttom);

        mDotslayout = (LinearLayout) view.findViewById(R.id.doc_zone);

        dots = new ArrayList<View>();
        dots.add(view.findViewById(R.id.doc_1));
        dots.add(view.findViewById(R.id.doc_2));
        dots.add(view.findViewById(R.id.doc_3));
        dots.add(view.findViewById(R.id.doc_4));

        ViewPagerAdapter adapter = new ViewPagerAdapter();
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.answer_viewpager);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                int temp = position % dots.size();

                dots.get(oldPostion).setBackgroundResource(R.drawable.dot_nomal_img);
                dots.get(temp).setBackgroundResource(R.drawable.dot_focus_img);
                oldPostion = temp;

                if (temp == dots.size() - 1) {
                    mDotslayout.setVisibility(View.GONE);
                } else {
                    mDotslayout.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        viewPager.setCurrentItem(0);
    }

    /**
     * @name ViewPager适配器
     */
    class ViewPagerAdapter extends PagerAdapter {

        private List<View> views;

        public ViewPagerAdapter() {
            views = new ArrayList<View>();
            for (int i = 0; i < imageIds.length; i++) {
                View imageView = View.inflate(getActivity(),
                        R.layout.layout_introduce_viewpager_item, null);
                ImageView image = (ImageView) imageView
                        .findViewById(R.id.instroduce_image);
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) image.getLayoutParams();
                layoutParams.bottomMargin = buttomMargin;
                ImageView imageDesc = (ImageView) imageView
                        .findViewById(R.id.instroduce_image_desc);
                try {
                    Bitmap bm = BitmapFactory.decodeResource(getResources(), imageIds[i]);
                    image.setImageBitmap(bm);
                    image.setLayoutParams(layoutParams);
                    Bitmap bmdesc = BitmapFactory.decodeResource(getResources(), imageDescIds[i]);
                    imageDesc.setImageBitmap(bmdesc);

                } catch (Throwable e) {
                    e.printStackTrace();
                }

                views.add(imageView);
            }
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = views.get(position % views.size());
            if (view.getParent() == null)
                ((ViewPager) container).addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView(views.get(position
                    % views.size()));
        }
    }
}
