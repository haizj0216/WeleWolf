package com.buang.welewolf.modules.classes;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.buang.welewolf.base.bean.OnlineStudentRankInfo;
import com.buang.welewolf.base.database.bean.ClassInfoItem;
import com.buang.welewolf.base.http.services.OnlineServices;
import com.buang.welewolf.base.utils.PreferencesController;
import com.buang.welewolf.modules.utils.DialogUtils;
import com.buang.welewolf.modules.utils.UmengConstant;
import com.buang.welewolf.modules.utils.Utils;
import com.buang.welewolf.widgets.ShareClassDialog;
import com.buang.welewolf.widgets.headerviewpager.InnerListView;
import com.buang.welewolf.widgets.headerviewpager.InnerScroller;
import com.buang.welewolf.widgets.headerviewpager.InnerScrollerContainer;
import com.buang.welewolf.widgets.headerviewpager.OuterScroller;
import com.hyena.framework.app.adapter.SingleTypeAdapter;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.fragment.bean.UrlModelPair;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.utils.ImageFetcher;
import com.hyena.framework.utils.RoundDisplayer;
import com.buang.welewolf.base.bean.OnlineGrantCoinInfo;
import com.buang.welewolf.base.bean.OnlineStudentInfo;
import com.buang.welewolf.base.services.updateclass.ClassGroupsChangeListener;
import com.buang.welewolf.base.services.updateclass.UpdateClassService;
import com.buang.welewolf.modules.utils.ConstantsUtils;
import com.buang.welewolf.modules.utils.ToastUtils;
import com.buang.welewolf.modules.utils.UIFragmentHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weilei on 17/2/13.
 */
public class ClassStudentFragment extends BaseUIFragment<UIFragmentHelper> implements InnerScrollerContainer {

    private final int ACTION_GET_LIST = 1;
    private final int ACTION_GRANT = 2;

    private InnerListView mListView;
    protected OuterScroller mOuterScroller;
    protected int mIndex;
    private ClassStudentAdapter mAdapter;

    private ClassInfoItem mClassInfoItem;
    private OnlineStudentRankInfo mStudentRankInfo;
    private OnlineGrantCoinInfo mOnlineGrantCoinInfo;
    private UpdateClassService updateClassService;

    private Dialog mDialog;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        updateClassService = (UpdateClassService) getSystemService(UpdateClassService.SERVICE_NAME);
        updateClassService.getObserver().addClassGroupChangeListener(classGroupsChangeListener);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), com.buang.welewolf.R.layout.layout_class_item_list, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        mListView = (InnerListView) view.findViewById(com.buang.welewolf.R.id.listView);
        mListView.register2Outer(mOuterScroller, mIndex);
        mListView.setDividerHeight(0);
        mAdapter = new ClassStudentAdapter(getActivity());
        mListView.setAdapter(mAdapter);
    }

    private void loadStudentData() {
        if (mClassInfoItem == null) {
            showStudentEmpty();
        } else {
            loadData(ACTION_GET_LIST, PAGE_FIRST);
        }
    }

    private void updateStudentList() {
        if (mStudentRankInfo != null && mStudentRankInfo.mRankListInfos != null && !mStudentRankInfo.mRankListInfos.isEmpty()) {
            List<OnlineStudentInfo> items = new ArrayList<OnlineStudentInfo>();
            items.addAll(mStudentRankInfo.mRankListInfos);
            OnlineStudentInfo footer = new OnlineStudentInfo();
            footer.isFooter = true;
            items.add(footer);
            mAdapter.setItems(items);
        } else {
            showStudentEmpty();
        }
        if (mStudentRankInfo != null) {
            updateClassService.updateClassCount(mClassInfoItem.classId, mStudentRankInfo.studentCount);
        }
    }

    private void onClassChanged(ClassInfoItem classInfoItem) {
        if (classInfoItem == mClassInfoItem) {
            return;
        }
        mClassInfoItem = classInfoItem;
        loadStudentData();
    }

    private void showStudentEmpty() {
        mAdapter.setEmpty(true);
        mAdapter.notifyDataSetChanged();
    }

    private void showGrantDialog() {
        if (PreferencesController.getBoolean(ConstantsUtils.PREFS_GRANT_COIN, false)) {
            return;
        }
        if (mDialog != null && mDialog.isShowing()){
            mDialog.dismiss();
        }
        mDialog = DialogUtils.getNewMessageDialog(getActivity(), "", 0 ,"确定", "", "您已成功奖励该同学10枚金币,积累金币可以兑换奖品", Gravity.LEFT, new DialogUtils.OnDialogButtonClickListener() {
            @Override
            public void onItemClick(Dialog dialog, int btnId) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
        PreferencesController.setBoolean(ConstantsUtils.PREFS_GRANT_COIN, true);
    }

    @Override
    public void onPreAction(int action, int pageNo) {
        super.onPreAction(action, pageNo);
    }

    @Override
    public UrlModelPair getRequestUrlModelPair(int action, int pageNo, Object... params) {
        if (action == ACTION_GET_LIST) {
            String url = OnlineServices.getClassStudentInfoUrl(mClassInfoItem.classId);
            return new UrlModelPair(url, new OnlineStudentRankInfo());
        }
        return super.getRequestUrlModelPair(action, pageNo, params);
    }

    @Override
    public BaseObject onProcess(int action, int pageNo, Object... params) {
        super.onProcess(action, pageNo, params);
        if (action == ACTION_GET_LIST) {
            String url = OnlineServices.getClassStudentInfoUrl(mClassInfoItem.classId);
            OnlineStudentRankInfo result = new DataAcquirer<OnlineStudentRankInfo>().acquire(url, new OnlineStudentRankInfo(), -1);
            return result;
        } else if (action == ACTION_GRANT) {
            String url = OnlineServices.getGrantCoinUrl();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("class_id", mClassInfoItem.classId);
                jsonObject.put("student_id", ((OnlineStudentInfo) params[0]).mStudentId);
                jsonObject.put("teacher_id", Utils.getLoginUserItem().userId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String data = jsonObject.toString();
            OnlineGrantCoinInfo result = new DataAcquirer<OnlineGrantCoinInfo>().post(url, data, new OnlineGrantCoinInfo());
            return result;
        }
        return null;

    }

    @Override
    public void onGetCache(int action, int pageNo, BaseObject result) {
        super.onGetCache(action, pageNo, result);
        if (action == ACTION_GET_LIST) {
            mStudentRankInfo = (OnlineStudentRankInfo) result;
            updateStudentList();
        } else if (action == ACTION_GRANT) {

        }
    }

    @Override
    public void onGet(int action, int pageNo, BaseObject result, Object... params) {
        super.onGet(action, pageNo, result, params);
        if (action == ACTION_GET_LIST) {
            mStudentRankInfo = (OnlineStudentRankInfo) result;
            updateStudentList();
        } else if (action == ACTION_GRANT) {
            mOnlineGrantCoinInfo = (OnlineGrantCoinInfo) result;
            ((OnlineStudentInfo) params[0]).isGrant = true;
            mAdapter.notifyDataSetChanged();
            ToastUtils.showShortToast(getActivity(), "鼓励成功");
            showGrantDialog();
        }
    }

    @Override
    public void onFail(int action, int pageNo, BaseObject result, Object... params) {
        if (action == ACTION_GET_LIST) {
            showContent();
            showStudentEmpty();
        } else {
            super.onFail(action, pageNo, result, params);
        }
    }

    private ClassGroupsChangeListener classGroupsChangeListener = new ClassGroupsChangeListener() {
        @Override
        public void refreshClassGroupList() {
        }

        @Override
        public void createClassGroupSuccess(ClassInfoItem classInfoItem) {
        }

        @Override
        public void deleteClassGroupSuccess(ClassInfoItem classInfoItem) {
        }

        @Override
        public void updateClassGroupSuccess(ClassInfoItem classInfoItem) {
            onClassChanged(classInfoItem);
        }

        @Override
        public void onClassSelectChanged(ClassInfoItem classInfoItem) {
            onClassChanged(classInfoItem);
        }
    };

    class ClassStudentAdapter extends SingleTypeAdapter<OnlineStudentInfo> {
        private boolean isEmpty;

        public ClassStudentAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), com.buang.welewolf.R.layout.layout_class_student_item, null);
                viewHolder = new ViewHolder();
                viewHolder.mEncourage = (TextView) convertView.findViewById(com.buang.welewolf.R.id.student_encourage);
                viewHolder.mRankIndex = (TextView) convertView.findViewById(com.buang.welewolf.R.id.student_ranking_index);
                viewHolder.mName = (TextView) convertView.findViewById(com.buang.welewolf.R.id.student_name);
                viewHolder.mLearnedCount = (TextView) convertView.findViewById(com.buang.welewolf.R.id.student_grasp_count);
                viewHolder.mLearnedRank = (TextView) convertView.findViewById(com.buang.welewolf.R.id.student_grasp_rate);
                viewHolder.mLearningCount = (TextView) convertView.findViewById(com.buang.welewolf.R.id.student_grasping_count);
                viewHolder.mLearningRank = (TextView) convertView.findViewById(com.buang.welewolf.R.id.student_grasping_rate);
                viewHolder.mHead = (ImageView) convertView.findViewById(com.buang.welewolf.R.id.student_head_photo);
                viewHolder.mRankImg = (ImageView) convertView.findViewById(com.buang.welewolf.R.id.rank_img);
                viewHolder.mEmptyView = convertView.findViewById(com.buang.welewolf.R.id.student_empty);
                viewHolder.mRankView = convertView.findViewById(com.buang.welewolf.R.id.student_ranking);
                viewHolder.mEmptyAdd = (TextView) convertView.findViewById(com.buang.welewolf.R.id.student_empty_add);
                viewHolder.mEmptyDesc = (TextView) convertView.findViewById(com.buang.welewolf.R.id.student_empty_desc);
                viewHolder.mEmptyCode = (TextView) convertView.findViewById(com.buang.welewolf.R.id.student_empty_code);
                viewHolder.mEmptyImg = (ImageView) convertView.findViewById(com.buang.welewolf.R.id.student_empty_img);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (getEmpty()) {
                viewHolder.mEmptyView.setVisibility(View.VISIBLE);
                viewHolder.mRankView.setVisibility(View.GONE);
                if (mClassInfoItem.studentNum == 0) {
                    viewHolder.mEmptyCode.setVisibility(View.VISIBLE);
                    viewHolder.mEmptyImg.setVisibility(View.VISIBLE);
                    viewHolder.mEmptyCode.setText("学生在学生端输入班级部落号\"" + mClassInfoItem.classCode+ "\"加入");
                    viewHolder.mEmptyDesc.setVisibility(View.VISIBLE);
                    viewHolder.mEmptyAdd.setVisibility(View.VISIBLE);
                    viewHolder.mEmptyAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showShareDialog();
                        }
                    });
                } else {
                    viewHolder.mEmptyCode.setText("获取数据失败");
                    viewHolder.mEmptyDesc.setVisibility(View.GONE);
                    viewHolder.mEmptyAdd.setVisibility(View.GONE);
                }
            } else {
                final OnlineStudentInfo studentInfo = getItem(position);
                if (studentInfo.isFooter) {
                    viewHolder.mEmptyView.setVisibility(View.VISIBLE);
                    viewHolder.mRankView.setVisibility(View.GONE);
                    viewHolder.mEmptyImg.setVisibility(View.GONE);
                    viewHolder.mEmptyDesc.setVisibility(View.GONE);
                    viewHolder.mEmptyCode.setVisibility(View.GONE);
                    viewHolder.mEmptyAdd.setVisibility(View.VISIBLE);
                    viewHolder.mEmptyAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showShareDialog();
                        }
                    });
                } else {
                    viewHolder.mEmptyView.setVisibility(View.GONE);
                    viewHolder.mRankView.setVisibility(View.VISIBLE);
                    switch (position) {
                        case 0:
                            viewHolder.mRankImg.setVisibility(View.VISIBLE);
                            viewHolder.mRankIndex.setVisibility(View.GONE);
                            viewHolder.mRankImg.setImageResource(com.buang.welewolf.R.drawable.icon_rank_first);
                            break;
                        case 1:
                            viewHolder.mRankImg.setVisibility(View.VISIBLE);
                            viewHolder.mRankIndex.setVisibility(View.GONE);
                            viewHolder.mRankImg.setImageResource(com.buang.welewolf.R.drawable.icon_rank_second);
                            break;
                        case 2:
                            viewHolder.mRankImg.setVisibility(View.VISIBLE);
                            viewHolder.mRankIndex.setVisibility(View.GONE);
                            viewHolder.mRankImg.setImageResource(com.buang.welewolf.R.drawable.icon_rank_third);
                            break;
                        default:
                            viewHolder.mRankImg.setVisibility(View.GONE);
                            viewHolder.mRankIndex.setVisibility(View.VISIBLE);
                            viewHolder.mRankIndex.setText("" + (position + 1));
                            break;
                    }

                    viewHolder.mEncourage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            grantStudent(studentInfo);
                        }
                    });

                    viewHolder.mEncourage.setEnabled(!studentInfo.isGrant);
                    if (studentInfo.isGrant) {
                        viewHolder.mEncourage.setText("已鼓励");
                    } else {
                        viewHolder.mEncourage.setText("鼓励TA");
                    }

                    viewHolder.mName.setText(studentInfo.mStudentName);
                    ImageFetcher.getImageFetcher().loadImage(studentInfo.mHeadPhoto, viewHolder.mHead,
                            com.buang.welewolf.R.drawable.default_img, new RoundDisplayer());
                    viewHolder.mLearnedCount.setText("" + studentInfo.learnedCount);
                    viewHolder.mLearningCount.setText("" + studentInfo.learningCount);

                    int learnedUp = studentInfo.weekLearned;
                    viewHolder.mLearnedRank.setText("" + learnedUp);
                    Drawable learnDb = null;
                    if (learnedUp > 0) {
                        learnDb = getResources().getDrawable(com.buang.welewolf.R.drawable.icon_student_rank_up);
                        learnDb.setBounds(0, 0, learnDb.getIntrinsicWidth(), learnDb.getIntrinsicHeight());
                        viewHolder.mLearnedRank.setTextColor(getResources().getColor(com.buang.welewolf.R.color.color_button_red));
                    } else if (learnedUp < 0) {
                        learnDb = getResources().getDrawable(com.buang.welewolf.R.drawable.icon_student_rank_down);
                        learnDb.setBounds(0, 0, learnDb.getIntrinsicWidth(), learnDb.getIntrinsicHeight());
                        viewHolder.mLearnedRank.setTextColor(getResources().getColor(com.buang.welewolf.R.color.color_main_app));
                    } else {
                        viewHolder.mLearnedRank.setText("-");
                        viewHolder.mLearnedRank.setTextColor(getResources().getColor(com.buang.welewolf.R.color.color_text_primary));
                    }
                    viewHolder.mLearnedRank.setCompoundDrawables(learnDb, null, null, null);

                    int learningUp = studentInfo.weekLearning;
                    viewHolder.mLearningRank.setText("" + learningUp);
                    Drawable learningDb = null;
                    if (learningUp > 0) {
                        learningDb = getResources().getDrawable(com.buang.welewolf.R.drawable.icon_student_rank_up);
                        learningDb.setBounds(0, 0, learningDb.getIntrinsicWidth(), learningDb.getIntrinsicHeight());
                        viewHolder.mLearningRank.setTextColor(getResources().getColor(com.buang.welewolf.R.color.color_button_red));
                    } else if (learningUp < 0) {
                        learningDb = getResources().getDrawable(com.buang.welewolf.R.drawable.icon_student_rank_down);
                        learningDb.setBounds(0, 0, learningDb.getIntrinsicWidth(), learningDb.getIntrinsicHeight());
                        viewHolder.mLearningRank.setTextColor(getResources().getColor(com.buang.welewolf.R.color.color_main_app));
                    } else {
                        viewHolder.mLearningRank.setText("-");
                        viewHolder.mLearningRank.setTextColor(getResources().getColor(com.buang.welewolf.R.color.color_text_primary));
                    }
                    viewHolder.mLearningRank.setCompoundDrawables(learningDb, null, null, null);

                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            UmengConstant.reportUmengEvent(UmengConstant.EVENT_HOME_STUDENT_ITEM, null);
                            Bundle mBundle = new Bundle();
                            mBundle.putParcelable(ConstantsUtils.KEY_CLASSINFOITEM, mClassInfoItem);
                            mBundle.putSerializable(ConstantsUtils.KEY_STUDENTINFO, studentInfo);
                            StudentUnitFragment fragment = newFragment(getActivity(), StudentUnitFragment.class, mBundle);
                            showFragment(fragment);
                        }
                    });
                }

            }

            return convertView;
        }

        private void grantStudent(OnlineStudentInfo studentInfo) {
//            if (Integer.parseInt(Utils.getLoginUserItem().authenticationStatus) == UserItem.AUTHENTICATION_SUCCESS) {
//                return;
//            }
            loadData(ACTION_GRANT, PAGE_MORE, studentInfo);
        }

        private void showShareDialog() {
            Bundle mBundle = new Bundle();
            mBundle.putParcelable(ConstantsUtils.KEY_CLASSINFOITEM, mClassInfoItem);
            ShareClassDialog fragment = ShareClassDialog.newFragment(getActivity(),
                    ShareClassDialog.class, mBundle, AnimType.ANIM_NONE);
            showFragment(fragment);
        }

        @Override
        public void setItems(List<OnlineStudentInfo> items) {
            isEmpty = false;
            super.setItems(items);
        }

        @Override
        public int getCount() {
            if (getEmpty()) {
                return 1;
            }
            return super.getCount();
        }

        public boolean getEmpty() {
            return isEmpty;
        }

        public void setEmpty(boolean empty) {
            isEmpty = empty;
            notifyDataSetChanged();
        }
    }

    class ViewHolder {
        public ImageView mHead;
        public ImageView mRankImg;
        public TextView mRankIndex;
        public TextView mName;
        public TextView mLearnedCount;
        public TextView mLearnedRank;
        public TextView mLearningCount;
        public TextView mLearningRank;
        public TextView mEncourage;
        public View mEmptyView;
        public View mRankView;
        public TextView mEmptyCode;
        public TextView mEmptyDesc;
        public TextView mEmptyAdd;
        public ImageView mEmptyImg;
    }

    @Override
    public void setOuterScroller(OuterScroller outerScroller, int myPosition) {
        if (outerScroller == mOuterScroller && myPosition == mIndex) {
            return;
        }
        mOuterScroller = outerScroller;
        mIndex = myPosition;

        if (getInnerScroller() != null) {
            getInnerScroller().register2Outer(mOuterScroller, mIndex);
        }
    }

    @Override
    public InnerScroller getInnerScroller() {
        return mListView;
    }

    @Override
    public void onDestroyImpl() {
        super.onDestroyImpl();
        if (updateClassService != null) {
            updateClassService.getObserver().removeClassGroupChangeListener(classGroupsChangeListener);
        }
    }
}
