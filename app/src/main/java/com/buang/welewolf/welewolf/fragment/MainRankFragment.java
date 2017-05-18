package com.buang.welewolf.welewolf.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.buang.welewolf.base.bean.OnlineRankItemInfo;
import com.buang.welewolf.modules.profile.ActivityWebViewFragment;
import com.hyena.framework.app.adapter.SingleTypeAdapter;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.utils.RoundDisplayer;
import com.hyena.framework.utils.UIUtils;
import com.knowbox.base.utils.ImageFetcher;
import com.buang.welewolf.base.bean.BannerInfoItem;
import com.buang.welewolf.base.bean.OnlineRankListInfo;
import com.buang.welewolf.base.bean.OnlineStudentInfo;
import com.buang.welewolf.modules.utils.UIFragmentHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weilei on 17/4/24.
 */
public class MainRankFragment extends BaseUIFragment<UIFragmentHelper> {

    private static final int MSG_NEXT = 1;
    private static final int NEXT_DELAY = 3000;

    private ListView mListView;
    private View header;
    private ViewPager mViewPager;
    private LinearLayout mPagerHintPanel;
    private FocusPagerAdapter mBannerPagerAdapter;
    private ListAdapter mListAdapter;

    private OnlineRankListInfo mOnlineRankListInfo;

    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_NEXT: {
                    try {
                        if (getActivity() == null || getActivity().isFinishing())
                            return;
                        int next = mViewPager.getCurrentItem() + 1;
                        mViewPager.setCurrentItem(next, true);
                        mHandler.removeMessages(MSG_NEXT);
                        mHandler.sendEmptyMessageDelayed(MSG_NEXT, NEXT_DELAY);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(false);
        setTitleStyle(STYLE_NO_TITLE);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), com.buang.welewolf.R.layout.layout_main_list, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        mListView = (ListView) view.findViewById(com.buang.welewolf.R.id.main_list_listview);

        initData();
        updateRankInfo();
    }

    private void initData() {
        mOnlineRankListInfo = new OnlineRankListInfo();
        mOnlineRankListInfo.mBannerInfos = new ArrayList<>();

//        mOnlineRankListInfo.mBannerInfos.add(new BannerInfoItem("http://img.taodiantong.cn/v55183/infoimg/2013-07/130720115322ky.jpg", "http://www.baidu.com"));
//        mOnlineRankListInfo.mBannerInfos.add(new BannerInfoItem("http://pic30.nipic.com/20130626/8174275_085522448172_2.jpg", "http://www.baidu.com"));
//        mOnlineRankListInfo.mBannerInfos.add(new BannerInfoItem("http://pic18.nipic.com/20111215/577405_080531548148_2.jpg", "http://www.baidu.com"));

        mOnlineRankListInfo.mRankInfos = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            OnlineRankItemInfo rankItem = new OnlineRankItemInfo();
            rankItem.type = i;
            rankItem.mStudentInfos = new ArrayList<>();
            for (int j = 0; j < 10; j++) {
                OnlineStudentInfo studentInfo = new OnlineStudentInfo();
                studentInfo.mStudentName = "朱大壮" + i;
                studentInfo.mHeadPhoto = "http://www.qqzhi.com/uploadpic/2015-01-02/203401111.jpg";
                rankItem.mStudentInfos.add(studentInfo);
            }
            mOnlineRankListInfo.mRankInfos.add(rankItem);
        }
    }

    private void updateRankInfo() {
        if (mOnlineRankListInfo == null) {
            getUIFragmentHelper().getEmptyView().showEmpty("数据错误");
        } else {
            if (mOnlineRankListInfo.mBannerInfos != null && mOnlineRankListInfo.mBannerInfos.size() > 0) {
                initBanner();
                updateBanner(mOnlineRankListInfo.mBannerInfos);
            }
            mListAdapter = new ListAdapter(getActivity());
            mListAdapter.setItems(mOnlineRankListInfo.mRankInfos);
            mListView.setAdapter(mListAdapter);
        }
    }

    private void initBanner() {
        header = View.inflate(getActivity(), com.buang.welewolf.R.layout.header_homework, null);
        final RelativeLayout bannerLayout = (RelativeLayout) header.findViewById(com.buang.welewolf.R.id.bank_focus_layou);
        ViewTreeObserver observer = bannerLayout.getViewTreeObserver();
        final float screenWidth = UIUtils.getWindowWidth(getActivity());
        final float bannerHeight = screenWidth / (800.0f / 280.0f);
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewGroup.LayoutParams params = bannerLayout.getLayoutParams();
                params.width = (int) screenWidth;
                params.height = (int) bannerHeight;
                bannerLayout.setLayoutParams(params);
            }
        });
        mListView.removeHeaderView(header);
        mListView.addHeaderView(header);

        mViewPager = (ViewPager) header.findViewById(com.buang.welewolf.R.id.viewPager);
        mPagerHintPanel =
                (LinearLayout) header.findViewById(com.buang.welewolf.R.id.blockade_focus_pager_hint_panel);
        mViewPager.setOnPageChangeListener(mPageChangeListener);
    }

    private void updateBanner(List<BannerInfoItem> bannerInfoItems) {
        mBannerPagerAdapter = new FocusPagerAdapter(bannerInfoItems);
        mViewPager.setAdapter(mBannerPagerAdapter);
        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    mHandler.removeMessages(MSG_NEXT);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_CANCEL
                        || motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    Message msg = mHandler.obtainMessage(MSG_NEXT);
                    mHandler.sendMessageDelayed(msg, 5000);
                }
                return false;
            }
        });
        initPagerDotHint(bannerInfoItems.size(), 0);
        mViewPager.getLayoutParams().height = (int) (UIUtils.getWindowWidth(getActivity()) * 0.35);
        mViewPager.setCurrentItem(Integer.MAX_VALUE / 2 - Integer.MAX_VALUE / 2 % mBannerPagerAdapter.getCount());
        mHandler.removeMessages(MSG_NEXT);
        Message msg = mHandler.obtainMessage(MSG_NEXT);
        mHandler.sendMessageDelayed(msg, NEXT_DELAY);
    }

    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int arg0) {
            if (mPagerHintPanel != null)
                initPagerDotHint(mPagerHintPanel.getChildCount(), (arg0 % mPagerHintPanel.getChildCount()));
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    };

    private void initPagerDotHint(int size, int index) {
        if (size == 1) {
            mPagerHintPanel.setVisibility(View.GONE);
        } else {
            mPagerHintPanel.removeAllViews();
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.leftMargin = UIUtils.dip2px(3);
            params.rightMargin = params.leftMargin;
            for (int i = 0; i < size; i++) {
                ImageView imageView = new ImageView(getActivity());
                imageView.setImageResource(com.buang.welewolf.R.drawable.pager_hint_dot);
                mPagerHintPanel.addView(imageView, params);
                if (i == index) {
                    imageView.setSelected(true);
                }
            }
            mPagerHintPanel.setVisibility(View.VISIBLE);
        }

    }

    private void openBannerDetailFragment(String url) {
        Bundle args = new Bundle();
        //BannerDetailFragment
        args.putString("title", "狼人杀");
        args.putString("url", url);
        BaseUIFragment fragment = BaseUIFragment.newFragment(getActivity(), ActivityWebViewFragment.class, args);
        showFragment(fragment);
    }

    class ListAdapter extends SingleTypeAdapter<OnlineRankItemInfo> {

        private LinearLayout.LayoutParams mParams;

        public ListAdapter(Context context) {
            super(context);
            mParams = new LinearLayout.LayoutParams(UIUtils.dip2px(80), ViewGroup.LayoutParams.WRAP_CONTENT);
            mParams.leftMargin = mParams.rightMargin = UIUtils.dip2px(5);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), com.buang.welewolf.R.layout.layout_main_list_item, null);
                viewHolder = new ViewHolder();

                viewHolder.mBg = convertView.findViewById(com.buang.welewolf.R.id.main_list_item_bg);
                viewHolder.mMoreView = (TextView) convertView.findViewById(com.buang.welewolf.R.id.main_list_item_more);
                viewHolder.mStudentList = (LinearLayout) convertView.findViewById(com.buang.welewolf.R.id.main_list_item_student);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            OnlineRankItemInfo rankItemInfo = getItem(position);
            switch (rankItemInfo.type) {
                case 0:
                    viewHolder.mBg.setBackgroundResource(com.buang.welewolf.R.drawable.rank_lv);
                    break;
                case 1:
                    viewHolder.mBg.setBackgroundResource(com.buang.welewolf.R.drawable.rank_popularity);
                    break;
                case 2:
                    viewHolder.mBg.setBackgroundResource(com.buang.welewolf.R.drawable.rank_score);
                    viewHolder.mStudentList.setVisibility(View.GONE);
                    viewHolder.mMoreView.setVisibility(View.GONE);
                    break;
            }

            viewHolder.mStudentList.removeAllViews();

            for (int i = 0; i < rankItemInfo.mStudentInfos.size(); i++) {
                OnlineStudentInfo studentInfo = rankItemInfo.mStudentInfos.get(i);
                View view = View.inflate(getActivity(), com.buang.welewolf.R.layout.layout_main_list_student, null);
                ImageView mHead = (ImageView) view.findViewById(com.buang.welewolf.R.id.student_head_photo);
                ImageFetcher.getImageFetcher().loadImage(studentInfo.mHeadPhoto, mHead, 0, new RoundDisplayer());

                TextView mName = (TextView) view.findViewById(com.buang.welewolf.R.id.student_name);
                mName.setText(studentInfo.mStudentName);

                ImageView mRank = (ImageView) view.findViewById(com.buang.welewolf.R.id.student_head_rank);
                switch (i) {
                    case 0:
                        mRank.setImageResource(com.buang.welewolf.R.drawable.ic_rank_crown_1st);
                        break;
                    case 1:
                        mRank.setImageResource(com.buang.welewolf.R.drawable.ic_rank_crown_2nd);
                        break;
                    case 2:
                        mRank.setImageResource(com.buang.welewolf.R.drawable.ic_rank_crown_3rd);
                        break;
                    default:
                        mRank.setVisibility(View.GONE);
                        break;
                }

                viewHolder.mStudentList.addView(view, mParams);
            }

            return convertView;
        }
    }

    class ViewHolder {
        public TextView mMoreView;
        public View mBg;
        public LinearLayout mStudentList;
    }

    /**
     * ViewPager适配器
     */
    class FocusPagerAdapter extends PagerAdapter {

        private List<View> mViews = new ArrayList<View>();

        public FocusPagerAdapter(final List<BannerInfoItem> advInfoList) {
            super();
            List<BannerInfoItem> items = new ArrayList<BannerInfoItem>();
            items.addAll(advInfoList);
            if (advInfoList.size() == 2) {
                items.addAll(advInfoList);
            }
            for (int i = 0; i < items.size(); i++) {

                final BannerInfoItem item = items.get(i);
                View view = View.inflate(getActivity(), com.buang.welewolf.R.layout.layout_homework_banner_item, null);
                final ImageView imageView = (ImageView) view.findViewById(com.buang.welewolf.R.id.ivHomeworkBannerItem);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                ImageFetcher.getImageFetcher().loadImage(item.image, imageView, 0);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!TextUtils.isEmpty(item.url)) {
                            openBannerDetailFragment(item.url);
                        }
                    }
                });
                mViews.add(imageView);
            }
        }

        @Override
        public int getCount() {
            if (mViews != null && mViews.size() == 1)
                return 1;
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = mViews.get(position % mViews.size());
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
        }

    }
}
