package com.buang.welewolf.modules.classes.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.buang.welewolf.modules.utils.ConstantsUtils;
import com.hyena.framework.app.adapter.SingleTypeAdapter;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.widget.AccuracGridView;
import com.buang.welewolf.base.bean.OnlineWordLearnInfo;

/**
 * Created by weilei on 17/2/15.
 */
public class WordLearnAdapter extends SingleTypeAdapter<OnlineWordLearnInfo.LearnStudentList> {

    private BaseUIFragment mFragment;

    public WordLearnAdapter(BaseUIFragment fragment) {
        super(fragment.getActivity());
        mFragment = fragment;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(mFragment.getActivity(), com.buang.welewolf.R.layout.layout_word_learn_gridview, null);
            viewHolder = new ViewHolder();
            viewHolder.mStatus = (TextView) convertView.findViewById(com.buang.welewolf.R.id.word_learn_status);
            viewHolder.mGridView = (AccuracGridView) convertView.findViewById(com.buang.welewolf.R.id.word_learn_student_list);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        OnlineWordLearnInfo.LearnStudentList learnStudentList = getItem(position);
        WordLearnStudentAdapter mAdapter = new WordLearnStudentAdapter(mFragment.getActivity());
        mAdapter.setItems(learnStudentList.studentList);
        viewHolder.mGridView.setAdapter(mAdapter);

        if (learnStudentList.learnStatus == ConstantsUtils.LEARN_STATUS_LEARNED) {
            viewHolder.mStatus.setText("-  " + learnStudentList.studentList.size() + "人已练熟  -");
        } else if (learnStudentList.learnStatus == ConstantsUtils.LEARN_STATUS_LEARNING) {
            viewHolder.mStatus.setText("-  " + learnStudentList.studentList.size() + "人练习中  -");
        } else if (learnStudentList.learnStatus == ConstantsUtils.LEARN_STATUS_UNLEARN) {
            viewHolder.mStatus.setText("-  " + learnStudentList.studentList.size() + "人未练习  -");
        }
        return convertView;
    }

    class ViewHolder {
        public TextView mStatus;
        public AccuracGridView mGridView;
    }
}
