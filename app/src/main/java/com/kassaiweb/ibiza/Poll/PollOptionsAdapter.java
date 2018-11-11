package com.kassaiweb.ibiza.Poll;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.kassaiweb.ibiza.Constant;
import com.kassaiweb.ibiza.R;
import com.kassaiweb.ibiza.Util.SPUtil;

import java.util.ArrayList;

public class PollOptionsAdapter extends RecyclerView.Adapter<PollOptionsAdapter.ViewHolder> {

    private Poll poll;
    private ArrayList<Answer> answers;
    private RadioButton lastChecked = null;
    private int lastCheckedPos = 0;
    private ArrayList<Boolean> selections = new ArrayList<>();
    private int totalVotes = 0;


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView answerTextView;
        public RadioButton answerRadioButton;
        public CheckBox answerCheckBox;
        public ProgressBar result;
        public ImageView removeButton;
        public TextView voteNumberTextView;

        public ViewHolder(View v) {
            super(v);
            answerTextView = v.findViewById(R.id.poll_answer);
            answerRadioButton = v.findViewById(R.id.poll_radioButton);
            answerCheckBox = v.findViewById(R.id.poll_checkBox);
            result = v.findViewById(R.id.poll_result);
            removeButton = v.findViewById(R.id.poll_remove);
            voteNumberTextView = v.findViewById(R.id.poll_number);
        }
    }


    public PollOptionsAdapter(Poll poll) {
        this.answers = poll.getAnswers();
        this.poll = poll;

        String userId = SPUtil.getString(Constant.CURRENT_USER_ID, null);
        for (int i = 0; i < answers.size(); i++) {
            selections.add(answers.get(i).isSelected(userId));
            totalVotes += answers.get(i).getVotesNumber();
        }
    }

    public PollOptionsAdapter(ArrayList<Answer> answers) {
        this.answers = answers;
        this.poll = new Poll();
    }


    @Override
    public PollOptionsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_poll_option, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.answerTextView.setText(answers.get(position).getAnswer());

        String userId = SPUtil.getString(Constant.CURRENT_USER_ID, null);
        if (poll.isPublicResult() || (poll.getCreatorId() != null && poll.getCreatorId().equals(userId))) {

            if (answers.get(position).getVotesNumber() > 0) {
                holder.voteNumberTextView.setText(Integer.toString(answers.get(position).getVotesNumber()));
            }

            holder.result.setMax(totalVotes);
            holder.result.setProgress(answers.get(position).getVotesNumber());

        } else {
            holder.result.setVisibility(View.GONE);
            holder.voteNumberTextView.setVisibility(View.GONE);
        }


        if (poll.getChoice() != null && poll.getChoice().equals(Constant.CHOICE_SINGLE)) {

            holder.answerRadioButton.setVisibility(View.VISIBLE);
            holder.answerRadioButton.setTag(position);

            if (selections.get(position)) {

                if (lastChecked != null) {
                    lastChecked.setChecked(false);
                    selections.set(lastCheckedPos, false);
                }

                lastChecked = holder.answerRadioButton;
                lastCheckedPos = position;
                holder.answerRadioButton.setChecked(true);
            }

            holder.answerRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    RadioButton cb = (RadioButton) v;
                    int clickedPos = (Integer) cb.getTag();

                    if (cb.isChecked()) {

                        if (lastChecked != null) {
                            lastChecked.setChecked(false);
                            selections.set(lastCheckedPos, false);
                        }

                        lastChecked = cb;
                        lastCheckedPos = clickedPos;

                    } else {
                        lastChecked = null;
                    }

                    selections.set(clickedPos, cb.isChecked());
                }
            });

        }

        if (poll.getChoice() != null && poll.getChoice().equals(Constant.CHOICE_MULTIPLE)) {

            holder.answerCheckBox.setVisibility(View.VISIBLE);
            holder.answerCheckBox.setTag(position);

            if (selections.get(position)) {
                holder.answerCheckBox.setChecked(true);
            }

            holder.answerCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    CheckBox cb = (CheckBox) v;
                    int clickedPos = (Integer) cb.getTag();

                    selections.set(clickedPos, cb.isChecked());
                }
            });

        }

        if (poll.getChoice() == null) {
            // ekkor van a létrehozás
            holder.removeButton.setVisibility(View.VISIBLE);
            holder.removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    answers.remove(answers.get(position));
                    notifyDataSetChanged();
                }
            });
        }

        if (poll.isClosed()) {
            holder.answerCheckBox.setClickable(false);
            holder.answerCheckBox.setFocusable(false);
            holder.answerRadioButton.setClickable(false);
            holder.answerRadioButton.setFocusable(false);
        }

    }

    public ArrayList<Boolean> getSelections() {
        return selections;
    }

    @Override
    public int getItemCount() {
        return answers.size();
    }
}
