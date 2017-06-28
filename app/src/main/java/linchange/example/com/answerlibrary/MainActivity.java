package linchange.example.com.answerlibrary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * 主要框架已经打好，只需替换assets下的file.txt文件的内容即可
 * 该文件的内容七行形成一个完整的问题，每一行分别是：
 * 第一行：问题（计算很长也要放在一行上）
 * 第二行：选项A
 * 第三行：选项B
 * 第四行：选项C（如没有的话留空，但这一行不能省略）
 * 第五行：选项D（如没有的话留空，但这一行不能省略）
 * 第六行：正确答案
 * 第七行：留空
 * PS：在写完所有的项之后要特别多加一行，不然最后一个问题出不来
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    //用以储存Question Activity出来的错题列表
    private static ArrayList<Question> errorQuestionList = new ArrayList<Question>();
    //判断错题数是否更改
    private boolean isErrorQuestionChange;
    //用以显示正确率百分比的TV
    private TextView percentTV;
    //显示正确题数
    private TextView tvRight;
    //显示错误题数
    private TextView tvWrong;
    //显示未做题数
    private TextView tvNotDo;

    private final int allTotalCount = 60; //所有题目总数
    private static int selectedProblemRightCount = 0; //选择题正确数目
    private static int selectedProblemWrongCount = 0; //选择题错误数目
    private static int fillProblemRightCount = 0; //填空题正确数目
    private static int fillProblemWrongCount = 0; //填空题错误数目


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isErrorQuestionChange = false;
        percentTV = (TextView) findViewById(R.id.tv_percent);
        tvRight = (TextView) findViewById(R.id.tvRight);
        tvWrong = (TextView) findViewById(R.id.tvWrong);
        tvNotDo = (TextView) findViewById(R.id.tvNotDo);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 100) {
            ArrayList<Question> questions = errorQuestionList;

            selectedProblemRightCount = data.getIntExtra("totalRightCount", 0); //获取选择题正确数目
            selectedProblemWrongCount = data.getIntExtra("totalWrongCount", 0); //获取选择题错误数目

            errorQuestionList = data.getParcelableArrayListExtra("errorQuestion");
            if (errorQuestionList.isEmpty()) {
                errorQuestionList = questions;
                return;
            }

            if (!questions.equals(errorQuestionList)) {
                isErrorQuestionChange = true;
                boolean isEndQuestion = data.getBooleanExtra("isEndQuestion", false);

//                if (isEndQuestion) {

//                }
                LogUtil.e(TAG, "isErrorQuestionChange = " + isErrorQuestionChange);
            }

            for (int i = 0; i < errorQuestionList.size(); i++) {
                Question question = errorQuestionList.get(i);
                System.out.println(question);
            }
        }

        if (resultCode == 200) {
            fillProblemRightCount = data.getIntExtra("totalRightCount", 0); //获取填空题正确数目
            fillProblemWrongCount = data.getIntExtra("totalWrongCount", 0); //获取填空题错误数目
        }

        int percent = (int)(((selectedProblemRightCount + fillProblemRightCount) * 1.0 / allTotalCount) * 100);
        int totalRightCount = selectedProblemRightCount + fillProblemRightCount;
        int totalWrongCount = selectedProblemWrongCount + fillProblemWrongCount;
        int notDoCount = allTotalCount - totalRightCount - totalWrongCount;

        LogUtil.e(TAG, "percent=" + percent);
        percentTV.setText(percent + "%");
        tvRight.setText(totalRightCount + "");
        tvWrong.setText(totalWrongCount + "");
        tvNotDo.setText(notDoCount + "");
    }


    //跳转到选择题界面
    public void startQuestion(View view) {
        Intent intent = new Intent(getApplicationContext(), QuestionActivity.class);
        startActivityForResult(intent, 100);
    }

    //跳转到判断题界面
    public void startFillQuestion(View view) {
        Intent intent = new Intent(getApplicationContext(), FillQuestionActivity.class);
        startActivityForResult(intent, 300);
    }

    //跳转到错题回顾界面
    public void startReview(View view) {
        Intent intent = new Intent(getApplicationContext(), ReviewActivity.class);
        LogUtil.e(TAG, "startReview");
        for (int i = 0; i < errorQuestionList.size(); i++) {
            Question question = errorQuestionList.get(i);
            System.out.println(question);
        }
        intent.putParcelableArrayListExtra("errorQuestionList", errorQuestionList);
        LogUtil.e(TAG, "put isErrorQuestionChange=" + isErrorQuestionChange);
        intent.putExtra("isQuestionChange", isErrorQuestionChange);

        startActivityForResult(intent, 200);
    }

}
