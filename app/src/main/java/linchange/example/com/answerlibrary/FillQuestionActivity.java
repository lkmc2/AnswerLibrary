package linchange.example.com.answerlibrary;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FillQuestionActivity extends AppCompatActivity {
    //问题在文件中的长度
    private static final int QUESTION_LINE = 2;
    //问题文字
    private TextView questionText;

    //正确答案
    private TextView rightAnswer;
    //用户答案输入框
    private EditText etInput;
    //提交答案按钮
    private Button btnCommit;
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
    private static final String TAG = "FillQuestionActivity";
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
        setContentView(R.layout.activity_fill_question);

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
            in = getResources().getAssets().open("file_fill_question.txt");
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


                btnCommit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String answerText;
                        int current = viewPager.getCurrentItem();
                        LogUtil.v(TAG, "onCheckedChanged current=" + current);

                        View currentView = viewList.get(current);

                        etInput = (EditText) currentView.findViewById(R.id.et_input);
                        btnCommit = (Button) currentView.findViewById(R.id.btn_commit);

                        answerText = questionList.get(current).getRightAnswer().trim(); //获取答案文字
                        int index = answerText.indexOf("：") + 1; //获取冒号的位置
                        answerText = answerText.substring(index); //从冒号后面开始截取答案文字

                        String userInputAnswer = etInput.getText().toString().trim(); //用户输入的文字

                        Log.i(TAG, "answerText=" + answerText);
                        Log.i(TAG, "userInputAnswer=" + userInputAnswer);

                        if (answerText.equals(userInputAnswer)) { //如果用户输入的文字跟正确答案一致
                            etInput.setTextColor(Color.rgb(140, 193, 82)); //设置输入框中文字颜色为绿色
//                            Toast.makeText(FillQuestionActivity.this, "对了", Toast.LENGTH_SHORT).show();
                            totalRightCount++;
                        } else { //如果用户输入的文字跟正确答案不一致
                            etInput.setTextColor(Color.rgb(218, 68, 83)); //设置输入框中文字颜色为红色
//                            Toast.makeText(FillQuestionActivity.this, "错了", Toast.LENGTH_SHORT).show();
                            totalWrongCount++;
                        }
                        etInput.setEnabled(false);
                        btnCommit.setEnabled(false);

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
                question = new Question(answerStrings[0], "", "", "", "", answerStrings[1]);
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
        View view = inflater.inflate(R.layout.item_fill, null);

        etInput = (EditText) view.findViewById(R.id.et_input);
        btnCommit = (Button) view.findViewById(R.id.btn_commit);

        questionText = (TextView) view.findViewById(R.id.question);
        rightAnswer = (TextView) view.findViewById(R.id.rightAnswer);

//        LogUtil.e(TAG, "question.getQuestion()=" + question.getQuestion());
        questionText.setText(question.getQuestion());
        rightAnswer.setText(question.getRightAnswer());

        return view;
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
                    Toast.makeText(FillQuestionActivity.this, "已至最前页", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(FillQuestionActivity.this, "再次点击返回", Toast.LENGTH_SHORT).show();
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

    /**
     * 给MainActivity返回必要的参数
     */
    private void createIntentAndSetResult() {
        Intent intent = new Intent();

        int notDoCount = totalQuestionCount - (totalRightCount + totalWrongCount);
        intent.putExtra("totalRightCount", totalRightCount);
        intent.putExtra("totalWrongCount", totalWrongCount);
        intent.putExtra("notDoCount", notDoCount);

        LogUtil.e(TAG, "totalRightCount=" + totalRightCount);
        LogUtil.e(TAG, "totalWrongCount=" + totalWrongCount);
        LogUtil.e(TAG, "totalQuestionCount=" + totalQuestionCount);
        LogUtil.e(TAG, "notDoCount=" + notDoCount);

        setResult(200, intent);
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
