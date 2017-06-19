package com.buang.welewolf.modules.guild;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.buang.welewolf.R;
import com.buang.welewolf.base.bean.OnlineGuildInfo;
import com.buang.welewolf.modules.utils.WolfUtils;
import com.hyena.framework.app.adapter.SingleTypeAdapter;

/**
 * Created by weilei on 17/6/6.
 */

public class GuildAdapter extends SingleTypeAdapter<OnlineGuildInfo> {
    public GuildAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.layout_welewolf_guild_item, null);
            viewHolder = new ViewHolder();
            viewHolder.mHead = (ImageView) convertView.findViewById(R.id.ivHead);
            viewHolder.mName = (TextView) convertView.findViewById(R.id.tvName);
            viewHolder.mLevel = (ImageView) convertView.findViewById(R.id.tvLevel);
            viewHolder.mNumber = (TextView) convertView.findViewById(R.id.tvMemberNumber);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        OnlineGuildInfo guildInfo = getItem(position);
        viewHolder.mName.setText(guildInfo.guildName);
        viewHolder.mNumber.setText(guildInfo.curCount + "/" + guildInfo.maxCount);
        viewHolder.mLevel.setImageResource(WolfUtils.getGuildLevel(guildInfo.level));
        return convertView;
    }

    class ViewHolder {
        public ImageView mHead;
        public TextView mName;
        public ImageView mLevel;
        public TextView mNumber;
    }
}
