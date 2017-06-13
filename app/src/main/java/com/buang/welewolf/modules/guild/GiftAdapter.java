package com.buang.welewolf.modules.guild;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.buang.welewolf.R;
import com.buang.welewolf.base.bean.GiftInfo;
import com.hyena.framework.app.adapter.SingleTypeAdapter;
import com.hyena.framework.utils.ImageFetcher;

/**
 * Created by weilei on 17/6/13.
 */

public class GiftAdapter extends SingleTypeAdapter<GiftInfo> {
    public GiftAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.layout_welewolf_gift_item, null);
            viewHolder = new ViewHolder();
            viewHolder.mImage = (ImageView) convertView.findViewById(R.id.ivImage);
            viewHolder.mName = (TextView) convertView.findViewById(R.id.tvName);
            viewHolder.mCount = (TextView) convertView.findViewById(R.id.tvCount);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        GiftInfo giftInfo = getItem(position);
        viewHolder.mName.setText(giftInfo.name);
        ImageFetcher.getImageFetcher().loadImage(giftInfo.image, viewHolder.mImage, 0);
        viewHolder.mCount.setText("x" + giftInfo.count);

        return convertView;
    }

    class ViewHolder {
        public ImageView mImage;
        public TextView mName;
        public TextView mCount;
    }
}
