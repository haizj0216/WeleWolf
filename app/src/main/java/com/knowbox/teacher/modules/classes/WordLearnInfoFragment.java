package com.knowbox.teacher.modules.classes;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.knowbox.base.utils.UIUtils;
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.bean.OnlineStudentInfo;
import com.knowbox.teacher.base.bean.OnlineWordInfo;
import com.knowbox.teacher.base.bean.OnlineWordLearnInfo;
import com.knowbox.teacher.base.database.bean.ClassInfoItem;
import com.knowbox.teacher.base.http.services.OnlineServices;
import com.knowbox.teacher.modules.classes.adapter.WordLearnAdapter;
import com.knowbox.teacher.modules.utils.ConstantsUtils;
import com.knowbox.teacher.modules.utils.UIFragmentHelper;

/**
 * Created by weilei on 17/2/15.
 */
public class WordLearnInfoFragment extends BaseUIFragment<UIFragmentHelper> {

    private ListView mListView;
    private TextView mExplain;
    private TextView mDesc;
    private WordLearnAdapter mAdapter;
    private OnlineWordInfo mWordInfo;
    private ClassInfoItem mClassInfo;
    private OnlineWordLearnInfo mWordLearnInfo;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
        mWordInfo = (OnlineWordInfo) getArguments().getSerializable(ConstantsUtils.KEY_WORDINFO);
        mClassInfo = getArguments().getParcelable(ConstantsUtils.KEY_CLASSINFOITEM);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_word_learn_info, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        getUIFragmentHelper().getTitleBar().setTitle("掌握详情");
        mListView = (ListView) view.findViewById(R.id.word_learn_list);
        mAdapter = new WordLearnAdapter(this);

        View header = View.inflate(getActivity(), R.layout.layout_word_learn_info_header, null);
        ((TextView) header.findViewById(R.id.word_learn_info_name)).setText("" + mWordInfo.content);
        mExplain = ((TextView) header.findViewById(R.id.word_learn_info_explain));
        mDesc = (TextView) header.findViewById(R.id.word_learn_info_desc);
        mListView.addHeaderView(header);
        loadDefaultData(PAGE_FIRST, mClassInfo.classId, mWordInfo.wordID);
    }

    private void updateStudentInfo() {
        mAdapter.setItems(mWordLearnInfo.learnStudentLists);
        mListView.setAdapter(mAdapter);
        mExplain.setText(mWordLearnInfo.translation);
        mDesc.setText(mWordLearnInfo.learnRemind);
        if (mAdapter.getCount() == 0) {
            String desc = "学生在学生端输入班级部落号\"" + mClassInfo.classCode + "\"加入";
            getUIFragmentHelper().getEmptyView().setTopMargin(UIUtils.dip2px(171));
            getUIFragmentHelper().getEmptyView().setEmptyBg(getResources().getColor(R.color.white));
            getUIFragmentHelper().getEmptyView().showEmpty(R.drawable.icon_empty_nodata, "该班级部落暂无学生"
                    ,desc, "", null);
        }
    }

    @Override
    public void onPreAction(int action, int pageNo) {
        super.onPreAction(action, pageNo);
    }

    @Override
    public BaseObject onProcess(int action, int pageNo, Object... params) {
        super.onProcess(action, pageNo, params);
        String url = OnlineServices.getWordStudentListUrl((String) params[0], (String) params[1]);
        OnlineWordLearnInfo result = new DataAcquirer<OnlineWordLearnInfo>().acquire(url, new OnlineWordLearnInfo(), -1);
        return result;
    }

    @Override
    public void onGet(int action, int pageNo, BaseObject result, Object... params) {
        super.onGet(action, pageNo, result, params);
        mWordLearnInfo = (OnlineWordLearnInfo) result;
        updateStudentInfo();
    }

    private void initData() {
        mWordLearnInfo = new OnlineWordLearnInfo();

        OnlineWordLearnInfo.LearnStudentList unlearn = new OnlineWordLearnInfo.LearnStudentList();
        unlearn.learnStatus = ConstantsUtils.LEARN_STATUS_UNLEARN;
        for (int i = 0; i < 5; i++) {
            OnlineStudentInfo studentInfo = new OnlineStudentInfo();
            studentInfo.mStudentName = "王狗蛋" + i;
            unlearn.studentList.add(studentInfo);
        }
        mWordLearnInfo.learnStudentLists.add(unlearn);

        OnlineWordLearnInfo.LearnStudentList learning = new OnlineWordLearnInfo.LearnStudentList();
        learning.learnStatus = ConstantsUtils.LEARN_STATUS_LEARNING;
        for (int i = 0; i < 3; i++) {
            OnlineStudentInfo studentInfo = new OnlineStudentInfo();
            studentInfo.mStudentName = "朱大壮" + i;
            learning.studentList.add(studentInfo);
        }
        mWordLearnInfo.learnStudentLists.add(learning);

        OnlineWordLearnInfo.LearnStudentList learned = new OnlineWordLearnInfo.LearnStudentList();
        learned.learnStatus = ConstantsUtils.LEARN_STATUS_LEARNED;
        for (int i = 0; i < 9; i++) {
            OnlineStudentInfo studentInfo = new OnlineStudentInfo();
            studentInfo.mStudentName = "毛蛋军" + i;
            learned.studentList.add(studentInfo);
        }
        mWordLearnInfo.learnStudentLists.add(learned);
    }

}
