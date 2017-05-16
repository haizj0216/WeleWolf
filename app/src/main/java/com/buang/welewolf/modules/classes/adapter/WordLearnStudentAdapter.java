package com.buang.welewolf.modules.classes.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.buang.welewolf.base.bean.OnlineStudentInfo;
import com.buang.welewolf.widgets.RoundDisplayer;
import com.hyena.framework.app.adapter.SingleTypeAdapter;
import com.knowbox.base.utils.ImageFetcher;

/**
 * Created by weilei on 17/2/15.
 */
public class WordLearnStudentAdapter extends SingleTypeAdapter<OnlineStudentInfo> {
    public WordLearnStudentAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext,
                    com.buang.welewolf.R.layout.layout_learn_student_item, null);
            holder = new ViewHolder();
            holder.mHeadPhoto = (ImageView) convertView
                    .findViewById(com.buang.welewolf.R.id.learn_student_item_head);
            holder.mStudentName = (TextView) convertView
                    .findViewById(com.buang.welewolf.R.id.learn_student_item_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        OnlineStudentInfo item = getItem(position);
        ImageFetcher.getImageFetcher().loadImage(item.mHeadPhoto,
                holder.mHeadPhoto, com.buang.welewolf.R.drawable.default_img, new RoundDisplayer());
        holder.mStudentName.setText(item.mStudentName);
        return convertView;
    }

    class ViewHolder {
        public TextView mStudentName;
        public ImageView mHeadPhoto;
    }

}
