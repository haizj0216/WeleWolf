package com.buang.welewolf.modules.base;

import android.view.KeyEvent;
import android.view.View;

import com.buang.welewolf.base.utils.PreferencesController;
import com.buang.welewolf.modules.utils.VirtualClassUtils;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.utils.UiThreadHandler;
import com.buang.welewolf.modules.utils.ConstantsUtils;
import com.buang.welewolf.modules.utils.UIFragmentHelper;

/**
 * Created by weilei on 16/6/12.
 */
public class BaseGuideFragment extends BaseUIFragment<UIFragmentHelper> {

    public static final int TYPE_HOMEWORK_MAIN = 1;
    public static final int TYPE_HOMEWORK_OVWEVIEW = 2;
    public static final int TYPE_HOMEWORK_DETAIL = 3;
    public static final int TYPE_HOMEWORK_CORRECT_COMMIT = 4;
    public static final int TYPE_HOMEWORK_CORRECT_COMPLETED = 5;

    private int mType;
    private View.OnClickListener mOnClickListener;

    public void setGuideClickListener(View.OnClickListener listener) {
        mOnClickListener = listener;
    }

    /**
     * 显示引导
     */
    public void showGuideView() {
        UiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                switch (mType) {
                    case TYPE_HOMEWORK_MAIN:
                        break;
                    case TYPE_HOMEWORK_OVWEVIEW:
                        VirtualClassUtils.getInstance(getActivity()).showOverViewChoiceDialog(getActivity(), mOnClickListener);
                        break;
                    case TYPE_HOMEWORK_DETAIL:
                        VirtualClassUtils.getInstance(getActivity()).showDetailDialog(getActivity(), mOnClickListener);
                        break;
                    case TYPE_HOMEWORK_CORRECT_COMMIT:
                        VirtualClassUtils.getInstance(getActivity()).showCorrectDialog(getActivity(), mOnClickListener);
                        break;
                    case TYPE_HOMEWORK_CORRECT_COMPLETED:
                        VirtualClassUtils.getInstance(getActivity()).showCorrectCompletion(getActivity(), mOnClickListener);
                        break;
                }
            }
        });
    }

    @Override
    public void onPanelOpened(View pPanel) {
        super.onPanelOpened(pPanel);
        if (isVirtualCorrect()) {
            showGuideView();
        }
    }

    /**
     * 获取数据
     *
     * @param type
     * @return
     */
    public String getJsonData(int type) {
        mType = type;
        String jsonData = "";
        switch (type) {
            case TYPE_HOMEWORK_MAIN:
                break;
            case TYPE_HOMEWORK_OVWEVIEW:
                jsonData = "{\"code\":99999,\"data\":{\"homeworkID\":\"795791\",\"addTime\":1467270000,\"classID\":\"130596\",\"endTime\":1483081200,\"studentCount\":\"20\",\"doCount\":5,\"rightRate\":\"0.80\",\"needCorrect\":1,\"questionList\":[{\"questionID\":\"233567\",\"questionType\":\"0\",\"questionNo\":\"0\",\"content\":\"<link href=\\\"http://7xohdn.com2.z0.glb.qiniucdn.com/resource/llkt2016051915.css\\\" rel=\\\"stylesheet\\\" /><span style=\\\"color:#64dcdc;margin-right:5px;margin-left: 0px; \\\">选择题</span><span class=\\\"colf43\\\"></span><span>如图，将</span><span>△ABC</span><span>绕点</span><span>P</span><span>顺时针旋转</span><span>90°</span><span>得到</span><span>△A′B′C′</span><span>，则点</span><span>P</span><span>的坐标是<span>（</span></span><span>&nbsp;&nbsp; </span><span>）</span><br><span><img width=\\\"159\\\" height=\\\"161\\\" src=\\\"http://7xohdn.com2.z0.glb.qiniucdn.com/upload/uploads/word_import/images/1003/14071433349MgndbwHoMeFyi94iAnn.gif\\\"></span><table width=\\\"100%\\\" cellspacing=\\\"0\\\" cellpadding=\\\"0\\\" name=\\\"optionsTable\\\"><tbody><tr><td>A．（1，1）</td></tr><tr><td>B．（1，2）</td></tr><tr><td>C．（1，3）</td></tr><tr><td>D．（1，4）</td></tr></tbody></table>\",\"rightRate\":\"1.00\",\"correctedCount\":3,\"contentSource\":\"\"},{\"questionID\":\"233567\",\"questionType\":\"0\",\"questionNo\":\"0\",\"content\":\"<link href=\\\"http://7xohdn.com2.z0.glb.qiniucdn.com/resource/llkt2016051915.css\\\" rel=\\\"stylesheet\\\" /><span style=\\\"color:#64dcdc;margin-right:5px;margin-left: 0px; \\\">选择题</span><span class=\\\"colf43\\\"></span><span>如图，将</span><span>△ABC</span><span>绕点</span><span>P</span><span>顺时针旋转</span><span>90°</span><span>得到</span><span>△A′B′C′</span><span>，则点</span><span>P</span><span>的坐标是<span>（</span></span><span>&nbsp;&nbsp; </span><span>）</span><br><span><img width=\\\"159\\\" height=\\\"161\\\" src=\\\"http://7xohdn.com2.z0.glb.qiniucdn.com/upload/uploads/word_import/images/1003/14071433349MgndbwHoMeFyi94iAnn.gif\\\"></span><table width=\\\"100%\\\" cellspacing=\\\"0\\\" cellpadding=\\\"0\\\" name=\\\"optionsTable\\\"><tbody><tr><td>A．（1，1）</td></tr><tr><td>B．（1，2）</td></tr><tr><td>C．（1，3）</td></tr><tr><td>D．（1，4）</td></tr></tbody></table>\",\"rightRate\":\"1.00\",\"correctedCount\":3,\"contentSource\":\"\"},{\"questionID\":\"233567\",\"questionType\":\"0\",\"questionNo\":\"0\",\"content\":\"<link href=\\\"http://7xohdn.com2.z0.glb.qiniucdn.com/resource/llkt2016051915.css\\\" rel=\\\"stylesheet\\\" /><span style=\\\"color:#64dcdc;margin-right:5px;margin-left: 0px; \\\">选择题</span><span class=\\\"colf43\\\"></span><span>如图，将</span><span>△ABC</span><span>绕点</span><span>P</span><span>顺时针旋转</span><span>90°</span><span>得到</span><span>△A′B′C′</span><span>，则点</span><span>P</span><span>的坐标是<span>（</span></span><span>&nbsp;&nbsp; </span><span>）</span><br><span><img width=\\\"159\\\" height=\\\"161\\\" src=\\\"http://7xohdn.com2.z0.glb.qiniucdn.com/upload/uploads/word_import/images/1003/14071433349MgndbwHoMeFyi94iAnn.gif\\\"></span><table width=\\\"100%\\\" cellspacing=\\\"0\\\" cellpadding=\\\"0\\\" name=\\\"optionsTable\\\"><tbody><tr><td>A．（1，1）</td></tr><tr><td>B．（1，2）</td></tr><tr><td>C．（1，3）</td></tr><tr><td>D．（1，4）</td></tr></tbody></table>\",\"rightRate\":\"0.80\",\"correctedCount\":3,\"contentSource\":\"\"},{\"questionID\":\"588869\",\"questionType\":\"0\",\"questionNo\":\"0\",\"content\":\"<link href=\\\"http://7xohdn.com2.z0.glb.qiniucdn.com/resource/llkt2016051915.css\\\" rel=\\\"stylesheet\\\" /><span style=\\\"color:#64dcdc;margin-right:5px;margin-left: 0px; \\\">选择题</span><span class=\\\"colf43\\\"></span><span>将一次函数<span>y=<span><img width=\\\"13\\\" height=\\\"36\\\" src=\\\"http://7xohdn.com2.z0.glb.qiniucdn.com/upload/uploads/word_import/images/444/140712011260L5UKBqzpML3FDGju5V.gif\\\"></span>x</span>的图象向上平移<span>2</span>个单位，平移后，若<span>y</span>＞<span>0</span>，则<span>x</span>的取值范围是</span><span>（<span>&nbsp;&nbsp; </span>）</span>\",\"rightRate\":\"0.60\",\"correctedCount\":3,\"contentSource\":\"\"},{\"questionID\":\"588869\",\"questionType\":\"0\",\"questionNo\":\"0\",\"content\":\"<link href=\\\"http://7xohdn.com2.z0.glb.qiniucdn.com/resource/llkt2016051915.css\\\" rel=\\\"stylesheet\\\" /><span style=\\\"color:#64dcdc;margin-right:5px;margin-left: 0px; \\\">选择题</span><span class=\\\"colf43\\\"></span><span>将一次函数<span>y=<span><img width=\\\"13\\\" height=\\\"36\\\" src=\\\"http://7xohdn.com2.z0.glb.qiniucdn.com/upload/uploads/word_import/images/444/140712011260L5UKBqzpML3FDGju5V.gif\\\"></span>x</span>的图象向上平移<span>2</span>个单位，平移后，若<span>y</span>＞<span>0</span>，则<span>x</span>的取值范围是</span><span>（<span>&nbsp;&nbsp; </span>）</span>\",\"rightRate\":\"0.60\",\"correctedCount\":3,\"contentSource\":\"\"},{\"questionID\":\"588513\",\"questionType\":\"2\",\"questionNo\":\"0\",\"content\":\"<link href=\\\"http://7xohdn.com2.z0.glb.qiniucdn.com/resource/llkt2016051915.css\\\" rel=\\\"stylesheet\\\" /><span style=\\\"color:#64dcdc;margin-right:5px;margin-left: 0px; \\\">解答题</span><span class=\\\"colf43\\\"></span><span>如图，在平面直角坐标系中，△<span>ABC</span>的三个顶点坐标为<span>A</span>（<span>1</span>，<span>-4</span>），<span>B</span>（<span>3</span>，<span>-3</span>），<span>C</span>（<span>1</span>，<span>-1</span>）．（每个小方格都是边长为一个单位长度的正方形）</span><br><span>（<span>1</span>）将△<span>ABC</span>沿<span>y</span>轴方向向上平移<span>5</span>个单位，画出平移后得到的△<span>A<sub>1</sub>B<sub>1</sub>C<sub>1</sub></span>；</span><br><span>（<span>2</span>）将△<span>ABC</span>绕点<span>O</span>顺时针旋转<span>90</span>°，画出旋转后得到的△<span>A<sub>2</sub>B<sub>2</sub>C<sub>2</sub></span>，并直接写出点<span>A</span>旋转到点<span>A<sub>2</sub></span>所经过的路径长．</span><br><span><img width=\\\"221\\\" height=\\\"226\\\" id=\\\"图片 270\\\" src=\\\"http://7xohdn.com2.z0.glb.qiniucdn.com/upload/uploads/word_import/images/196/1418118567NJ0RbXBL69rUEvLHUd3G.jpg\\\"></span><table name=\\\"optionsTable\\\" cellpadding=\\\"0\\\" cellspacing=\\\"0\\\" width=\\\"100%\\\"><tbody></tbody></table>\",\"rightRate\":\"0.00\",\"correctedCount\":0,\"contentSource\":\"\"},{\"questionID\":\"589602\",\"questionType\":\"2\",\"questionNo\":\"0\",\"content\":\"<link href=\\\"http://7xohdn.com2.z0.glb.qiniucdn.com/resource/llkt2016051915.css\\\" rel=\\\"stylesheet\\\" /><span style=\\\"color:#64dcdc;margin-right:5px;margin-left: 0px; \\\">解答题</span><span class=\\\"colf43\\\"></span><span>在平面直角坐标系中，已知点</span><span>A</span><span>（﹣</span><span>2</span><span>，</span><span>0</span><span>），点</span><span>B</span><span>（</span><span>0</span><span>，</span><span>4</span><span>），点</span><span>E</span><span>在</span><span>OB</span><span>上，且</span><span style=\\\"font-family:\\\"Calibri\\\",\\\"sans-serif\\\"\\\">∠</span><span>OAE=</span><span style=\\\"font-family:\\\"Calibri\\\",\\\"sans-serif\\\"\\\">∠</span><span>0BA</span><span>．</span><br> <span>（</span><span style=\\\"font-family:\\\"Calibri\\\",\\\"sans-serif\\\"\\\">Ⅰ</span><span>）如图</span><span style=\\\"font-family:\\\"Calibri\\\",\\\"sans-serif\\\"\\\">①</span><span>，求点</span><span>E</span><span>的坐标；</span><br> <span>（</span><span style=\\\"font-family:\\\"Calibri\\\",\\\"sans-serif\\\"\\\">Ⅱ</span><span>）如图</span><span style=\\\"font-family:\\\"Calibri\\\",\\\"sans-serif\\\"\\\">②</span><span>，将</span><span style=\\\"font-family:\\\"Calibri\\\",\\\"sans-serif\\\"\\\">△</span><span>AEO</span><span>沿</span><span>x</span><span>轴向右平移得到</span><span style=\\\"font-family:\\\"Calibri\\\",\\\"sans-serif\\\"\\\">△</span><span>A</span><span style=\\\"font-family:\\\"Calibri\\\",\\\"sans-serif\\\"\\\">′</span><span>E</span><span style=\\\"font-family:\\\"Calibri\\\",\\\"sans-serif\\\"\\\">′</span><span>O</span><span style=\\\"font-family:\\\"Calibri\\\",\\\"sans-serif\\\"\\\">′</span><span>，连接</span><span>A</span><span style=\\\"font-family:\\\"Calibri\\\",\\\"sans-serif\\\"\\\">′</span><span>B</span><span>、</span><span>BE</span><span style=\\\"font-family:\\\"Calibri\\\",\\\"sans-serif\\\"\\\">′</span><span>．</span><br> <span style=\\\"font-family:\\\"Calibri\\\",\\\"sans-serif\\\"\\\">①</span><span>设</span><span>AA</span><span style=\\\"font-family:\\\"Calibri\\\",\\\"sans-serif\\\"\\\">′</span><span>=m</span><span>，其中</span><span>0</span><span>＜</span><span>m</span><span>＜</span><span>2</span><span>，试用含</span><span>m</span><span>的式子表示</span><span>A</span><span style=\\\"font-family:\\\"Calibri\\\",\\\"sans-serif\\\"\\\">′</span><span>B</span><sup><span>2</span></sup><span>+BE</span><span style=\\\"font-family:\\\"Calibri\\\",\\\"sans-serif\\\"\\\">′</span><sup><span>2</span></sup><span>，并求出使</span><span>A</span><span style=\\\"font-family: \\\"Calibri\\\",\\\"sans-serif\\\"\\\">′</span><span>B</span><sup><span>2</span></sup><span>+BE</span><span style=\\\"font-family:\\\"Calibri\\\",\\\"sans-serif\\\"\\\">′</span><sup><span>2</span></sup><span>取得最小值时点</span><span>E</span><span style=\\\"font-family: \\\"Calibri\\\",\\\"sans-serif\\\"\\\">′</span><span>的坐标；</span><br> <span style=\\\"font-family:\\\"Calibri\\\",\\\"sans-serif\\\"\\\">②</span><span>当</span><span>A</span><span style=\\\"font-family:\\\"Calibri\\\",\\\"sans-serif\\\"\\\">′</span><span>B+BE</span><span style=\\\"font-family:\\\"Calibri\\\",\\\"sans-serif\\\"\\\">′</span><span>取得最小值时，求点</span><span>E</span><span style=\\\"font-family:\\\"Calibri\\\",\\\"sans-serif\\\"\\\">′</span><span>的坐标（直接写出结果即可）．</span><br><span><img width=\\\"324\\\" height=\\\"216\\\" src=\\\"http://7xohdn.com2.z0.glb.qiniucdn.com/upload/uploads/word_import/images/869/1418785484U3t2Ji3R0Y1Koh2moTHK.png\\\"></span><span>&nbsp;</span><table name=\\\"optionsTable\\\" cellpadding=\\\"0\\\" cellspacing=\\\"0\\\" width=\\\"100%\\\"><tbody></tbody></table>\",\"rightRate\":\"0.00\",\"correctedCount\":0,\"contentSource\":\"\"},{\"questionID\":\"590066\",\"questionType\":\"2\",\"questionNo\":\"0\",\"content\":\"<link href=\\\"http://7xohdn.com2.z0.glb.qiniucdn.com/resource/llkt2016051915.css\\\" rel=\\\"stylesheet\\\" /><span style=\\\"color:#64dcdc;margin-right:5px;margin-left: 0px; \\\">解答题</span><span class=\\\"colf43\\\"></span><span>如图，在正方形网络中，</span><span>△</span><span>ABC</span><span>的三个顶点都在格点上，点<span>A</span>、<span>B</span>、<span>C</span>的坐标分别为（<span>-2</span>，<span>4</span>）、（<span>-2</span>，<span>0</span>）、（<span>-4</span>，<span>1</span>），结合所给的平面直角坐标系解答下列问题：</span><br><span>（<span>1</span>）画出</span><span>△</span><span>ABC</span><span>关于原点<span>O</span>对称的</span><span>△</span><span>A<sub>1</sub>B<sub>1</sub>C<sub>1</sub>.</span><br><span>（<span>2</span>）平移</span><span>△</span><span>ABC</span><span>，使点<span>A</span>移动到点<span>A<sub>2</sub></span>（<span>0</span>，<span>2</span>），画出平移后的</span><span>△</span><span>A<sub>2</sub>B<sub>2</sub>C<sub>2</sub></span><span>并写出点<span>B<sub>2</sub></span>、<span>C<sub>2</sub></span>的坐标<span>.</span></span><br><span>（<span>3</span>）在</span><span>△</span><span>ABC</span><span>、</span><span>△</span><span>A<sub>1</sub>B<sub>1</sub>C<sub>1</sub></span><span>、</span><span>△</span><span>A<sub>2</sub>B<sub>2</sub>C<sub>2</sub></span><span>中，</span><span>△</span><span>A<sub>2</sub>B<sub>2</sub>C<sub>2</sub></span><span>与<span>_________</span>成中心对称，其对称中心的坐标为<span>_________.</span></span><br><span><img width=\\\"255\\\" height=\\\"215\\\" id=\\\"图片 107\\\" src=\\\"http://7xohdn.com2.z0.glb.qiniucdn.com/upload/uploads/word_import/images/942/1420009813AcyKg3E9TYq5OfNPoLCX.jpg\\\"></span><br><table name=\\\"optionsTable\\\" cellpadding=\\\"0\\\" cellspacing=\\\"0\\\" width=\\\"100%\\\"><tbody></tbody></table>\",\"rightRate\":\"0.00\",\"correctedCount\":0,\"contentSource\":\"\"}],\"homeworkIcon\":\"\",\"homeworkIconDesc\":\"\",\"homeworkProDesc\":\"\",\"homeworkProUrl\":\"\"}}";
                break;
            case TYPE_HOMEWORK_DETAIL:
                jsonData = "{\"code\":\"99999\",\"data\":{\"questionID\":\"588513\",\"questionNo\":\"0\",\"questionType\":\"2\",\"content\":\"<link href=\\\"http://7xohdn.com2.z0.glb.qiniucdn.com/resource/llkt2016051915.css\\\" rel=\\\"stylesheet\\\" /><span style=\\\"color:#64dcdc;margin-right:5px;margin-left: 0px; \\\">解答题</span><span class=\\\"colf43\\\"></span><span>如图，在平面直角坐标系中，△<span>ABC</span>的三个顶点坐标为<span>A</span>（<span>1</span>，<span>-4</span>），<span>B</span>（<span>3</span>，<span>-3</span>），<span>C</span>（<span>1</span>，<span>-1</span>）．（每个小方格都是边长为一个单位长度的正方形）</span><br><span>（<span>1</span>）将△<span>ABC</span>沿<span>y</span>轴方向向上平移<span>5</span>个单位，画出平移后得到的△<span>A<sub>1</sub>B<sub>1</sub>C<sub>1</sub></span>；</span><br><span>（<span>2</span>）将△<span>ABC</span>绕点<span>O</span>顺时针旋转<span>90</span>°，画出旋转后得到的△<span>A<sub>2</sub>B<sub>2</sub>C<sub>2</sub></span>，并直接写出点<span>A</span>旋转到点<span>A<sub>2</sub></span>所经过的路径长．</span><br><span><img width=\\\"221\\\" height=\\\"226\\\" id=\\\"图片 270\\\" src=\\\"http://7xohdn.com2.z0.glb.qiniucdn.com/upload/uploads/word_import/images/196/1418118567NJ0RbXBL69rUEvLHUd3G.jpg\\\"></span><table name=\\\"optionsTable\\\" cellpadding=\\\"0\\\" cellspacing=\\\"0\\\" width=\\\"100%\\\"><tbody></tbody></table>\",\"rightAnswer\":\"<link href=\\\"http://7xohdn.com2.z0.glb.qiniucdn.com/resource/llkt2016051915.css\\\" rel=\\\"stylesheet\\\" />见解析\",\"answerExplain\":\"<link href=\\\"http://7xohdn.com2.z0.glb.qiniucdn.com/resource/llkt2016051915.css\\\" rel=\\\"stylesheet\\\" /><br>解：（1）如图，△A<sub>1</sub>B<sub>1</sub>C<sub>1</sub>即为所求；<br>（2）如图，△A<sub>2</sub>B<sub>2</sub>C<sub>2</sub>即为所求；<br>由勾股定理得，OA=<img width=\\\"57\\\" height=\\\"24\\\" id=\\\"图片 273\\\" src=\\\"http://7xohdn.com2.z0.glb.qiniucdn.com/upload/uploads/word_import/images/40/1418118567cgURWWgn81KV7BNv8QCz.gif\\\" alt=\\\"菁优网-jyeoo\\\">=<img width=\\\"28\\\" height=\\\"18\\\" id=\\\"图片 274\\\" src=\\\"http://7xohdn.com2.z0.glb.qiniucdn.com/upload/uploads/word_import/images/40/14181185678awNlQWFXTcuZCOEXGXK.gif\\\" alt=\\\"菁优网-jyeoo\\\">，<br>点A旋转到点A<sub>2</sub>所经过的路径长为：<img width=\\\"78\\\" height=\\\"37\\\" id=\\\"图片 283\\\" src=\\\"http://7xohdn.com2.z0.glb.qiniucdn.com/upload/uploads/word_import/images/40/1418118567WNTLeHMfEE325HoYwOuw.gif\\\" alt=\\\"菁优网-jyeoo\\\">=<img width=\\\"46\\\" height=\\\"37\\\" id=\\\"图片 284\\\" src=\\\"http://7xohdn.com2.z0.glb.qiniucdn.com/upload/uploads/word_import/images/40/14181185671ts3QJEJQm3KjgaylHNv.gif\\\" alt=\\\"菁优网-jyeoo\\\">．<br><img width=\\\"222\\\" height=\\\"228\\\" id=\\\"图片 293\\\" src=\\\"http://7xohdn.com2.z0.glb.qiniucdn.com/upload/uploads/word_import/images/40/1418118567eiHE4Kepw8rRIPSkpJXs.jpg\\\"><br>\",\"knowledgeName\":[],\"sectionName\":\"\",\"questionAudio\":[],\"contentSource\":\"\",\"studentList\":[{\"userID\":\"69322\",\"studentID\":\"57816\",\"studentName\":\"学生1\",\"headPhoto\":\"http://7xjnvd.com2.z0.glb.qiniucdn.com/20150925/69322_1443162857220_88.png\",\"sex\":\"1\",\"answerAudio\":[],\"correctRate\":\"0\",\"correctScore\":\"-1\",\"isPraise\":\"0\",\"isSuggest\":\"0\",\"answerID\":\"39438028\",\"answers\":[\"http://7xjnvd.com2.z0.glb.qiniucdn.com/40675_20150923114325_8623\"],\"correctAnswers\":[],\"height\":\"\",\"width\":\"\"},{\"userID\":\"69323\",\"studentID\":\"57817\",\"studentName\":\"学生2\",\"headPhoto\":\"http://7xjnvd.com2.z0.glb.qiniucdn.com/20150925/69323_1443163062982_22.png\",\"sex\":\"2\",\"answerAudio\":[],\"correctRate\":\"0\",\"correctScore\":\"-1\",\"isPraise\":\"0\",\"isSuggest\":\"0\",\"answerID\":\"39438033\",\"answers\":[\"http://7xjnvd.com2.z0.glb.qiniucdn.com/40675_20150923114325_7583\"],\"correctAnswers\":[],\"height\":\"600\",\"width\":\"800\"},{\"userID\":\"69324\",\"studentID\":\"57818\",\"studentName\":\"学生3\",\"headPhoto\":\"http://7xjnvd.com2.z0.glb.qiniucdn.com/20150925/69324_1443162637621_18.png\",\"sex\":\"2\",\"answerAudio\":[],\"correctRate\":\"0\",\"correctScore\":\"-1\",\"isPraise\":\"0\",\"isSuggest\":\"0\",\"answerID\":\"39438038\",\"answers\":[\"http://7xjnvd.com2.z0.glb.qiniucdn.com/40675_20150923114325_7791\"],\"correctAnswers\":[],\"height\":\"600\",\"width\":\"450\"},{\"userID\":\"69325\",\"studentID\":\"57819\",\"studentName\":\"学生4\",\"headPhoto\":\"http://7xjnvd.com2.z0.glb.qiniucdn.com/20150925/69325_1443163118509_84.png\",\"sex\":\"2\",\"correctScore\":\"-2\"},{\"userID\":\"69326\",\"studentID\":\"57820\",\"studentName\":\"学生5\",\"headPhoto\":\"http://7xjnvd.com2.z0.glb.qiniucdn.com/20150925/69326_1443163213695_27.png\",\"sex\":\"2\",\"correctScore\":\"-2\"}]}}";
                break;
            case TYPE_HOMEWORK_CORRECT_COMMIT:
                jsonData = "{\"code\":\"99999\",\"data\":{\"questionID\":\"588513\",\"questionNo\":\"0\",\"questionType\":\"2\",\"content\":\"<link href=\\\"http://7xohdn.com2.z0.glb.qiniucdn.com/resource/llkt2016051915.css\\\" rel=\\\"stylesheet\\\" /><span style=\\\"color:#64dcdc;margin-right:5px;margin-left: 0px; \\\">解答题</span><span class=\\\"colf43\\\"></span><span>如图，在平面直角坐标系中，△<span>ABC</span>的三个顶点坐标为<span>A</span>（<span>1</span>，<span>-4</span>），<span>B</span>（<span>3</span>，<span>-3</span>），<span>C</span>（<span>1</span>，<span>-1</span>）．（每个小方格都是边长为一个单位长度的正方形）</span><br><span>（<span>1</span>）将△<span>ABC</span>沿<span>y</span>轴方向向上平移<span>5</span>个单位，画出平移后得到的△<span>A<sub>1</sub>B<sub>1</sub>C<sub>1</sub></span>；</span><br><span>（<span>2</span>）将△<span>ABC</span>绕点<span>O</span>顺时针旋转<span>90</span>°，画出旋转后得到的△<span>A<sub>2</sub>B<sub>2</sub>C<sub>2</sub></span>，并直接写出点<span>A</span>旋转到点<span>A<sub>2</sub></span>所经过的路径长．</span><br><span><img width=\\\"221\\\" height=\\\"226\\\" id=\\\"图片 270\\\" src=\\\"http://7xohdn.com2.z0.glb.qiniucdn.com/upload/uploads/word_import/images/196/1418118567NJ0RbXBL69rUEvLHUd3G.jpg\\\"></span><table name=\\\"optionsTable\\\" cellpadding=\\\"0\\\" cellspacing=\\\"0\\\" width=\\\"100%\\\"><tbody></tbody></table>\",\"rightAnswer\":\"<link href=\\\"http://7xohdn.com2.z0.glb.qiniucdn.com/resource/llkt2016051915.css\\\" rel=\\\"stylesheet\\\" />见解析\",\"answerExplain\":\"<link href=\\\"http://7xohdn.com2.z0.glb.qiniucdn.com/resource/llkt2016051915.css\\\" rel=\\\"stylesheet\\\" /><br>解：（1）如图，△A<sub>1</sub>B<sub>1</sub>C<sub>1</sub>即为所求；<br>（2）如图，△A<sub>2</sub>B<sub>2</sub>C<sub>2</sub>即为所求；<br>由勾股定理得，OA=<img width=\\\"57\\\" height=\\\"24\\\" id=\\\"图片 273\\\" src=\\\"http://7xohdn.com2.z0.glb.qiniucdn.com/upload/uploads/word_import/images/40/1418118567cgURWWgn81KV7BNv8QCz.gif\\\" alt=\\\"菁优网-jyeoo\\\">=<img width=\\\"28\\\" height=\\\"18\\\" id=\\\"图片 274\\\" src=\\\"http://7xohdn.com2.z0.glb.qiniucdn.com/upload/uploads/word_import/images/40/14181185678awNlQWFXTcuZCOEXGXK.gif\\\" alt=\\\"菁优网-jyeoo\\\">，<br>点A旋转到点A<sub>2</sub>所经过的路径长为：<img width=\\\"78\\\" height=\\\"37\\\" id=\\\"图片 283\\\" src=\\\"http://7xohdn.com2.z0.glb.qiniucdn.com/upload/uploads/word_import/images/40/1418118567WNTLeHMfEE325HoYwOuw.gif\\\" alt=\\\"菁优网-jyeoo\\\">=<img width=\\\"46\\\" height=\\\"37\\\" id=\\\"图片 284\\\" src=\\\"http://7xohdn.com2.z0.glb.qiniucdn.com/upload/uploads/word_import/images/40/14181185671ts3QJEJQm3KjgaylHNv.gif\\\" alt=\\\"菁优网-jyeoo\\\">．<br><img width=\\\"222\\\" height=\\\"228\\\" id=\\\"图片 293\\\" src=\\\"http://7xohdn.com2.z0.glb.qiniucdn.com/upload/uploads/word_import/images/40/1418118567eiHE4Kepw8rRIPSkpJXs.jpg\\\"><br>\",\"knowledgeName\":[],\"sectionName\":\"\",\"questionAudio\":[],\"contentSource\":\"\",\"studentList\":[{\"userID\":\"69322\",\"studentID\":\"57816\",\"studentName\":\"学生1\",\"headPhoto\":\"http://7xjnvd.com2.z0.glb.qiniucdn.com/20150925/69322_1443162857220_88.png\",\"sex\":\"1\",\"answerAudio\":[],\"correctRate\":\"0\",\"correctScore\":\"-1\",\"isPraise\":\"0\",\"isSuggest\":\"0\",\"answerID\":\"36333064\",\"answers\":[\"http://7xjnvd.com2.z0.glb.qiniucdn.com/40675_20150923114325_8623\"],\"correctAnswers\":[],\"height\":\"\",\"width\":\"\"},{\"userID\":\"69323\",\"studentID\":\"57817\",\"studentName\":\"学生2\",\"headPhoto\":\"http://7xjnvd.com2.z0.glb.qiniucdn.com/20150925/69323_1443163062982_22.png\",\"sex\":\"2\",\"answerAudio\":[],\"correctRate\":\"0\",\"correctScore\":\"-1\",\"isPraise\":\"0\",\"isSuggest\":\"0\",\"answerID\":\"36333069\",\"answers\":[\"http://7xjnvd.com2.z0.glb.qiniucdn.com/40675_20150923114325_7583\"],\"correctAnswers\":[],\"height\":\"600\",\"width\":\"800\"},{\"userID\":\"69324\",\"studentID\":\"57818\",\"studentName\":\"学生3\",\"headPhoto\":\"http://7xjnvd.com2.z0.glb.qiniucdn.com/20150925/69324_1443162637621_18.png\",\"sex\":\"2\",\"answerAudio\":[],\"correctRate\":\"0\",\"correctScore\":\"-1\",\"isPraise\":\"0\",\"isSuggest\":\"0\",\"answerID\":\"36333074\",\"answers\":[\"http://7xjnvd.com2.z0.glb.qiniucdn.com/40675_20150923114325_7791\"],\"correctAnswers\":[],\"height\":\"600\",\"width\":\"450\"},{\"userID\":\"69325\",\"studentID\":\"57819\",\"studentName\":\"学生4\",\"headPhoto\":\"http://7xjnvd.com2.z0.glb.qiniucdn.com/20150925/69325_1443163118509_84.png\",\"sex\":\"2\",\"correctScore\":\"-2\"},{\"userID\":\"69326\",\"studentID\":\"57820\",\"studentName\":\"学生5\",\"headPhoto\":\"http://7xjnvd.com2.z0.glb.qiniucdn.com/20150925/69326_1443163213695_27.png\",\"sex\":\"2\",\"correctScore\":\"-2\"}]}}";
                break;
            case TYPE_HOMEWORK_CORRECT_COMPLETED:
                break;
        }
        return jsonData;
    }

    public void setVirtualView() {

    }

    public boolean isVirtualCorrect() {
        return PreferencesController.getBoolean(ConstantsUtils.PREFS_VIRTUAL_HOMEWORK, false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && isVirtualCorrect()) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}