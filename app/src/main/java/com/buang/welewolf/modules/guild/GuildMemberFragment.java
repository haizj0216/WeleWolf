package com.buang.welewolf.modules.guild;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.buang.welewolf.R;
import com.buang.welewolf.base.bean.OnlineMemberListInfo;
import com.buang.welewolf.base.database.bean.UserItem;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.hyena.framework.app.adapter.SingleTypeAdapter;
import com.hyena.framework.app.fragment.BaseUIFragment;

import java.util.ArrayList;

/**
 * Created by weilei on 17/6/6.
 */

public class GuildMemberFragment extends BaseUIFragment<UIFragmentHelper> {

    private ListView mListView;
    private MemberAdapter mAdapter;

    private OnlineMemberListInfo onlineMemberListInfo;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_welewolf_member_list, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        getUIFragmentHelper().getTitleBar().setTitle("公会成员");
        getUIFragmentHelper().setTintBar(getResources().getColor(R.color.color_title_bar));
        mListView = (ListView) view.findViewById(R.id.lvListView);
        mAdapter = new MemberAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        initData();
    }

    private void initData() {
        onlineMemberListInfo = new OnlineMemberListInfo();
        onlineMemberListInfo.mMembers = new ArrayList<>();
        int size = 20;
        for (int i = 0; i < size; i++) {
            UserItem contactInfo = new UserItem();
            contactInfo.userName = "李一" + "i";
            onlineMemberListInfo.mMembers.add(contactInfo);
        }
        mAdapter.setItems(onlineMemberListInfo.mMembers);
    }

    class MemberAdapter extends SingleTypeAdapter<UserItem> {

        public MemberAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.layout_welewolf_member_item, null);
                viewHolder = new ViewHolder();
                viewHolder.mHead = (ImageView) convertView.findViewById(R.id.ivHead);
                viewHolder.mActive = (TextView) convertView.findViewById(R.id.tvActive);
                viewHolder.mName = (TextView) convertView.findViewById(R.id.tvName);
                viewHolder.mLevel = (TextView) convertView.findViewById(R.id.tvLevel);
                viewHolder.mPost = (TextView) convertView.findViewById(R.id.tvPost);
                viewHolder.mOnline = (TextView) convertView.findViewById(R.id.tvOnline);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }


            return convertView;
        }
    }

    class ViewHolder {
        public ImageView mHead;
        public TextView mName;
        public TextView mLevel;
        public TextView mPost;
        public TextView mActive;
        public TextView mOnline;
    }
}
