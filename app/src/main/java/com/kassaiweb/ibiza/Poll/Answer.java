package com.kassaiweb.ibiza.Poll;

import java.util.ArrayList;
import java.util.HashMap;

public class Answer {

    private String answer;
    private HashMap<String, Boolean> votes = new HashMap<>();

    public Answer() {
    }

    public Answer(String answer) {
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public HashMap<String, Boolean> getVotes() {
        return votes;
    }

    public void setVotes(HashMap<String, Boolean> votes) {
        this.votes = votes;
    }

    public boolean isSelected(String userID) {
        return votes.containsKey(userID) && votes.get(userID);
    }

    public void setSelected(String userID, boolean selected)
    {
        if (selected) {
            votes.put(userID, true);
        }else {
            votes.put(userID, false);
        }
    }

    public int getVotesNumber() {
        int votesNumber=0;
        for (Boolean vote : votes.values())
        {
            if (vote) { votesNumber++; }
        }
        return votesNumber;
    }

}
