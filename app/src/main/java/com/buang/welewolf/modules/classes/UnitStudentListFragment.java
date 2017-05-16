package com.buang.welewolf.modules.classes;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.buang.welewolf.base.bean.OnlineStudentInfo;
import com.buang.welewolf.base.bean.OnlineUnitInfo;
import com.buang.welewolf.base.bean.OnlineUnitStudentListInfo;
import com.buang.welewolf.base.database.bean.ClassInfoItem;
import com.buang.welewolf.base.http.services.OnlineServices;
import com.buang.welewolf.base.services.updateclass.UpdateClassService;
import com.buang.welewolf.modules.utils.ConstantsUtils;
import com.buang.welewolf.modules.utils.DialogUtils;
import com.buang.welewolf.modules.utils.Utils;
import com.hyena.framework.app.adapter.SingleTypeAdapter;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.fragment.bean.UrlModelPair;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.utils.ImageFetcher;
import com.hyena.framework.utils.RoundDisplayer;
import com.hyena.framework.utils.UiThreadHandler;
import com.knowbox.base.utils.UIUtils;
import com.buang.welewolf.modules.utils.UIFragmentHelper;

/**
 * Created by weilei on 17/2/14.
 */
public class UnitStudentListFragment extends BaseUIFragment<UIFragmentHelper> {

    private ListView mListView;
    private StudentListAdapter mAdapter;
    private OnlineUnitInfo mUnitInfo;
    private ClassInfoItem mClassInfoItem;
    private TextView mStudentCount;
    private TextView mLearnedCount;
    private Dialog mDialog;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(false);
        setTitleStyle(STYLE_NO_TITLE);
        mUnitInfo = (OnlineUnitInfo) getArguments().getSerializable(ConstantsUtils.KEY_UNITINFO);
        mClassInfoItem = getArguments().getParcelable(ConstantsUtils.KEY_CLASSINFOITEM);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), com.buang.welewolf.R.layout.layout_unit_student_list, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        mListView = (ListView) view.findViewById(com.buang.welewolf.R.id.unit_student_list);
        mAdapter = new StudentListAdapter(getActivity());

        View header = View.inflate(getActivity(), com.buang.welewolf.R.layout.layout_unit_student_header, null);
        mStudentCount = (TextView) header.findViewById(com.buang.welewolf.R.id.unit_student_header_count);
        mLearnedCount = (TextView) header.findViewById(com.buang.welewolf.R.id.unit_student_header_learned);

        ((TextView) header.findViewById(com.buang.welewolf.R.id.unit_student_header_rule)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRuleDialog();
            }
        });
        mListView.addHeaderView(header);
        mListView.setAdapter(mAdapter);
        UiThreadHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadDefaultData(PAGE_FIRST, mClassInfoItem.classId, mUnitInfo.mUnitID, Utils.getLoginUserItem().mBookId);
            }
        }, 200);
    }

    private void showRuleDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }

        String msg = "答题正确，增加熟练度\n" +
                "答题错误，减少熟练度\n" +
                "达到所需熟练度，为已练熟单词\n" +
                "\n此为[全班同学已熟练]的单词数量";
        mDialog = DialogUtils.getNewMessageDialog(getActivity(), com.buang.welewolf.R.drawable.icon_dialog_rule, "" ,msg, "", "确定", new DialogUtils.OnDialogButtonClickListener() {
            @Override
            public void onItemClick(Dialog dialog, int btnId) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    private void updateWordList(OnlineUnitStudentListInfo result) {
        mStudentCount.setText("/" + result.studentCount);
        mLearnedCount.setText("" + result.studentLearnedCount);
        if (result.mStudentInfos != null && !result.mStudentInfos.isEmpty()) {
            mAdapter.setItems(result.mStudentInfos);
        } else {
            String desc = "学生在学生端输入班级部落号\"" + mClassInfoItem.classCode + "\"加入";
            getUIFragmentHelper().getEmptyView().setTopMargin(UIUtils.dip2px(171));
            getUIFragmentHelper().getEmptyView().setEmptyBg(getResources().getColor(com.buang.welewolf.R.color.white));
            getUIFragmentHelper().getEmptyView().showEmpty(com.buang.welewolf.R.drawable.icon_empty_nodata, "该班级部落暂无学生"
                    ,desc, "", null);
        }
        UpdateClassService updateClassService = (UpdateClassService) getSystemService(UpdateClassService.SERVICE_NAME);
        updateClassService.updateClassCount(mClassInfoItem.classId, result.studentCount);
    }

    @Override
    public void onPreAction(int action, int pageNo) {
        super.onPreAction(action, pageNo);
    }

    @Override
    public UrlModelPair getRequestUrlModelPair(int action, int pageNo, Object... params) {
        String url = OnlineServices.getUnitStudentUrl((String) params[0], (String) params[2], (String) params[1]);
        return new UrlModelPair(url, new OnlineUnitStudentListInfo());
    }

    @Override
    public BaseObject onProcess(int action, int pageNo, Object... params) {
        super.onProcess(action, pageNo, params);
        String url = OnlineServices.getUnitStudentUrl((String) params[0], (String) params[2], (String) params[1]);
        OnlineUnitStudentListInfo result = new DataAcquirer<OnlineUnitStudentListInfo>().acquire(url, new OnlineUnitStudentListInfo(), -1);
        return result;
    }

    @Override
    public void onGetCache(int action, int pageNo, BaseObject result) {
        super.onGetCache(action, pageNo, result);
        updateWordList((OnlineUnitStudentListInfo) result);
    }

    @Override
    public void onGet(int action, int pageNo, BaseObject result, Object... params) {
        super.onGet(action, pageNo, result, params);
        updateWordList((OnlineUnitStudentListInfo) result);
    }

    @Override
    public void onFail(int action, int pageNo, BaseObject result, Object... params) {
        super.onFail(action, pageNo, result, params);
    }

    class StudentListAdapter extends SingleTypeAdapter<OnlineStudentInfo> {

        public StudentListAdapter(Context context) {
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
                viewHolder.mDataLayout = (RelativeLayout) convertView.findViewById(com.buang.welewolf.R.id.student_data_layout);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.mEncourage.setVisibility(View.GONE);
            viewHolder.mRankImg.setVisibility(View.GONE);
            viewHolder.mRankIndex.setVisibility(View.GONE);
            viewHolder.mLearnedRank.setVisibility(View.GONE);
            viewHolder.mLearningRank.setVisibility(View.GONE);

            RelativeLayout.LayoutParams headLp = (RelativeLayout.LayoutParams) viewHolder.mHead.getLayoutParams();
            headLp.leftMargin = UIUtils.dip2px(30);
            viewHolder.mHead.setLayoutParams(headLp);

            RelativeLayout.LayoutParams dataLp = (RelativeLayout.LayoutParams) viewHolder.mDataLayout.getLayoutParams();
            dataLp.leftMargin = UIUtils.dip2px(20);
            viewHolder.mDataLayout.setLayoutParams(dataLp);

            OnlineStudentInfo studentInfo = getItem(position);

            viewHolder.mName.setText(studentInfo.mStudentName);
            viewHolder.mLearnedCount.setText("" + studentInfo.learnedCount);
            viewHolder.mLearningCount.setText("" + studentInfo.learningCount);
            ImageFetcher.getImageFetcher().loadImage(studentInfo.mHeadPhoto, viewHolder.mHead, com.buang.welewolf.R.drawable.default_img, new RoundDisplayer());

            return convertView;
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
        public RelativeLayout mDataLayout;
    }
}
