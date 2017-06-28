package linchange.example.com.answerlibrary;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class ReviewActivity extends AppCompatActivity {
    private static final String TAG = "ReviewActivity";
    //问题文字
    private TextView questionText;
    //选项组
    private RadioGroup answerGroup;
    //当前选择的选项
    private RadioButton answerChoose;
    //答案选项1
    private RadioButton answer1;
    //答案选项2
    private RadioButton answer2;
    //答案选项3
    private RadioButton answer3;
    //答案选项4
    private RadioButton answer4;
    //正确答案
    private TextView rightAnswer;
    //切换到上一个问题按键
    private Button lastButton;
    //切换到下一个问题按键
    private Button nextButton;
    //用于储存问题和选项文字
    private String answerStrings[] = new String[6];
    //页面切换控件
    private ViewPager viewPager;
    //用来存储新建的临时对象
    private Question question;
    //问题列表
    ArrayList<Question> questionList;
    //页面列表
    List<View> viewList = new ArrayList<View>();
    //页表布局加载器
    LayoutInflater inflater;
    //页面适配器
    private MyViewPagerAdapter adapter;
    //特效器
    private DepthPageTransformer transformer;
    //储存错题的列表
    private ArrayList<Question> errorQuestionList = new ArrayList<>();
    //nextButton到最后一页点击次数
    private int nextButtonClickCount = 0;
    private Question question1;
    //统计已加载的Question的数目
    private int haveReadyLoadViewCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);


        boolean isQuestionChange = getIntent().getBooleanExtra("isQuestionChange", false);
        LogUtil.e(TAG, "isErrorQuestionChange=" + isQuestionChange);

        if (isQuestionChange) {
            questionList = getIntent().getParcelableArrayListExtra("errorQuestionList");
        }

        System.out.println("questionList == null" + questionList == null);
        if (questionList == null || questionList.size() == 0) {
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            linearLayout.setBackgroundColor(Color.rgb(254, 223, 225));
            TextView tvShowNothing = new TextView(this);
            linearLayout.addView(tvShowNothing);
            tvShowNothing.setText("暂无错题");
            tvShowNothing.setTextSize(20);
            tvShowNothing.setGravity(Gravity.CENTER);
            tvShowNothing.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            setContentView(linearLayout);
        }  else {
            LogUtil.e(TAG, "get questionListSize=" + questionList.size());
            for (int i = 0; i < questionList.size(); i++) {
                question1 = questionList.get(i);
                System.out.println("haha" + question1);
            }

            inflater = getLayoutInflater();

            initViews();
            initDatas();
            initEvents();
        }

    }


    /**
     * 加载数据
     */
    private void initDatas() {
        createFiveView();
    }

    private void createFiveView() {
        int index = haveReadyLoadViewCount;
        for (int i = 0; i < 5; i++) {
            if (i + index < questionList.size()) {
                LogUtil.e(TAG, "questionList.get(" + (i + index) + ")");
                question = questionList.get(i + index);
            } else {
                break;
            }


            if (question != null) {
                final View view = createPageView(question);

                answerGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    private int radioButtonId;
                    String chooseText;
                    String answerText;
//                        int rightAnswerLetter;

                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        int current = viewPager.getCurrentItem();
                        LogUtil.v(TAG, "current=" + current);
                        answerText = questionList.get(current).getRightAnswer().trim();
                        LogUtil.v(TAG, "answerText=" + answerText);
                        int rightAnswerLetter = answerText.charAt(answerText.length() - 1) - 65;
                        LogUtil.v(TAG, "rightAnswerLetter=" + (char) rightAnswerLetter);
                        //获取当前选项的选中的选项ID
                        radioButtonId = radioGroup.getCheckedRadioButtonId();
                        LogUtil.v(TAG, "radioButtonId=" + radioButtonId);
                        //通过ID找到RadioButton
                        answerChoose = (RadioButton) view.findViewById(radioButtonId);
                        chooseText = (String) answerChoose.getText();
                        LogUtil.v(TAG, "chooseText=" + chooseText);
                        //所选择选项的开头字母
                        int chooseLetter = chooseText.charAt(0) - 65;
                        LogUtil.v(TAG, "chooseLetter=" + chooseLetter);

                        setRadioButtonTextColor(radioGroup, answerChoose, rightAnswerLetter, chooseLetter);

                        View currentView = viewList.get(current);
                        rightAnswer = (TextView) currentView.findViewById(R.id.rightAnswer);
                        rightAnswer.setVisibility(View.VISIBLE);

                    }
                });
                viewList.add(view);
                haveReadyLoadViewCount++;
            } else {
                break;
            }
        }
        nextButton.setEnabled(true);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        },1000);


    }

    //给Page的每一页创造页面
    private View createPageView(Question question) {
        View view = inflater.inflate(R.layout.item, null);

        questionText = (TextView) view.findViewById(R.id.question);
        answer1 = (RadioButton) view.findViewById(R.id.answer1);
        answer2 = (RadioButton) view.findViewById(R.id.answer2);
        answer3 = (RadioButton) view.findViewById(R.id.answer3);
        answer4 = (RadioButton) view.findViewById(R.id.answer4);
        rightAnswer = (TextView) view.findViewById(R.id.rightAnswer);
        answerGroup = (RadioGroup) view.findViewById(R.id.answerGroup);

//        LogUtil.e(TAG, "question.getQuestion()=" + question.getQuestion());
        questionText.setText(question.getQuestion());
        answer1.setText(question.getAnswerText1());
        answer2.setText(question.getAnswerText2());

        if ("".equals(question.getAnswerText3()) && "".equals(question.getAnswerText4())) {
            answer3.setVisibility(View.INVISIBLE);
            answer3.setEnabled(false);
            answer4.setVisibility(View.INVISIBLE);
            answer4.setEnabled(false);
        } else if ("".equals(question.getAnswerText4())) {
            answer3.setText(question.getAnswerText3());
            answer4.setVisibility(View.INVISIBLE);
            answer4.setEnabled(false);
        } else {
            answer3.setText(question.getAnswerText3());
            answer4.setText(question.getAnswerText4());
        }

        rightAnswer.setText(question.getRightAnswer());

        return view;
    }

    /**
     * 整组设置RadioButton的文字颜色
     * @param radioGroup RadioButton组
     * @param answerChoose 选中的RadioButton
     * @param rightAnswerLetter 正确答案的字母
     * @param chooseLetter 已选中答案的字母
     */
    private void setRadioButtonTextColor(RadioGroup radioGroup, RadioButton answerChoose, int rightAnswerLetter, int chooseLetter) {
        RadioButton radioButton;
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
//            LogUtil.v(TAG, "radioGroup.getChildCount()=" + radioGroup.getChildCount());getChildCount()=4
            radioButton = (RadioButton) radioGroup.getChildAt(i);
            radioButton.setEnabled(false);

            if (radioButton == answerChoose && rightAnswerLetter == chooseLetter) {
                //如果当前的radioButton与已选中的一致，并且字母与正确答案一致，按钮文字设为绿色
                radioButton.setTextColor(Color.rgb(140, 193, 82));
            } else if (radioButton == answerChoose && rightAnswerLetter != chooseLetter) {
                //如果当前的radioButton与已选中的一致，并且字母不正确答案一致，按钮文字设为红色
                radioButton.setTextColor(Color.rgb(218, 68, 83));
                //获取当前的页数
                int current = viewPager.getCurrentItem();
                //将选错的题目添加到错题列表
                errorQuestionList.add(questionList.get(current));
            } else {
                //如果当前的radioButton与已选中的不一致，按钮文字设为绿色
                radioButton.setTextColor(Color.BLACK);
            }
        }

    }


    /**
     * 初始化事件
     */
    private void initEvents() {

        transformer = new DepthPageTransformer();

        lastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
                int current = viewPager.getCurrentItem();
                if (current == 0) {
                    Toast.makeText(ReviewActivity.this, "已至最前页", Toast.LENGTH_SHORT).show();
                }

            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //获取当前页数
                int current = viewPager.getCurrentItem();

                LogUtil.e(TAG, "setOnClickListener current=" + current);
                LogUtil.e(TAG, "setOnClickListener haveReadyLoadViewCount=" + haveReadyLoadViewCount);


//                if (current == haveReadyLoadViewCount - 4) {
//                    nextButton.setEnabled(false);
//                    LogUtil.e(TAG, "setOnClickListener nextButton.setEnabled(false);");
//                }

                if (current != viewList.size() - 1) {
                    LogUtil.e(TAG, "setOnClickListener viewPager.setCurrentItem(current + 1, true);");
                    viewPager.setCurrentItem(current + 1, true);
                }


                nextButtonClickEvent();

            }
        });

        adapter = new MyViewPagerAdapter(viewList);

        viewPager.setAdapter(adapter);
        viewPager.setPageTransformer(true, transformer);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //获取当前页数
                int current = viewPager.getCurrentItem();
                LogUtil.e(TAG, "onPageScrollStateChanged current=" + current);
                LogUtil.e(TAG, "onPageScrollStateChanged haveReadyLoadViewCount=" + haveReadyLoadViewCount);

                if (current == haveReadyLoadViewCount - 3) {
                    LogUtil.e(TAG, "onPageScrollStateChanged haveReadyLoadViewCount1=" + haveReadyLoadViewCount);

                    nextButton.setEnabled(false);
                    createFiveView();
                    adapter.notifyDataSetChanged();
                    LogUtil.e(TAG, "onPageScrollStateChanged haveReadyLoadViewCount2=" + haveReadyLoadViewCount);

                    LogUtil.e(TAG, "createFiveView();");
                    LogUtil.e(TAG, "haveReadyLoadViewCount=" + haveReadyLoadViewCount);
                    LogUtil.e(TAG, "adapter.getCount()=" + adapter.getCount());
                }
                if (current != viewList.size() - 1) {
                    nextButton.setText("下一题");
                    nextButtonClickCount = 0;
                    LogUtil.e(TAG, "current=" + current);


                    System.out.println("nextButtonClickCount" + nextButtonClickCount);
                } else {
                    nextButton.setText("完成");
                    System.out.println("nextButtonClickCount" + nextButtonClickCount);
                }
                LogUtil.e(TAG, "current=" + current);
                LogUtil.e(TAG, "viewList.size()=" + viewList.size());
//                showQuestionNow();
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    /**
     * 用以展示现在已加载问题的内容
     */
    private void showQuestionNow() {
        int count = questionList.size();
        for (int i = 0; i < count; i++) {
            Question question = questionList.get(i);
            System.out.println(question);
        }
    }

    /**
     * 对于nextButton这个控件的跳转事件
     */
    private void nextButtonClickEvent() {
        int current = viewPager.getCurrentItem();
        boolean flag = false;
        if (current == viewList.size() - 1) {
            nextButtonClickCount++;

            if (nextButtonClickCount == 1) {
                Toast.makeText(ReviewActivity.this, "再次点击返回", Toast.LENGTH_SHORT).show();
            }

            flag = true;
        } else {
            nextButtonClickCount = 0;
        }

        if (flag && nextButtonClickCount == 2) {
            nextButtonClickCount = 0;
            onBackPressed();
        }

    }

    /**
     * 初始化视图
     */
    private void initViews() {
        lastButton = (Button) findViewById(R.id.lastButtonReview);
        nextButton = (Button) findViewById(R.id.nextButtonReview);
        viewPager = (ViewPager) findViewById(R.id.viewPagerReview);
    }


    /**
     * 给MainActivity返回必要的参数
     */
    private void createIntentAndSetResult() {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra("errorQuestion", errorQuestionList);
        setResult(300, intent);
    }

    @Override
    public void onBackPressed() {
        createIntentAndSetResult();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        createIntentAndSetResult();
        super.onDestroy();
    }
}
