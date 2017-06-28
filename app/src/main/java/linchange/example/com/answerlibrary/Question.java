package linchange.example.com.answerlibrary;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Lin Change on 2017-01-13.
 */

public class Question implements Parcelable {
    //问题文字
    private String question;
    private String answerText1;
    private String answerText2;
    private String answerText3;
    private String answerText4;
    private String rightAnswer;


    public Question(String question, String answerText1, String answerText2, String answerText3, String answerText4, String rightAnswer) {
        this.question = question;
        this.answerText1 = answerText1;
        this.answerText2 = answerText2;
        this.answerText3 = answerText3;
        this.answerText4 = answerText4;
        this.rightAnswer = rightAnswer;
    }

    protected Question(Parcel in) {
        question = in.readString();
        answerText1 = in.readString();
        answerText2 = in.readString();
        answerText3 = in.readString();
        answerText4 = in.readString();
        rightAnswer = in.readString();
    }

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswerText1() {
        return answerText1;
    }

    public void setAnswerText1(String answerText1) {
        this.answerText1 = answerText1;
    }

    public String getAnswerText2() {
        return answerText2;
    }

    public void setAnswerText2(String answerText2) {
        this.answerText2 = answerText2;
    }

    public String getAnswerText3() {
        return answerText3;
    }

    public void setAnswerText3(String answerText3) {
        this.answerText3 = answerText3;
    }

    public String getAnswerText4() {
        return answerText4;
    }

    public void setAnswerText4(String answerText4) {
        this.answerText4 = answerText4;
    }

    public String getRightAnswer() {
        return rightAnswer;
    }

    public void setRightAnswer(String rightAnswer) {
        this.rightAnswer = rightAnswer;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(question);
        parcel.writeString(answerText1);
        parcel.writeString(answerText2);
        parcel.writeString(answerText3);
        parcel.writeString(answerText4);
        parcel.writeString(rightAnswer);
    }

    @Override
    public String toString() {
        return "Question{" +
                "question='" + question + '\'' +
                ", answerText1='" + answerText1 + '\'' +
                ", answerText2='" + answerText2 + '\'' +
                ", answerText3='" + answerText3 + '\'' +
                ", answerText4='" + answerText4 + '\'' +
                ", rightAnswer='" + rightAnswer + '\'' +
                '}';
    }
}
