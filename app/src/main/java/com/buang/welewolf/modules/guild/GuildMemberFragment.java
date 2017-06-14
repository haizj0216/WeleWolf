package com.buang.welewolf.modules.guild;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.buang.welewolf.R;
import com.buang.welewolf.base.bean.OnlineGuildInfo;
import com.buang.welewolf.base.bean.OnlineMemberListInfo;
import com.buang.welewolf.base.database.bean.UserItem;
import com.buang.welewolf.base.http.services.OnlineServices;
import com.buang.welewolf.modules.services.GuildService;
import com.buang.welewolf.modules.utils.ConstantsUtils;
import com.buang.welewolf.modules.utils.DialogUtils;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.buang.welewolf.modules.utils.WelewolfUtils;
import com.hyena.framework.app.adapter.SingleTypeAdapter;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.fragment.bean.MenuItem;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.utils.ImageFetcher;
import com.hyena.framework.utils.RoundDisplayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weilei on 17/6/6.
 */

public class GuildMemberFragment extends BaseUIFragment<UIFragmentHelper> {

    private static final int ACTION_GET_LIST = 1;
    private static final int ACTION_SET_JOB = 1;
    private static final int ACTION_REMOVE = 1;

    private ListView mListView;
    private MemberAdapter mAdapter;

    private OnlineMemberListInfo onlineMemberListInfo;
    private OnlineGuildInfo guildInfo;
    private OnlineGuildInfo mCurGuildInfo;
    private Dialog mDialog;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
        guildInfo = (OnlineGuildInfo) getArguments().getSerializable("guild");
        mCurGuildInfo = ((GuildService) getSystemService(GuildService.SERVICE_NAME)).getGuildInfo();
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
        loadData(ACTION_GET_LIST, PAGE_FIRST, guildInfo.guildID);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemClick(mAdapter.getItem(position));
            }
        });
    }

    /**
     * @param userItem
     */
    private void itemClick(UserItem userItem) {
        if (mCurGuildInfo == null) { //自己无公会
            openContactFragment(userItem);
        } else {
            if (mCurGuildInfo.guildID.equals(guildInfo.guildID)) {
                if (mCurGuildInfo.job == WelewolfUtils.JOB_HUIZHANG ||
                        mCurGuildInfo.job == WelewolfUtils.JOB_FUHUIZHANG) { //会长、副会长
                    showClickDialog(userItem);
                } else {
                    openContactFragment(userItem);
                }
            } else {
                openContactFragment(userItem);
            }
        }
    }

    /**
     *
     */
    private void showClickDialog(final UserItem userItem) {
        final List<MenuItem> items = new ArrayList<MenuItem>();
        items.add(new MenuItem(1, "查看详情"));
        items.add(new MenuItem(2, "设置职务"));
        items.add(new MenuItem(3, "移出公会"));
        mDialog = DialogUtils.getListDialog(getActivity(), "", items,
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        switch (position) {
                            case 0:
                                openContactFragment(userItem);
                                break;
                            case 1:
                                showJobDialog(userItem);
                                break;
                            case 2:
                                loadData(ACTION_REMOVE, PAGE_MORE, userItem.userId);
                                break;
                        }
                        if (mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                    }
                });
        if (!mDialog.isShowing()) {
            mDialog.show();
        }
    }

    private void showJobDialog(UserItem userItem) {
    }

    /**
     * 打开个人详情页
     *
     * @param contactInfo
     */
    private void openContactFragment(UserItem contactInfo) {
        Bundle mBundle = new Bundle();
        mBundle.putSerializable(ConstantsUtils.KEY_BUNDLE_CONTACT_INFO, contactInfo);
        ContactInfoFragment fragment = ContactInfoFragment.newFragment(getActivity(), ContactInfoFragment.class, mBundle);
        showFragment(fragment);
    }

    @Override
    public BaseObject onProcess(int action, int pageNo, Object... params) {
        if (action == ACTION_GET_LIST) {
            String url = OnlineServices.getGuildMemberListUrl((String) params[0]);
            OnlineMemberListInfo result = new DataAcquirer<OnlineMemberListInfo>().acquire(url, new OnlineMemberListInfo(), -1);
            return result;
        } else if (action == ACTION_REMOVE) {
            String url = OnlineServices.getRemoveMemberUrl((String) params[0]);
            BaseObject result = new DataAcquirer<>().acquire(url, new BaseObject(), -1);
            return result;
        } else if (action == ACTION_SET_JOB) {
            String url = OnlineServices.getSetJobUrl((String) params[0], (int) params[1]);
            BaseObject result = new DataAcquirer<>().acquire(url, new BaseObject(), -1);
            return result;
        }
        return super.onProcess(action, pageNo, params);
    }

    @Override
    public void onGet(int action, int pageNo, BaseObject result, Object... params) {
        super.onGet(action, pageNo, result, params);
        if (action == ACTION_GET_LIST) {
            onlineMemberListInfo = (OnlineMemberListInfo) result;
            mAdapter.setItems(onlineMemberListInfo.mMembers);
        }
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
            UserItem userItem = getItem(position);
            viewHolder.mName.setText(userItem.userName);
            ImageFetcher.getImageFetcher().loadImage(userItem.headPhoto, viewHolder.mHead, R.drawable.bt_message_default_head, new RoundDisplayer());
            viewHolder.mLevel.setText("Lv." + userItem.level);
            viewHolder.mPost.setText(WelewolfUtils.getGuildJob(userItem.job));
            viewHolder.mPost.setText("" + userItem.popularity);
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
