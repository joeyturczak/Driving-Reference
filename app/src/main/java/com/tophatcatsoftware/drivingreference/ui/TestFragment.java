package com.tophatcatsoftware.drivingreference.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tophatcatsoftware.drivingreference.R;
import com.tophatcatsoftware.drivingreference.models.Question;
import com.tophatcatsoftware.drivingreference.models.Test;
import com.tophatcatsoftware.drivingreference.utils.QuestionUtility;
import com.tophatcatsoftware.drivingreference.utils.TestUtility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnTestCompleteListener} interface
 * to handle interaction events.
 * Use the {@link TestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TestFragment extends Fragment implements View.OnClickListener {

    private static final String TEST = "Test";

    private Test mTest;

    private RadioGroup mRadioGroup;

    private RadioButton mRadioButtonA;
    private RadioButton mRadioButtonB;
    private RadioButton mRadioButtonC;
    private RadioButton mRadioButtonD;

    private ImageButton mPreviousButton;
    private ImageButton mCheckButton;
    private ImageButton mNextButton;

    private TextView mQuestionNumberText;
    private TextView mStatementText;

    private View mRootView;

    private int mCurrentQuestionNumber;

    private List<String> mQuestionIds;
    private List<String> mAnswers;
    private List<String> mChecked;

    private List<String> mStatements;
    private List<String> mCorrectAnswers;
    private List<String> mAnswerAs;
    private List<String> mAnswerBs;
    private List<String> mAnswerCs;
    private List<String> mAnswerDs;

    private boolean mComplete;

    private OnTestCompleteListener mListener;

    public TestFragment() {
        // Required empty public constructor
    }

    public static TestFragment newInstance(Test test) {
        TestFragment fragment = new TestFragment();
        Bundle args = new Bundle();
        args.putParcelable(TEST, test);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTest = getArguments().getParcelable(TEST);
        }
        mCurrentQuestionNumber = -1;
        mComplete = false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle bundle = new Bundle();
        saveTest(false);
        bundle.putParcelable("Test", saveTestInstance(false));
        bundle.putInt("CurrentQuestion", mCurrentQuestionNumber);
        outState.putBundle("Bundle", bundle);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null) {
            Bundle bundle = savedInstanceState.getBundle("Bundle");
            if(bundle != null) {
                mTest = bundle.getParcelable("Test");
                mCurrentQuestionNumber = bundle.getInt("CurrentQuestion");
            }

            setupTest();

            displayQuestion();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(!mComplete) {
            saveTest(false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_test, container, false);

        mQuestionNumberText = (TextView) mRootView.findViewById(R.id.question_number);
        mStatementText = (TextView) mRootView.findViewById(R.id.question_statement);

        mRadioGroup = (RadioGroup) mRootView.findViewById(R.id.radioGroup);

        initializeRadioButtons();

        mPreviousButton = (ImageButton) mRootView.findViewById(R.id.previous_button);
        mPreviousButton.setOnClickListener(this);
        mCheckButton = (ImageButton) mRootView.findViewById(R.id.check_button);
        mCheckButton.setOnClickListener(this);
        mNextButton = (ImageButton) mRootView.findViewById(R.id.next_button);
        mNextButton.setOnClickListener(this);

        setupTest();

        displayQuestion();

        return mRootView;
    }

    private void initializeRadioButtons() {

        mRadioGroup.removeAllViewsInLayout();

        mRadioButtonA = addRadioButton();
        mRadioButtonA.setId(R.id.answerA);
        mRadioButtonB = addRadioButton();
        mRadioButtonB.setId(R.id.answerB);
        mRadioButtonC = addRadioButton();
        mRadioButtonC.setId(R.id.answerC);
        mRadioButtonD = addRadioButton();
        mRadioButtonD.setId(R.id.answerD);

        mRadioGroup.addView(mRadioButtonA);
        mRadioGroup.addView(mRadioButtonB);
        mRadioGroup.addView(mRadioButtonC);
        mRadioGroup.addView(mRadioButtonD);

        mRadioButtonA.setOnClickListener(this);
        mRadioButtonB.setOnClickListener(this);
        mRadioButtonC.setOnClickListener(this);
        mRadioButtonD.setOnClickListener(this);
    }

    private RadioButton addRadioButton() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.test_radio_button, null, false);

        return (RadioButton) view;
    }

    private void setupTest() {

        if(mTest.getCompleted() == Test.COMPLETED) {
            Toast.makeText(getContext(), "There was an error", Toast.LENGTH_SHORT).show();
            onFinish(false, mTest);
        }

        TestUtility.setTestInProgress(getContext(), true);

        String answerString = mTest.getAnswers();

        mAnswers = new ArrayList<>();
        mQuestionIds = new ArrayList<>();
        mChecked = new ArrayList<>();

        if(!answerString.isEmpty()) {
            mAnswers = TestUtility.getListFromString(answerString);
        }

        mQuestionIds = TestUtility.getListFromString(mTest.getQuestionIds());
        mChecked = TestUtility.getListFromString(mTest.getChecked());

        if(mCurrentQuestionNumber == -1) {
            mCurrentQuestionNumber = mAnswers.size();
        }

        setupQuestions();
    }

    private void setupQuestions() {

        Question question;

        mStatements = new ArrayList<>();
        mCorrectAnswers = new ArrayList<>();
        mAnswerAs = new ArrayList<>();
        mAnswerBs = new ArrayList<>();
        mAnswerCs = new ArrayList<>();
        mAnswerDs = new ArrayList<>();
//        mSelectedAnswers = new ArrayList<>();

        for(String id : mQuestionIds) {
            question = QuestionUtility.getQuestionFromDatabase(getContext(), id);

            if(question == null) {
                return;
            }

            mStatements.add(question.getStatement());
            mCorrectAnswers.add(question.getCorrectAnswer());

            // Randomize answers
            List<String> randomAnswers = new ArrayList<>();
            randomAnswers.add(question.getCorrectAnswer());
            randomAnswers.add(question.getIncorrectAnswerA());
            randomAnswers.add(question.getIncorrectAnswerB());
            String answerC = question.getIncorrectAnswerC();

            boolean isCEmpty = answerC.isEmpty();
            if(!isCEmpty) {
                randomAnswers.add(answerC);
            }

            Collections.shuffle(randomAnswers);

            mAnswerAs.add(randomAnswers.get(0));
            mAnswerBs.add(randomAnswers.get(1));
            mAnswerCs.add(randomAnswers.get(2));

            if(!isCEmpty) {
                mAnswerDs.add(randomAnswers.get(3));
            } else {
                mAnswerDs.add("");
            }
        }
    }

    private void displayQuestion() {

        resetDisplay();

        // Display question
        mQuestionNumberText.setText("Question " + (mCurrentQuestionNumber + 1) + "/" + Test.QUESTION_MAX);
        mStatementText.setText(mStatements.get(mCurrentQuestionNumber));

        String answerA = mAnswerAs.get(mCurrentQuestionNumber);
        String answerB = mAnswerBs.get(mCurrentQuestionNumber);
        String answerC = mAnswerCs.get(mCurrentQuestionNumber);
        String answerD = mAnswerDs.get(mCurrentQuestionNumber);

        mRadioButtonA.setText(answerA);
        mRadioButtonB.setText(answerB);
        mRadioButtonC.setText(answerC);

        if(answerD.isEmpty()) {
            mRadioButtonD.setVisibility(View.GONE);
        } else {
            mRadioButtonD.setText(answerD);
        }

        if(isAnswered()) {
            selectRadioButton();
        } else {
            mRadioGroup.clearCheck();
            setBold(null);
        }

        boolean checked = isQuestionChecked();

        if(checked) {
            String correctAnswer = mCorrectAnswers.get(mCurrentQuestionNumber);
            String selectedAnswer = mAnswers.get(mCurrentQuestionNumber);

            RadioButton correctButton;

            RadioButton selectedButton = getSelectedRadioButton();

            boolean correct = false;

            if(selectedAnswer.equals(correctAnswer)) {
                correct = true;
            }

            if(correct) {
                selectedButton.setButtonDrawable(TestUtility.getCheckDrawable(getContext(), TestUtility.CORRECT));
            } else {
                selectedButton.setButtonDrawable(TestUtility.getCheckDrawable(getContext(), TestUtility.INCORRECT));

                if(answerA.equals(correctAnswer)) {
                    correctButton = mRadioButtonA;
                } else if(answerB.equals(correctAnswer)) {
                    correctButton = mRadioButtonB;
                } else if(answerC.equals(correctAnswer)) {
                    correctButton = mRadioButtonC;
                } else {
                    correctButton = mRadioButtonD;
                }

                correctButton.setButtonDrawable(TestUtility.getCheckDrawable(getContext(), TestUtility.CORRECT));
            }
        }

        updateButtons();
    }

    public void resetDisplay() {
        setBold(null);
        mRadioGroup.clearCheck();
        initializeRadioButtons();
    }

    private void selectRadioButton() {
        String answer = mAnswers.get(mCurrentQuestionNumber);

        int id = getRadioButtonIdFromAnswer(answer);

        mRadioGroup.check(id);
        setBold((RadioButton) mRadioGroup.findViewById(id));
    }

    private int getRadioButtonIdFromAnswer(String answer) {

        String answerA = mAnswerAs.get(mCurrentQuestionNumber);
        String answerB = mAnswerBs.get(mCurrentQuestionNumber);
        String answerC = mAnswerCs.get(mCurrentQuestionNumber);

        if(answer.equals(answerA)) {
            return R.id.answerA;
        } else if(answer.equals(answerB)) {
            return R.id.answerB;
        } else if(answer.equals(answerC)) {
            return R.id.answerC;
        } else {
            return R.id.answerD;
        }
    }

    public void onFinish(boolean complete, Test test) {
        if (mListener != null) {
            mListener.onTestComplete(complete, test);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTestCompleteListener) {
            mListener = (OnTestCompleteListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTestCompleteListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id) {
            case R.id.answerA:
            case R.id.answerB:
            case R.id.answerC:
            case R.id.answerD:
                onAnswerSelected((RadioButton) v);
                break;
            case R.id.previous_button:
                loadPreviousQuestion();
                break;
            case R.id.check_button:
                checkAnswer();
                break;
            case R.id.next_button:
                loadNextQuestion();
                break;
        }
    }

    private void loadPreviousQuestion() {
        mCurrentQuestionNumber--;
        displayQuestion();
    }

    private void checkAnswer() {
        mChecked.set(mCurrentQuestionNumber, Test.CHECKED);
        displayQuestion();
    }

    private void loadNextQuestion() {
        // load next question in array
        mCurrentQuestionNumber++;
        if(mCurrentQuestionNumber >= Test.QUESTION_MAX) {
            // Save test and display review screen
            mComplete = true;
            saveTest(true);
        } else {
            displayQuestion();
        }
    }

    private void enableButtons() {

        // If it's not the first question, enable the previous button
        enableButton(mPreviousButton, mCurrentQuestionNumber > 0);

        // If anything is selected, enable the next button
        if(mRadioButtonA.isChecked() || mRadioButtonB.isChecked() || mRadioButtonC.isChecked() || mRadioButtonD.isChecked()) {
            enableButton(mNextButton, true);
        } else {
            enableButton(mNextButton, false);
        }

        // If the question has been checked then disable it
        if(!isQuestionChecked() && mNextButton.isEnabled()) {
            enableButton(mCheckButton, true);
        } else {
            enableButton(mCheckButton, false);
        }

        if(mCurrentQuestionNumber == Test.QUESTION_MAX - 1) {
            mNextButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_done_all_black_24dp));
        } else {
            mNextButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_forward_black_24dp));
        }
    }

    private void enableRadioButtons() {

        boolean enable = !isQuestionChecked();

        mRadioButtonA.setEnabled(enable);
        mRadioButtonB.setEnabled(enable);
        mRadioButtonC.setEnabled(enable);
        mRadioButtonD.setEnabled(enable);

        RadioButton selectedRadioButton = getSelectedRadioButton();

        if(selectedRadioButton != null) {
            setBold(selectedRadioButton);
        }
    }

    private void enableButton(ImageButton imageButton, boolean enable) {

        if(enable) {
            imageButton.setBackground(getResources().getDrawable(R.drawable.rounded_button_enabled));
            imageButton.setEnabled(true);
        } else {
            imageButton.setBackground(getResources().getDrawable(R.drawable.rounded_button_disabled));
            imageButton.setEnabled(false);
        }
    }

    private void updateButtons() {
        enableButtons();
        enableRadioButtons();
    }

    private void setBold(RadioButton radioButton) {

        mRadioButtonA.setTypeface(null, Typeface.NORMAL);
        mRadioButtonB.setTypeface(null, Typeface.NORMAL);
        mRadioButtonC.setTypeface(null, Typeface.NORMAL);
        mRadioButtonD.setTypeface(null, Typeface.NORMAL);

        if(radioButton != null) {
            radioButton.setTypeface(null, Typeface.BOLD);
        }
    }

    private RadioButton getSelectedRadioButton() {
        int id = mRadioGroup.getCheckedRadioButtonId();

        RadioButton radioButton = null;

        switch (id) {
            case R.id.answerA:
                radioButton = mRadioButtonA;
                break;
            case R.id.answerB:
                radioButton = mRadioButtonB;
                break;
            case R.id.answerC:
                radioButton = mRadioButtonC;
                break;
            case R.id.answerD:
                radioButton = mRadioButtonD;
                break;
        }

        return radioButton;
    }

    private boolean isQuestionChecked() {

        return TestUtility.isChecked(mChecked.get(mCurrentQuestionNumber));
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnTestCompleteListener {
        void onTestComplete(boolean complete, Test test);
    }

    public void onAnswerSelected(RadioButton radioButton) {

        updateButtons();
        setBold(radioButton);

        setAnswer(radioButton.getText().toString());
    }

    private void setAnswer(String answer) {
        if(!isAnswered()) {
            mAnswers.add(answer);
        } else {
            mAnswers.set(mCurrentQuestionNumber, answer);
        }
    }

    private boolean isAnswered() {
        return mAnswers.size() > mCurrentQuestionNumber;
    }

    private int gradeTest() {
        int score = 0;

        for(int i = 0; i < mAnswers.size(); i++) {
            String answer = mAnswers.get(i);
            String correctAnswer = mCorrectAnswers.get(i);
            if(answer.equals(correctAnswer)) {
                score++;
            }
        }

        return score;
    }

    private Test saveTestInstance(boolean complete) {
        String questionIds = TestUtility.getStringFromList(mQuestionIds);
        String answers = TestUtility.getStringFromList(mAnswers);
        String checked = TestUtility.getStringFromList(mChecked);

        int score = -1;
        if(complete) {
            score = gradeTest();
            TestUtility.setTestInProgress(getContext(), false);
        }

        long date = System.currentTimeMillis();

        String location = mTest.getLocation();
        String type = mTest.getType();
        String language = mTest.getLanguage();

        int completed = TestUtility.isCompleted(complete);

        return new Test(date, location, type, language, questionIds, answers, checked, score, completed);
    }

    private void saveTest(boolean complete) {

        Test test = saveTestInstance(complete);

        TestUtility.saveTestToDb(getContext(), test);

        onFinish(complete, test);
    }
}
