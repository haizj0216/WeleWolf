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
import com.buang.welewolf.base.bean.ContactInfo;
import com.buang.welewolf.base.bean.OnlineContactListInfo;
import com.buang.welewolf.modules.guild.ContactInfoFragment;
import com.buang.welewolf.modules.utils.ConstantsUtils;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.hyena.framework.app.adapter.SingleTypeAdapter;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.utils.ImageFetcher;
import com.hyena.framework.utils.RoundDisplayer;

import java.util.ArrayList;

import io.rong.imkit.model.UIConversation;

/**
 * Created by weilei on 17/5/19.
 */

public class ContactsFragment extends BaseUIFragment<UIFragmentHelper> {

    private ListView mListView;
    private ContactAdapter mAdapter;
    private OnlineContactListInfo onlineContactListInfo;

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
    }

    private void openContactFragment(ContactInfo contactInfo) {
        Bundle mBundle = new Bundle();
        mBundle.putSerializable(ConstantsUtils.KEY_BUNDLE_CONTACT_INFO, contactInfo);
        ContactInfoFragment fragment = ContactInfoFragment.newFragment(getActivity(), ContactInfoFragment.class, mBundle);
        showFragment(fragment);
    }

    private void initData() {
        onlineContactListInfo = new OnlineContactListInfo();
        onlineContactListInfo.mContacts = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ContactInfo contactInfo = new ContactInfo();
            contactInfo.name = "朱大壮" + i;
            contactInfo.sign = "他很懒什么都没说";
            contactInfo.sex = i % 2;
            onlineContactListInfo.mContacts.add(contactInfo);
        }
        mAdapter.setItems(onlineContactListInfo.mContacts);
        getUIFragmentHelper().getTitleBar().setTitle("我的好友(" + mAdapter.getCount() + ")");
    }

    class ContactAdapter extends SingleTypeAdapter<ContactInfo> {

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
            ContactInfo contactInfo = getItem(position);
            viewHolder.mHead = (ImageView) convertView.findViewById(R.id.ivHead);
            viewHolder.mSex = (ImageView) convertView.findViewById(R.id.ivSex);
            viewHolder.mName = (TextView) convertView.findViewById(R.id.tvName);
            viewHolder.mMsg = (TextView) convertView.findViewById(R.id.tvMsg);

            ImageFetcher.getImageFetcher().loadImage(contactInfo.head, viewHolder.mHead,
                    R.drawable.bt_message_default_head, new RoundDisplayer());
            viewHolder.mName.setText(contactInfo.name);
            viewHolder.mMsg.setText(contactInfo.sign);
            viewHolder.mSex.setImageResource(contactInfo.sex == 1 ? R.drawable.icon_windows_female : R.drawable.icon_windows_man);
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
