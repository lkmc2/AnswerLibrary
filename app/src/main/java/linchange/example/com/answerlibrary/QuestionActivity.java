package linchange.example.com.answerlibrary;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class QuestionActivity extends AppCompatActivity {
    //问题在文件中的长度
    private static final int QUESTION_LINE = 6;
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
    List<Question> questionList = new ArrayList<Question>();
    //页面列表
    List<View> viewList = new ArrayList<View>();
    //页表布局加载器
    LayoutInflater inflater;
    //页面适配器
    private MyViewPagerAdapter adapter;
    //特效器
    private DepthPageTransformer transformer;
    //MainActivity的标记
    private static final String TAG = "QuestionActivity";
    //储存错题的列表
    private ArrayList<Question> errorQuestionList = new ArrayList<Question>();
    //nextButton到最后一页点击次数
    private int nextButtonClickCount = 0;
    //文件中的Question的总数
    private int totalQuestionCount = 0;
    //答对题目总数
    private int totalRightCount = 0;
    //答对题目总数
    private int totalWrongCount = 0;
    //统计是否完成答题
    private boolean isEndQuestion = false;
    //统计已加载的Question的数目
    private int haveReadyLoadQuestionCount = 0;
    private int createQuestionCountNow = 0;

    private InputStream in = null;
    private InputStreamReader isr = null;
    private BufferedReader br = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        inflater = getLayoutInflater();

        initViews();
        initDatas();
        initEvents();
    }

    /**
     * 根据文件中的行数获取总问题数
     * @return 文件中的总问题数
     */
    private int getTotalQuestionCount() {
        int count = 0;

        try {
            in = getResources().getAssets().open("file.txt");
            isr = new InputStreamReader(in, "utf-8");
            br = new BufferedReader(isr);

            String str;

            while ((str = br.readLine()) != null) {
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (isr != null) {
                    isr.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        LogUtil.e(TAG, "totalLineCount=" + count);
        return count / 7;
    }


    /**
     * 加载数据
     */
    private void initDatas() {
        totalQuestionCount = getTotalQuestionCount();
        LogUtil.e(TAG, "totalQuestionCount" + totalQuestionCount);

        try {
            in = getResources().getAssets().open("file.txt");
            isr = new InputStreamReader(in, "utf-8");
            br = new BufferedReader(isr);

            createFiveView();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createFiveView() {
        for (int i = 0; i < 5; i++) {
            question = createAQuestion();

            if (question != null) {
                final View view = createPageView(question);

                answerGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    private int radioButtonId;
                    String chooseText;
                    String answerText;
//                        int rightAnswerLetter;

                    //wrong id:2131427431 D
                    //right id:2131427431 D
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        int current = viewPager.getCurrentItem();
                        LogUtil.v(TAG, "onCheckedChanged current=" + current);
//                        View currentView = viewList.get(current);
                        View currentView = viewList.get(current);

                        answerText = questionList.get(current).getRightAnswer().trim();
                        LogUtil.v(TAG, "answerText=" + answerText);
                        int rightAnswerLetter = answerText.charAt(answerText.length() - 1) - 65;
                        LogUtil.v(TAG, "rightAnswerLetter=" + (char) rightAnswerLetter);
                        //获取当前选项的选中的选项ID
                        radioButtonId = radioGroup.getCheckedRadioButtonId();
                        LogUtil.v(TAG, "radioButtonId=" + radioButtonId);
                        //通过ID找到RadioButton
                        answerChoose = (RadioButton) currentView.findViewById(radioButtonId);
                        chooseText = (String) answerChoose.getText();
                        LogUtil.v(TAG, "chooseText=" + chooseText);
                        //所选择选项的开头字母
                        int chooseLetter = chooseText.charAt(0) - 65;
                        LogUtil.v(TAG, "chooseLetter=" + chooseLetter);

                        setRadioButtonTextColor(radioGroup, answerChoose, rightAnswerLetter, chooseLetter);


                        rightAnswer = (TextView) currentView.findViewById(R.id.rightAnswer);
                        rightAnswer.setVisibility(View.VISIBLE);
                    }
                });
                viewList.add(view);
            } else {
                break;
            }
        }
        nextButton.setEnabled(true);

    }


    /**
     * 用输入流创建一个Question
     * @return 一个新建的Question
     */
    public Question createAQuestion() {
        if (haveReadyLoadQuestionCount == totalQuestionCount) {
            return null;
        }

        try {
            int times = 0;
            String str;

            for (int i = 0; i <= QUESTION_LINE; i++) {
                if ((str = br.readLine()) != null) {
                    times++;
                }
                if (i == QUESTION_LINE) {
                    break;
                }
                answerStrings[i] = str;
            }

            if (times == QUESTION_LINE + 1) {
                question = new Question(answerStrings[0], answerStrings[1], answerStrings[2], answerStrings[3], answerStrings[4], answerStrings[5]);
                questionList.add(question);
                haveReadyLoadQuestionCount++;
                return question;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     *给Page的每一页创造页面
     * @param question 传入对Question对象
     * @return 新建好的页面
     */
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
                totalRightCount++;
                if (totalRightCount + totalWrongCount == totalQuestionCount) {
                    isEndQuestion = true;
                }
            } else if (radioButton == answerChoose && rightAnswerLetter != chooseLetter) {
                //如果当前的radioButton与已选中的一致，并且字母不正确答案一致，按钮文字设为红色
                radioButton.setTextColor(Color.rgb(218, 68, 83));
                //获取当前的页数
                int current = viewPager.getCurrentItem();
                //将选错的题目添加到错题列表
                errorQuestionList.add(questionList.get(current));
                totalWrongCount++;
                if (totalRightCount + totalWrongCount == totalQuestionCount) {
                    isEndQuestion = true;
                }
            } else {
                //如果当前的radioButton与已选中的不一致，按钮文字设为绿色
                radioButton.setTextColor(Color.BLACK);
            }
        }

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
                    Toast.makeText(QuestionActivity.this, "已至最前页", Toast.LENGTH_SHORT).show();
                }

            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取当前页数
                int current = viewPager.getCurrentItem();
                if (current != viewList.size() - 1) {
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

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //获取当前页数
                int current = viewPager.getCurrentItem();
                if (current == haveReadyLoadQuestionCount - 3) {
                    createFiveView();
                    adapter.notifyDataSetChanged();

                    LogUtil.e(TAG, "createFiveView();");
                    LogUtil.e(TAG, "haveReadyLoadQuestionCount=" + haveReadyLoadQuestionCount);
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
        });

    }

    /**
     * 对于nextButton这个控件的跳转事件
     */
    private void nextButtonClickEvent() {
        int current = viewPager.getCurrentItem();
        boolean flag = false;
        LogUtil.v(TAG, "current=" + current);

        if (current == viewList.size() - 1) {
            nextButtonClickCount++;

            if (nextButtonClickCount == 1) {
                Toast.makeText(QuestionActivity.this, "再次点击返回", Toast.LENGTH_SHORT).show();
            }

            flag = true;
        } else {
            nextButtonClickCount = 0;
        }

        if (current == haveReadyLoadQuestionCount - 3) {
            nextButton.setEnabled(false);
        }
        LogUtil.v(TAG, "flag=" + flag);
        LogUtil.v(TAG, "nextButtonClickCount=" + nextButtonClickCount);

        if (flag && nextButtonClickCount == 2) {
            nextButtonClickCount = 0;
            onBackPressed();
        }
    }

    /**
     * 初始化视图
     */
    private void initViews() {
        lastButton = (Button) findViewById(R.id.lastButton);
        nextButton = (Button) findViewById(R.id.nextButton);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
    }

    /**
     * 给MainActivity返回必要的参数
     */
    private void createIntentAndSetResult() {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra("errorQuestion", errorQuestionList);
        int percent = (int)((totalRightCount * 1.0 / totalQuestionCount) * 100);
        int notDoCount = totalQuestionCount - (totalRightCount + totalWrongCount);
        intent.putExtra("percent", percent);
        intent.putExtra("totalRightCount", totalRightCount);
        intent.putExtra("totalWrongCount", totalWrongCount);
        intent.putExtra("notDoCount", notDoCount);

        LogUtil.e(TAG, "totalRightCount=" + totalRightCount);
        LogUtil.e(TAG, "totalWrongCount=" + totalWrongCount);
        LogUtil.e(TAG, "totalQuestionCount=" + totalQuestionCount);
        LogUtil.e(TAG, "notDoCount=" + notDoCount);
        LogUtil.e(TAG, "percent=" + percent);

        setResult(100, intent);
    }

    /**
     * 关闭输入流
     */
    private void closeInputStream() {
        try {
            if (in != null) {
                in.close();
            }
            if (in != null) {
                isr.close();
            }
            if (in != null) {
                br.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        closeInputStream();
        createIntentAndSetResult();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        closeInputStream();
        createIntentAndSetResult();
        super.onDestroy();
    }
}
