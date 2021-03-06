package com.buang.welewolf.modules.message;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.buang.welewolf.R;
import com.buang.welewolf.base.bean.OnlineFriendListInfo;
import com.buang.welewolf.base.database.bean.UserItem;
import com.buang.welewolf.base.http.services.OnlineServices;
import com.buang.welewolf.modules.guild.ContactInfoFragment;
import com.buang.welewolf.modules.utils.ConstantsUtils;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.hyena.framework.app.adapter.SingleTypeAdapter;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.utils.ImageFetcher;
import com.hyena.framework.utils.RoundDisplayer;

import java.util.ArrayList;

/**
 * Created by weilei on 17/5/19.
 */

public class ContactsFragment extends BaseUIFragment<UIFragmentHelper> {

    private ListView mListView;
    private ContactAdapter mAdapter;
    private OnlineFriendListInfo onlineContactListInfo;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
        setTitleStyle(STYLE_WITH_TITLE);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_welewolf_msg_contacts, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        getUIFragmentHelper().setTintBar(getResources().getColor(R.color.color_title_bar));
        mListView = (ListView) view.findViewById(R.id.listview);
        mAdapter = new ContactAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        initData();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openContactFragment(mAdapter.getItem(position));
            }
        });

        loadDefaultData(PAGE_MORE);
    }

    private void openContactFragment(UserItem contactInfo) {
        Bundle mBundle = new Bundle();
        mBundle.putSerializable(ConstantsUtils.KEY_BUNDLE_CONTACT_INFO, contactInfo);
        ContactInfoFragment fragment = ContactInfoFragment.newFragment(getActivity(), ContactInfoFragment.class, mBundle);
        showFragment(fragment);
    }

    private void initData() {
        onlineContactListInfo = new OnlineFriendListInfo();
        onlineContactListInfo.userItems = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            UserItem contactInfo = new UserItem();
            contactInfo.userName = "朱大壮" + i;
            contactInfo.sign = "他很懒什么都没说";
            contactInfo.sex = String.valueOf(i % 2);
            onlineContactListInfo.userItems.add(contactInfo);
        }
        updateData();
        showContent();
    }

    private void updateData() {
        mAdapter.setItems(onlineContactListInfo.userItems);
        getUIFragmentHelper().getTitleBar().setTitle("我的好友(" + mAdapter.getCount() + ")");
    }

    @Override
    public BaseObject onProcess(int action, int pageNo, Object... params) {
        String url = OnlineServices.getFriendListUrl();
        OnlineFriendListInfo result = new DataAcquirer<OnlineFriendListInfo>().acquire(url, new OnlineFriendListInfo(), -1);
        return result;
    }

    @Override
    public void onGet(int action, int pageNo, BaseObject result, Object... params) {
        super.onGet(action, pageNo, result, params);
        onlineContactListInfo = (OnlineFriendListInfo) result;
        updateData();
    }

    @Override
    public void onFail(int action, int pageNo, BaseObject result, Object... params) {
        super.onFail(action, pageNo, result, params);
        initData();
    }

    class ContactAdapter extends SingleTypeAdapter<UserItem> {

        public ContactAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.layout_welewolf_contact_item, null);
                viewHolder = new ViewHolder();

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            UserItem contactInfo = getItem(position);
            viewHolder.mHead = (ImageView) convertView.findViewById(R.id.ivHead);
            viewHolder.mSex = (ImageView) convertView.findViewById(R.id.ivSex);
            viewHolder.mName = (TextView) convertView.findViewById(R.id.tvName);
            viewHolder.mMsg = (TextView) convertView.findViewById(R.id.tvMsg);

            ImageFetcher.getImageFetcher().loadImage(contactInfo.headPhoto, viewHolder.mHead,
                    R.drawable.bt_message_default_head, new RoundDisplayer());
            viewHolder.mName.setText(contactInfo.userName);
            viewHolder.mMsg.setText(contactInfo.sign);
            viewHolder.mSex.setImageResource(contactInfo.sex.equals("1") ? R.drawable.icon_windows_female : R.drawable.icon_windows_man);
            return convertView;
        }
    }

    class ViewHolder {
        public ImageView mHead;
        public ImageView mSex;
        public TextView mName;
        public TextView mMsg;
    }

}
