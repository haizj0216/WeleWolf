package com.knowbox.teacher.modules.homework.competition;

import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hyena.framework.app.adapter.SingleTypeAdapter;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.utils.ImageFetcher;
import com.knowbox.base.utils.UIUtils;
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.bean.OptionsItemInfo;
import com.knowbox.teacher.base.database.bean.QuestionItem;
import com.knowbox.teacher.base.services.player.AudioPlayerService;
import com.knowbox.teacher.modules.utils.ConstantsUtils;
import com.knowbox.teacher.modules.utils.UIFragmentHelper;
import com.knowbox.teacher.widgets.GymOptionView;
import com.knowbox.teacher.widgets.GymWordBlankView;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by weilei on 17/4/9.
 */
public class LoopWordDetailItemFragment extends BaseUIFragment<UIFragmentHelper> {

    private ListView mListView;
    private ArrayList<QuestionItem> mQuestions;
    private AudioPlayerService mAudioPlayer;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(false);
        setTitleStyle(STYLE_NO_TITLE);
        mQuestions = (ArrayList<QuestionItem>) getArguments().getSerializable(ConstantsUtils.KEY_BUNDLE_QUESTION_LIST);
        mAudioPlayer = (AudioPlayerService) getSystemService(AudioPlayerService.SERVICE_NAME);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_common_listview, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        mListView = (ListView) view.findViewById(R.id.common_list);
        QuestionAdapter adapter = new QuestionAdapter(getActivity());
        adapter.setItems(mQuestions);
        mListView.setAdapter(adapter);
    }

    private void playAudio(String url) {
        mAudioPlayer.playSong(url);
    }

    class QuestionAdapter extends SingleTypeAdapter<QuestionItem> {

        public QuestionAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.layout_loop_word_detail_question_item, null);
                viewHolder = new ViewHolder();
                viewHolder.mIndex = (TextView) convertView.findViewById(R.id.question_index);
                viewHolder.mCountView = (TextView) convertView.findViewById(R.id.question_wrong_count);
                viewHolder.mContentText = (TextView) convertView.findViewById(R.id.gym_question_conent_text);
                viewHolder.mContentImage = (ImageView) convertView.findViewById(R.id.gym_question_conent_image);
                viewHolder.mContentAudio = (GifImageView) convertView.findViewById(R.id.gym_question_content_audio);
                viewHolder.mBlankView = (GymWordBlankView) convertView.findViewById(R.id.gym_question_blank);
                viewHolder.mOptionLayout = (LinearLayout) convertView.findViewById(R.id.gym_question_options);
                viewHolder.mDivider = convertView.findViewById(R.id.divider);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final QuestionItem questionItem = getItem(position);
            //文本类型
            if (!TextUtils.isEmpty(questionItem.gymText)) {
                viewHolder.mContentText.setText(questionItem.gymText);
                viewHolder.mContentText.setVisibility(View.VISIBLE);
                viewHolder.mContentText.setTextSize(19);
            } else {
                viewHolder.mContentText.setVisibility(View.GONE);
            }
            //图片类型
            if (!TextUtils.isEmpty(questionItem.gymImage)) {
                viewHolder.mContentImage.setVisibility(View.VISIBLE);
                ImageFetcher.getImageFetcher().loadImage(questionItem.gymImage, viewHolder.mContentImage, 0);
            } else {
                viewHolder.mContentImage.setVisibility(View.GONE);
            }
            //听力类型
            if (!TextUtils.isEmpty(questionItem.gymAudio) && TextUtils.isEmpty(questionItem.gymText) && TextUtils.isEmpty(questionItem.gymImage)) {
                viewHolder.mContentAudio.setVisibility(View.VISIBLE);
                viewHolder.mContentAudio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playAudio(questionItem.gymAudio);
                    }
                });
            } else {
                viewHolder.mContentAudio.setVisibility(View.GONE);
            }

            if (questionItem.isBlank()) {//挖空
                viewHolder.mBlankView.setVisibility(View.VISIBLE);
                viewHolder.mOptionLayout.removeAllViews();
                viewHolder.mOptionLayout.setVisibility(View.GONE);
                viewHolder.mBlankView.updateAnlyze(questionItem);
            } else {
                viewHolder.mBlankView.setVisibility(View.GONE);
                viewHolder.mOptionLayout.setVisibility(View.VISIBLE);
                viewHolder.mOptionLayout.removeAllViews();
                if (questionItem.itemList != null && questionItem.itemList.size() > 0) {
                    viewHolder.mOptionLayout.removeAllViews();
                    List<OptionsItemInfo> optionItems = questionItem.itemList;
                    for (int i = 0; i < optionItems.size(); i++) {
                        final OptionsItemInfo optionItem = optionItems.get(i);
                        final GymOptionView gymOptionView = (GymOptionView) View.inflate(getActivity(), R.layout.layout_gym_question_option, null);
                        gymOptionView.setOptionContent(optionItem.getCode(), optionItem.getValue(), R.drawable.bg_gym_question_option_analyze);
                        boolean isRight = questionItem.mRightAnswer.equals(questionItem.mAnswer);
                        boolean isSelect = optionItem.getCode().equals(questionItem.mAnswer);
                        if (isSelect) {
                            gymOptionView.setSelectResult(isRight);
                        }
                        if (!isRight) {
                            if (optionItem.getCode().equals(questionItem.mRightAnswer)) {
                                gymOptionView.setUnSelectRight();
                            }
                        }

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.rightMargin = params.leftMargin = UIUtils.dip2px(15);
                        params.bottomMargin = UIUtils.dip2px(8);
                        viewHolder.mOptionLayout.addView(gymOptionView, params);
                    }
                }
            }
            viewHolder.mIndex.setText("题目" + (position + 1) + ".");

            String wrongCount = String.valueOf(questionItem.wrongCount);
            String text = "答错" + wrongCount + "次";
            SpannableString spannableString = new SpannableString(text);
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.color_button_red));
            spannableString.setSpan(foregroundColorSpan, text.indexOf(wrongCount), wrongCount.length() + text.indexOf(wrongCount), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            viewHolder.mCountView.setText(spannableString);

            if (position == getCount() - 1) {
                viewHolder.mDivider.setVisibility(View.GONE);
            } else {
                viewHolder.mDivider.setVisibility(View.VISIBLE);
            }
            return convertView;
        }
    }

    class ViewHolder {
        public TextView mIndex;
        public TextView mCountView;
        public GymWordBlankView mBlankView;
        public TextView mContentText;
        public ImageView mContentImage;
        public GifImageView mContentAudio;
        public LinearLayout mOptionLayout;
        public View mDivider;
    }
}
