package de.qa.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;

import de.qa.R;
import de.qa.qa.QuestionAnswerer;
import de.qa.qa.result.FooterResult;
import de.qa.qa.result.HeaderResult;
import de.qa.qa.result.QAResult;
import de.qa.qa.result.UriResult;
import de.qa.view.adapter.QAAdapter;
import de.qa.view.animator.SlideUpItemAnimator;

public class QAFragment extends Fragment implements View.OnClickListener,
        QuestionAnswerer.QAListener, QAAdapter.OnItemClickListener {
    private static final String TAG = QAFragment.class.getSimpleName();

    private View mRootView;
    private ImageView mQaButton;
    private EditText mQuestionInput;
    private RecyclerView mResultsRecycler;
    private ArrayList<QAResult> mAnswers = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_qa, container, false);
        mQaButton = (ImageView) mRootView.findViewById(R.id.qa_btn);
        mQuestionInput = (EditText) mRootView.findViewById(R.id.question_input);
        mQaButton.setOnClickListener(this);
        mResultsRecycler = (RecyclerView) mRootView.findViewById(R.id.results_recycler);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setReverseLayout(true);
        mResultsRecycler.setLayoutManager(llm);
        QAAdapter adapter = new QAAdapter(mAnswers);
        adapter.setOnItemClickListener(this);
        mResultsRecycler.setAdapter(adapter);
        return mRootView;
    }

    @Override
    public void onClick(View view) {
        if (view == mQaButton) {
            String question = mQuestionInput.getText().toString();
            QuestionAnswerer questionAnswerer = new QuestionAnswerer(getContext(), this);
            questionAnswerer.answerQuestion(question);
            mAnswers.add(0, new HeaderResult(question));
            mResultsRecycler.getAdapter().notifyItemInserted(0);
            mAnswers.add(0, new FooterResult(question));
            mResultsRecycler.getAdapter().notifyItemInserted(0);
            mQuestionInput.setText("");
            mResultsRecycler.smoothScrollToPosition(0);
        }
    }

    @Override
    public void onQuestionAnswered(QAResult[] results) {
        for (QAResult result : results) {
            mAnswers.add(1, result);
            mResultsRecycler.getAdapter().notifyItemInserted(1);
        }
        mResultsRecycler.smoothScrollToPosition(0);
    }

    @Override
    public void onItemClick(int position, View view) {
        if (mAnswers.get(position) instanceof UriResult) {
            ViewCompat.setTransitionName(view.findViewById(R.id.item_qa_text), "title");
            getFragmentManager()
                    .beginTransaction()
                    .addSharedElement(view.findViewById(R.id.item_qa_text), "title")
                    .addToBackStack(null)
                    .replace(android.R.id.content,
                            UriDetailFragment.newInstance(getContext(),
                                    (UriResult) mAnswers.get(position)))
                    .commit();
        }
    }
}
