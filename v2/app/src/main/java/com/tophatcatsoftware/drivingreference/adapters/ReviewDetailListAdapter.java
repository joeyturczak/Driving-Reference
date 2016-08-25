package com.tophatcatsoftware.drivingreference.adapters;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tophatcatsoftware.drivingreference.R;
import com.tophatcatsoftware.drivingreference.models.Question;
import com.tophatcatsoftware.drivingreference.models.Test;
import com.tophatcatsoftware.drivingreference.utils.QuestionUtility;
import com.tophatcatsoftware.drivingreference.utils.TestUtility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joeyturczak on 4/28/16.
 */
public class ReviewDetailListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Question> mQuestions;
    List<String> mSelectedAnswers;

    Context mContext;
    View mEmptyView;

    public ReviewDetailListAdapter(Context context, View emptyView) {
        mQuestions = new ArrayList<>();
        mSelectedAnswers = new ArrayList<>();

        mContext = context;
        mEmptyView = emptyView;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(parent instanceof RecyclerView) {
            int layoutId = R.layout.review_question_list_item;
            return new ReviewQuestionViewHolder(LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false));
        } else {
            throw new RuntimeException(mContext.getString(R.string.recycler_view_error));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Question question = mQuestions.get(position);
        String selectedAnswer = mSelectedAnswers.get(position);

        String statement = question.getStatement();
        String correctAnswer = question.getCorrectAnswer();


        // Set the views
        ReviewQuestionViewHolder reviewQuestionViewHolder = (ReviewQuestionViewHolder) holder;

        reviewQuestionViewHolder.mStatement.setText(statement);
        reviewQuestionViewHolder.mSelectedAnswerView.setText(selectedAnswer);
        reviewQuestionViewHolder.mCorrectAnswerView.setText(correctAnswer);

        reviewQuestionViewHolder.mCorrectIconView.setImageDrawable(TestUtility.getCheckDrawable(mContext, TestUtility.CORRECT));

        if(selectedAnswer.equals(correctAnswer)) {
            reviewQuestionViewHolder.mSelectedIconView.setImageDrawable(TestUtility.getCheckDrawable(mContext, TestUtility.CORRECT));
        } else {
            reviewQuestionViewHolder.mSelectedIconView.setImageDrawable(TestUtility.getCheckDrawable(mContext, TestUtility.INCORRECT));
        }

        // this enables better animations. even if we lose state due to a device rotation,
        // the animator can use this to re-find the original view
        ViewCompat.setTransitionName(reviewQuestionViewHolder.mSelectedIconView, mContext.getString(R.string.transition_list_icon) + position);
        ViewCompat.setTransitionName(reviewQuestionViewHolder.mCorrectIconView, mContext.getString(R.string.transition_list_icon) + position);
    }

    @Override
    public int getItemCount() {
        return mQuestions.size();
    }

    public class ReviewQuestionViewHolder extends RecyclerView.ViewHolder {
        public final TextView mStatement;
        public final ImageView mSelectedIconView;
        public final TextView mSelectedAnswerView;
        public final ImageView mCorrectIconView;
        public final TextView mCorrectAnswerView;

        public ReviewQuestionViewHolder(View view) {
            super(view);
            mStatement = (TextView) view.findViewById(R.id.question_statement);
            mSelectedIconView = (ImageView) view.findViewById(R.id.selected_answer_icon);
            mSelectedAnswerView = (TextView) view.findViewById(R.id.selected_answer_text);
            mCorrectIconView = (ImageView) view.findViewById(R.id.correct_answer_icon);
            mCorrectAnswerView = (TextView) view.findViewById(R.id.correct_answer_text);
        }
    }

    public void swapData(Test test) {

        List<String> questionIds = TestUtility.getListFromString(test.getQuestionIds());

        mSelectedAnswers = TestUtility.getListFromString(test.getAnswers());

        Question question;

        for(String id : questionIds) {
            question = QuestionUtility.getQuestionFromDatabase(mContext, id);
            if(question != null) {
                mQuestions.add(QuestionUtility.getQuestionFromDatabase(mContext, id));
            } else {
                mSelectedAnswers.remove(questionIds.indexOf(id));
            }
        }
    }
}
