package com.knowbox.teacher.modules.classes.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyena.framework.app.adapter.SingleTypeAdapter;
import com.knowbox.base.utils.ImageFetcher;
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.bean.OnlineStudentInfo;
import com.knowbox.teacher.widgets.RoundDisplayer;

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
                    R.layout.layout_learn_student_item, null);
            holder = new ViewHolder();
            holder.mHeadPhoto = (ImageView) convertView
                    .findViewById(R.id.learn_student_item_head);
            holder.mStudentName = (TextView) convertView
                    .findViewById(R.id.learn_student_item_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        OnlineStudentInfo item = getItem(position);
        ImageFetcher.getImageFetcher().loadImage(item.mHeadPhoto,
                holder.mHeadPhoto, R.drawable.default_img, new RoundDisplayer());
        holder.mStudentName.setText(item.mStudentName);
        return convertView;
    }

    class ViewHolder {
        public TextView mStudentName;
        public ImageView mHeadPhoto;
    }

}
