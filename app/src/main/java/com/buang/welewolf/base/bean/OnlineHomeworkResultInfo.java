package com.buang.welewolf.base.bean;

import com.buang.welewolf.base.database.bean.QuestionItem;
import com.hyena.framework.datacache.BaseObject;
import com.buang.welewolf.modules.utils.SubjectUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Fanjb
 * @name 作业结果
 * @date 2015年5月27日
 */
public class OnlineHomeworkResultInfo extends BaseObject implements Serializable {

    private static final long serialVersionUID = 137202004883L;

//	public static final int TYPE_CHOICE = 0;// 选择题 done
//	public static final int TYPE_MUTICHOICE = 1;// 多选题 done
//	public static final int TYPE_ANSWER = 2;// 解答题
//	public static final int TYPE_FILLIN = 3;// 填空
//	public static final int TYPE_TRANSLATE = 4;// 翻译
//	public static final int TYPE_CLOZE = 5;// 完型
//	public static final int TYPE_COMPREHENISON = 6;// 阅读
//	public static final int TYPE_RATIONAL = 7;// 语法
//	public static final int TYPE_COMPOSITION = 8;// 作文
//	public static final int TYPE_FILLIN_WITH_PIC = 9;// 带照片的填空题

    public String homeworkID;
    public int totalCount = 1;
    public float rightRate;
    public float classRightRate;
    public int submitRank;

    /**
     * 习题列表
     */
    public List<QuestionItem> list = new LinkedList<QuestionItem>();

    public List<QuestionItem> getQuestionList() {
        return this.list;
    }

    @Override
    public void parse(JSONObject data) {
        super.parse(data);
        JSONObject json = data.optJSONObject("data");
        this.mJsonStr = json.toString();

        // 正确率与提交次数
        if (json.has("rightRate")) {
            this.rightRate = Float.parseFloat(json.optString("rightRate"));
        }
        if (json.has("classRightRate")) {
            this.classRightRate = Float.parseFloat(json
                    .optString("classRightRate"));
        }
        this.submitRank = json.optInt("submitRank");

        // 作业结果列表
        this.homeworkID = json.optString("homeworkID");
        this.totalCount = json.optInt("totalCount");
        JSONArray array = json.optJSONArray("list");
        if (array == null) {
            array = json.optJSONArray("questionList");
        }
        if (array != null) {
            int index = 0;
            for (int i = 0; i < array.length(); i++) {
                QuestionItem model = new QuestionItem();

                // 解析数据
                model = model.getQuestionItem(array.optJSONObject(i));

                // 设置题号
                model.mHomeworkId = homeworkID;

                list.add(model);
            }


            list = sortQuestions(list);

            for (int i = 0; i < list.size(); i++) {
                QuestionItem model = list.get(i);
                // 一个大题包含若干小题的特殊处理（完型填空、阅读、语法）
                int type = model.mQuestionType;
                if (SubjectUtils.isMultiQuestionType(type)) {
//					model = new QuestionItem();
//					model = model.parseSingleQuestion(array.optJSONObject(i));

                    List<QuestionItem> clozes = model.mSubQuestions;
                    for (int j = 0; j < clozes.size(); j++) {
                        QuestionItem opt = clozes.get(j);
                        opt.index = (opt.tempIndex = index += 1);
                    }
                    totalCount += clozes.size();
                } else {
                    model.index = model.tempIndex = index += 1;
                    totalCount++;
                }
            }


        }
    }

    private List<QuestionItem> sortQuestions(List<QuestionItem> items) {
        List<QuestionItem> mSortQuestions = new ArrayList<QuestionItem>();
        int[] typelist = SubjectUtils.getSortQuestionType();
        if (typelist != null) {
            for (int i = 0; i < typelist.length; i++) {
                int type = typelist[i];
                List<QuestionItem> questionItems = getQuestionItems(items, type);
                if (questionItems != null && questionItems.size() > 0) {
                    mSortQuestions.addAll(questionItems);
                }
            }
        }
        return mSortQuestions;
    }

    private List<QuestionItem> getQuestionItems(
            List<QuestionItem> questionItems, int type) {
        List<QuestionItem> items = new ArrayList<QuestionItem>();
        for (int i = 0; i < questionItems.size(); i++) {
            QuestionItem item = questionItems.get(i);
            if (item.mQuestionType == type) {
                items.add(item);
            }
        }
        return items;
    }

    private String mJsonStr;

    public String getJsonString() {
        return this.mJsonStr;
    }

    private List<List<QuestionItem>> mGroupCounts;

    public List<List<QuestionItem>> getGroupCounts() {
        return mGroupCounts;
    }

    /**
     * 分类汇总不同的题型，加入分类列表中
     */
    public void sort() {
        List<QuestionItem> choiceQuestions = new ArrayList<QuestionItem>();
        List<QuestionItem> multiChoiceQuestions = new ArrayList<QuestionItem>();
        List<QuestionItem> answerQuestions = new ArrayList<QuestionItem>();
        List<QuestionItem> fillinQuestions = new ArrayList<QuestionItem>();
        List<QuestionItem> translateQuestions = new ArrayList<QuestionItem>();
        List<QuestionItem> clozeQuestions = new ArrayList<QuestionItem>();
        List<QuestionItem> comprehensionQuestions = new ArrayList<QuestionItem>();
        List<QuestionItem> rationalQuestions = new ArrayList<QuestionItem>();
        List<QuestionItem> compositionQuestions = new ArrayList<QuestionItem>();
        List<QuestionItem> answerFillinQuestions = new ArrayList<QuestionItem>();
        List<QuestionItem> shortDialogueQuestions = new ArrayList<QuestionItem>();
        List<QuestionItem> longDialogueQuestions = new ArrayList<QuestionItem>();
        List<QuestionItem> monoDialogueQuestions = new ArrayList<QuestionItem>();
        for (QuestionItem model : list) {
            switch (model.mQuestionType) {
                case SubjectUtils.QUESTION_TYPE_CHOICE:
                    sortQuestions(choiceQuestions, model);
                    break;
                case SubjectUtils.QUESTION_TYPE_MULTI_CHOICE:
                    sortQuestions(multiChoiceQuestions, model);
                    break;
                case SubjectUtils.QUESTION_TYPE_SUBJECTIVE:
                    sortQuestions(answerQuestions, model);
                    break;
                case SubjectUtils.QUESTION_TYPE_FILL_IN:
                    sortQuestions(fillinQuestions, model);
                    break;
                case SubjectUtils.QUESTION_TYPE_TRANSLATE:
                    sortQuestions(translateQuestions, model);
                    break;
                case SubjectUtils.QUESTION_TYPE_CLOZE:
                    sortQuestions(clozeQuestions, model);
                    break;
                case SubjectUtils.QUESTION_TYPE_COMPREHENSION:
                    sortQuestions(comprehensionQuestions, model);
                    break;
                case SubjectUtils.QUESTION_TYPE_RATIONAL:
                    sortQuestions(rationalQuestions, model);
                    break;
                case SubjectUtils.QUESTION_TYPE_COMPOSITION:
                    sortQuestions(compositionQuestions, model);
                    break;
                case SubjectUtils.QUESTION_TYPE_ANSWERFILLIN:
                    sortQuestions(answerFillinQuestions, model);
                    break;
                case SubjectUtils.QUESTION_TYPE_SHORT_DIALOGUE:
                    sortQuestions(shortDialogueQuestions, model);
                    break;
                case SubjectUtils.QUESTION_TYPE_LONG_DIALGUE:
                    sortQuestions(longDialogueQuestions, model);
                    break;
                case SubjectUtils.QUESTION_TYPE_MONOLOGUE:
                    sortQuestions(monoDialogueQuestions, model);
                    break;
                default:
                    break;
            }
        }

        // 加入分类列表中
        if (mGroupCounts == null) {
            mGroupCounts = new ArrayList<List<QuestionItem>>();
        }
        if (mGroupCounts.size() > 0) {
            mGroupCounts.clear();
        }
        mGroupCounts.add(shortDialogueQuestions);
        mGroupCounts.add(longDialogueQuestions);
        mGroupCounts.add(monoDialogueQuestions);
        mGroupCounts.add(choiceQuestions);
        mGroupCounts.add(multiChoiceQuestions);
        mGroupCounts.add(fillinQuestions);
        mGroupCounts.add(translateQuestions);
        mGroupCounts.add(clozeQuestions);
        mGroupCounts.add(comprehensionQuestions);
        mGroupCounts.add(rationalQuestions);
        mGroupCounts.add(compositionQuestions);
        mGroupCounts.add(answerFillinQuestions);
        mGroupCounts.add(answerQuestions);
    }

    /**
     * 分类题型
     *
     * @param questions
     * @param model
     */
    private void sortQuestions(List<QuestionItem> questions,
                               QuestionItem model) {
        questions.add(model);
    }

//	/**
//	 * 解析题目
//	 * 
//	 * @param question
//	 */
//	private QuestionItem getQuestionItem(JSONObject question) {
//		int type = question.optInt("questionType");
//		switch (type) {
//		case TYPE_CHOICE:
//		case TYPE_MUTICHOICE:
//		case TYPE_ANSWER:
//		case TYPE_TRANSLATE: {
//			return parseSingleQuestion(question);
//		}
//		case TYPE_FILLIN_WITH_PIC:
//		case TYPE_COMPREHENISON:
//		case TYPE_CLOZE:
//		case TYPE_FILLIN:
//		case TYPE_RATIONAL: {
//			QuestionItem item = parseSingleQuestion(question);
//			JSONArray array = question.optJSONArray("itemList");
//			if (array != null) {
//				for (int i = 0; i < array.length(); i++) {
//					JSONObject subQuestion = array.optJSONObject(i);
//					if (subQuestion != null) {
//						QuestionItem subitem = getQuestionItem(subQuestion);
//						if (item.mSubQuestions == null) {
//							item.mSubQuestions = new ArrayList<QuestionItem>();
//						}
//						item.mSubQuestions.add(subitem);
//					}
//				}
//			}
//			return item;
//		}
//		case TYPE_COMPOSITION: {
//			QuestionItem item = parseSingleQuestion(question);
//			JSONArray pTagsArray = question.optJSONArray("positiveList");
//			if (pTagsArray != null && pTagsArray.length() > 0) {
//				List<OnlineTagInfo> pTags = new ArrayList<OnlineTagInfo>(
//						pTagsArray.length());
//				for (int i = 0; i < pTagsArray.length(); i++) {
//					OnlineTagInfo pTag = new OnlineTagInfo(
//							pTagsArray.optJSONObject(i));
//					pTag.mTagType = OnlineTagInfo.FLAG_POSITIVE_TAG;
//					pTag.mQuestionId = item.mQuestionId;
//					pTag.mHomeworkId = item.mHomeworkId;
//					pTags.add(pTag);
//				}
//				item.mPositiveTags = pTags;
//			}
//			JSONArray nTagsArray = question.optJSONArray("negativeList");
//			if (nTagsArray != null && nTagsArray.length() > 0) {
//				List<OnlineTagInfo> nTags = new ArrayList<OnlineTagInfo>(
//						nTagsArray.length());
//				for (int i = 0; i < nTagsArray.length(); i++) {
//					OnlineTagInfo nTag = new OnlineTagInfo(
//							nTagsArray.optJSONObject(i));
//					nTag.mTagType = OnlineTagInfo.FLAG_NEGATIVE_TAG;
//					nTag.mQuestionId = item.mQuestionId;
//					nTag.mHomeworkId = item.mHomeworkId;
//					nTags.add(nTag);
//				}
//				item.mNegativeTags = nTags;
//			}
//			return item;
//		}
//		default:
//			break;
//		}
//		return null;
//	}
//
//	private QuestionItem parseSingleQuestion(JSONObject question) {
//		int type = question.optInt("questionType");
//		QuestionItem item = new QuestionItem();
//		item.mQuestionType = type;
//		item.mQuestionId = question.optString("questionID");
//		item.mContent = question.optString("content");
//		if (TextUtils.isEmpty(item.mContent)) {
//			item.mContent = question.optString("question");
//		}
//		item.mQuestionIndex = question.optString("questionNo");
//		item.mAnswerExplain = question.optString("answerExplain");
//		item.mRightAnswer = question.optString("rightAnswer");
//		item.mAnswer = question.optString("answer");
//		item.correctScore = question.optInt("correctScore");
//		item.mKnowledgeName = new ArrayList<String>();
//		JSONArray arrays = question.optJSONArray("knowledgeName");
//		if (arrays != null) {
//			for (int i = 0; i < arrays.length(); i++) {
//				item.mKnowledgeName.add(arrays.optString(i, ""));
//			}
//		}
//
//		item.mSectionName = question.optString("sectionName");
//		try {
//			item.mRightRate = (float) question.optDouble("rightRate");
//		} catch (Exception e) {
//		}
//		try {
//			item.mCorrectedNum = question.optInt("correctedNum");
//		} catch (Exception e) {
//		}
//		item.mDifficulty = question.optInt("difficulty");
//		item.mHot = question.optLong("hot");
//		item.mIsOut = question.optString("isOut").equals("1");
//		item.mIsCollect = question.optString("isCollect").equals("1");
//		if (question.has("groupInfo")) {
//			JSONObject group = question.optJSONObject("groupInfo");
//			JSONArray groups = question.optJSONArray("groupInfo");
//			if (group != null) {
//				item.mGroupID = group.optString("groupId");
//				item.mGroupName = group.optString("name");
//			} else if (groups != null && groups.length() > 0) {
//				group = groups.optJSONObject(0);
//				if (group != null) {
//					item.mGroupID = group.optString("groupId");
//					item.mGroupName = group.optString("name");
//				}
//			}
//
//		}
//		if (question.has("itemList")) {
//			JSONArray itemList = question.optJSONArray("itemList");
//			for (int j = 0; j < itemList.length(); j++) {
//				JSONObject object = itemList.optJSONObject(j);
//				if (object.has("code")) {
//					OptionsItemInfo info = new OptionsItemInfo();
//					info.setCode(object.optString("code"));
//					info.setValue(object.optString("value"));
//					item.itemList.add(info);
//				} else
//					break;
//
//			}
//
//		}
//		return item;
//	}
}
